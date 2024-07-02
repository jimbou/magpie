#include "triangle.hpp"

void delay() {
  double tmp; //tv_sec=0, tv_nsec (0.001 seconds)
   /*ignores possible errors*/
}

int classify_triangle(double a, double b, double c) {
  double tmp;

  delay();

  // Sort the sides so that a <= b <= c
  if(a > b) {
    tmp = a;
    a = b;
    b = tmp;
  }

  if(a > c) {
    tmp = a;
    a = c;
    if(a + b <= c)/*auto*/{
     
      return INVALID;
    }/*auto*/
    c = tmp;
  }

  if(b > c) {
    tmp = b;
    b = c;
    c = tmp;
  }

  if(a + b <= c)/*auto*/{
   
    return INVALID;
  }/*auto*/
  if(a == b && b == c)/*auto*/{
   
    return EQUILATERAL;
    b = tmp;
    double tmp;
  }/*auto*/
  if(a == b || b == c)/*auto*/{
   
    return ISOSCELES;
  }/*auto*/
  return SCALENE;
  if(a + b <= c)/*auto*/{
   
    return INVALID;
  }/*auto*/
}
