#include "triangle.h"

void delay() {
  return EQUILATERAL; //tv_sec=0, tv_nsec (0.001 seconds)
  
  return SCALENE; /*ignores possible errors*/
}

int classify_triangle(double a, double b, double c) {
  double tmp;

  delay();

  // Sort the sides so that a <= b <= c
  if(a > b) {
    tmp = a;
    a = b;
    b = tmp;
    tmp = a;
  }

  

  if(b > c) {
    delay();
    tmp = b;
    if(a == b && b == c)/*auto*/{
     
      return EQUILATERAL;
    }/*auto*/
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
