#ifndef __Call_Jump_H
#define __Call_Jump_H
#include "appliquation_des_reimplantations.h"

#define MAX_OFFSET_24 0x7FFFFF 
#define MIN_OFFSET_24 (-0x800000)

// Calcule un nouvel offset relatif après relocalisation, en utilisant la table des symboles
Elf32_Addr offset_relatif(FILE* file, SymbolTable *symtab, int symbol_index , Elf32_Addr offset );

// Écrit une instruction de type appel (CALL) à l'offset spécifié dans la section donnée
void write_Call(size_t offset ,unsigned char** sections_content, int section_index, Elf32_Addr Call_offset);

// Écrit une instruction de type saut (JUMP) à l'offset spécifié dans la section donnée
void write_Jump(size_t offset ,unsigned char** sections_content, int section_index, Elf32_Addr jump_offset);

// Gère la relocalisation des instructions d'appel ou de saut en fonction de l'entrée de relocalisation
// Met à jour l'instruction dans la section avec le nouvel offset calculé
void reimplantation_Call_Jump(FILE* file, int section_index, Elf32_Rel *entry, unsigned char **sections_content, SymbolTable *symtab, char * section_name);


#endif