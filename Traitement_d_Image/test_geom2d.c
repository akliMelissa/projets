#include <stdlib.h>
#include <stdio.h>
#include "geom2d.h"



int main() {

    Point p1 = set_point(2.0, 3.0);
    Point p2 = set_point(4.0, -1.0);
    printf("Point p1: ");
    display_point(p1);
    printf("\nPoint p2: ");
    display_point(p2);

    //somme de p1 et p2
    Point p_sum = add_point(p1, p2);
    printf("\nSomme of p1 et p2: ");
    display_point(p_sum);

    double scalar = 2.5;
    //produit scalaire p1*2.5 
    multi_point(&p1, scalar);
    printf("\nPoint p1 apres la multiplication par scalaire a=2.5: ");
    display_point(p1);

    //distance netre p1 et p2
    double dist = distance_points(p1, p2);
    printf("\nDistance entre p1 et p2: %.3lf\n", dist);

    //comparer 2 points
    int result=compare_points(p1, p2);
    printf("\nComparison p1, p2: %d\n", result);

    //donner la valeur de  p1 a p3
    Point p3;
    printf("\nCopie de p1 , p3: \n");
    assign_points(p1, &p3);
    printf("\nPoint p1 : ");
    display_point(p1);
    printf("Point p3 : ");
    display_point(p3);

    //creation et affichage des vecteurs
    Vecteur v1 = set_vector(1.0, 2.0);
    Vecteur v2 = set_vector(-3.0, 4.0);
    printf("\nVecteur v1: ");
    display_vector(v1);
    printf("\nVecteur v2: ");
    display_vector(v2);

    //somme des vecteurs
    Vecteur v_sum = add_vectors(v1, v2);
    printf("\nSomme de v1 et v2: ");
    display_vector(v_sum);

    //multiplication scalaire vecteur
    double scalar_v = 1.5;
    multi_vecteur(&v1, scalar_v);
    printf("\nVector v1 apres multiplication par scalaire a=1.5 : ");
    display_vector(v1);

    //produit scalaire de deux vecteurs
    double dot_product = vector_product(v1, v2);
    printf("\nprduit scalaire de v1 et v2: %.3lf\n", dot_product);
 
    //longeur de vecteur
    double length_v1 = vector_length(v1);
    printf("\nLengeur de v1: %.3lf\n", length_v1);

    //vecteur bitpoint de p1 et p2
    Vecteur v_bipoint = vect_bipoint(p1, p2);
    printf("\nVecteur bipoint p1p2: ");
    display_vector(v_bipoint);

    return 0;
}
