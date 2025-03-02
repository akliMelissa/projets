#ifndef CORRECTION_DES_SYMBOLES_H
#define CORRECTION_DES_SYMBOLES_H

#include "affichage_table_reimplantation.h"


//******************************Structures*****************************************************************//

// Structure pour stocker les noms des sections et leurs indices    
typedef struct nom_indice_section {
    char *names;
    int ancienindice;
    int nvindice;
} nom_indice_section;

// Structure pour stocker les noms des sections et leurs indices    
typedef struct nom_indice_section_table {
    int size;
    nom_indice_section *table;
} nom_indice_section_table;

// Structure pour contenir une table des symboles corrigée et une table des indices des sections
typedef struct Symtab_nom_section {
    SymbolTable *symtab;
    nom_indice_section_table *table;
} Symtab_nom_section;

//*****************************************************************************************************************//


// Fonction pour créer une table de correspondance entre les anciens et les nouveaux indices des sections
nom_indice_section_table* create_nom_indice_section_table(
    Elf32_Shdr** new_sections, 
    Elf32_Ehdr en_tete, 
    char* section_names, 
    Elf32_Shdr* elf_section, 
    int number_of_sections
);

// Fonction pour corriger les symboles en fonction des nouveaux indices des sections
SymbolTable* corriger_symbole(SymbolTable *table_symbole, SectionHeaderTable *entete , nom_indice_section_table *table);

#endif // CORRECTION_DES_SYMBOLES_H
