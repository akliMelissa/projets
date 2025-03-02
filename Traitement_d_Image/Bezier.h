/******************************************************************************
 * Déclaration des types de Bézier 2 et 3
 *****************************************************************************/

#ifndef _BEZIER_H_
#define _BEZIER_H_

#include <stdio.h>
#include <stdlib.h>
#include "geom2d.h"

/* Type Bézier2 */
typedef struct bezier2_ {
    Point C0;
    Point C1;
    Point C2;
} bezier2;

/* Type Bézier3 */
typedef struct bezier3_ {
    Point C0;
    Point C1;
    Point C2;
    Point C3;
} bezier3;

// Séquence de Bézier 2 ------------------------------>>>>>>>>>

/* Type noad_b2 */
typedef struct noad_b2_ {
    bezier2 B;
    struct noad_b2_* next;
} noad_b2;

/* Type séquence de Bézier2 */
typedef struct sequence_b2_ {
    noad_b2* head;
    noad_b2* tail;
    int size;
} sequence_b2;

// Liste des séquences de Bézier 2 ---------------------->>>>>>>>>

/* Type noad de liste de séquences de Bézier 2 */
typedef struct noad_list_b2 {
    sequence_b2* sequenceB2;
    struct noad_list_b2* next;
} noad_listB2;

/* Type liste de séquences de Bézier 2 */
typedef struct sequences_list_b2 {
   noad_listB2* list_head;
   noad_listB2* list_tail;
   int list_size;
} sequences_listB2;

// Séquence de Bézier 3 ------------------------------>>>>>>>>>

/* Type noad_b3 */
typedef struct noad_b3_ {
    bezier3 B;
    struct noad_b3_* next;
} noad_b3;

/* Type séquence de Bézier3 */
typedef struct sequence_b3_ {
    noad_b3* head;
    noad_b3* tail;
    int size;
} sequence_b3;

// Liste des séquences de Bézier 3 ------------------------------>>>>>>>>>

/* Type noad de liste de séquences */
typedef struct noad_list_b3 {
    sequence_b3* sequenceB3;
    struct noad_list_b3* next;
} noad_listB3;

/* Type liste de séquences */
typedef struct sequences_list_b3 {
   noad_listB3* list_head;
   noad_listB3* list_tail;
   int list_size;
} sequences_listB3;

// Bézier 2________________________________________________________>>>>>>>>>>>>

// Calculer le point C(ti)
Point calculate_C_ti(bezier2 B, double ti);

// Approximer cont(j1, ..., j2) par une Bézier B de degré 2
void approx_bezier2(Point cont[], int j1, int j2, bezier2* B);

// Distance entre le point P et la courbe Bézier de degré 2
double distance_point_bezier2(Point Pj, bezier2 B, double ti);

// Afficher les points de contrôle de Bézier de degré 2 avec l'indice i, Bi
void display_bezier2(bezier2 B, int i);

// Getters
Point getC0_d2(bezier2 B);
Point getC1_d2(bezier2 B);
Point getC2_d2(bezier2 B);

// Séquence de Bézier 2 ________________________________________>>>>>>>>>>>>

// Créer et initialiser la séquence
sequence_b2* create_sequence_b2();

// Obtenir la taille de la séquence
int size_b2(sequence_b2 seq);

// Ajouter une courbe Bézier2 à la fin de la séquence
void add_to_end_sequence_b2(sequence_b2* seq, bezier2 B);

// Afficher la séquence des courbes de Bézier de degré 2
void display_sequence_b2(sequence_b2* seq);

// Détruire la séquence
void destroy_sequence_b2(sequence_b2* seq);

// Vérifier l'existence d'un élément dans la liste, retourner 1 s'il existe, 0 sinon
int is_in_sequence_b2(sequence_b2 seq, bezier2 B);

// Supprimer au début
void delete_beginning_b2(sequence_b2* seq);

// Supprimer un élément s'il existe
void delete_element_b2(sequence_b2* seq, bezier2 B);

void concatination_sequences_b2(sequence_b2* L1, sequence_b2* L2, sequence_b2* L);

// Liste des séquences de courbes Bézier 2____________________________________

// Obtenir la taille de la liste des séquences
int size_sequences_list_b2(sequences_listB2 list);

// Créer et initialiser la liste des séquences
sequences_listB2* create_sequences_list_b2();

// Ajouter une séquence_bezier2 à la fin de la liste
void add_to_end_sequences_list_b2(sequences_listB2* list, sequence_b2* seq);

// Afficher la liste des séquences
void display_sequences_list_b2(sequences_listB2 list, unsigned long long int* summ_bezier2);

// Détruire la liste des séquences
void destroy_sequences_list_b2(sequences_listB2* list);

// Bézier 3_______________________________________________________>>>>>>>>>>>>

// Conversion de Bézier 2 à Bézier 3
bezier3 conversion_bezier3(bezier2 B);

// Getters
Point getC0_d3(bezier3 B);
Point getC1_d3(bezier3 B);
Point getC2_d3(bezier3 B);
Point getC3_d3(bezier3 B);

// Afficher les points de contrôle de Bézier de degré 3 avec l'indice i, Bi
void display_bezier3(bezier3 B, int i);

// Approximer cont(j1, ..., j2) par une Bézier B de degré 3
void approx_bezier3(Point cont[], int j1, int j2, bezier3* B);

// Calculer le point C(ti)
Point calculate_C_ti_b3(bezier3 B, double ti);

// Distance entre le point P et la courbe Bézier de degré 3
double distance_point_bezier3(Point Pj, bezier3 B, double ti);


// Séquence de Bézier 3________________________________________>>>>>>>>>>>>

// Créer et initialiser la séquence
sequence_b3* create_sequence_b3();

// Obtenir la taille de la séquence
int size_b3(sequence_b3 seq);

// Ajouter une courbe Bézier3 à la fin de la séquence
void add_to_end_sequence_b3(sequence_b3* seq, bezier3 B);

// Afficher la séquence des courbes de Bézier de degré 3
void display_sequence_b3(sequence_b3* seq);

// Détruire la séquence
void destroy_sequence_b3(sequence_b3* seq);

// Vérifier l'existence d'un élément dans la liste, retourner 1 s'il existe, 0 sinon
int is_in_sequence_b3(sequence_b3 seq, bezier3 B);

// Supprimer au début
void delete_beginning_b3(sequence_b3* seq);

// Supprimer un élément s'il existe
void delete_element_b3(sequence_b3* seq, bezier3 B);

//concatination de séquence des courbes de Bézier de degré 3
void concatination_sequences_b3(sequence_b3* L1, sequence_b3* L2, sequence_b3* L);

// Liste des séquences de courbes Bézier 3____________________________________

// Obtenir la taille de la liste des séquences
int size_sequences_list_b3(sequences_listB3 list);

// Créer et initialiser la liste des séquences
sequences_listB3* create_sequences_list_b3();

// Ajouter une séquence_bezier3 à la fin de la liste
void add_to_end_sequences_list_b3(sequences_listB3* list, sequence_b3* seq);

// Afficher la liste des séquences
void display_sequences_list_b3(sequences_listB3 list , unsigned long long int* summ_bezier3);

// Détruire la liste des séquences
void destroy_sequences_list_b3(sequences_listB3* list);

#endif



