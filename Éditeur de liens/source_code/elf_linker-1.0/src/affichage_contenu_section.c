
#include "affichage_contenu_section.h"



int is_integer(const char *str) {
    int x;
    return sscanf(str, "%d", &x) == 1;
}

//*************************************************************************//
//*************************************************************************//


unsigned char * read_section_content(FILE* file , const char *section_name_or_number,  Elf32_Ehdr en_tete ,
    Elf32_Shdr* elf_section , char *section_names , int* section_index ){

    //Recherche de la section par nom ou numéro
    *section_index = -1;
        if ( is_integer(section_name_or_number)){
                for (int i = 0; i < en_tete.e_shnum; i++) {
                    if (atoi(section_name_or_number) == i){
                        *section_index = i;
                        break;
                    }
                }
        }else{
             for (int i = 0; i < en_tete.e_shnum; i++) {
                    const char *section_name = &section_names[elf_section[i].sh_name]; 
                    if (strcmp(section_name, section_name_or_number)==0){
                        *section_index = i;
                        break;
                    }
                }
        }

    // si la section n'est pas trouvé
    if (*section_index == -1) {
        fprintf(stderr, "readelf: Warning: Section '%s' was not dumped because it does not exist!\n", section_name_or_number);
        free(elf_section);
        free(section_names);
        exit(EXIT_FAILURE);
    }

    // Lire le contenu de la section
    Elf32_Shdr section = elf_section[*section_index];
    unsigned char *section_data = malloc(section.sh_size);
    if (!section_data) {
        perror("Erreur d'allocation mémoire pour les données de la section");
        free(elf_section);
        free(section_names);
        exit(EXIT_FAILURE);
    }

    if (fseek(file, section.sh_offset, SEEK_SET) != 0 ||
     fread(section_data, 1, section.sh_size, file) != section.sh_size) {
        perror("Erreur de lecture des données de la section");
        free(section_data);
        free(elf_section);
        free(section_names);
        exit(EXIT_FAILURE);
    }
    return section_data;
}

//*************************************************************************//
//*************************************************************************//


void display_section_content( Elf32_Ehdr en_tete,Elf32_Shdr* elf_section,  int section_index, 
unsigned char* section_data , const char *section_name  ) {

    // Afficher le contenu de la section en hexadécimal avec l'ASCII à la fin de chaque ligne
    char ascii_buffer[17];   //Pour stocker l'ASCII
    memset(ascii_buffer, 0, 17);  //Initialiser le buffer ASCII

    Elf32_Shdr section = elf_section[section_index];

    if (section.sh_size < 1 || (elf_section[section_index].sh_type == SHT_NOBITS)) {  
        printf("Section '%s' has no data to dump.\n", section_name); 

    } else{
        printf("\nHex dump of section '%s':\n", section_name);
        // Vérifier si cette section a des relocations associées
        int has_relocations = 0;
        for (int i = 0; i < en_tete.e_shnum; i++) {
            if ((elf_section[i].sh_type == SHT_REL || elf_section[i].sh_type == SHT_RELA) &&
                ((int)elf_section[i].sh_info == section_index)) {
                has_relocations = 1;
                break;
            }
        }
        // Si c'est une section de relocalisation
        if (has_relocations) {
            printf(" NOTE: This section has relocations against it, but these have NOT been applied to this dump.\n");
        }
    }

    if(!(elf_section[section_index].sh_type == SHT_NOBITS)){  
        for (size_t i = 0; i < section.sh_size; i++) {
            // Début de ligne : adresse
            if (i % 16 == 0) {
                if (i != 0) {
                    // Fin de la ligne précédente
                    printf(" %s\n", ascii_buffer);
                }
                printf("  0x%08lx ", (unsigned long) i);
                memset(ascii_buffer, 0, sizeof(ascii_buffer));
            }

            // Imprimer un espace avant chaque groupe de 4 octets, sauf avant le premier groupe de la ligne
            if ((i % 16) % 4 == 0 && (i % 16) != 0) {
                printf(" ");
            }

            // Imprimer l'octet en hexadécimal
            printf("%02x", section_data[i]);

            // Stocker le caractère ASCII ou '.' dans le buffer
            ascii_buffer[i % 16] = (section_data[i] >= 32 && section_data[i] <= 126) ? section_data[i] : '.';
        }

        // Gestion de la dernière ligne (si incomplète)
        size_t remaining_bytes = section.sh_size % 16;
        if (remaining_bytes == 0 && section.sh_size > 0) {
            // Ligne complète, juste un espace avant ASCII
            printf(" %s\n", ascii_buffer);
        } else if (remaining_bytes != 0) {
            // Ligne incomplète : ajouter des espaces pour l'alignement
            size_t padding = (16 - remaining_bytes) * 2 + (15 - remaining_bytes) / 4;
            for (size_t j = 0; j <= padding; j++) printf(" ");
            printf(" %s\n", ascii_buffer);
        } 
    }
    printf("\n");
   
}
