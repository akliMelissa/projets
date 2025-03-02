#include <stdlib.h>
#include <stdio.h>
#include "image.h"
#include "robot.h"
#include "sequence.h"
#include "files_generation.h"

int main(int argc, char* argv[]) {

    
    if (argc < 2) {
        printf("ERREUR : Veuillez fournir un fichier PBM\n");
        exit(1);
    }

    // Lecture de la variable image à partir d'un fichier PBM
    Image I = lire_fichier_image(argv[1]);
    ecrire_image(I);

    // Création et initialisation de la séquence de contours
    sequence seq;
    init_sequence(&seq);

    // Extraction du contour
    extrait_un_contour(I, &seq);

    // Création du fichier pour sauvgarder des contours de l'image
    FILE* file;

    // Nom du fichier de résultat
    char file_name[100];
    create_file_name(argv[1] , file_name);
    int contours_number = 1;

    // Ouverture du fichier de résultats des contours de l'image
    file = fopen(file_name, "a");

    // Écriture des résultats
    if (file != NULL) {
        // Écriture du nombre de contours
        fprintf(file, "%d\n\n", contours_number);

        // Écriture des contours
        add_contour_to_file(file, &seq);

        // Fermeture du fichier
        fclose(file);
    } else {
        printf("Erreur lors de l'ouverture du fichier de résultats de l'image\n");
    }

    // Création du fichier de résultats
    create_result_file(argv[1], I.la_hauteur_de_l_image, I.la_largeur_de_l_image, seq.size - 1);

    // Suppression de la séquence de contours
    destroy_sequence(&seq);

    // Suppression des variables image I et I_inverse
    supprimer_image(&I);

    return 0;
}