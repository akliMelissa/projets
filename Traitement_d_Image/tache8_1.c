#include <stdlib.h>
#include <stdio.h>
#include<string.h>

#include "image.h"
#include"robot.h"
#include"sequence.h"
#include"Bezier.h"
#include "files_generation.h"



int main(int argc, char* argv[]) {

    if (argc < 3) {
        printf("Erreur: format ./tache8_1 <PBM fichier> <d>\n");
        exit(1);
    }

    //la distance de simplification 
    double d=strtod(argv[2], NULL);

    //lecteur image au format.pbm
    Image I=lire_fichier_image(argv[1]);

    //affiche d'image sur l'ecran
    //ecrire_image(I);
     
    //liste des contours
    sequences_list contours;

    //inilialiser contours liste 
    initialiser_sequences_list(&contours);

    //extraire les contours
    extrait__contours(I, &contours);

    //affichage des contours sur l'ecran 
    unsigned long long  summ_points=0;
    unsigned long long  summ_segments_init =0;
    display_sequences_list(contours , &summ_points , & summ_segments_init);
    create_contours_file(argv[1], contours);


    //creation et initialization des liste simplifees: par segment 
    sequences_list simplified_contours ;
    initialiser_sequences_list(&simplified_contours); 

    //simplification segment , bezier2 et bezier3 
    segment_simplification(contours, d , &simplified_contours);
  
    //affichage de liste simplifee 
    unsigned long long summ_points2=0;
    unsigned long long summ_segments_simple =0;
    display_sequences_list(simplified_contours , &summ_points2 , &summ_segments_simple);


    //affichage sur l'ecran   
    printf("\nLe nombre de contours : %llu \n", contours.list_size);
    printf("Le nombre total de segments des contours de l’image initiale : %llu\n", summ_segments_init );
    printf("simplification avec d=%0.2f : \n ",d);
    printf("Le nombre total de segments des contours simplifées: %llu\n", summ_segments_simple);

   
    //suppression des listes : contour et contours simplifees
    destroy_sequences_list(&contours);
    destroy_sequences_list(&simplified_contours);
    
    //delete the image variables I and I_inverse
    supprimer_image(&I);

    return 0; 

}