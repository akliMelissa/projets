#ifndef _MODIFICATION_ELF_H
#define _MODIFICATION_ELF_H

#include "renumeration_des_sections.h"
#include "appliquation_des_reimplantations.h" 
#include "Correction_des_symboles.h"
#include "affichage_table_reimplantation.h"


typedef struct {
    nom_indice_section_table *table;
    char *shstrtab;
}
indice_section_string_table;



//pour la lecture de contenu des sections sauf celles de type REL
unsigned char** read_non_rel_sections_content(FILE* input, Elf32_Shdr* sections, char* shstrtab, Elf32_Ehdr en_tete);


/*Liberation des sections*/
void free_non_rel_sections_content(Elf32_Ehdr en_tete, unsigned char **sections_content);


//********************ecriture dans le fichier output*********************************************************//

/*Ecriture de l'en_tete dans le fichier*/
void write_header(FILE* file,  Elf32_Ehdr* en_tete);

/*Ecriture de la table des sections dans le fichier*/
void write_sections_table(FILE* output, Elf32_Ehdr en_tete, Elf32_Shdr** non_rel_sections);

/*Ecriture de la table des chaines ".shstrtab" dans le fichier*/
void write_string_table(FILE *output, Elf32_Ehdr en_tete, Elf32_Shdr** non_rel_sections, char* section_names);

/*Ecriture du contenu des sections dans le fichier*/
void write_sections_content(FILE* output,  Elf32_Ehdr en_tete, Elf32_Shdr** non_rel_sections , unsigned char** sections_content );

/*Ecriture de la nouvelle table des symbole ".symtab" dans le fichier*/
void write_symbol_table(FILE* output, SymbolTable* table_symbole, Elf32_Off offset);


//***************************************************************************************************************//


/*Fonction de toute la procedure*/ 
void modification_elf(FILE* input, FILE* output);



#endif 
