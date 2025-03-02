#include <stdlib.h>
#include <stdio.h>
#include "image.h"



int main(int argc, char* argv[]) {
    if (argc < 2) {
        printf("ERROR: provide PBM file\n");
        exit(1);
    }

//reding the image variable from a pbm file
Image I=lire_fichier_image(argv[1]);
ecrire_image(I);

//calculating the Inverse image 
Image I_inverse=negatif_image(I);
ecrire_image(I_inverse);

//delete the image variables I and I_inverse
supprimer_image(&I);
supprimer_image(&I_inverse);
return 0; 

}