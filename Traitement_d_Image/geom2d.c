
#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include"geom2d.h"
#include <math.h>

/*Points operations __________________________________________________________________________*/

//creation d'une point avec les coordonees (a,b)
Point set_point(double a, double b){
    Point p; 
    p.a=a; 
    p.b=b;
 return p;
}

//affichage d'une point
void display_point(Point p){
    printf("( %.2lf; %.2lf) " ,p.a , p.b);
}

//Somme des deux points P1+P2
Point add_point(Point p1, Point p2){
    return set_point(p1.a+p2.a, p1.b+p2.b);
}

//P1+=P2
void add_to_point(Point* p1, Point p2){
    p1->a=p1->a + p2.a;
    p1->b=p1->b + p2.b;
}

//multiplication de point par scalaire a
void multi_point(Point* p, double x){
    (*p).a=(*p).a*x;
    (*p).b=(*p).b*x;
}

//devision de point par scalaire x
void devide_point(Point* p, double x){
     (*p).a=(*p).a/x;
     (*p).b=(*p).b/x;
}

//Distance entre deux points P1, P2
double distance_points(Point p1, Point p2){

    double dist=0; 
    dist = sqrt(((p1.a-p2.a)*(p1.a-p2.a))+ ((p1.b-p2.b)*(p1.b-p2.b)));

    return dist;
}

//return 1 si il sont egaux , 0 sinon
int compare_points(Point p1, Point p2){
    if(p1.a==p2.a && p1.b==p2.b){
        return 1;
    }
    return 0;
}


//donner la valeur de  p1 a p2
void assign_points(Point p1, Point* p2){
    (*p2).a=p1.a;
    (*p2).b=p1.b;
}


/*vecteurs operations____________________________________________________________________________*/

//creation d'un vecteur avec les coordonees (x,y)
Vecteur set_vector(double x, double y){
    Vecteur v;
    v.x=x; 
    v.y=y;
    return v;
}

//affichage des cordonnes d'un vecteur
void display_vector(Vecteur v){
    printf("( %.3lf; %.3lf)\n" ,v.x, v.y);
}

//Somme des deux points P1+P2
Vecteur add_vectors(Vecteur v1, Vecteur v2){
    return set_vector(v1.x+v2.x , v1.y+ v2.y);
}

//multiplication de pscalaire vecteur a.V
void multi_vecteur(Vecteur* v, double a){
    (*v).x=(*v).x*a;
    (*v).y=(*v).y*a;
}

//produit scalaire de deux vecteurs
double vector_product(Vecteur v1, Vecteur v2){
    double result=(v1.x*v2.x)+(v1.y*v2.y);
    return result;
}

//longeur vecteur (norme eudlidienne)
double vector_length(Vecteur v){
    double length= sqrt((v.x*v.x)+(v.y*v.y));
    return length;
}

//vecteur correspandant au bipoint AB
Vecteur vect_bipoint(Point A , Point B){
    return set_vector(B.a-A.a, B.b-A.b);
}


//segment_____________________________________________________________________________>>>>>>>>>>>>

segment create_segment(Point p1_t , Point p2_t){
  
    segment seg_t;
    assign_points(p1_t, &(seg_t.p1));
    assign_points(p2_t, &(seg_t.p2));  

    return seg_t;
}


//distance entre un point p , et un segment s=[A,B]
double distance_point_segment(Point p, segment s){

    double distance=0;

    //case A=B ________________>>>>>>

        if(compare_points(s.p1, s.p2)==1){

            //Distance entre deux points A , P
            distance=distance_points(s.p1, p);
            return distance;
        }

    //case A!=B________________>>>>>>

    Point Q; 
    double alpha;

    //vecteur AP
    Vecteur p1_p= vect_bipoint(s.p1, p);

    //vecteur AB=B-A
    Vecteur p1_p2= vect_bipoint(s.p1 , s.p2);
    
    //alpha= <AP, AB> / <AB, AB>
    alpha=vector_product(p1_p, p1_p2)/vector_product(p1_p2, p1_p2);
    
    // C= alpha * (B-A)
    Point C; 
    C.a=alpha * (p1_p2.x);
    C.b=alpha * (p1_p2.y);

    // A + alpha(B-A) = A + C
    Q= add_point(s.p1, C);

    //is Q in S 
    if( 0 <=alpha && alpha <=1){

        distance=distance_points(Q ,p);
    }
    else if (alpha<0){

        distance=distance_points(s.p1 ,p);
    }
    else{
        distance=distance_points(s.p2 ,p);
    }

    return distance;
}
