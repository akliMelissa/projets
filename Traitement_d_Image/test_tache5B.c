#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "image.h"
#include "robot.h"
#include "sequence.h"
#include "files_generation.h"



int main(int argc, char* argv[]) {

    if (argc < 2) {
        printf("Erreur: format ./tests_tache5_2 <fichier PBM>\n");
        exit(1);
    }

    // Lecture de l'image au format PBM
    Image I = lire_fichier_image(argv[1]);

     
    // Liste des contours
    sequences_list contours;

    // Initialisation de la liste des contours
    initialiser_sequences_list(&contours);

    // Extraction des contours
    extrait__contours(I, &contours);

    unsigned long long summ_points = 0, summ_segments = 0;
    display_sequences_list(contours, &summ_points, &summ_segments);

    printf("Le nombre total de points est : %llu\n", summ_points);
    printf("Le nombre total de segments est : %llu\n", summ_segments);

    // Création du fichier de contours
    create_contours_file(argv[1], contours);

    int L = largeur_image(I);
    int H = hauteur_image(I);

    // Mode tracé (stroke mode)
    create_eps_file_contours(argv[1], H, L, contours, 'S');

    // Mode remplissage (fill mode)
    create_eps_file_contours(argv[1], H, L, contours, 'F');

    // Suppression de la liste des contours
    destroy_sequences_list(&contours);
    
    // Suppression de l'image
    supprimer_image(&I);

    return 0;
}