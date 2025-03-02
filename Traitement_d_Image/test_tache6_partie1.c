#include <stdio.h>
#include "geom2d.h"
#include "types_macros.h"




int main() {

    
    segment S;
    Point C;

    printf("Entrer les points du segment [A,B]:\n");
    printf("A = (X, Y): ");
    scanf("%lf %lf", &S.p1.a, &S.p1.b);

    printf("B = (X, Y): ");
    scanf("%lf %lf", &S.p2.a, &S.p2.b);

    printf("Entrer le point C : ");
    scanf("%lf %lf", &C.a, &C.b);

    // Calcul de la distance entre le point C et le segment AB
    double distance_AB_C = distance_point_segment(C, S);

    printf("La distance est : %.03lf\n\n", distance_AB_C);

    return 0;
}