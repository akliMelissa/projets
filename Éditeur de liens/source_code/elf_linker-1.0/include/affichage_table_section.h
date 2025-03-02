#ifndef affichage_table_section_H
#define affichage_table_section_H

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <elf.h> 

// Renvoie le type de la section en fonction de son type sh_type
const char *get_section_type(Elf32_Word sh_type);

// Convertit l'endianess de l'en-tête de section
void convert_endian_section_header(Elf32_Shdr *section);

// Lit la table des sections depuis le fichier ELF et renvoie un tableau de Elf32_Shdr
Elf32_Shdr* read_sections_table(FILE *F , Elf32_Ehdr en_tete);

// Lit la table des chaînes de caractères (string table) depuis le fichier ELF
char* read_string_table( FILE *F , Elf32_Ehdr en_tete ,  Elf32_Shdr* elf_section);

// Obtient les flags d'une section et les copie dans la chaîne de caractères 'flags'
void get_flags(Elf32_Word sh_flags, char *flags);

// Affiche la table des sections, incluant les noms et les informations des sections
void display_section_table(Elf32_Ehdr en_tete , Elf32_Shdr* elf_section , char* section_names);

#endif