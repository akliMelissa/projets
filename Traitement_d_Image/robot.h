/****************************************************************************** 
  Interface du module robot
******************************************************************************/

#ifndef _robot_H_
#define _robot_H_
#include"image.h"
#include"geom2d.h"
#include "types_macros.h"
#include"sequence.h"
#include "Bezier.h"
/* 
 Type enum pour les orientations
 */
typedef enum {Nord=0,Est=1,Sud=2,Ouest=3} orientation;


/* 
 Type robot
 */
typedef struct robot_
{
	  Point position; 
    orientation orientation_curr;
} robot;

/* 
 Type environnement
 */
typedef struct environnement_
{
	Image I; 
    Point position_init;
    robot robot_t;
} environnement;


//trouver un pixel de depart dans l'image donnée (tache3)
void pixel_depart_un_contour(Image Img, Point* pos);

// cas d'un contour (tache3)
void initialiser_environnement_un_contour(environnement* env, Image Img);

//extraire le contour d'une image (tache 3)
void extrait_un_contour(Image Im,  sequence* seq);

//tache5, 6, 7, 8 ______________________________________________________________

//trouver tous les points condidtas pour les pixel de depart
Image set_mask(Image Img);

//chercher un point condidat dans l'image mask
int pixel_depart(Image mask ,Point* position );

//initialiser un environnement avec un point de depart
void initialiser_environnement(environnement* env, Image Img , Image mask, Point depart, sequences_list* contours);

//re_orientataion de robot
void nouvelle_orientation(environnement* env);

//fonction avancer robot
void avancer_robot(environnement* env);

// extrait un contour de les contours d'une image
void extrait_contour(Image Im, Image mask , Point depart, sequences_list* contours);

//extrait l'ensemble descontours
void extrait__contours(Image Im , sequences_list* contours);

//simplification algorithm de doughlas peucker
void douglas_peucker_simplification(sequence* L, Point cont[] , int j1 , int j2 , double d );

//silmplification des contour par segments
void segment_simplification(sequences_list seq_list ,double d , sequences_list* segments_cont);

//douglas_peucker_simplification_bezier2
void douglas_peucker_simplification_bezier2(sequence_b2* L,  Point cont[], int j1, int j2, double d) ;

// creation de liste simplifee avec bezier2 courbes 
void simplification_bezier2(sequences_list seq_list, double d , sequences_listB2* simplified_contours ) ;

//douglas_peucker_simplification_bezier3
void douglas_peucker_simplification_bezier3(sequence_b3* L,  Point cont[], int j1, int j2, double d) ;

// creation de liste simplifee avec bezier3 courbes 
void simplification_bezier3(sequences_list seq_list, double d , sequences_listB3* simplified_contours ) ;




#endif /* _robot_H_ */
