#include <stdio.h>
#include <stdlib.h>
#include <elf.h>
#include "affichage_entete.h"
#include "affichage_table_section.h"
#include "affichage_contenu_section.h"
#include "affichage_table_symbole.h"
#include "affichage_table_reimplantation.h"



int main(int argc, char *argv[]) {

    if(argc <3){
        printf("Erreur : Enter une operation et un fichier pour les traiter\n");
        printf("Fromat : ./Program <option> <nom_fichier> \n");
        printf("Ou avec l'option x : ./Program <option> <nom_fichier> <nb_section>\n ");
        printf("\nLes Options :\n");
        printf("    h: affichage de l'en_tete \n");
        printf("    S: affichage de la table des sections\n");
        printf("    s: affichage de la table des symboles\n");
        printf("    r: affichage des tables de realocation\n");
        printf("    x : affichage de contenu de section avec le nom  ou le numero nb_section\n");
        exit(1);
    }

    if(argv[1][0] =='x' && argc < 4 ){
        printf("Erreur de format, ca doit etre de la forme : ./Program x <nom_fichier> <nb_section>\n");
        printf("ou <nb_section> : le nom ou le numero de la section\n");
        exit(1);
    }
    

    FILE *file = fopen(argv[2], "rb");

    if (!file) {
        perror("Erreur d'ouverture du fichier");
        exit(EXIT_FAILURE);
    }
    
    char ch=argv[1][0];

    switch(ch){
        case 'h':{
            Elf32_Ehdr en_tete= read_header(file);
            display_header(en_tete);
            break;
        }

        case 'S':{
            Elf32_Ehdr en_tete = read_header(file);
            Elf32_Shdr* elf_section = read_sections_table(file, en_tete);
            char *section_names = read_string_table(file, en_tete, elf_section);
            display_section_table( en_tete , elf_section, section_names);
            free(elf_section);
            free(section_names);
            break;
         }

        case 'x':{
            Elf32_Ehdr en_tete=read_header(file);
            Elf32_Shdr* elf_section=read_sections_table(file , en_tete);
            char *section_names = read_string_table(file , en_tete , elf_section);
        
            int section_index=-1;
            unsigned char* section_data= read_section_content(file, argv[3] , en_tete ,elf_section 
            ,section_names , &section_index); //lecture de contenu de la section

            const char *section_name = &section_names[elf_section[section_index].sh_name];

            display_section_content(en_tete, elf_section, section_index,section_data , section_name );

            free(section_data);
            free(elf_section);
            free(section_names);
            break;
        }

        case 's':{
            Elf32_Ehdr en_tete = read_header(file);
            Elf32_Shdr *sections = read_sections_table(file , en_tete);

            char *shstrtab = read_string_table(file , en_tete , sections);
            SectionHeaderTable section_table = {sections, (char *)shstrtab, en_tete.e_shnum};

            SymbolTable symtab, dynsym;
            if (read_symbol_table(file, &en_tete, &section_table, &dynsym, ".dynsym") == 0) {
                display_symboles_table(&dynsym, &section_table);
                free_table_symbol(&dynsym);
            } 

            if (read_symbol_table(file, &en_tete, &section_table, &symtab, ".symtab") == 0) {
                display_symboles_table(&symtab, &section_table);
                free_table_symbol(&symtab);
            } else {
                printf(" ");
            }

            free(sections);
            free(shstrtab);
            break;
        }

        case 'r':{
            Elf32_Ehdr en_tete = read_header(file);
            Elf32_Shdr *sections = read_sections_table(file , en_tete);

            char *shstrtab = read_string_table(file , en_tete , sections);
            if (!shstrtab) {
                free(sections);
                fclose(file);
                exit(1);
            }

            
            SectionHeaderTable section_table = {sections, (char *)shstrtab, en_tete.e_shnum};
            SectionHeaderTable rel_sections_table= get_relocation_sections(&section_table);

            SymbolTable symtab = {0}, dynsymtab = {0};
            int symtab_index = -1, dynsym_index = -1;

        
            for (size_t i = 0; i < section_table.size; i++) {
                if (strcmp(&shstrtab[sections[i].sh_name], ".symtab") == 0) {
                    read_symbol_table(file, &en_tete, &section_table, &symtab, ".symtab");
                    symtab_index = i;
                } else if (strcmp(&shstrtab[sections[i].sh_name], ".dynsym") == 0) {
                    read_symbol_table(file, &en_tete, &section_table, &dynsymtab, ".dynsym");
                    dynsym_index = i;
                }
            }

            if ( section_table.size - rel_sections_table.size == section_table.size){
                printf("There are no relocations in this file.\n");
            }else{
                display_relocation_table(file, &rel_sections_table, 
                                        symtab_index != -1 ? &symtab : NULL,
                                        dynsym_index != -1 ? &dynsymtab : NULL,
                                        symtab_index, dynsym_index,
                                        &section_table);
            }

            free_table_symbol(&symtab);
            free_table_symbol(&dynsymtab);

            free(section_table.headers);
            free(rel_sections_table.headers);
            free(section_table.shstrtab);

            break;
        }
             
        default:
            printf("Erreur: option invalide\n");
            break;
    }

    //fermeture du fichier
    fclose(file);


    return 0;
}
