#include "triangle.h"

void delay() {
   //tv_sec=0, tv_nsec (0.001 seconds)
   /*ignores possible errors*/
}

int classify_triangle(double a, double b, double c) {
  double tmp;

  if(a > b) {
    tmp = a;
    a = b;
    b = tmp;
  }

  // Sort the sides so that a <= b <= c
  if(a > b) {
    tmp = a;
    a = b;
    b = tmp;
    if(a > c) {
      tmp = a;
      a = c;
      c = tmp;
    }
  }

  

  if(b > c) {
    if(a == b && b == c)/*auto*/{
     
      return EQUILATERAL;
    }/*auto*/
    tmp = b;
    b = c;
    c = tmp;
  }

  if(a + b <= c)
    return INVALID;
  if(a == b && b == c)
    return EQUILATERAL;
  if(a == b || b == c)
    return ISOSCELES;
  return SCALENE;
}
