#ifndef AFFICHAGE_ENTETE_H
#define AFFICHAGE_ENTETE_H
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <elf.h>

// Renvoie la classe ELF (32 ou 64 bits) en fonction de la valeur 'classe'
const char* get_elf_class(unsigned char classe);

// Renvoie le type du fichier ELF (ex: exécutable, objet, etc.) en fonction de 'e_type'
const char* get_elf_type(Elf32_Half e_type);

// Renvoie l'endianness (ordre des octets) du fichier ELF en fonction de 'data'
const char* get_elf_endianness(unsigned char data);

// Renvoie la version ELF en fonction de 'data'
const char* get_elf_version(unsigned char data);

// Renvoie le nom de la machine cible en fonction de 'machine'
const char* get_elf_machine(uint16_t machine);

int is_system_in_little_endian(); //Pour le boutisme de systeme utilisé: 1 si little endian , 0 sinon
void swap_bytes(void *data, size_t size); // converter les octets 
void convert_endian_header(Elf32_Ehdr* en_tete) ; //converter le boutisme au big endian

// Lit l'en-tête ELF depuis le fichier et le retourne
Elf32_Ehdr read_header(FILE* file);

// Affiche l'en-tête ELF
void display_header(Elf32_Ehdr en_tete);


#endif