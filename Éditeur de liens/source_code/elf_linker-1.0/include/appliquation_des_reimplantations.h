#ifndef APPLICATION_DES_REIMPLANTATIONS_H
#define APPLICATION_DES_REIMPLANTATIONS_H

#include "modification_elf.h"
#include "renumeration_des_sections.h"
#include "Call_Jump.h"


// Fonction pour extraire le nom de la section cible en supprimant le préfixe ".rel"
const char* extract_target_section_name(const char* section_name);

// Fonction pour trouver l'index d'une section dans un tableau de sections non-relocables
int find_section_index(Elf32_Shdr** non_rel_sections, int nb_sections, const char* shstrtab, Elf32_Shdr* section);

// Fonction pour vérifier si un symbole est une instruction Thumb
int is_thumb_instruction(Elf32_Sym *symbol);

// Fonction pour lire un addend de 32 bits à partir d'un offset donné dans le contenu des sections
uint32_t read_addend32(size_t offset, unsigned char** sections_content, int section_index);

// Fonction pour lire un addend de 16 bits à partir d'un offset donné dans le contenu des sections
uint16_t read_addend16(size_t offset, unsigned char** sections_content, int section_index);

// Fonction pour lire un addend de 8 bits à partir d'un offset donné dans le contenu des sections
uint8_t read_addend8(size_t offset, unsigned char** sections_content, int section_index);

// Fonction pour écrire une valeur de 32 bits à un offset donné dans le contenu des sections
void write_32_bits(size_t offset, unsigned char** sections_content, int section_index, uint32_t value);

// Fonction pour écrire une valeur de 16 bits à un offset donné dans le contenu des sections
void write_16_bits(size_t offset, unsigned char** sections_content, int section_index, uint16_t value);

// Fonction pour écrire une valeur de 8 bits à un offset donné dans le contenu des sections
void write_8_bits(size_t offset, unsigned char** sections_content, int section_index, uint8_t value);

// Fonction pour chercher la valeur d'un symbole dans la table des symboles
Elf32_Addr lookup_symbol(SymbolTable *symtab, int symbol_index);

// Fonction pour appliquer la réimplantation pour les instructions ARM de type absolu
void reimplantation_arm_abs(FILE * file , int section_index, Elf32_Rel *entry, unsigned char **sections_content, SymbolTable *symtab);

// Fonction pour appliquer toutes les réimplantations aux sections spécifiées
void apply_relocations(FILE* file, SectionHeaderTable *rel_sections_table, Elf32_Shdr** non_rel_sections, Elf32_Ehdr en_tete, SymbolTable *symtab,
 unsigned char ** sections_content, nom_indice_section_table* table );

int find_ancien_index_section(nom_indice_section_table table , int nouveau_indice);

void display_to_file_section_content(FILE* file, Elf32_Ehdr en_tete, Elf32_Shdr** elf_section, int section_index, unsigned char* section_data,
const char *section_name) ;

#endif // APPLICATION_DES_REIMPLANTATIONS_H