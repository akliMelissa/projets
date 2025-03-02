#ifndef affichage_table_symbole_H
#define affichage_table_symbole_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <elf.h>
#include "affichage_contenu_section.h"


/* Codes de retour */
#define SUCCESS 0
#define ERROR_FORMAT -1
#define ERROR_ALLOC -2


//************************structure*******************************************************//
//****************************************************************************************//

/* Structure pour la table des symboles */
typedef struct {
    Elf32_Sym *symbols;   /* Pointeur vers les symboles ELF */
    char *strtab;         /* Pointeur vers la table des chaînes */
    size_t size;          /* Taille de la table des symboles */
    char *name;           /* Nom de la section */
} SymbolTable;

/* Structure pour la table des en-têtes de section */
typedef struct {
    Elf32_Shdr *headers;  /* Pointeur vers les en-têtes de section */
    char *shstrtab;       /* Pointeur vers la table des chaînes des sections */
    size_t size;          /* Nombre d'en-têtes de section */
} SectionHeaderTable;


//*********************************************************************************************//

// Lit la table des symboles depuis un fichier ELF et la stocke dans la structure symtab
int read_symbol_table(FILE *file, Elf32_Ehdr *elf_header, SectionHeaderTable *sections, SymbolTable *symtab, const char *section_name);

// Affiche les symboles de la table
void display_symboles_table(const SymbolTable *symtab, const SectionHeaderTable *sections);

// Libère la mémoire allouée pour la table des symboles
void free_table_symbol(SymbolTable *symtab);

// Convertit l'endianess d'un symbole
void convert_endian_symbol(Elf32_Sym *symbol);

// Renvoie le type d'un symbole en fonction de ses informations
const char *get_symbol_type(unsigned char st_info);

// Renvoie le type de liaison d'un symbole (ex: local, global)
const char *get_symbol_bind(unsigned char st_info);

// Renvoie la visibilité d'un symbole
const char *get_symbol_visibility(unsigned char st_other);

// Renvoie le nom de la section basé sur son index
const char *get_section_index_name(uint16_t shndx);


#endif /* SYMBOL_H */
