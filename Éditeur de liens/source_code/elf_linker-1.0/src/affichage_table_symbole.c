#include "affichage_table_symbole.h"

#include <string.h>

void convert_endian_symbol(Elf32_Sym *symbol) {
    swap_bytes(&symbol->st_name, sizeof(symbol->st_name));
    swap_bytes(&symbol->st_value, sizeof(symbol->st_value));
    swap_bytes(&symbol->st_size, sizeof(symbol->st_size));
    swap_bytes(&symbol->st_shndx, sizeof(symbol->st_shndx));
}

//****************************************************************************************//
//****************************************************************************************//

const char *get_symbol_type(unsigned char st_info) {
    switch (ELF32_ST_TYPE(st_info)) {
        case STT_NOTYPE: return "NOTYPE";
        case STT_OBJECT: return "OBJECT";
        case STT_FUNC: return "FUNC";
        case STT_SECTION: return "SECTION";
        case STT_FILE: return "FILE";
        case STT_COMMON: return "COMMON";
        case STT_TLS: return "TLS";
        default: return "UNKNOWN";
    }
}

//****************************************************************************************//
//****************************************************************************************//

const char *get_symbol_bind(unsigned char st_info) {
    switch (ELF32_ST_BIND(st_info)) {
        case STB_LOCAL: return "LOCAL";
        case STB_GLOBAL: return "GLOBAL";
        case STB_WEAK: return "WEAK";
        default: return "UNKNOWN";
    }
}

//****************************************************************************************//
//****************************************************************************************//

const char *get_symbol_visibility(unsigned char st_other) {
    switch (ELF32_ST_VISIBILITY(st_other)) {
        case STV_DEFAULT: return "DEFAULT";
        case STV_INTERNAL: return "INTERNAL";
        case STV_HIDDEN: return "HIDDEN";
        case STV_PROTECTED: return "PROTECTED";
        default: return "UNKNOWN";
    }
}

//****************************************************************************************//
//****************************************************************************************//

const char *get_section_index_name(uint16_t shndx) {
    switch (shndx) {
        case SHN_UNDEF: return "UND";   // Section non définie
        case SHN_ABS: return "ABS";     // Section absolue
        case SHN_COMMON: return "COMMON"; // Section commune
        default: return NULL;           // Retourne NULL pour les sections ordinaires
    }
}

//****************************************************************************************//
//****************************************************************************************//


int read_symbol_table(FILE *file, Elf32_Ehdr *header, SectionHeaderTable *sections, SymbolTable *symtab, const char *section_name) {
    for (size_t i = 0; i < sections->size; i++) {
        const char *name = &sections->shstrtab[sections->headers[i].sh_name];
        if (strcmp(name, section_name) == 0) {
            Elf32_Shdr *symtab_section = &sections->headers[i];
            symtab->size = symtab_section->sh_size / sizeof(Elf32_Sym);
            symtab->symbols = malloc(symtab_section->sh_size);

            if (!symtab->symbols) {
                perror("Erreur d'allocation pour la table des symboles");
                return -1;
            }

            if (fseek(file, symtab_section->sh_offset, SEEK_SET) != 0 ||
                fread(symtab->symbols, sizeof(Elf32_Sym), symtab->size, file) != symtab->size) {
                perror("Erreur de lecture de la table des symboles");
                free(symtab->symbols);
                return -1;
            }

            // Conversion des symboles si Big Endian
            if (header->e_ident[EI_DATA] == ELFDATA2MSB) {
                for (size_t j = 0; j < symtab->size; j++) {
                    convert_endian_symbol(&symtab->symbols[j]);
                }
            }

            // Lecture de la table des chaînes
            Elf32_Shdr *strtab_section = &sections->headers[symtab_section->sh_link];
            symtab->strtab = malloc(strtab_section->sh_size);
            if (!symtab->strtab) {
                perror("Erreur d'allocation pour la table des chaînes");
                free(symtab->symbols);
                return -1;
            }

            if (fseek(file, strtab_section->sh_offset, SEEK_SET) != 0 ||
                fread(symtab->strtab, 1, strtab_section->sh_size, file) != strtab_section->sh_size) {
                perror("Erreur de lecture de la table des chaînes");
                free(symtab->symbols);
                free(symtab->strtab);
                return -1;
            }

            symtab->name = (char *)section_name;
            return 0;
        }
    }
    return -1; // Section introuvable
}

//****************************************************************************************//
//****************************************************************************************//

void free_table_symbol(SymbolTable *symtab) {
    if (symtab) {
        free(symtab->symbols);
        free(symtab->strtab);
    }
}

//****************************************************************************************//
//****************************************************************************************//

void display_symboles_table(const SymbolTable *symtab, const SectionHeaderTable *sections) {
    printf("\nSymbol table '%s' contains %lu entries:\n", symtab->name, symtab->size);
    printf("   Num:    Value  Size Type    Bind   Vis      Ndx Name\n");
    for (size_t i = 0; i < symtab->size; i++) {
        Elf32_Sym *sym = &symtab->symbols[i];
        const char *symbol_name = NULL;

        // Retrieve the section name if the index is valid
        const char *section_name = get_section_index_name(sym->st_shndx);

        // Retrieve the symbol name
        if (sym->st_name != 0) {  // Check if there is a name in the string table
            symbol_name = &symtab->strtab[sym->st_name];
        } else {
            // If the symbol name is empty but linked to a section, display the section name
            symbol_name = &sections->shstrtab[sections->headers[sym->st_shndx].sh_name];
        } 

        if (!section_name) {
            printf("%6lu: %08x %5u %-7s %-6s %-8s %4u %s\n",
                   i,
                   sym->st_value,
                   sym->st_size,
                   get_symbol_type(sym->st_info),
                   get_symbol_bind(sym->st_info),
                   get_symbol_visibility(sym->st_other),
                   sym->st_shndx, 
                   symbol_name);
        } else {
            printf("%6lu: %08x %5u %-7s %-6s %-8s %4s %s\n",
                   i,
                   sym->st_value,
                   sym->st_size,
                   get_symbol_type(sym->st_info),
                   get_symbol_bind(sym->st_info),
                   get_symbol_visibility(sym->st_other),
                   section_name, 
                   symbol_name);
        }
    }
}


