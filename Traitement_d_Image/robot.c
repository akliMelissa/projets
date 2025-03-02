#include"robot.h"
#include"math.h"




//trouver un pixel de depart dans l'image donnée 
void pixel_depart_un_contour(Image Img, Point* pos){

     UINT l=Img.la_largeur_de_l_image;
     UINT h=Img.la_hauteur_de_l_image;

     for(int i=1; i<=h; i++){
        for(int j=1; j<=l; j++){
            if((get_pixel_image(Img,j, i)==NOIR)&&(get_pixel_image(Img,j,i-1)==BLANC)){
                    (*pos).a=j-1;
                    (*pos).b=i-1;

                    return;
            }
        }
    }
}

// cas d'un contour (tache3)
void initialiser_environnement_un_contour(environnement* env, Image Img){
     
    (*env).I=Img;
    pixel_depart(Img, &( (*env).position_init));

    //initialiser robot
    (*env).robot_t.orientation_curr=Est;
    assign_points((*env).position_init, &((*env).robot_t.position ));

}


//extraire le contour d'une image (tache 3)
void extrait_un_contour(Image Im,  sequence* seq)
{

    //initialiser environnment
    environnement env;
    initialiser_environnement_un_contour(&env, Im);

    int boucle=1;
    //Le nombre totale des points de contour 
    long unsigned int num_points=0; 

    while(boucle==1){
        add_to_end_sequence(seq, env.robot_t.position ) ;
        num_points++;
        avancer_robot(&env);
        nouvelle_orientation(&env);

    if(compare_points(env.robot_t.position, env.position_init)==1 && env.robot_t.orientation_curr==Est)
    {
        boucle=0;
    }
        
  }

   add_to_end_sequence(seq,env.robot_t.position ) ;
   num_points++;

   //Le point initial de contour est:
    printf("Le point initial de contour est: ");
    display_point(env.position_init);
    printf("\n");

   //Le nombre totale des segments de contour 
   printf("Le nombre totale des segments :%llu\n", seq->size-1);
   
   //display sequence 
   display_sequence(seq);

}



// tache 5 , 6, 7 , 8 __________________________________________________________________


//trouver les points condidtas pour le pixel de depart
//return 1 si pixel_depart trouver, 0 sinon
int pixel_depart(Image mask ,Point* position ){

     UINT l=largeur_image(mask);
     UINT h=hauteur_image(mask);

     for(int i=1; i<=h; i++){
        for(int j=1; j<=l; j++){
            if((get_pixel_image(mask,j, i)==NOIR)&&(get_pixel_image(mask,j,i-1)==BLANC)){
                    
                    //chercher les points condidats 
                    position->a=j-1;
                    position->b=i-1;
                    return 1;      
            }
        }
    }
    return 0;
}



//trouver les points condidtas pour les pixel de depart
Image set_mask(Image Img){

    //mask de tous les points condidats

    UINT L= largeur_image(Img);
    UINT H=hauteur_image(Img);
    Pixel v=NOIR;

    Image mask= creer_image(L, H);
     
     for(int i=1; i<=H; i++){
        for(int j=1; j<=L; j++){
            if((get_pixel_image(Img,j, i)==NOIR)&&(get_pixel_image(Img,j,i-1)==BLANC)){
                    
                    //detecter les points condidats
                    set_pixel_image(mask , j, i, v); 
            }
        }
    }

    return mask;
}


void initialiser_environnement(environnement* env, Image Img , Image mask, Point depart, sequences_list* contours){
     
    (*env).I=Img;
    Pixel v=BLANC;

    //une nouvelle point i de depart
    assign_points(depart , &(env->position_init));

    //initialiser robot avec la nouvelle point de depart
    (*env).robot_t.orientation_curr=Est;
    assign_points((*env).position_init, &((*env).robot_t.position ));

    //suppression de point i de positions
    set_pixel_image(mask , depart.a+1 ,depart.b+1 ,v);
    
}


//fonction avancer robot
void avancer_robot(environnement* env){

    switch (env->robot_t.orientation_curr)
    {
    case Est:
        env->robot_t.position.a= (env->robot_t.position.a)+1;
    break;
    
    case Ouest:
        env->robot_t.position.a= (env->robot_t.position.a)-1;
    break;
    
    case Nord:
        env->robot_t.position.b= (env->robot_t.position.b)-1;
    break;
    
    case Sud:
        env->robot_t.position.b= (env->robot_t.position.b)+1;
    break;
    }

}


//re_orientataion de robot
void nouvelle_orientation(environnement* env){

Pixel G;
Pixel D;

 switch (env->robot_t.orientation_curr)
 {
    case Est:
    G=get_pixel_image(env->I, (env->robot_t.position.a)+1,(env->robot_t.position.b));
    D=get_pixel_image(env->I, (env->robot_t.position.a)+1,(env->robot_t.position.b)+1);

    //nouvelle orientation
    if(G==1){
         env->robot_t.orientation_curr=Nord;
    }
    else if(D==0){
        env->robot_t.orientation_curr=Sud;
    }
    break;


    case Nord :
    G=get_pixel_image(env->I, (env->robot_t.position.a),(env->robot_t.position.b));
    D=get_pixel_image(env->I, (env->robot_t.position.a)+1,(env->robot_t.position.b));

    //nouvelle orientation
    if(G==1){
        env->robot_t.orientation_curr=Ouest;
    }
    else if( D ==0){
        env->robot_t.orientation_curr=Est;
    }  
    break;


    case Ouest:
    G=get_pixel_image(env->I, (env->robot_t.position.a),(env->robot_t.position.b)+1);
    D=get_pixel_image(env->I, (env->robot_t.position.a),(env->robot_t.position.b));
   
    //nouvelle orientation
    if(G==1){
        env->robot_t.orientation_curr=Sud;
    }
    else if(D ==0){
        env->robot_t.orientation_curr=Nord;
    }  
    break;

    case Sud:
    G=get_pixel_image(env->I, (env->robot_t.position.a)+1,(env->robot_t.position.b)+1);
    D=get_pixel_image(env->I, (env->robot_t.position.a),(env->robot_t.position.b)+1);
   
    //nouvelle orientation
    if(G==1){
        env->robot_t.orientation_curr=Est;
    }
    else if(D==0){
        env->robot_t.orientation_curr=Ouest;
    }  
    break;
    
 }
}




//extraire un contour i de l'image 
void extrait_contour(Image Im, Image mask , Point depart, sequences_list* contours)
{
    //noveau contour
    sequence* contour = (sequence*)malloc(sizeof(sequence));

    //initialiser contour
    init_sequence(contour);

    //initialiser environnment
    environnement env;
    initialiser_environnement(&env, Im, mask , depart, contours);

    int boucle=1;
    Pixel v=BLANC;

    while(boucle==1){

        //detecter l'esnsemble des points de contour
        add_to_end_sequence(contour, env.robot_t.position ) ;

        if(env.robot_t.orientation_curr== Est){
            set_pixel_image(mask , env.robot_t.position.a+1 ,env.robot_t.position.b+1 ,v);
        }

        avancer_robot(&env);
        nouvelle_orientation(&env);

        if(compare_points(env.robot_t.position, env.position_init)==1 && env.robot_t.orientation_curr==Est)
        {
            boucle=0;
        }
        
  }
    //ajouter le dernier point =point de depart
    add_to_end_sequence(contour,env.robot_t.position );

   //ajouter contour sequence to the list contours
    add_to_end_sequences_list(contours, contour);
          
}


//extrait l'ensemble des contours de l'image
void extrait__contours(Image Im , sequences_list* contours){

    //chercher tous les points condidats depart
    Image mask=set_mask(Im);
    Point pixel_depart_point ;
  
    while(pixel_depart(mask , &pixel_depart_point)==1){

         //extrait l'ensemble des contour 
         extrait_contour(Im , mask , pixel_depart_point , contours);

    }

    supprimer_image(&mask);
  
}



// Ici, la fonction ajoute directement la séquence de segments à la liste L 
// Puisque pour deux segments S1=[A, B] et S2=[B, C], nous ajoutons uniquement B une fois
// Et nous le traitons comme un contour simplifié.
// Sinon on ajoute le segment et on modifie la fonction esp et contour des segments pour considirer 2 points 
//au lieu de nombre de points-1 

void douglas_peucker_simplification(sequence* L, Point cont[], int j1, int j2, double d)
{
    segment segment_t = create_segment(cont[j1], cont[j2]);
    double dmax = 0;
    double dj = 0;
    int k = j1;

    for (int j = j1 + 1; j <= j2; j++)
    {
        dj = distance_point_segment(cont[j], segment_t);
        if (dmax < dj)
        {
            dmax = dj;
            k = j;
        }
    }


     if (dmax <= d )
    {
        //si contour implifiee vide on ajoute A sinon on ajoute B
        if (L->size == 0)
        {
            add_to_end_sequence(L, segment_t.p1);
        }
        add_to_end_sequence(L, segment_t.p2);

    }
    else if (dmax > d)
    {
        douglas_peucker_simplification(L, cont, j1, k, d);
        douglas_peucker_simplification(L, cont, k, j2, d);
    }
}


//liste of contours saved in segments format 
void segment_simplification(sequences_list seq_list, double d , sequences_list* simplified_contours )
{
    
    noad_list* ptr_list=seq_list.list_head;
    int max_size=0;
    
    while(ptr_list!=NULL){
        
        //creation de tab avec size= sequence.size pour chaque contour
        max_size=size(*ptr_list->sequence);
        Point* cont =(Point*)malloc(max_size*sizeof(Point));
        sequence_to_array(cont,*(ptr_list->sequence ));

         sequence* L = (sequence*)malloc(sizeof(sequence));
         init_sequence(L);

        //douglas_peucker_simplification
        douglas_peucker_simplification(L, cont ,0, max_size-1 , d );

        add_to_end_sequences_list(simplified_contours, L);

      free(cont);
      ptr_list=ptr_list->next;

    }
    
}


//douglas_peucker_simplification_bezier2
void douglas_peucker_simplification_bezier2(sequence_b2* L,  Point cont[], int j1, int j2, double d)
{   
    bezier2 B;
    double n= j2 -j1;

    //approximation 
    approx_bezier2(cont , j1 , j2 , &B);
    
    double dmax = 0;
    int k = j1 ;

        for (int j = j1 + 1; j <=j2; ++j){
        
        double i =j-j1;
        double ti=i/n; 
          
        //calcule de distance entre Pj et B 
        double dj = distance_point_bezier2(cont[j] , B , ti);
       
        if ( dj >dmax)
        {   
            dmax = dj;
            k = j;
        } 
    }
    
    //create sequence L

     if (dmax <= d  )
    {   
       //ajouter B courbe bezier degree 2 a la fin de liste L 
       add_to_end_sequence_b2(L , B);
       
    }
    else 
    {
        sequence_b2* L1= create_sequence_b2();
        sequence_b2* L2= create_sequence_b2();

        douglas_peucker_simplification_bezier2(L1, cont, j1, k, d);
        douglas_peucker_simplification_bezier2(L2,  cont, k, j2, d);

        concatination_sequences_b2(L1 , L2 , L);
    }
}



// creation de liste simplifee avec bezier2 courbes 
void simplification_bezier2(sequences_list seq_list, double d , sequences_listB2* simplified_contours )
{ 
    noad_list* ptr_list=seq_list.list_head;
    int max_size=0;
    
    while(ptr_list!=NULL){
        
        //creation de cont tableau avec size= sequence.size pour chaque contour
        max_size=size(*ptr_list->sequence);
        Point* cont =(Point*)malloc(max_size*sizeof(Point));

        //conersion d'une liste chinee a un tableau 
        sequence_to_array(cont,*(ptr_list->sequence ));

        // L= sequence bezier2 
        sequence_b2* L= create_sequence_b2();

        //douglas_peucker_simplification avec bezier2
        douglas_peucker_simplification_bezier2( L, cont ,0, max_size-1 , d );

        //ajouter L a la fin de liste des contours simplifee avec bezier2 
        add_to_end_sequences_list_b2(simplified_contours, L);

      free(cont);
      ptr_list=ptr_list->next;

    }  
}



//douglas_peucker_simplification_bezier3
void douglas_peucker_simplification_bezier3(sequence_b3* L,  Point cont[], int j1, int j2, double d)
{   
    bezier3 B;
    double n= j2 -j1;

    //approximation 
    approx_bezier3(cont , j1 , j2 , &B);
    
    double dmax = 0;
    int k = j1 ;

        for (int j = j1 + 1; j <=j2; ++j){
        
        double i =j-j1;
        double ti=i/n; 
         
        //calcule de distance entre Pj et B Bézier de degré 3
        double dj = distance_point_bezier3(cont[j] , B , ti);
       
        if ( dj >dmax)
        {   
            dmax = dj;
            k = j;
        } 
    }
    
    //create sequence L
     if (dmax <= d  )
    {   
       //ajouter B courbe bezier degree 3 a la fin de liste L 
       
       add_to_end_sequence_b3(L , B);
       
    }
    else 
    {
        sequence_b3* L1= create_sequence_b3();
        sequence_b3* L2= create_sequence_b3();

        douglas_peucker_simplification_bezier3(L1, cont, j1, k, d);
        douglas_peucker_simplification_bezier3(L2,  cont, k, j2, d);

        concatination_sequences_b3(L1 , L2 , L);
    }
}



// creation de liste simplifee avec bezier3 courbes 
void simplification_bezier3(sequences_list seq_list, double d , sequences_listB3* simplified_contours )
{ 
    noad_list* ptr_list=seq_list.list_head;
    int max_size=0;
    
    while(ptr_list!=NULL){
        
        //creation de cont tableau avec size= sequence.size pour chaque contour
        max_size=size(*ptr_list->sequence);
        Point* cont =(Point*)malloc(max_size*sizeof(Point));

        //conersion d'une liste chinee a un tableau 
        sequence_to_array(cont,*(ptr_list->sequence ));

        // L= sequence bezier2 
        sequence_b3* L= create_sequence_b3();

        //douglas_peucker_simplification avec bezier2
        douglas_peucker_simplification_bezier3( L, cont ,0, max_size-1 , d );

        //ajouter L a la fin de liste des contours simplifee avec bezier2 
        add_to_end_sequences_list_b3(simplified_contours, L);

      free(cont);
      ptr_list=ptr_list->next;

    }  
}