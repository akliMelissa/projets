#include <stdlib.h>
#include <stdio.h>
#include<string.h>

#include "image.h"
#include"robot.h"
#include"sequence.h"
#include"files_generation.h"




int main(int argc, char* argv[]) {

    if (argc < 2) {
        printf("Erreur: format ./test_tache5_1 <fichier PBM> \n");
        exit(1);
    }

    //lecteur image au format.pbm
    Image I=lire_fichier_image(argv[1]);

    //affiche d'image sur l'ecran
    ecrire_image(I);
     
    //liste des contours
    sequences_list contours;

    //inilialiser contours liste 
    initialiser_sequences_list(&contours);

    //extraire les contours
    extrait__contours(I, &contours);

    //affichage des contours sur l'ecran 
    //cette fonction affiche aussi le nombre total des contours , points , et segments
    unsigned long long summ_points=0, summ_segments=0;
    display_sequences_list(contours , &summ_points, &summ_segments );

    printf("Le nombre totale des points est : %llu\n", summ_points);
    printf("Le nombre totale des segments est : %llu\n", summ_segments);

    //creation de fichier pour sauvgarder les contours
    create_contours_file(argv[1], contours);


    //suppression de liste des contours 
    destroy_sequences_list(&contours);
    
    //suppression de l'image
    supprimer_image(&I);
    return 0; 

}