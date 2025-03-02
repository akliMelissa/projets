
#include "files_generation.h"


// Fonction pour mémoriser un contour de type séquence de points dans un fichier
void add_contour_to_file(FILE* file , sequence* seq){
      
    //ecriture de nombre des points de contour
    fprintf(file, "%llu\n",size(*seq) );

    noad* ptr=seq->head;
    while(ptr!=NULL){
        fprintf(file, "%.1f  %.1f\n",ptr->p.a , ptr->p.b);
        ptr=ptr->next;
    }
}

// Pour générer un nom de fichier avec le format "contours/nom_d'image_contour.txt"
void create_file_name(char* image_name, char* file_name_) {

    char* direction = "contours/";
    char* extension = ".pbm";
    char* new_exten = "_contour.txt";

    // trouver la position de /
    char* slash_pos = strrchr(image_name, '/');

    // si / est trouver
    if (slash_pos != NULL) {

        // ajouter le fichier de direction contours/
        strncpy(file_name_, direction, 100);
        strncat(file_name_, slash_pos + 1, 100 - strlen(direction) - 1);
    } else {
        //si y'a pas de /
        strncpy(file_name_, direction, 100);
        strncat(file_name_, image_name, 100 - strlen(direction) - 1);
    }

    // trouver l'extention ".pbm"
    char* extension_ptr = strstr(file_name_, extension);

    if (extension_ptr != NULL) {

        //trouver la taille de l'extention
        size_t length = extension_ptr - file_name_;
        file_name_[length] = '\0';

        // remplacer avec "_contours.txt"
        strncat(file_name_, new_exten, 100 - length - 1);
    }
}


// Fonction pour la création d'un fichier de contours en utilisant le résultat de create_file_name 
// et pour écrire l'ensemble des contours d'une image (taille et coordonnées des points)
void create_contours_file( char* image_name , sequences_list contours){
        //creation_fichier_des_contours_de_l'image
    FILE* file; 

    //nomer le fichier des resultatas
    char file_name[100];
    create_file_name(image_name, file_name);


    //ouverture de fichier resultats contours d'image
    file=fopen(file_name, "w");

    //ecriture des resultats 
    if(file!=NULL){

        //ecriture de nombre des contoures 
        fprintf(file, "%llu\n\n",size_sequences_list(contours));

        //ecriture des contours 
        noad_list* ptr=contours.list_head;

        while(ptr!=NULL){
            add_contour_to_file(file, ptr->sequence);
            fprintf(file, "\n");
            ptr=ptr->next;
        }
        
        //Fermeture de fichier
        fclose(file);
    }
    else{
        printf("Erreur lors ouverture de fichier results image ");
    }
}

//pour sauvgarder les resultats de tout les tests (tache3) (nom_d'imgae , dimentions H, L, et nombre des segments)
void create_result_file(char* test_file_name, unsigned int h_img, unsigned int l_img, 
unsigned long int segment_number) {
    
    FILE* f;

    f = fopen("resultats_test.txt", "a");

    if (f != NULL) {
        fprintf(f, "%s\n", test_file_name);
        fprintf(f, "%u %u\n", l_img, h_img);
        fprintf(f, "%lu\n\n", segment_number);

        fclose(f);
    } else {
        printf("Probleme lors de l'ouverture du fichier!\n");
    }
}

//pour générer le nom du fichier au format EPS pour un seul contour (tache4)
void eps_file_name_contour(char* image_name, char* file_name_, char mode) {

    char* direction = "eps/";
    char* extension = ".pbm";
    char* new_extension;

    switch(mode){
        case 'S': 
            new_extension = "_Stroke.eps";
            break;

        case 'F':
            new_extension = "_fill.eps";
            break;
    }

    // chercher la position de '/'
    char* slash_pos = strrchr(image_name, '/');

    // si / exist 
    if (slash_pos != NULL) {

        // remplacer direction fichier eps/
        strncpy(file_name_, direction, 100);
        strncat(file_name_, slash_pos + 1, 100 - strlen(direction) - 1);
    } else {

        //si y'a pas de fichier direction ajouter eps/
        strncpy(file_name_, direction, 100);
        strncat(file_name_, image_name, 100 - strlen(direction) - 1);
    }

    // trouver la position de ".pbm"
    char* extension_ptr = strstr(file_name_, extension);

    if (extension_ptr != NULL) {
        // calculer la taille
        size_t length = extension_ptr - file_name_;
        file_name_[length] = '\0';

        // Remplacer avec".eps"
        strncat(file_name_, new_extension, 100 - length - 1);

    }
}


//pour générer une Sortie fichier au format EPS pour un seul contour (tache4)
void create_eps_file_un_contour(char* input_file, unsigned int h, unsigned int l, sequence seq, char mode){
 
 FILE* f_eps;

    // creation de fichier de nom.eps
    char file_name[100];  
    eps_file_name_contour(input_file, file_name, mode);

    //ouverture de fichier
    f_eps=fopen(file_name, "w");
    
    if(f_eps!=NULL){
       
       //premier ligne
       fprintf(f_eps, "%%!PS-Adobe-3.0 EPSF-3.0\n" );
       
       //dimonsions de l'image 
      fprintf(f_eps, "%%%%BoundingBox: 0 0 %u %u\n\n", l, h);
    
       //segments
        int n_par_ligne=0;
        int n=4;

        noad* ptr=seq.head;
        while(ptr!=NULL){

            if(ptr==seq.head){
                fprintf(f_eps, "%.1f %.1f moveto ", ptr->p.a, h-(ptr->p.b));
                n_par_ligne++;
            }
            else{
                fprintf(f_eps, "%.1f %.1f lineto ", ptr->p.a, h-(ptr->p.b));
                n_par_ligne++;

                if(n_par_ligne==n){
                    fprintf(f_eps, "\n");
                    n_par_ligne=0;
                }
            }

            ptr=ptr->next;
        }

        //remplissage
        switch(mode){
            case 'S': 
                fprintf(f_eps, "\n0 setlinewidth");
                fprintf(f_eps, "\nstroke\n\n"); 
            break;

            case'F':
            fprintf(f_eps, "\nfill\n\n"); 
            break;
        }

        fprintf(f_eps, "showpage\n"); 
        
    }
    
 
    else{
        printf("Erreur lors ouverture de fichier.eps!\n");
    }

}



//creation fichier .eps pour l'ensemble des contoures 
void create_eps_file_contours(char* input_file, unsigned int h, unsigned int l, sequences_list contours, char mode){
 
    FILE* f_eps;

    // creation de fichier de nom.eps
    char file_name[100];  
    eps_file_name_contour(input_file, file_name, mode);

    //ouverture de fichier
    f_eps=fopen(file_name, "w");
    
    if(f_eps!=NULL){
       
       //premier ligne
       fprintf(f_eps, "%%!PS-Adobe-3.0 EPSF-3.0\n" );
       
       //dimonsions de l'image 
      fprintf(f_eps, "%%%%BoundingBox: 0 0 %u %u\n\n", l, h);
    
       //segments
       noad_list* ptr_contours=contours.list_head;
       sequence* seq;
      
      while(ptr_contours!=NULL){

        seq=ptr_contours->sequence;
        add_contour_eps(f_eps, seq, h);

        ptr_contours=ptr_contours->next;
      }

        //remplissage
        switch(mode){
            case 'S': 
                fprintf(f_eps, "\n0 setlinewidth");
                fprintf(f_eps, "\nstroke\n\n"); 
            break;

            case'F':
            fprintf(f_eps, "\nfill\n\n"); 
            break;
        }

        fprintf(f_eps, "showpage\n"); 
        
    }
    
 
    else{
        printf("Erreur lors ouverture de fichier.eps!\n");
    }

}



// Pour générer un nom de fichier EPS avec le format "eps/nom_d'image_mode_d=%d.eps"
// pour sauvegarder le fichier EPS d'un contour simplifié par segment
void eps_file_name(char* image_name, char* file_name_, char mode, double d) {
    char* direction = "eps_d/";
    char* extension = ".pbm";
    char new_exten[50];

    switch (mode) {
        case 'S':
            sprintf(new_exten, "_Stroke_d=%.2f.eps", d);
            break;

        case 'F':
            sprintf(new_exten, "_Fill_d=%.2f.eps", d);
            break;
    }

    // Chercher la position du dernier '/'
    char* slash_pos = strrchr(image_name, '/');

    // Si '/' existe
    if (slash_pos != NULL) {
        // Remplacer le chemin de fichier par "eps_d=2/"
        strncpy(file_name_, direction, 100);
        strncat(file_name_, slash_pos + 1, 100 - strlen(direction) - 1);
    } else {
        // Si aucun '/' n'est trouvé, ajouter "eps_d=2/"
        strncpy(file_name_, direction, 100);
        strncat(file_name_, image_name, 100 - strlen(direction) - 1);
    }

    // Trouver la position de ".pbm"
    char* extension_ptr = strstr(file_name_, extension);

    if (extension_ptr != NULL) {
        // Calculer la taille
        size_t length = extension_ptr - file_name_;
        file_name_[length] = '\0';

        // Remplacer avec le nouveau suffixe
        strncat(file_name_, new_exten, 100 - length - 1);
    }
}


//ajouter un contour au fichier.eps
void add_contour_eps(FILE* f_eps, sequence* seq, unsigned int h){

     noad* ptr=seq->head;
     int n_par_ligne=0;
     int n=4;

        while(ptr!=NULL){

            if(ptr==seq->head){
                fprintf(f_eps, "%.1f %.1f moveto ", ptr->p.a, h-(ptr->p.b));
                n_par_ligne++;
            }
            else{
                fprintf(f_eps, "%.1f %.1f lineto ", ptr->p.a, h-(ptr->p.b));
                n_par_ligne++;

                if(n_par_ligne==n){
                    fprintf(f_eps, "\n");
                    n_par_ligne=0;
                }
            }

            ptr=ptr->next;
        }
        fprintf(f_eps,"\n");
}



//creation fichier .eps
void create_eps_file(char* input_file, unsigned int h, unsigned int l, sequences_list contours, char mode, double d){
 
    FILE* f_eps;

    // creation de fichier de nom.eps
    char file_name[100];  
    eps_file_name(input_file, file_name, mode , d);

    //ouverture de fichier
    f_eps=fopen(file_name, "w");
    
    if(f_eps!=NULL){
       
       //premier ligne
       fprintf(f_eps, "%%!PS-Adobe-3.0 EPSF-3.0\n" );
       
       //dimonsions de l'image 
       fprintf(f_eps, "%%%%BoundingBox: 0 0 %u %u\n\n", l, h);
    
       //segments
       noad_list* ptr_contours=contours.list_head;
       sequence* seq;
      
      while(ptr_contours!=NULL){

        seq=ptr_contours->sequence;
        add_contour_eps(f_eps, seq, h);

        ptr_contours=ptr_contours->next;
      }

        //remplissage
        switch(mode){
            case 'S': 
                fprintf(f_eps, "\n0 setlinewidth");
                fprintf(f_eps, "\nstroke\n\n"); 
            break;

            case'F':
            fprintf(f_eps, "\nfill\n\n"); 
            break;
        }

        fprintf(f_eps, "showpage\n"); 
        
    }
    
 
    else{
        printf("Erreur lors ouverture de fichier.eps!\n");
    }

}


//EPS_BEZIER2 ________________________________________________________________________________________

// Fonction pour générer un nom de fichier EPS pour les courbes de Bézier 2 avce le repertoire "eps_bezier3"
void eps_file_name_b2(char* image_name, char* file_name_, char mode, double d) {
    char* direction = "eps_bezier2/";
    char* extension = ".pbm";
    char new_exten[50];

    switch (mode) {
        case 'S':
            sprintf(new_exten, "_bezier2_Stroke_d=%.2f.eps", d);
            break;

        case 'F':
            sprintf(new_exten, "_bezier2_Fill_d=%.2f.eps", d);
            break;
    }

    // Chercher la position du dernier '/'
    char* slash_pos = strrchr(image_name, '/');

    // Si '/' existe
    if (slash_pos != NULL) {
        // Remplacer le chemin de fichier par "eps_d=2/"
        strncpy(file_name_, direction, 100);
        strncat(file_name_, slash_pos + 1, 100 - strlen(direction) - 1);
    } else {
        // Si aucun '/' n'est trouvé, ajouter "eps_d=2/"
        strncpy(file_name_, direction, 100);
        strncat(file_name_, image_name, 100 - strlen(direction) - 1);
    }

    // Trouver la position de ".pbm"
    char* extension_ptr = strstr(file_name_, extension);

    if (extension_ptr != NULL) {
        // Calculer la taille
        size_t length = extension_ptr - file_name_;
        file_name_[length] = '\0';

        // Remplacer avec le nouveau suffixe
        strncat(file_name_, new_exten, 100 - length - 1);
    }
}


//ajouter un contour au fichier.eps
void add_courbe_B2_eps(FILE* f_eps, sequence_b2* seq, unsigned int h){

     noad_b2* ptr=seq->head;
     

        while(ptr!=NULL){
        
        //conversion bezier2 a bezier3
        bezier3 B3 = conversion_bezier3(ptr->B);

            if(ptr==seq->head){
                
                //si c'est la premier point ajouter le point de controle C0 moveto
                fprintf(f_eps, "%.1f %.1f moveto ", B3.C0.a, h-(B3.C0.b));
                fprintf(f_eps, "\n");

                fprintf(f_eps, "%.1f %.1f %.1f %.1f %.1f %.1f curveto ", B3.C1.a, h-(B3.C1.b),
                B3.C2.a, h-(B3.C2.b), B3.C3.a, h-(B3.C3.b));
                fprintf(f_eps, "\n");
               
            }
            else{
                //sinon ajouter juste C1 , C2 , C3 
                fprintf(f_eps, "%.1f %.1f %.1f %.1f %.1f %.1f curveto ", B3.C1.a, h-(B3.C1.b),
                B3.C2.a, h-(B3.C2.b), B3.C3.a, h-(B3.C3.b));
                fprintf(f_eps, "\n");
                
            }

            ptr=ptr->next;
        }
       
}



//creation fichier .eps bezier2
void create_eps_bezier2(char* input_file, unsigned int h, unsigned int l, sequences_listB2 contours, char mode, double d){
 
    FILE* f_eps;

    // creation de fichier de nom.eps
    char file_name[100];  
    eps_file_name_b2(input_file, file_name, mode , d);

    //ouverture de fichier
    f_eps=fopen(file_name, "w");
    
    if(f_eps!=NULL){
       
       //premier ligne
       fprintf(f_eps, "%%!PS-Adobe-3.0 EPSF-3.0\n" );
       
       //dimonsions de l'image 
       fprintf(f_eps, "%%%%BoundingBox: 0 0 %u %u\n\n", l, h);
    
       //segments
       noad_listB2* ptr_contours=contours.list_head;
       sequence_b2* seq;
      
      while(ptr_contours!=NULL){

        seq=ptr_contours->sequenceB2;
        add_courbe_B2_eps(f_eps, seq, h);

        ptr_contours=ptr_contours->next;
      }

        //remplissage
        switch(mode){
            case 'S': 
                fprintf(f_eps, "\n0 setlinewidth");
                fprintf(f_eps, "\nstroke\n\n"); 
            break;

            case'F':
            fprintf(f_eps, "\nfill\n\n"); 
            break;
        }

        fprintf(f_eps, "showpage\n"); 
        
    }
    
 
    else{
        printf("Erreur lors ouverture de fichier.eps!\n");
    }

}



//EPS_BEZIER3________________________________________________________________________________________

// Fonction pour générer un nom de fichier EPS pour les courbes de Bézier degré 3
void eps_file_name_b3(char* image_name, char* file_name_, char mode, double d) {
    char* direction = "eps_bezier3/";
    char* extension = ".pbm";
    char new_exten[50];

    switch (mode) {
        case 'S':
            sprintf(new_exten, "_bezier3_Stroke_d=%.2f.eps", d);
            break;

        case 'F':
            sprintf(new_exten, "_bezier3_Fill_d=%.2f.eps", d);
            break;
    }

    // Chercher la position du dernier '/'
    char* slash_pos = strrchr(image_name, '/');

    // Si '/' existe
    if (slash_pos != NULL) {
        // Remplacer le chemin de fichier par "eps_d=2/"
        strncpy(file_name_, direction, 100);
        strncat(file_name_, slash_pos + 1, 100 - strlen(direction) - 1);
    } else {
        // Si aucun '/' n'est trouvé, ajouter "eps_d=2/"
        strncpy(file_name_, direction, 100);
        strncat(file_name_, image_name, 100 - strlen(direction) - 1);
    }

    // Trouver la position de ".pbm"
    char* extension_ptr = strstr(file_name_, extension);

    if (extension_ptr != NULL) {
        // Calculer la taille
        size_t length = extension_ptr - file_name_;
        file_name_[length] = '\0';

        // Remplacer avec le nouveau suffixe
        strncat(file_name_, new_exten, 100 - length - 1);
    }
}


//ajouter un contour au fichier.eps
void add_courbe_B3_eps(FILE* f_eps, sequence_b3* seq, unsigned int h){

     noad_b3* ptr=seq->head;
     

        while(ptr!=NULL){

            if(ptr==seq->head){
                
                //si c'est la premier point ajouter le point de controle C0 moveto
                fprintf(f_eps, "%.1f %.1f moveto ", ptr->B.C0.a, h-(ptr->B.C0.b));
                fprintf(f_eps, "\n");

                fprintf(f_eps, "%.1f %.1f %.1f %.1f %.1f %.1f curveto ", ptr->B.C1.a, h-(ptr->B.C1.b),
                ptr->B.C2.a, h-(ptr->B.C2.b), ptr->B.C3.a, h-(ptr->B.C3.b));
                fprintf(f_eps, "\n");
               
            }
            else{
                //sinon ajouter juste C1 , C2 , C3 
                fprintf(f_eps, "%.1f %.1f %.1f %.1f %.1f %.1f curveto ", ptr->B.C1.a, h-(ptr->B.C1.b),
                ptr->B.C2.a, h-(ptr->B.C2.b), ptr->B.C3.a, h-(ptr->B.C3.b));
                fprintf(f_eps, "\n");
                
            }

            ptr=ptr->next;
        }
       
}



//creation fichier .eps bezier3
void create_eps_bezier3(char* input_file, unsigned int h, unsigned int l, sequences_listB3 contours, char mode, double d){
 
    FILE* f_eps;

    // creation de fichier de nom.eps
    char file_name[100];  
    eps_file_name_b3(input_file, file_name, mode , d);

    //ouverture de fichier
    f_eps=fopen(file_name, "w");
    
    if(f_eps!=NULL){
       
       //premier ligne
       fprintf(f_eps, "%%!PS-Adobe-3.0 EPSF-3.0\n" );
       
       //dimonsions de l'image 
       fprintf(f_eps, "%%%%BoundingBox: 0 0 %u %u\n\n", l, h);
    
       //segments
       noad_listB3* ptr_contours=contours.list_head;
       sequence_b3* seq;
      
      while(ptr_contours!=NULL){

        seq=ptr_contours->sequenceB3;
        add_courbe_B3_eps(f_eps, seq, h);

        ptr_contours=ptr_contours->next;
      }

        //remplissage
        switch(mode){
            case 'S': 
                fprintf(f_eps, "\n0 setlinewidth");
                fprintf(f_eps, "\nstroke\n\n"); 
            break;

            case'F':
            fprintf(f_eps, "\nfill\n\n"); 
            break;
        }

        fprintf(f_eps, "showpage\n"); 
        
    }
    
 
    else{
        printf("Erreur lors ouverture de fichier.eps!\n");
    }

}


