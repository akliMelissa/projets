#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "image.h"
#include "robot.h"
#include "sequence.h"
#include "files_generation.h"



int main(int argc, char* argv[]) {

    if (argc < 3) {
        printf("Erreur: format ./tests_tache6_2 <d> <fichier PBM>\n");
        exit(1);
    }

    // Distance de simplification
    double d = strtod(argv[1], NULL);

    // Lecture de l'image au format PBM
    Image I = lire_fichier_image(argv[2]);

    // Liste des contours
    sequences_list contours;

    // Initialisation de la liste des contours
    initialiser_sequences_list(&contours);

    // Extraction des contours
    extrait__contours(I, &contours);

    // Affichage des contours à l'écran
    unsigned long long int summ_points = 0;
    unsigned long long int summ_segments_init = 0;
    display_sequences_list(contours, &summ_points, &summ_segments_init);

    // Création du fichier de contours
    create_contours_file(argv[2], contours);

    // Liste des contours simplifiés
    sequences_list simplified_contours;
    initialiser_sequences_list(&simplified_contours);

    // Simplification des contours par segments
    segment_simplification(contours, d, &simplified_contours);

    // Affichage des contours simplifiés à l'écran
    unsigned long long int summ_points2 = 0;
    unsigned long long int summ_segments_simple = 0;
    display_sequences_list(simplified_contours, &summ_points2, &summ_segments_simple);

    int L = largeur_image(I);
    int H = hauteur_image(I);

    // Mode remplissage (fill mode)
    create_eps_file(argv[2], H, L, simplified_contours, 'F', d);

    // Affichage à l'écran
    printf("\nLe nombre de contours : %llu\n", contours.list_size);
    printf("Le nombre total de segments des contours de l'image initiale : %llu\n", summ_segments_init);
    printf("Le nombre total de segments des contours de la simplification avec d : %llu\n", summ_segments_simple);


    // Suppression de la liste des contours et simplified_contours
    destroy_sequences_list(&contours);
    destroy_sequences_list(&simplified_contours);

    // Suppression de l'image I
    supprimer_image(&I);

    return 0;
}