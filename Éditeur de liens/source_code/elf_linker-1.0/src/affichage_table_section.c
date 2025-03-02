#include <stdio.h> 
#include <stdlib.h>
#include <elf.h>
#include "affichage_entete.h"
#include "affichage_table_section.h"


void convert_endian_section_header(Elf32_Shdr *section) {
    swap_bytes(&section->sh_name, sizeof(section->sh_name));
    swap_bytes(&section->sh_type, sizeof(section->sh_type));
    swap_bytes(&section->sh_flags, sizeof(section->sh_flags));
    swap_bytes(&section->sh_addr, sizeof(section->sh_addr));
    swap_bytes(&section->sh_offset, sizeof(section->sh_offset));
    swap_bytes(&section->sh_size, sizeof(section->sh_size));
    swap_bytes(&section->sh_link, sizeof(section->sh_link));
    swap_bytes(&section->sh_info, sizeof(section->sh_info));
    swap_bytes(&section->sh_addralign, sizeof(section->sh_addralign));
    swap_bytes(&section->sh_entsize, sizeof(section->sh_entsize));
}   

//****************************************************************************************//
//****************************************************************************************//

const char *get_section_type(Elf32_Word sh_type) {
    switch (sh_type) {
        case 0: return "NULL";
        case 1: return "PROGBITS";
        case 2: return "SYMTAB";
        case 3: return "STRTAB";
        case 4: return "RELA";
        case 5: return "HASH";
        case 6: return "DYNAMIC";
        case 7: return "NOTE";
        case 8: return "NOBITS";
        case 9: return "REL";
        case 10: return "SHLIB";
        case 11: return "DYNSYM";
        case 14: return "INIT_ARRAY";
        case 15: return "FINI_ARRAY";
        case 16: return "PREINIT_ARRAY";
        case 17: return "GROUP";
        case 18: return "SYMTAB_SHNDX";
        case 0x60000000: return "LOOS";
        case 0x6FFFFFFF: return "HIOS";
        case 0x6FFFFFF6: return "GNU_HASH";
        case 0x6FFFFFFE: return "VERNEED";
        case 0x70000000: return "LOPROC";
        case 0x7FFFFFFF: return "HIPROC";
        case 0x70000003: return "ARM_ATTRIBUTES";
        default: return "UNKNOWN";
    }
}

//****************************************************************************************//
//****************************************************************************************//

Elf32_Shdr* read_sections_table(FILE *F , Elf32_Ehdr en_tete){ 

    // Allouer de la mémoire pour la table des sections
    Elf32_Shdr *elf_section = malloc(en_tete.e_shnum * sizeof(Elf32_Shdr));
    if (!elf_section) {
        fprintf(stderr, "Erreur d'allocation mémoire pour les sections.\n");
        exit(EXIT_FAILURE);
    }

    if (fseek(F, en_tete.e_shoff, SEEK_SET) != 0) {
        perror("Erreur lors du positionnement pour lire les sections");
        free(elf_section);
        exit(EXIT_FAILURE);
    }

    //lecture des sections
    size_t n = fread(elf_section, sizeof(Elf32_Shdr), en_tete.e_shnum, F);
    if (n != en_tete.e_shnum) {
        perror("Erreur de lecture des en-têtes de section");
        free(elf_section);
        exit(EXIT_FAILURE);
    }

    if((is_system_in_little_endian() && en_tete.e_ident[EI_DATA]==2)|| 
    ( !is_system_in_little_endian() &&  en_tete.e_ident[EI_DATA]==1)){

        for (int i = 0; i < en_tete.e_shnum; i++) {
            convert_endian_section_header(&elf_section[i]);
        }
    }
    return elf_section;

}

//****************************************************************************************//
//****************************************************************************************//

char* read_string_table(FILE *F, Elf32_Ehdr en_tete, Elf32_Shdr* elf_section) {
    
    // Allouer de la mémoire pour string table (shstrtab)
    Elf32_Shdr shstrtab = elf_section[en_tete.e_shstrndx];
    char *section_names = malloc(shstrtab.sh_size);

    if (!section_names) {
        perror("Erreur d'allocation mémoire pour la table des noms de section");
        free(elf_section);
        exit(EXIT_FAILURE);
    }

    // Se positionner sur le début de la table des noms des sections
    if (fseek(F, shstrtab.sh_offset, SEEK_SET) != 0) {
        perror("Erreur lors du positionnement pour lire la table des noms de section");
        free(section_names);
        free(elf_section);
        exit(EXIT_FAILURE);
    }

    // Lire la table des noms de section
    if (fread(section_names, 1, shstrtab.sh_size, F) != shstrtab.sh_size) {
        perror("Erreur de lecture de la table des noms de section");
        free(section_names);
        free(elf_section);
        exit(EXIT_FAILURE);
    }

    return section_names;
}

//****************************************************************************************//
//****************************************************************************************//

void get_flags(Elf32_Word sh_flags, char *flags) {
    int flag_index = 0;

    if (sh_flags & 0x1) flags[flag_index++] = 'W'; // Writable
    if (sh_flags & 0x2) flags[flag_index++] = 'A'; // Allocated
    if (sh_flags & 0x4) flags[flag_index++] = 'X'; // Executable
    if (sh_flags & 0x10) flags[flag_index++] = 'M'; // Mergeable
    if (sh_flags & 0x20) flags[flag_index++] = 'S'; // Strings
    if (sh_flags & 0x40) flags[flag_index++] = 'I'; // Info
    if (sh_flags & 0x80) flags[flag_index++] = 'L'; // Link order
    if (sh_flags & 0x100) flags[flag_index++] = 'O'; // OS-specific
    if (sh_flags & 0x200) flags[flag_index++] = 'G'; // Group
    if (sh_flags & 0x400) flags[flag_index++] = 'T'; // TLS
    if (sh_flags & 0x800) flags[flag_index++] = 'C'; // Compressed
    if (sh_flags & 0x1000) flags[flag_index++] = 'x'; // Unknown
    if (sh_flags & 0x2000) flags[flag_index++] = 'o'; // OS-specific
    if (sh_flags & 0x4000) flags[flag_index++] = 'E'; // Exclude
    if (sh_flags & 0x8000) flags[flag_index++] = 'p'; // Processor-specific

    flags[flag_index] = '\0'; 
}

//****************************************************************************************//
//****************************************************************************************//

void display_section_table( Elf32_Ehdr en_tete , Elf32_Shdr* elf_section , char* section_names) {

    Elf32_Shdr shstrtab = elf_section[en_tete.e_shstrndx];

    printf("There are %d section headers, starting at offset 0x%x:\n\n", en_tete.e_shnum, en_tete.e_shoff);
    printf("Section Headers:\n");
    printf("  [Nr] Name              Type            Addr     Off    Size   ES Flg Lk Inf Al\n");

    for (int i = 0; i < en_tete.e_shnum; i++) {
        Elf32_Shdr *section = &elf_section[i];

        if (section->sh_name >= shstrtab.sh_size) {
            fprintf(stderr, "Erreur : sh_name invalide pour la section %d\n", i);
            continue;
        }

        const char *name = &section_names[section->sh_name]; 
        const char *type = get_section_type(section->sh_type); 
        
        char flags[16] = {0};
        get_flags(section->sh_flags, flags);

        // Print section information
        printf("  [%2d] %-16s %-15s %08x %06x %06x %02x %-3s %-2d %-3d %-2d\n",
            i,
            name,                             
            type,                             
            section->sh_addr,                 
            section->sh_offset,               
            section->sh_size,                 
            section->sh_entsize,            
            flags,                            // Flags (Flg)
            section->sh_link,                 // Link index (Lk)
            section->sh_info,                 // Extra info (Inf)
            section->sh_addralign);           // Alignment (Al)
    }

    printf("\n\nKey to Flags:\n");
    printf(" W (write), A (alloc), X (execute), M (merge), S (strings), I (info),\n"
           " L (link order), O (extra OS processing required), G (group), T (TLS),\n"
           " C (compressed), x (unknown), o (OS specific), E (exclude),\n"
           " p (processor specific)\n");

}
