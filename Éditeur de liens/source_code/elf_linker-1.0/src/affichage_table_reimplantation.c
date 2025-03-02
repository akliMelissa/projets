#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "affichage_table_reimplantation.h"  
#include <stdbool.h>


// Fonction pour obtenir le nom du type de réimplantation
const char* get_type_rel(unsigned int type) {
    switch (type) {
        case 0: return "R_ARM_NONE";
        case 1: return "R_ARM_PC24";
        case 2: return "R_ARM_ABS32";
        case 3: return "R_ARM_REL32";
        case 4: return "R_ARM_LDR_PC_G0";
        case 5: return "R_ARM_ABS16";
        case 6: return "R_ARM_ABS12";
        case 7: return "R_ARM_THM_ABS5";
        case 8: return "R_ARM_ABS8";
        case 9: return "R_ARM_SBREL32";
        case 10: return "R_ARM_THM_CALL";
        case 11: return "R_ARM_THM_PC8";
        case 12: return "R_ARM_BREL_ADJ";
        case 13: return "R_ARM_TLS_DESC";
        case 14: return "R_ARM_THM_SWI8";
        case 15: return "R_ARM_XPC25";
        case 16: return "R_ARM_THM_XPC22";
        case 17: return "R_ARM_TLS_DTPMOD32";
        case 18: return "R_ARM_TLS_DTPOFF32";
        case 19: return "R_ARM_TLS_TPOFF32";
        case 20: return "R_ARM_COPY";
        case 21: return "R_ARM_GLOB_DAT";
        case 22: return "R_ARM_JUMP_SLOT";
        case 23: return "R_ARM_RELATIVE";
        case 24: return "R_ARM_GOTOFF32";
        case 25: return "R_ARM_BASE_PREL";
        case 26: return "R_ARM_GOT_BREL";
        case 27: return "R_ARM_PLT32";
        case 28: return "R_ARM_CALL";
        case 29: return "R_ARM_JUMP24";
        case 30: return "R_ARM_THM_JUMP24";
        case 31: return "R_ARM_BASE_ABS";
        case 32: return "R_ARM_ALU_PCREL_7_0";
        case 33: return "R_ARM_ALU_PCREL_15_8";
        case 34: return "R_ARM_ALU_PCREL_23_15";
        case 35: return "R_ARM_LDR_SBREL_11_0_NC";
        case 36: return "R_ARM_ALU_SBREL_19_12_NC";
        case 37: return "R_ARM_ALU_SBREL_27_20_CK";
        case 38: return "R_ARM_TARGET1";
        case 39: return "R_ARM_SBREL31";
        case 40: return "R_ARM_V4BX";
        case 41: return "R_ARM_TARGET2";
        case 42: return "R_ARM_PREL31";
        case 43: return "R_ARM_MOVW_ABS_NC";
        case 44: return "R_ARM_MOVT_ABS";
        case 45: return "R_ARM_MOVW_PREL_NC";
        case 46: return "R_ARM_MOVT_PREL";
        case 47: return "R_ARM_THM_MOVW_ABS_NC";
        case 48: return "R_ARM_THM_MOVT_ABS";
        case 49: return "R_ARM_THM_MOVW_PREL_NC";
        case 50: return "R_ARM_THM_MOVT_PREL";
        case 51: return "R_ARM_THM_JUMP19";
        case 52: return "R_ARM_THM_JUMP6";
        case 53: return "R_ARM_THM_ALU_PREL_11_0";
        case 54: return "R_ARM_THM_PC12";
        case 55: return "R_ARM_ABS32_NOI";
        case 56: return "R_ARM_REL32_NOI";
        case 57: return "R_ARM_ALU_PC_G0_NC";
        case 58: return "R_ARM_ALU_PC_G0";
        case 59: return "R_ARM_ALU_PC_G1_NC";
        case 60: return "R_ARM_ALU_PC_G1";
        case 61: return "R_ARM_ALU_PC_G2";
        case 62: return "R_ARM_LDR_PC_G1";
        case 63: return "R_ARM_LDR_PC_G2";
        case 64: return "R_ARM_LDRS_PC_G0";
        case 65: return "R_ARM_LDRS_PC_G1";
        case 66: return "R_ARM_LDRS_PC_G2";
        case 67: return "R_ARM_LDC_PC_G0";
        case 68: return "R_ARM_LDC_PC_G1";
        case 69: return "R_ARM_LDC_PC_G2";
        case 70: return "R_ARM_ALU_SB_G0_NC";
        case 71: return "R_ARM_ALU_SB_G0";
        case 72: return "R_ARM_ALU_SB_G1_NC";
        case 73: return "R_ARM_ALU_SB_G1";
        case 74: return "R_ARM_ALU_SB_G2";
        case 75: return "R_ARM_LDR_SB_G0";
        case 76: return "R_ARM_LDR_SB_G1";
        case 77: return "R_ARM_LDR_SB_G2";
        case 78: return "R_ARM_LDRS_SB_G0";
        case 79: return "R_ARM_LDRS_SB_G1";
        case 80: return "R_ARM_LDRS_SB_G2";
        case 81: return "R_ARM_LDC_SB_G0";
        case 82: return "R_ARM_LDC_SB_G1";
        case 83: return "R_ARM_LDC_SB_G2";
        case 84: return "R_ARM_MOVW_BREL_NC";
        case 85: return "R_ARM_MOVT_BREL";
        case 86: return "R_ARM_MOVW_BREL";
        case 87: return "R_ARM_THM_MOVW_BREL_NC";
        case 88: return "R_ARM_THM_MOVT_BREL";
        case 89: return "R_ARM_THM_MOVW_BREL";
        case 90: return "R_ARM_TLS_GOTDESC";
        case 91: return "R_ARM_TLS_CALL";
        case 92: return "R_ARM_TLS_DESCSEQ";
        case 93: return "R_ARM_THM_TLS_CALL";
        case 94: return "R_ARM_PLT32_ABS";
        case 95: return "R_ARM_GOT_ABS";
        case 96: return "R_ARM_GOT_PREL";
        case 97: return "R_ARM_GOT_BREL12";
        case 98: return "R_ARM_GOTOFF12";
        case 99: return "R_ARM_GOTRELAX";
        case 100: return "R_ARM_GNU_VTENTRY";
        case 101: return "R_ARM_GNU_VTINHERIT";
        case 102: return "R_ARM_THM_JUMP11";
        case 103: return "R_ARM_THM_JUMP8";
        case 104: return "R_ARM_TLS_GD32";
        case 105: return "R_ARM_TLS_LDM32";
        case 106: return "R_ARM_TLS_LDO32";
        case 107: return "R_ARM_TLS_IE32";
        case 108: return "R_ARM_TLS_LE32";
        case 109: return "R_ARM_TLS_LDO12";
        case 110: return "R_ARM_TLS_LE12";
        case 111: return "R_ARM_TLS_IE12GP";
        case 112: return "R_ARM_ME_TOO";
        case 113: return "R_ARM_THM_TLS_DESCSEQ16";
        case 114: return "R_ARM_THM_TLS_DESCSEQ32";
        case 252: return "R_ARM_IRELATIVE";
        case 253: return "R_ARM_RXPC25";
        case 254: return "R_ARM_RSBREL32";
        case 255: return "R_ARM_THM_RPC22";
        default: return "UNKNOWN";
    }
}

//********************************************************************************************************//
//********************************************************************************************************//


const char* find_symbol_name_with_shlink(SymbolTable *symtab, SymbolTable *dynsymtab, 
    int sym_idx, Elf32_Addr *sym_value, Elf32_Shdr *section, 
    int symtab_index, int dynsym_index) {

    SymbolTable *target_symtab = NULL;

    if ((unsigned int)section->sh_link == (unsigned int)symtab_index) {
        target_symtab = symtab; 
    } else if ((unsigned int)section->sh_link == (unsigned int)dynsym_index) {
        target_symtab = dynsymtab; 
    }

    if (target_symtab && (size_t)sym_idx < target_symtab->size) {
        Elf32_Sym *symbol = &target_symtab->symbols[sym_idx];
        if (symbol->st_name != 0) {
            *sym_value = symbol->st_value;
            return &target_symtab->strtab[symbol->st_name];
        }
    }
    // Si le symbole n'est pas trouvé, renvoyer le nom de la section
    return (char*) &target_symtab->symbols->st_name ;
}

//********************************************************************************************************//
//********************************************************************************************************//


// Fonction pour obtenir le nom de la section d'un symbole donné
const char* obtenir_nom_section_pour_symbole(SymbolTable *symtab, SectionHeaderTable *section_table, int sym_index) {
    if (!symtab || !section_table || sym_index < 0 || (size_t)sym_index >= symtab->size) {
        return "";
    }

    Elf32_Sym *symbole = &symtab->symbols[sym_index];
    if (symbole->st_shndx == SHN_UNDEF || symbole->st_shndx >= section_table->size) {
        return "";
    }
    

    return &section_table->shstrtab[section_table->headers[symbole->st_shndx].sh_name];
}

//********************************************************************************************************//
//********************************************************************************************************//


void convert_endian_rel_entries(Elf32_Rel* rel_entries , int num_entries){
    //conversion en little endian pour la lecture
    if (is_system_in_little_endian()) {
        for (int j = 0; j < num_entries; j++) {
            swap_bytes(&rel_entries[j].r_offset, sizeof(rel_entries[j].r_offset));
            swap_bytes(&rel_entries[j].r_info, sizeof(rel_entries[j].r_info));
        }
    }
}

//********************************************************************************************************//
//********************************************************************************************************//


Elf32_Rel * read_relocation_entries( FILE* file, Elf32_Shdr *section , int num_entries , int size ){
      // Allouer de la mémoire pour lire les entrées REL
    Elf32_Rel *rel_entries = malloc(size);
    if (!rel_entries) {
        perror("Erreur d'allocation pour les entrées REL");
        exit(EXIT_FAILURE);
    }

    // Lire les entrées REL depuis le fichier
    fseek(file, section->sh_offset, SEEK_SET);
    if (fread(rel_entries, size, 1, file) != 1) {
        perror("Erreur de lecture des entrées REL");
        free(rel_entries);
        exit(EXIT_FAILURE);
    }

    convert_endian_rel_entries(rel_entries , num_entries);
    return rel_entries; 
       
}

//********************************************************************************************************//
//********************************************************************************************************//


SectionHeaderTable get_relocation_sections(SectionHeaderTable *section_table) {
    if (!section_table || section_table->size == 0 || !section_table->headers) {
        fprintf(stderr, "Invalid section table.\n");
        exit(EXIT_FAILURE);
    }

    // Allocate memory for the relocation sections
    Elf32_Shdr *rel_sections = malloc(section_table->size * sizeof(Elf32_Shdr));
    if (!rel_sections) {
        fprintf(stderr, "Memory allocation error for relocation sections.\n");
        exit(EXIT_FAILURE);
    }

    int nb_rel_sections = 0;

    // Iterate through the section headers
    for (size_t i = 0; i < section_table->size; i++) {
        Elf32_Shdr *section = &section_table->headers[i];

        // Check if the section is a relocation section (SHT_REL or SHT_RELA)
        if (section->sh_type == SHT_REL || section->sh_type == SHT_RELA) {
            // Copy the section to the relocation section array
            memcpy(&rel_sections[nb_rel_sections], section, sizeof(Elf32_Shdr));
            nb_rel_sections++;
        }
    }

    SectionHeaderTable rel_section_headers_table = {rel_sections, section_table->shstrtab , nb_rel_sections};
    return rel_section_headers_table;
}

//********************************************************************************************************//
//********************************************************************************************************//


// Fonction pour afficher les tables de réimplantation pour les sections SHT_REL
void display_relocation_table(FILE *file, SectionHeaderTable *rel_sections_table, SymbolTable *symtab, SymbolTable *dynsymtab, 
    int symtab_index, int dynsym_index, SectionHeaderTable *entete) {

    for (size_t i = 0; i < rel_sections_table->size; i++) {
        
        Elf32_Shdr *section = &rel_sections_table->headers[i];
        
            int num_entries = (section->sh_entsize > 0)  ? (section->sh_size / section->sh_entsize) : 0;
            if (num_entries == 1){
                printf("\nRelocation section '%s' at offset 0x%x contains %d entry:\n",
                   &rel_sections_table->shstrtab[section->sh_name],
                   section->sh_offset,
                   num_entries);
            }else{
                printf("\nRelocation section '%s' at offset 0x%x contains %d entries:\n",
                   &rel_sections_table->shstrtab[section->sh_name],
                   section->sh_offset,
                   num_entries);
            }
            
            Elf32_Rel *rel_entries=read_relocation_entries(file, section , num_entries , section->sh_size);

            printf(" Offset     Info    Type            Sym.Value    Sym. Name \n");

            // Parcourir chaque entrée REL
            for (int j = 0; j < num_entries; j++) {
                Elf32_Rel *entry = &rel_entries[j];
                int sym_idx = ELF32_R_SYM(entry->r_info);
                int type = ELF32_R_TYPE(entry->r_info);

                Elf32_Addr sym_value = 0;
                const char *sym_name = find_symbol_name_with_shlink(symtab, dynsymtab, 
                    sym_idx, &sym_value, 
                    section, symtab_index, dynsym_index);

                if (!sym_name || sym_name[0] == '\0') {
                    // Si le nom du symbole est vide mais lié à une section, afficher le nom de la section
                    sym_name = obtenir_nom_section_pour_symbole(symtab, entete, sym_idx);
                }

                // Afficher les informations de l'entrée REL
                if (strcmp(get_type_rel(type) , "R_ARM_V4BX") != 0)     
                {
                    printf("%08x  %08x %-16s %08x    %s \n",
                       entry->r_offset,
                       entry->r_info,
                       get_type_rel(type),
                       sym_value,
                       sym_name);
                }
                else{
                    printf("%08x  %08x %-16s  \n",
                       entry->r_offset,
                       entry->r_info,
                       get_type_rel(type));
                }
            }
            
            free(rel_entries);
    }
   
}

