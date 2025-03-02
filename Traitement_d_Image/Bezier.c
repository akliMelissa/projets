/****************************************************************************** 
  Implementation du module Bezier
******************************************************************************/

#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include"Bezier.h"
#include <math.h>


//bezier 2&3____________________________________________________________________________>>>>>>>>>>>>


Point getC0_d2(bezier2 B){
    return B.C0;
}

Point getC1_d2(bezier2 B){
    return B.C1;
}

Point getC2_d2(bezier2 B){
    return B.C2;
}


bezier3 conversion_bezier3(bezier2 B2){

    bezier3 B3;

    //C0=C0_B2 , C3=C2_B2
    assign_points(B2.C0 , &B3.C0);
    assign_points(B2.C2 , &B3.C3);

    //C1_B2= 2*C1_B2
    multi_point(&B2.C1 , 2);
    
    //C1= (C0_B2 +2* C1_B2)/3
    B3.C1 = add_point(B2.C0 , B2.C1);
    devide_point(&B3.C1 , 3);

    //C2= (C2_B2 +2* C1_B2)/3
    B3.C2=add_point(B2.C1 , B2.C2);
    devide_point(&B3.C2 , 3);

return B3;
}



Point getC0_d3(bezier3 B3){
    return B3.C0;
}

Point getC1_d3(bezier3 B3){
    return B3.C1;
}

Point getC2_d3(bezier3 B3){
    return B3.C2;
}

Point getC3_d3(bezier3 B3){
    return B3.C3;
}

void display_bezier2(bezier2 B , int i){
     printf("B%d = B[" , i);

        display_point(B.C0);
        printf(" , ");

        display_point(B.C1);
        printf(" , ");

        display_point(B.C2);
        printf(" ]\n");
}

//approcher cont(j1,. . . ,j2) par une bezier B de degree 2
void approx_bezier2(Point cont[], int j1 , int j2 , bezier2* B2){
 
	assign_points(cont[j1], &B2->C0);
	assign_points(cont[j2], &B2->C2);

	double n=j2 -j1 ; 

	if(n==1){

        //C1=1/2(Pj1 + Pj2)
         assign_points(add_point(cont[j1], cont[j2]) , &B2->C1);
         multi_point(&B2->C1, 0.5);
	}
	else {
        
        double a = (3 * n) / ((n * n )- 1);
        double b = (1 - (2 * n)) / (2.0 * (n + 1));

        // A = a * summ(Pi+j1)
        double x=0, y=0;

        for (int i = 1; i <= n - 1; i++) {
            // A += Pi+j1
            x=x+ cont[i + j1].a;
            y=y+cont[i + j1].b;
        }
        Point A=set_point(x, y);
        multi_point(&A, a);

        // B = b * (Pj1 + Pj2)
        Point B = add_point(cont[j1], cont[j2]);
        multi_point(&B, b);

        B2->C1 = add_point(A, B);
	}

}


// Calculate point C(ti) on a quadratic Bezier curve
Point calculate_C_ti(bezier2 B, double ti) {
    Point C_ti = set_point(0, 0);

   C_ti.a= ((1 - ti) * (1 - ti) * B.C0.a)+ (2 * (1 - ti) * ti * B.C1.a)+ (ti*ti * B.C2.a);
   C_ti.b= ((1 - ti) * (1 - ti) * B.C0.b)+ (2 * (1 - ti) * ti * B.C1.b)+ (ti*ti * B.C2.b);
    
    return C_ti;
}

//distance entre point P et courbe bezier degree 2
double distance_point_bezier2(Point Pj , bezier2 B , double ti){

    Point C_t = calculate_C_ti(B, ti);
    return distance_points(Pj, C_t);
}


double sigma(double k , double n ){
    double sigma = ((6*k*k*k*k ) - (8*n*k*k*k) +(6*k*k) -(4*n*k ) +( n*n*n*n) -(n*n));
    return sigma;
}


//approcher cont(j1,. . . ,j2) par une bezier B de degree 3
void approx_bezier3(Point cont[], int j1 , int j2 , bezier3* B3){

    //C0=Pj1
    assign_points(cont[j1], &B3->C0);

    //C2=Pj2
    assign_points(cont[j2], &B3->C3);

    double n=j2 -j1 ; 

    if(n<3){
        bezier2 B2; 
        approx_bezier2(cont , j1 , j2 , &B2);

        bezier3 B3_t= conversion_bezier3(B2);

        assign_points(B3_t.C1 , &(B3->C1));
        assign_points(B3_t.C2 , &(B3->C2));

    }
    else{

        double alpha=(-(15*n*n*n )+ (5*n*n )+ (2*n )+ 4)/(3*(n+2)*((3*n*n) +1)); 
        double betha= ((10*n*n*n) - (15*n*n)  + n +2)/(3*(n+2)*((3*n*n) +1));
        double lamda= (70*n) /(3* ((n*n) -1)*((n*n) -4)*((3*n*n) +1));
    
        // C1_t 
        Point C1_t=set_point(0, 0);

        for(int i=1 ; i<=n-1 ; i++){
            
            Point Pj=cont[j1+i];

            double sigma_i = sigma(i ,n);
            multi_point(&Pj , sigma_i);

            //C1_t+= sigma(i)*Pj1+i
            add_to_point(&C1_t , Pj);

        }
        //lamda*C1_t
        multi_point(&C1_t, lamda);

        //C1_t+= alpha* Pj1 + betha* Pj2
        C1_t.a += (alpha*cont[j1].a + betha*cont[j2].a);
        C1_t.b += (alpha*cont[j1].b + betha*cont[j2].b);

        //C1=C1_t
        assign_points(C1_t, &B3->C1);

         // C2_t
        Point C2_t=set_point(0, 0);

        for(int i=1 ; i<=n-1 ; i++){
            
            Point Pj2=cont[j1+i];

            double sigma_i = sigma((n-i) ,n);
            multi_point(&Pj2 , sigma_i);

            //C2_t+= sigma(n-i)*Pj1+i
            add_to_point(&C2_t , Pj2);

        }
        //lamda*C2_t
        multi_point(&C2_t, lamda);

        //C2_t+= alpha* Pj1 + betha* Pj2
        C2_t.a += (betha*cont[j1].a + alpha*cont[j2].a);
        C2_t.b += (betha*cont[j1].b + alpha*cont[j2].b);

        //C2=C2_t
        assign_points(C2_t, &B3->C2);

    }
}


Point calculate_C_ti_b3(bezier3 B, double ti) {

    Point C_ti = set_point(0, 0);

   C_ti.a= ((1 - ti) * (1 - ti)*(1 - ti) * B.C0.a)+ (3 * (1 - ti) *(1-ti)* ti * B.C1.a)+ 
   ( 3*ti*ti*(1 - ti) * B.C2.a) + (ti*ti*ti *B.C3.a);

   C_ti.b= ((1 - ti) * (1 - ti)*(1 - ti) * B.C0.b)+ (3 * (1 - ti) *(1-ti)* ti * B.C1.b)+ 
   ( 3*ti*ti*(1 - ti) * B.C2.b) + (ti*ti*ti *B.C3.b);
    
    return C_ti;
}

//distance entre point P et courbe bezier degree 3
double distance_point_bezier3(Point Pj , bezier3 B , double ti){

    Point C_t = calculate_C_ti_b3(B, ti);
    return distance_points(Pj, C_t);
}



// sequence de bezier 2 ______________________________________________________________>>>>>>>>>>>>

//initialiser la sequence
sequence_b2* create_sequence_b2(){

    sequence_b2* seq=(sequence_b2*)malloc(sizeof(sequence_b2));
    seq->size = 0;
    seq->head = NULL;
    seq->tail = NULL;

    return seq;
}

//returner la taille de sequence
int size_b2(sequence_b2 seq){
    return seq.size;
}

//ajouter bezier2 courbe a la fin de seq  
void add_to_end_sequence_b2(sequence_b2* seq, bezier2 B2){
    
    noad_b2* new_noad = (noad_b2*)malloc(sizeof(noad_b2));
    
    new_noad->B.C0=B2.C0;
    new_noad->B.C1=B2.C1;
    new_noad->B.C2=B2.C2;

    new_noad->next = NULL;

    if (seq->head == NULL) {
        seq->head = new_noad;
        seq->tail = new_noad;
    }
    else {
        seq->tail->next = new_noad;
        seq->tail = new_noad;
    }

    seq->size++;
}

//afficher la sequence bes courbes de bezier degree 2
void display_sequence_b2(sequence_b2* seq ){

    //afficher la taille 
    printf("Le nombre des courbes de bezier2  : %d\n", seq->size);

    //afficher les elements 
    noad_b2* current = seq->head;

    int i=1;
    while (current != NULL) {

       display_bezier2(current->B , i);
        
        current = current->next;
        i++;
    }
}

//detruire la liste sequence
void destroy_sequence_b2(sequence_b2* seq){
    
    noad_b2* current = seq->head;
    noad_b2* next;

    while (current != NULL) {
        next = current->next;
        free(current);
        current = next;
    }

    seq->head = NULL;
    seq->tail = NULL;
    seq->size = 0;
}


//verfier l'existance d'un element dans la liste returner 1 si il existe , 0 sinon
int is_in_sequence_b2(sequence_b2 seq , bezier2 B2){
     
    noad_b2* current = seq.head;

    while (current != NULL) {

        if (compare_points(current->B.C0,B2.C0)==1 && compare_points(current->B.C1,B2.C1)==1 && 
        compare_points(current->B.C2,B2.C2)==1) {

            return 1; // Point exist
        }
        current = current->next;
    }

    return 0; // Point n'exite pas dans la sequence
}

//suppression au debut
void delete_beginning_b2(sequence_b2* seq){

    if (seq->head == NULL) {
        printf("La liste est vide!\n");
        return;
    }

    noad_b2* temp = seq->head;
    seq->head = (seq->head)->next;
    free(temp);

    if (seq->head == NULL) {
        seq->tail = NULL; 
    }
}


//suppression d'un element s'il exist
void delete_element_b2(sequence_b2* seq , bezier2 B2){
     
    noad_b2* current = seq->head;
    noad_b2* previous = NULL;

    while (current != NULL) {
        
        if (compare_points(current->B.C0,B2.C0)==1 && compare_points(current->B.C1,B2.C1)==1 && 
        compare_points(current->B.C2,B2.C2)==1) {

            if (previous != NULL) {
                previous->next = current->next;
            } 
            else {
                seq->head = current->next;
            }

            // si suppression de dernier elment mis a jour le tail
            if (current == seq->tail) {
                seq->tail = previous;
            }

            free(current);

            //mis a jour la taille
            seq->size--;

            return;
        }

        previous = current;
        current = current->next;
    }

}

void concatination_sequences_b2(sequence_b2* L1, sequence_b2* L2, sequence_b2* L)
{
    L->size = L1->size + L2->size;

    if (L1->head != NULL) {
        L->head = L1->head;
        L1->tail->next = L2->head;
    } else {
        L->head = L2->head;
    }

    if (L2->tail != NULL) {
        L->tail = L2->tail;
    } else {
        L->tail = L1->tail;
    }

    L1->head = NULL;
    L1->tail = NULL;
    L2->head = NULL;
    L2->tail = NULL;
}

//list of sequences des courbes bezier 2 ________________________________________________________________

//returner la taille de sequence
int size_sequences_list_b2(sequences_listB2 list){
    return list.list_size;
}

//initialiser la liste des sequences
sequences_listB2* create_sequences_list_b2(){

    sequences_listB2* list=(sequences_listB2*)malloc(sizeof(sequences_listB2));
    list->list_head=NULL;
    list->list_tail=NULL;
    list->list_size=0;

    return list;
}

//ajouter une nouvelle point a la fin 
void add_to_end_sequences_list_b2(sequences_listB2* list, sequence_b2* seq){
    
    noad_listB2* new_noad = (noad_listB2*)malloc(sizeof(noad_listB2));
    
    new_noad->sequenceB2=seq;
    new_noad->next = NULL;

    if (list->list_head== NULL) {
        list->list_head= new_noad;
        list->list_tail= new_noad;
    }
    else {
        list->list_tail->next= new_noad;
        list->list_tail = new_noad;
    }

    list->list_size++;
}

//afficher la sequence 
void display_sequences_list_b2(sequences_listB2 list  ,unsigned long long int* summ_bezier2 ){

    noad_listB2* ptr=list.list_head;
    sequence_b2* seq;

    //contour i 
    int i=1; 

    printf("\nLe nombre totale des suequences des courbes bezier2: %d\n\n", list.list_size);

    while(ptr!=NULL){

        printf("\nsequence %d : \n", i);
        seq =ptr->sequenceB2;
       
       display_sequence_b2(seq);
       (*summ_bezier2)+= size_b2(*seq);
        i++;
        ptr=ptr->next;
    }
    }

//detruire la liste sequence
void destroy_sequences_list_b2(sequences_listB2* list){
     
    noad_listB2* current = list->list_head;

    while (current != NULL) {
        noad_listB2* next = current->next;

        //de_allocate memoire reserver pour les noads objets de sequence
        destroy_sequence_b2(current->sequenceB2);

        //de_allocate memoire reserver pour la sequence objet
        free(current->sequenceB2);

        //de_allocate memoire reserver pour le noad_list_ objet
        free(current);

        current = next;
    }

    list->list_head = NULL;
    list->list_tail = NULL;
    list->list_size = 0;
}




// sequence de bezier 3________________________________________>>>>>>>>>>>>

//creer et initialiser la sequence
sequence_b3* create_sequence_b3(){
    
    sequence_b3* seq=(sequence_b3*)malloc(sizeof(sequence_b3));
    seq->size = 0;
    seq->head = NULL;
    seq->tail = NULL;

    return seq;
}

//returner la taille de sequence
int size_b3(sequence_b3 seq){
    return seq.size;
}


//ajouter bezier2 courbe a la fin de seq  
void add_to_end_sequence_b3(sequence_b3* seq, bezier3 B3){
    
    noad_b3* new_noad = (noad_b3*)malloc(sizeof(noad_b3));
    
    assign_points(B3.C0,&new_noad->B.C0);
    assign_points(B3.C1,&new_noad->B.C1);
    assign_points(B3.C2,&new_noad->B.C2);
    assign_points(B3.C3,&new_noad->B.C3);
    new_noad->next = NULL;

    if (seq->head == NULL) {
        seq->head = new_noad;
        seq->tail = new_noad;
    }
    else {
        seq->tail->next = new_noad;
        seq->tail = new_noad;
    }

    seq->size++;
}


void display_bezier3(bezier3 B , int i){
     printf("B%d = B[" , i);

        display_point(B.C0);
        printf(" , ");

        display_point(B.C1);
        printf(" , ");

        display_point(B.C2);
        printf(" , ");

        display_point(B.C3);
        printf(" ]\n");
}


//afficher la séquence des courbes de bézier degré 3
void display_sequence_b3(sequence_b3* seq ){

    //afficher la taille 
    printf("Le nombre des courbes de bezier3 : %d\n", seq->size);

    //afficher les elements 
    noad_b3* current = seq->head;

    int i=1;
    while (current != NULL) {

       display_bezier3(current->B , i);
        
        current = current->next;
        i++;
    }
}

//détruire la séquence
void destroy_sequence_b3(sequence_b3* seq){
    
    noad_b3* current = seq->head;
    noad_b3* next;

    while (current != NULL) {
        next = current->next;
        free(current);
        current = next;
    }

    seq->head = NULL;
    seq->tail = NULL;
    seq->size = 0;
}


//vérfiér l'éxistance d'un élément dans la liste returner 1 si il existe , 0 sinon
int is_in_sequence_b3(sequence_b3 seq , bezier3 B3){
     
    noad_b3* current = seq.head;

    while (current != NULL) {

        if (compare_points(current->B.C0,B3.C0)==1 && compare_points(current->B.C1,B3.C1)==1 && 
        compare_points(current->B.C2,B3.C2)==1 &&  compare_points(current->B.C3,B3.C3)==1) {

            return 1; // Point exist
        }
        current = current->next;
    }

    return 0; // Point n'exite pas dans la sequence
}

//suppression au début
void delete_beginning_b3(sequence_b3* seq){

    if (seq->head == NULL) {
        printf("La liste est vide!\n");
        return;
    }

    noad_b3* temp = seq->head;
    seq->head = (seq->head)->next;
    free(temp);

    if (seq->head == NULL) {
        seq->tail = NULL; 
    }
}


//suppression d'un élément s'il exist
void delete_element_b3(sequence_b3* seq , bezier3 B3){
     
    noad_b3* current = seq->head;
    noad_b3* previous = NULL;

    while (current != NULL) {
        
        if (compare_points(current->B.C0,B3.C0)==1 && compare_points(current->B.C1,B3.C1)==1 && 
        compare_points(current->B.C2,B3.C2)==1 &&  compare_points(current->B.C3,B3.C3)==1) {

            if (previous != NULL) {
                previous->next = current->next;
            } 
            else {
                seq->head = current->next;
            }

            // si suppression de derniér élément mis à jour le tail
            if (current == seq->tail) {
                seq->tail = previous;
            }

            free(current);

            //mis à jour la taille
            seq->size--;

            return;
        }

        previous = current;
        current = current->next;
    }

}

void concatination_sequences_b3(sequence_b3* L1, sequence_b3* L2, sequence_b3* L)
{
    L->size = L1->size + L2->size;

    if (L1->head != NULL) {
        L->head = L1->head;
        L1->tail->next = L2->head;
    } else {
        L->head = L2->head;
    }

    if (L2->tail != NULL) {
        L->tail = L2->tail;
    } else {
        L->tail = L1->tail;
    }

    L1->head = NULL;
    L1->tail = NULL;
    L2->head = NULL;
    L2->tail = NULL;
}

//list des séquences des courbes bezier 3 ________________________________________________________________

//retourner la taille de séquence
int size_sequences_list_b3(sequences_listB3 list){
    return list.list_size;
}

//initialiser la liste des séquences
sequences_listB3* create_sequences_list_b3(){

    sequences_listB3* list=(sequences_listB3*)malloc(sizeof(sequences_listB3));
    list->list_head=NULL;
    list->list_tail=NULL;
    list->list_size=0;

    return list;
}

//ajouter une nouvelle point a la fin 
void add_to_end_sequences_list_b3(sequences_listB3* list, sequence_b3* seq){
    
    noad_listB3* new_noad = (noad_listB3*)malloc(sizeof(noad_listB3));
    
    new_noad->sequenceB3=seq;
    new_noad->next = NULL;

    if (list->list_head== NULL) {
        list->list_head= new_noad;
        list->list_tail= new_noad;
    }
    else {
        list->list_tail->next= new_noad;
        list->list_tail = new_noad;
    }

    list->list_size++;
}

//afficher la séquence 
void display_sequences_list_b3(sequences_listB3 list , unsigned long long int* summ_bezier3){

    noad_listB3* ptr=list.list_head;
    sequence_b3* seq;

    //contour i 
    int i=1; 

    printf("\nLe nombre totale des suequences des courbes bezier3: %d\n\n", list.list_size);

    while(ptr!=NULL){

        printf("\nsequence %d : \n", i);
        seq =ptr->sequenceB3;
       
       display_sequence_b3(seq);
       (*summ_bezier3)+= size_b3(*seq);
        i++;
        ptr=ptr->next;
    }
}

//detruire la liste séquence
void destroy_sequences_list_b3(sequences_listB3* list){
     
    noad_listB3* current = list->list_head;

    while (current != NULL) {
        noad_listB3* next = current->next;

        //de_allocate memoire reserver pour les noads objets de sequence
        destroy_sequence_b3(current->sequenceB3);

        //de_allocate memoire reserver pour la sequence objet
        free(current->sequenceB3);

        //de_allocate memoire reserver pour le noad_list_ objet
        free(current);

        current = next;
    }

    list->list_head = NULL;
    list->list_tail = NULL;
    list->list_size = 0;
}


