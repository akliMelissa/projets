#ifndef __affichage_contenu_section_H
#define __affichage_contenu_section_H


#include <stdio.h>
#include <stdlib.h>
#include <elf.h>
#include "affichage_entete.h"
#include "affichage_table_section.h"

// Lit le contenu d'une section depuis le fichier ELF et le retourne sous forme de tableau d'octets
unsigned char * read_section_content(FILE* file , const char *section_name_or_number,  Elf32_Ehdr en_tete ,
Elf32_Shdr* elf_section , char *section_names , int* section_index);


// Affiche le contenu d'une section, incluant les données et le nom de la section
void display_section_content( Elf32_Ehdr en_tete,Elf32_Shdr* elf_section,  int section_index, 
unsigned char* section_data , const char *section_name );


#endif