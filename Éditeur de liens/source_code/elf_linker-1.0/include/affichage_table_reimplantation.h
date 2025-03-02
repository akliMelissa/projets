#ifndef AFFICHAGE_TABLE_REIMPLANTATION_H
#define AFFICHAGE_TABLE_REIMPLANTATION_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "affichage_table_symbole.h"



void convert_endian_header_relocation_entry(Elf32_Rel *entry);

// Fonction pour obtenir le nom du type de réimplantation
const char* get_type_rel(unsigned int type);

// Fonction pour trouver le nom du symbole lié à un indice et une section
const char* find_symbol_name_with_shlink(SymbolTable *symtab, SymbolTable *dynsymtab, 
    int sym_idx, Elf32_Addr *sym_value, 
    Elf32_Shdr *section, 
    int symtab_index, int dynsym_index);


// Fonction pour inverser les octets d'une structure Elf32_Rel
void convert_endian_relocation_rel(Elf32_Rel *rela);


// Fonction pour obtenir le nom de la section d'un symbole donné
const char* obtenir_nom_section_pour_symbole(SymbolTable *symtab, SectionHeaderTable *section_table, 
    int sym_index);


// Fonction pour afficher les tables de réimplantation pour les sections SHT_REL
void display_relocation_table(FILE *file, SectionHeaderTable *section_table, 
    SymbolTable *symtab, SymbolTable *dynsymtab, 
    int symtab_index, int dynsym_index , SectionHeaderTable *entete);


SectionHeaderTable get_relocation_sections(SectionHeaderTable *section_table);


Elf32_Rel * read_relocation_entries( FILE* file, Elf32_Shdr *section , int num_entries , int size );


void convert_endian_rel_entries(Elf32_Rel* rel_entries , int num_entries);


#endif