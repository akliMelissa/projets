#include "appliquation_des_reimplantations.h"


const char* extract_target_section_name(const char* section_name) {
    const char prefix[] = ".rel";
    size_t prefix_len = strlen(prefix);

    if (strncmp(section_name, prefix, prefix_len) == 0) {
        return section_name + prefix_len;
    }

    return section_name;
}

//**********************************************************************************************************//
//**********************************************************************************************************//


int find_section_index(Elf32_Shdr** non_rel_sections, int nb_sections, const char* shstrtab,
 Elf32_Shdr* section){

    // Récupérer le nom de la section qu'on cherche
    const char* target_name = extract_target_section_name(&shstrtab[section->sh_name]);

    for (int i = 0; i < nb_sections; i++) {
        Elf32_Shdr* sec = non_rel_sections[i];
        if (sec == NULL) {
            continue;
        }

        // Récupérer le nom de la section i dans non_rel_sections
        const char* sec_name = &shstrtab[sec->sh_name];

        // Compare les deux noms
        if (strcmp(sec_name, target_name) == 0) {
            return i; // On a trouvé l'index
        }
    }
    return -1; //index non trouvé
}

//**********************************************************************************************************//
//**********************************************************************************************************//

int is_thumb_instruction(Elf32_Sym *symbol) {
    // Vérifier si le symbole est de type fonction
    if (ELF32_ST_TYPE(symbol->st_info) == STT_FUNC) {
        // Vérifier si l'adresse du symbole pointe vers une instruction Thumb
        if (symbol->st_value & 1) {
            return 1; // T = 1, c'est une fonction Thumb
        }
    }
    return 0; //pas une instruction Thumb
}


//*************************************fonctions_de_lecture*************************************************//
//**********************************************************************************************************//


uint32_t read_addend32(size_t offset, unsigned char** sections_content, int section_index) {
    unsigned char* data = sections_content[section_index];

    // Vérification de la validité du pointeur
    if (data == NULL) {
        fprintf(stderr, "Erreur : données de section NULL pour l'indice %d\n", section_index);
        return 0;
    }

    // Lecture des 4 octets en big-endian
    uint32_t value = 0;
    value |= (data[offset] << 24);      //(MSB)
    value |= (data[offset + 1] << 16);  
    value |= (data[offset + 2] << 8);  
    value |= data[offset + 3];          // (LSB)

    return value;
}


uint16_t read_addend16(size_t offset, unsigned char** sections_content, int section_index) {
    unsigned char* data = sections_content[section_index];

    // Vérification de la validité du pointeur
    if (data == NULL) {
        fprintf(stderr, "Erreur : données de section NULL pour l'indice %d\n", section_index);
        return 0;
    }

    // Lecture des 2 octets en big-endian
    uint16_t value = 0;
    value |= (data[offset] << 8);   
    value |= data[offset + 1];    

    return value;
}


uint8_t read_addend8(size_t offset, unsigned char** sections_content, int section_index) {
    unsigned char* data = sections_content[section_index];

    // Vérification de la validité du pointeur
    if (data == NULL) {
        fprintf(stderr, "Erreur : données de section NULL pour l'indice %d\n", section_index);
        return 0;
    }

    // Lecture d'un octet
    uint8_t value = data[offset];

    return value;
}

//*************************************fonctions_d'ecriture*************************************************//
//**********************************************************************************************************//


void write_32_bits(size_t offset, unsigned char** sections_content, int section_index, uint32_t value) {
    if (sections_content[section_index] == NULL) {
        fprintf(stderr, "Error: Section content at index %d is NULL.\n", section_index);
        return;
    }

    unsigned char* data = sections_content[section_index];

    data[offset] = (value >> 24) & 0xFF; // MSB
    data[offset + 1] = (value >> 16) & 0xFF;
    data[offset + 2] = (value >> 8) & 0xFF;
    data[offset + 3] = value & 0xFF;     // LSB
}       


void write_16_bits(size_t offset, unsigned char** sections_content, int section_index, uint16_t value){
    unsigned char* data = sections_content[section_index];
    data[offset] = (value >> 8) & 0xFF; // MSB
    data[offset + 1] = value & 0xFF;    // LSB
}


void write_8_bits(size_t offset, unsigned char** sections_content, int section_index, uint8_t value){
     if (sections_content[section_index] == NULL) {
        fprintf(stderr, "Error: Section content at index %d is NULL.\n", section_index);
        return;
    }
    sections_content[section_index][offset] = value;
}


//********************************************************************************************************//
//********************************************************************************************************//


// Fonction pour chercher la valeur d'un symbole dans la table des symboles
Elf32_Addr lookup_symbol(SymbolTable *symtab, int symbol_index) {
    // Vérifier si l'index du symbole est valide
    if (symbol_index < 0 || symbol_index >= symtab->size) {
        fprintf(stderr, "Erreur : index du symbole invalide.\n");
        return 0; 
    }

    // Récupérer l'entrée du symbole à l'index donné
    Elf32_Sym *symbol_entry = &symtab->symbols[symbol_index];

    // Retourner la valeur du symbole
    return symbol_entry->st_value;
}

//********************************************************************************************************//
//********************************************************************************************************//


void reimplantation_arm_abs(FILE * file , int section_index, Elf32_Rel *entry, unsigned char **sections_content,
 SymbolTable *symtab){
   
    // Offset de la donée dans la section 
    Elf32_Addr offset = entry->r_offset;
    fprintf(file, "Offset: 0x%08X\n", offset);
    
    // Type de réimplantation + index du symbole
    int relocation_type = ELF32_R_TYPE(entry->r_info);
    int symbol_index    = ELF32_R_SYM(entry->r_info);

    // Valeur du symbole
    Elf32_Addr symbol_value = lookup_symbol(symtab, symbol_index);
    fprintf( file, "Valeur de symbol : 0x%08X\n", symbol_value);
    
    // Appliquer la réimplantation
    switch (relocation_type) {
        case R_ARM_ABS32:
        {
            // (S + A) | T 
            uint32_t addend = read_addend32(offset, sections_content, section_index);
            fprintf(file, "Addend récupéré (32 bits): 0x%08X\n", addend);

            // Vérifie le bit Thumb si c'est une fonction Thumb
            Elf32_Sym *symbol = &symtab->symbols[symbol_index];
            Elf32_Addr target_value = symbol_value + addend;
            if (is_thumb_instruction(symbol)) {
                target_value |= 1; // bit T
            }

            write_32_bits(offset, sections_content, section_index, (uint32_t)target_value);
            break;
        }

        case R_ARM_ABS16:
        {
            // (S + A)
            uint16_t addend = read_addend16(offset, sections_content, section_index);
            fprintf(file, "Addend récupéré (16 bits): 0x%08X\n", addend);

            Elf32_Addr target_value = symbol_value + addend;

            // Écrit seulement 16 bits
            write_16_bits(offset, sections_content, section_index, (uint16_t)target_value);
            break;
        }

        case R_ARM_ABS8:
        {
            // (S + A)
            uint8_t addend = read_addend8(offset, sections_content, section_index);
            fprintf(file, "Addend récupéré (8 bits): 0x%08X\n", addend);

            Elf32_Addr target_value = symbol_value + addend;

            // Écrit seulement 8 bits
            write_8_bits(offset, sections_content, section_index, (uint8_t)target_value);
            break;
        }

        default:
          break;
    }

}

//********************************************************************************************************//
//********************************************************************************************************//


int find_ancien_index_section(nom_indice_section_table table , int nouveau_indice){
    for (int i = 0; i < table.size; i++) {
        if (table.table[i].nvindice == nouveau_indice) {
            return table.table[i].ancienindice;
        }
    }
    return -1;
}

//********************************************************************************************************//
//********************************************************************************************************//

// Fonction pour écrire le contenu d'une section ELF dans le fichier "etape8.txt"
void display_to_file_section_content(FILE* file, Elf32_Ehdr en_tete, Elf32_Shdr** elf_section, int section_index, unsigned char* section_data,
const char *section_name) {
   
    // Buffer pour stocker les caractères ASCII
    char ascii_buffer[17];
    memset(ascii_buffer, 0, sizeof(ascii_buffer));

    Elf32_Shdr* section = elf_section[section_index];
    if (section->sh_size < 1 || (section->sh_type == SHT_NOBITS)) {  
        fprintf(file, "Section '%s' n'a pas de données à afficher.\n", section_name); 
    } else {
        fprintf(file, "Dump hexadécimal de la section '%s':\n", section_name);
        
        // Vérifier si cette section a des relocations associées
        int has_relocations = 0;
        for (int i = 0; i < en_tete.e_shnum; i++) {
            if ((elf_section[i]->sh_type == SHT_REL || elf_section[i]->sh_type == SHT_RELA) &&
                ((int)elf_section[i]->sh_info == section_index)) {
                has_relocations = 1;
                break;
            }
        }

        // Si c'est une section de relocalisation
        if (has_relocations) {
            fprintf(file, " NOTE: Cette section a des relocations associées, mais elles n'ont PAS été appliquées à ce dump.\n");
        }
    }

    if (!(elf_section[section_index]->sh_type == SHT_NOBITS)) {  
        for (size_t i = 0; i < section->sh_size; i++) {
            // Début de ligne : adresse
            if (i % 16 == 0) {
                if (i != 0) {
                    // Fin de la ligne précédente
                    fprintf(file, " %s\n", ascii_buffer);
                }
                fprintf(file, " 0x%08lx ", (unsigned long)i);
                memset(ascii_buffer, 0, sizeof(ascii_buffer));
            }

            // Imprimer un espace avant chaque groupe de 4 octets, sauf avant le premier groupe de la ligne
            if ((i % 16) % 4 == 0 && (i % 16) != 0) {
                fprintf(file, " ");
            }

            // Imprimer l'octet en hexadécimal
            fprintf(file, "%02x", section_data[i]);

            // Stocker le caractère ASCII ou '.' dans le buffer
            ascii_buffer[i % 16] = (section_data[i] >= 32 && section_data[i] <= 126) ? section_data[i] : '.';
        }

        // Gestion de la dernière ligne (si incomplète)
        size_t remaining_bytes = section->sh_size % 16;
        if (remaining_bytes == 0 && section->sh_size > 0) {
            // Ligne complète, juste un espace avant ASCII
            fprintf(file, " %s\n", ascii_buffer);
        } else if (remaining_bytes != 0) {
            // Ligne incomplète : ajouter des espaces pour l'alignement
            size_t padding = (16 - remaining_bytes) * 2 + (15 - remaining_bytes) / 4;
            for (size_t j = 0; j < padding; j++) {
                fprintf(file, " ");
            }
            fprintf(file, " %s\n", ascii_buffer);
        } 
    }
    fprintf(file, "\n");
  
}

//********************************************************************************************************//
//********************************************************************************************************//

void apply_relocations(FILE* file , SectionHeaderTable *rel_sections_table ,Elf32_Shdr** non_rel_sections , Elf32_Ehdr en_tete,
 SymbolTable *symtab, unsigned char ** sections_content , nom_indice_section_table *table){

    //pour chaque section de relocation
    Elf32_Ehdr tmp_header = read_header(file);
    Elf32_Shdr* tmp_sections = read_sections_table(file, tmp_header);
    
   
    // Ouvrir le fichier "etape8.txt" en mode écriture (écrase le contenu existant)
    FILE *file_etape8 = fopen("etape8.txt", "w");
    if (file_etape8 == NULL) {
        perror("Erreur lors de l'ouverture de etape8.txt");
        return;
    }

    // Ouvrir le fichier "etape8.txt" en mode écriture (écrase le contenu existant)
    FILE *file_etape9 = fopen("etape9.txt", "w");
    if (file_etape9 == NULL) {
        perror("Erreur lors de l'ouverture de etape8.txt");
        return;
    }

    for(int i=0; i< rel_sections_table->size; i++){

        Elf32_Shdr *section = &rel_sections_table->headers[i];
        int num_entries = (section->sh_entsize > 0)  ? (section->sh_size / section->sh_entsize) : 0;
        
        //lecture des entries de la section
        Elf32_Rel *rel_entries=read_relocation_entries(file, section , num_entries , section->sh_size);

        //Trouver l'indice de la section  
        int section_index = find_section_index(non_rel_sections, en_tete.e_shnum, 
        rel_sections_table->shstrtab, section);
   
        if (section_index < 0) {
            perror("Erreur : la section n'a pas été trouvée dans non_rel_sections") ;
            exit(EXIT_FAILURE);
        }

        //appliquer chaque entrie et afficher le changement
        for (int j = 0; j < num_entries; j++) {

            Elf32_Rel *entry = &rel_entries[j];
            int type = ELF32_R_TYPE(entry->r_info);

            if(type == R_ARM_ABS32 || type==R_ARM_ABS16 || type == R_ARM_ABS8){

                const char* section_name = extract_target_section_name(& rel_sections_table->shstrtab[section->sh_name]);
                if (sections_content[section_index] != NULL) {
                    fprintf(file_etape8, "%s\n", "Contenu de section avant:");
                    display_to_file_section_content(file_etape8,  en_tete, non_rel_sections, section_index, sections_content[section_index],section_name );
                }

                reimplantation_arm_abs(file_etape8, section_index, entry, sections_content , symtab);
                
                if (sections_content[section_index] != NULL) {
                    fprintf(file_etape8, "%s\n","\nContenu de section après:");
                    display_to_file_section_content(file_etape8, en_tete, non_rel_sections, section_index, sections_content[section_index],section_name );                
                }
            }

            else if (type == R_ARM_JUMP24 || type == R_ARM_CALL || type == R_ARM_PC24){

                const char* section_name = extract_target_section_name(& rel_sections_table->shstrtab[section->sh_name]);

                //int index find_ancien_index_section(table , section_name);
                //nom_indice_section_table table;
                
                int symbol_index    = ELF32_R_SYM(entry->r_info);
                int ancien_indice = find_ancien_index_section(*table , symtab->symbols[symbol_index].st_shndx);
                
                //SI le type de la section est une fonction ou si la section realocable est differente de la section ou se trouve le symbole
                if (strcmp(section_name, &rel_sections_table->shstrtab[tmp_sections[ancien_indice].sh_name]) || is_thumb_instruction(&symtab->symbols[symbol_index])){ 
                    if (sections_content[section_index] != NULL) {
                        fprintf(file_etape9,  "%s\n", "Contenu de section avant :");
                        display_to_file_section_content(file_etape9, en_tete, non_rel_sections, section_index, sections_content[section_index],section_name );                
                    }

                    reimplantation_Call_Jump(file_etape9, section_index, entry, sections_content , symtab, (char *) section_name);

                   
                    if (sections_content[section_index] != NULL) {
                        fprintf(file_etape9,  "%s\n","\nContenu de section après:");
                        display_to_file_section_content(file_etape9, en_tete, non_rel_sections, section_index, sections_content[section_index],section_name );                
                    }
                }
                
            }
   
        }
    }

    // Fermer le fichier
    fclose(file_etape8);
    fclose(file_etape9);
    printf("Les réimplantations ont été appliquées avec succès.\n");
}

