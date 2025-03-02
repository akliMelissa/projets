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
        printf("Erreur: format ./tache8_2 <PBM fichier> <d>\n");
        exit(1);
    }

    //la distance de simplification 
    double d=strtod(argv[2], NULL);

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
    unsigned long long  summ_points=0;
    unsigned long long  summ_segments_init =0;
    display_sequences_list(contours , &summ_points , & summ_segments_init);
    create_contours_file(argv[1], contours);


    //creation et initialization des liste simplifees: par segment , bezier2 et bezier3
    sequences_list simplified_contours ;
    initialiser_sequences_list(&simplified_contours); 
    sequences_listB2* simplified_contours_b2=create_sequences_list_b2();
    sequences_listB3* simplified_contours_b3=create_sequences_list_b3();


    //simplification segment , bezier2 et bezier3 
    segment_simplification(contours, d , &simplified_contours);
    simplification_bezier2(contours, d , simplified_contours_b2);
    simplification_bezier3(contours, d , simplified_contours_b3);
  
    //affichage de liste simplifees
    unsigned long long summ_points2=0;
    unsigned long long summ_segments_simple =0;
    unsigned long long int summ_bezier2=0;
    unsigned long long int summ_bezier3=0;
    
    display_sequences_list(simplified_contours , &summ_points2 , &summ_segments_simple);
    display_sequences_list_b2(*simplified_contours_b2,  &summ_bezier2);
    display_sequences_list_b3(*simplified_contours_b3,  &summ_bezier3);


    //affichage sur l'ecran   
    printf("\nLe nombre de contours : %llu \n", contours.list_size);
    printf("Le nombre total de segments des contours de l’image initiale : %llu\n", summ_segments_init );
    printf("simplification avec d=%0.2f : \n ",d);
    printf("Le nombre total de segments des contours simplifées: %llu\n", summ_segments_simple);
    printf("Le nombre total de courbes Bézier 2 après simplification: %llu\n", summ_bezier2);
    printf("Le nombre total de courbes Bézier 3 après simplification: %llu\n", summ_bezier3);


    int L=largeur_image(I);
    int H=hauteur_image(I);
    
    //creation de ficher eps mode remplissage 
    create_eps_file(argv[1], H, L, simplified_contours, 'F', d);
    
    //creation de ficher eps mode remplissage 
    create_eps_bezier3(argv[1], H, L, *simplified_contours_b3, 'F', d);
    create_eps_bezier2(argv[1], H, L, *simplified_contours_b2, 'F', d);
    
    //delete the image variables I and I_inverse
    supprimer_image(&I);

     //suppression des listes : contour et contours simplifees
    destroy_sequences_list(&contours);
    destroy_sequences_list(&simplified_contours);
    destroy_sequences_list_b2(simplified_contours_b2);
    destroy_sequences_list_b3(simplified_contours_b3);
  

    return 0; 

}