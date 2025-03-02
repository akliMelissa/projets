/******************************************************************************
  Interface du module liste des points
******************************************************************************/

#ifndef _sequence_H_
#define _sequence_H_
#include "geom2d.h"

/* Type noad */
typedef struct noad_
{
    Point p;
    struct noad_* next;
} noad;

/* Type sequence of points */
typedef struct sequence_
{
    noad* head;
    noad* tail;
    unsigned long long size;
} sequence;


/*type noad of liste of sequences*/
typedef struct noad_list
{
  sequence* sequence;
  struct noad_list* next;
}noad_list;

/* Type liste of sequences */
typedef struct sequences_list_
{
   noad_list* list_head;
   noad_list* list_tail;
   unsigned long long list_size;
}sequences_list;


// sequence __________________________________________________________________

//initialiser la sequence
void init_sequence(sequence* seq);

//returner la taille de sequence
unsigned long long size(sequence seq);

//ajouter une nouvelle point a la fin 
void add_to_end_sequence(sequence* seq, Point p);

//afficher la sequence 
void display_sequence(sequence* seq);

//detruire la liste sequence
void destroy_sequence(sequence* seq);

//verfier l'existance d'un element dans la liste returner 1 si il existe , 0 sinon
int is_in_sequence(sequence seq , Point p);

//suppression au debut
void delete_beginning(sequence* seq);

//suppression d'un element s'il exist
void delete_element(sequence* seq , Point p);


//list of sequences ________________________________________________________________

//returner la taille de sequence
unsigned long long size_sequences_list(sequences_list list);

//initialiser la liste des sequences
void initialiser_sequences_list(sequences_list* list);

//ajouter une nouvelle point a la fin 
void add_to_end_sequences_list(sequences_list* list, sequence* seq);

//afficher la sequence 
void display_sequences_list(sequences_list list , unsigned long long int *summ_points ,
unsigned long long int* summ_segments );

//detruire la liste sequence
void destroy_sequences_list(sequences_list* list);

//transformer une sequence a un tableau des points
void sequence_to_array(Point* tab ,sequence seq);

//conctatination deux sequences des segments
void concatination_sequences(sequence* L1 , sequence* L2, sequence*L);




#endif /* _sequence_H_ */