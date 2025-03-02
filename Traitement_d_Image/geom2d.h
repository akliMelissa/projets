/****************************************************************************** 
  declaration de types geometriques
******************************************************************************/
 
#ifndef _GEOM2D_H_
#define _GEOM2D_H_

#include <stdio.h>
#include <stdlib.h>
#include "types_macros.h"

/* Type POINT */
typedef struct POINT_
{
	double a,b; //coordonees de point
} Point;

/* Type Vecteur */
typedef struct VECTEUR_
{
	double x,y; //coordonees de vecteur
} Vecteur;


/* Type Segment */
typedef struct POINT_SEGMENT{

 	Point p1; 
 	Point p2;

}segment;



/*Points operations __________________________________________________________________________*/

//creation d'une point avec les coordonees (a,b)
Point set_point(double a, double b);

//affichage d'une point
void display_point(Point p);

//Somme des deux points P1+P2
Point add_point(Point p1, Point p2);

//P1+=P2
void add_to_point(Point* p1, Point p2);

//multiplication de point par scalaire x
void multi_point(Point* p, double x);

//devision de point par scalaire x
void devide_point(Point* p, double x);

//Distance entre deux points P1+P2
double distance_points(Point p1, Point p2);

//return 1 si il sont egaux , 0 sinon
int compare_points(Point p1, Point p2);

//donner la valeur de  p1 a p2
void assign_points(Point p1, Point* p2);

/*vecteurs operations____________________________________________________________________________*/

//creation d'un vecteur avec les coordonees (x,y)
Vecteur set_vector(double x, double y);

//affichage des cordonnes d'un vecteur
void display_vector(Vecteur v);

//Somme des deux points P1+P2
Vecteur add_vectors(Vecteur v1, Vecteur v2);

//multiplication de pscalaire vecteur a.V
void multi_vecteur(Vecteur* v, double a);

//produit scalaire de deux vecteurs
double vector_product(Vecteur v1, Vecteur v2);

//longeur vecteur (norme eudlidienne)
double vector_length(Vecteur v);

//vecteur correspandant au bipoint AB
Vecteur vect_bipoint(Point A , Point B);



//segment_____________________________________________________________________________>>>>>>>>>>>>

segment create_segment(Point p1_t , Point p2_t);

//distance entre un point p , et un segment s=[A,B]
double distance_point_segment(Point p, segment s);


#endif