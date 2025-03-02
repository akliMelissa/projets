#include <stdio.h>
#include "test_sections.h"

int main(int argc, char *argv[]) {
    if (argc != 2) {
        printf("Usage: %s <fichier_elf>\n", argv[0]);
        return 1;
    }

    FILE *input = fopen(argv[1], "rb");
    if (!input) {
        printf("Erreur: impossible d'ouvrir le fichier %s\n", argv[1]);
        return 1;
    }

    char output_name[256];
    snprintf(output_name, sizeof(output_name), "%s.modified", argv[1]);
    FILE *output = fopen(output_name, "wb");
    
    if (!output) {
        printf("Erreur: impossible de créer le fichier %s\n", output_name);
        fclose(input);
        return 1;
    }

    modification_elf(input, output);
    fclose(input);
    fclose(output);

    // Réouvrir pour les tests
    FILE *test_file = fopen(output_name, "rb");
    if (!test_file) {
        printf("Erreur: impossible d'ouvrir le fichier modifié pour les tests\n");
        return 1;
    }

    test_elf_modifications(test_file);
    fclose(test_file);
    
    printf("Tests de cohérence réussis!\n");
    return 0;
}