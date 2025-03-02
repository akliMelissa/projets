#include <stdlib.h>
#include <stdio.h>
#include<string.h>

#include "image.h"
#include"robot.h"
#include"sequence.h"
#include"Bezier.h"
#include"files_generation.h"


int main(int argc, char* argv[]) {

/*
Point P1=set_point(-1 , 2 );
Point P2=set_point(4 , 2);
Point P3=set_point(70 , 15);
Point P4=set_point(4 , 0);
Point P5=set_point(3 , -8);

Point P6=set_point(0, 0 );
Point P7=set_point(1, 0);
Point P8=set_point(1 , 1);
Point P9=set_point(1, 2);
Point P10=set_point(2, 2);
Point P11=set_point(3, 2 );
Point P12=set_point(3, 3);
Point P13=set_point(4, 3);
Point P14=set_point(5, 3);


Point cont1[2];
assign_points(P1 , &cont1[0]);
assign_points(P2 , &cont1[1]);

bezier3 B; 
approx_bezier3(cont1 , 0 , 1, &B);
display_bezier3(B , 1);

// 3 points, n=2
Point cont2[5]; 
assign_points(P1 , &cont2[0]);
assign_points(P2 , &cont2[1]);
assign_points(P3 , &cont2[2]);

bezier3 B2; 
approx_bezier3(cont2 , 0 , 2, &B2);
display_bezier3(B2 , 2);

//5 points , n=4
Point cont3[5];
assign_points(P1 , &cont3[0]);
assign_points(P2 , &cont3[1]);
assign_points(P3 , &cont3[2]);
assign_points(P4 , &cont3[3]);
assign_points(P5 , &cont3[4]);

bezier3 B3; 
approx_bezier3(cont3 , 0 , 4, &B3);
display_bezier3(B3 , 3);

//9 points , n=8
Point cont4[9];
assign_points(P6 , &cont4[0]);
assign_points(P7 , &cont4[1]);
assign_points(P8 , &cont4[2]);
assign_points(P9 , &cont4[3]);
assign_points(P10 , &cont4[4]);
assign_points(P11 , &cont4[5]);
assign_points(P12 , &cont4[6]);
assign_points(P13 , &cont4[7]);
assign_points(P14, &cont4[8]);

bezier3 B4; 
approx_bezier3(cont4 , 0 , 8, &B4);
display_bezier3(B4, 4);

    Point P17=set_point(0 , 0);
    Point P18=set_point(1 , 3);
    Point P19= set_point(2,4);
    Point P20=set_point(3,3);
    Point P21=set_point(4,0);

    Point cont5[5];
    assign_points(P17 , &cont5[0]);
    assign_points(P18, &cont5[1]);
    assign_points(P19, &cont5[2]);
    assign_points(P20, &cont5[3]);
    assign_points(P21, &cont5[4]);

    bezier3 B5; 
    approx_bezier3(cont5 , 0 , 4, &B5);
    display_bezier3(B5 , 1);


*/
 
   if (argc < 3) {
        printf("Erreur: format  <d> <PBM fichier> \n");
        exit(1);
    }

    //la distance de simplification 
    double d=strtod(argv[1], NULL);

    //lecteur image au format.pbm
    Image I=lire_fichier_image(argv[2]);

     
    //liste des contours
    sequences_list contours;

    //inilialiser contours liste 
    initialiser_sequences_list(&contours);

    //extraire les contours
    extrait__contours(I, &contours);
    
    //creation de fichier contour_image
    create_contours_file(argv[2], contours);
   
    //creation et initialization de liste simplifee par bezier2 courbes  
    sequences_listB3* simplified_contours=create_sequences_list_b3(); 

    //simplification bezier2
    simplification_bezier3(contours , d, simplified_contours);

    //affichage de liste simplifee 
    unsigned long long summ_bezier3=0;
    display_sequences_list_b3(*simplified_contours , &summ_bezier3);
    printf("Le nombre total de courbes Bézier 3 après simplification: %llu\n", summ_bezier3);
    

    int L=largeur_image(I);
    int H=hauteur_image(I);
    
    //creation de ficher eps mode remplissage 
    create_eps_bezier3(argv[2], H, L, *simplified_contours, 'F', d);

    //suppression des listes des contours , et contour simplifee
    destroy_sequences_list(&contours);
    destroy_sequences_list_b3(simplified_contours);

    //delete the image variables I 
    supprimer_image(&I);

    return 0; 

}

