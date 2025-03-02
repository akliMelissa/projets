#ifndef APPLICATION_DES_REIMPLANTATIONS_H
#define APPLICATION_DES_REIMPLANTATIONS_H

#include <getopt.h>
#include <stdio.h>
#include <stdlib.h>
#include "debug.h"
#include "arm_simulator_interface.h"

#include "../include/affichage_entete.h"
#include "../include/affichage_table_section.h"
#include "../include/affichage_contenu_section.h"


#define Program_Counter 15 

//Chargement du code ELF dans un simulateur ARM et exécution du programme
void code_run(char *hostname, char *servicename , char* file_name);

//Affichage du mode d'emploi de l'application
void usage(char *name);

//main
int main(int argc, char *argv[]) ;

#endif