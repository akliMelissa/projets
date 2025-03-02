#ifndef EXECUTABLE_H
#define EXECUTABLE_H

#include  "affichage_table_reimplantation.h"
#include "affichage_table_symbole.h"
#include "affichage_contenu_section.h"
#include "affichage_entete.h"
#include "affichage_table_section.h"
#include "modification_elf.h"
#include <stdbool.h>


// Structure pour stocker les sections allouables et leur taille totale dans un fichier ELF.
typedef struct {
    Elf32_Shdr *section;
    Elf32_Word size;
} Executable;



// Fonction pour extraire les sections allouables du fichier ELF et calculer leur taille totale.
void section_alocabl(Elf32_Ehdr header, Elf32_Shdr *section_table);

// Fonction pour récupérer l'adresse de la section `.text` dans le fichier ELF.
Elf32_Addr get_adress_text(Elf32_Ehdr header, Elf32_Shdr *section_table) ;

// Fonction pour créer des segments de programme à partir du fichier ELF.
Elf32_Phdr *cree_segement(FILE *fichier, Elf32_Ehdr *header, Elf32_Addr base_address,char *section_names) ;
// Fonction pour convertir l'endianness des segments de programme.
void convert_endian_segment(Elf32_Phdr* segment) ;

// Fonction pour écrire les segments de programme dans un fichier ELF.
void write_segment(FILE *fichier, Elf32_Phdr *segments, int num_segments, Elf32_Off phoff) ;

// Fonction pour appliquer des modifications à un fichier ELF et écrire un fichier ELF modifié.
void modification(FILE* input, FILE* output, Elf32_Addr base_address);

// Fonction principale pour gérer les arguments de la ligne de commande et contrôler l'exécution.
int main(int argc, char* argv[]) ;

#endif