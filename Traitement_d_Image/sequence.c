#include <stdio.h>
#include"sequence.h"

//initialiser la sequence
void init_sequence(sequence* seq)
{
    seq->size = 0;
    seq->head = NULL;
    seq->tail = NULL;
}

//returner la taille de sequence
unsigned long long size(sequence seq)
{
    return seq.size;
}


//ajouter une nouvelle point a la fin 
void add_to_end_sequence(sequence* seq, Point p_t)
{
    noad* new_noad = (noad*)malloc(sizeof(noad));
    
    new_noad->p.a=p_t.a;
    new_noad->p.b=p_t.b;
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


//afficher la sequence 
void display_sequence(sequence* seq)
{
    //afficher la taille 
    printf("Le nombre des points de contour : %llu\n", seq->size);
    printf("Le nombre des segments de contour :%llu\n", (seq->size)-1);

    //afficher les elements 
    printf("\nE = ");
    noad* current = seq->head;
    int crr=0; 

    printf("{");
    while (current->next != NULL) {
        printf(" (%.1lf, %.1lf),", current->p.a, current->p.b);
        current = current->next;

        //pour afficher 4 elements par lignes 
        crr++;
        if(crr==4){
            printf("\n ");
           crr=0;
        }
    }
    printf(" (%.1lf, %.1lf) }\n\n", current->p.a, current->p.b);
    current = current->next;

}

//detruire la liste sequence
void destroy_sequence(sequence* seq)
{
    noad* current = seq->head;
    noad* next;

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
int is_in_sequence(sequence seq, Point p)
{
    noad* current = seq.head;

    while (current != NULL) {
        if (current->p.a == p.a && current->p.b == p.b) {
            return 1; // Point exist
        }
        current = current->next;
    }

    return 0; // Point n'exite pas dans la sequence
}


void delete_beginning(sequence* seq) {
    if (seq->head == NULL) {
        printf("La liste est vide!\n");
        return;
    }

    noad* temp = seq->head;
    seq->head = (seq->head)->next;
    free(temp);

    if (seq->head == NULL) {
        seq->tail = NULL; 
    }
}

//suppression d'un element p
void delete_element(sequence* seq, Point p) {

    noad* current = seq->head;
    noad* previous = NULL;

    while (current != NULL) {
        
        if (current->p.a == p.a && current->p.b == p.b) {

            if (previous != NULL) {
                previous->next = current->next;
            } else {
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




//_________________________________________________________________________________________

//initialiser la liste des sequences
void initialiser_sequences_list(sequences_list* list){
    list->list_head=NULL;
    list->list_tail=NULL;
    list->list_size=0;
}


//returner la taille de sequence
unsigned long long size_sequences_list(sequences_list list){
    return list.list_size;
}

//ajouter une nouvelle point a la fin 
void add_to_end_sequences_list(sequences_list* list, sequence* seq){

    noad_list* new_noad = (noad_list*)malloc(sizeof(noad_list));
    
    new_noad->sequence=seq;
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
void display_sequences_list(sequences_list list , unsigned long long int *summ_points ,
unsigned long long int* summ_segments ){

    noad_list* ptr=list.list_head;
    sequence* seq;

    //contour i 
    int i=1; 

   

    while(ptr!=NULL){

        printf("contour %d : \n", i);
        seq =ptr->sequence;
       
       display_sequence(seq);
       (*summ_points)+=seq->size;
       (*summ_segments)+=seq->size-1;
    
        i++;
        ptr=ptr->next;
    }

}

void destroy_sequences_list(sequences_list* list) {
    
    noad_list* current = list->list_head;

    while (current != NULL) {
        noad_list* next = current->next;

        //de_allocate memoire reserver pour les noads objets de sequence
        destroy_sequence(current->sequence);

        //de_allocate memoire reserver pour la sequence objet
        free(current->sequence);

        //de_allocate memoire reserver pour le noad_list_ objet
        free(current);

        current = next;
    }

    list->list_head = NULL;
    list->list_tail = NULL;
    list->list_size = 0;
}

//transformer une sequence a un tableau des points
void sequence_to_array(Point* tab ,sequence seq){
   
    noad* ptr=seq.head;
    int i=0;
    
    while(ptr!=NULL){

      //copier tous les points au tab
      assign_points(ptr->p, &(tab[i]));

      ptr=ptr->next;
      i++;

    }
}

void concatination_sequences(sequence* L1, sequence* L2, sequence* L)
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


