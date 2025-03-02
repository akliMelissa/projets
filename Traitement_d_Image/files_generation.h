/****************************************************************************** 
  Interface du module FILES_GENERATION
******************************************************************************/


#ifndef FILE_GENERATION_H
#define FILE_GENERATION_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "geom2d.h"
#include "sequence.h"
#include "Bezier.h"


// Fonction pour mémoriser un contour de type séquence de points dans un fichier
void add_contour_to_file(FILE* file, sequence* seq);

// Pour générer un nom de fichier avec le format "contours/nom_d'image_contour.txt"
void create_file_name(char* image_name, char* file_name_);

// Fonction pour la création d'un fichier de contours en utilisant le résultat de create_file_name 
// et pour écrire l'ensemble des contours d'une image (taille et coordonnées des points)
void create_contours_file(char* image_name, sequences_list contours);

//pour sauvgarder les resultats de tout les tests (tache3) (nom_d'imgae , dimentions H, L, et nombre des segments)
void create_result_file(char* test_file_name, unsigned int h_img, unsigned int l_img, 
unsigned long int segment_number);

//pour générer le nom du fichier au format EPS pour un seul contour (tache4)
void eps_file_name_contour(char* image_name, char* file_name_, char mode);

//pour générer une Sortie fichier au format EPS pour un seul contour (tache4)
void create_eps_file_un_contour(char* input_file, unsigned int h, unsigned int l, sequence seq, char mode);


// Fonction pour ajouter les points d'un contour de type séquence de points au fichier EPS
void add_contour_eps(FILE* f_eps, sequence* seq, unsigned int h);

//creation fichier .eps pour l'ensemble des contoures 
void create_eps_file_contours(char* input_file, unsigned int h, unsigned int l, sequences_list contours, char mode);


// Pour générer un nom de fichier EPS avec le format "eps/nom_d'image_mode_d=%d.eps"
// pour sauvegarder le fichier EPS d'un contour simplifié par segment
void eps_file_name(char* image_name, char* file_name_, char mode, double d);

// Fonction pour la création d'un fichier EPS pour l'ensemble des contours simplifiés par segments 
// avec une distance d donnée 
void create_eps_file(char* input_file, unsigned int h, unsigned int l, sequences_list contours, char mode,
 double d);

// Fonction pour générer un nom de fichier EPS pour les courbes de Bézier 2 avce le repertoire "eps_bezier3"
void eps_file_name_b2(char* image_name, char* file_name_, char mode, double d);

// Fonction pour ajouter les points d'un contour de type séquence de points pour les courbes de Bézier 2
// au fichier EPS  
void add_courbe_B2_eps(FILE* f_eps, sequence_b2* seq, unsigned int h);

// Fonction pour créer un fichier EPS pour les courbes de degré 2
void create_eps_bezier2(char* input_file, unsigned int h, unsigned int l, sequences_listB2 contours, char mode, double d);

// Fonction pour générer un nom de fichier EPS pour les courbes de Bézier degré 3
void eps_file_name_b3(char* image_name, char* file_name_, char mode, double d);

// Fonction pour créer un fichier EPS pour les courbes de Bézier degré 3
void create_eps_bezier3(char* input_file, unsigned int h, unsigned int l, sequences_listB3 contours, char mode, double d);

#endif /* FILE_GENERATION */