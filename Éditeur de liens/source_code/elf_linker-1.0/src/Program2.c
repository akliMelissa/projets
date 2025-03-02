#include <stdio.h>
#include <stdlib.h>
#include <elf.h>
#include "affichage_entete.h"
#include "affichage_table_section.h"
#include "affichage_contenu_section.h"
#include "affichage_table_symbole.h"
#include "affichage_table_reimplantation.h"

#include"modification_elf.h"
#include"appliquation_des_reimplantations.h"



int main(int argc, char* argv[]) {
    if (argc < 3) {
        fprintf(stderr, "Erreur : format incorrect.\n");
        fprintf(stderr, "Utilisation : ./Program2 <nom_fichier_entree> <nom_fichier_sortie>\n");
        exit(EXIT_FAILURE);
    }

   
    FILE* input = fopen(argv[1], "rb");
    if (!input) {
        perror("Erreur d'ouverture du fichier en lecture");
        exit(EXIT_FAILURE);
    }

    FILE* output = fopen(argv[2], "wb");
    if (!output) {
        perror("Erreur d'ouverture du fichier en écriture");
        fclose(input);
        exit(EXIT_FAILURE);
    }

    modification_elf(input, output);

    fclose(input);
    fclose(output);
   
    return 0;
}
