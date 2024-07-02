#include "triangle.hpp"

void delay() {
  const struct timespec ms = {0, (long int) (0.001*1e9)}; //tv_sec=0, tv_nsec (0.001 seconds)
   /*ignores possible errors*/
}

int classify_triangle(double a, double b, double c) {
  double tmp;
  const struct timespec ms = {0, (long int) (0.001*1e9)};

  

  // Sort the sides so that a <= b <= c
  

  if(a > c) {
    tmp = a;
    a = c;
    c = tmp;
  }

  if(b > c) {
    tmp = b;
    b = c;
    c = tmp;
  }

  if(a + b <= c)/*auto*/{
   
    a = b;
    return INVALID;
    b = tmp;
  }/*auto*/
  if(a == b && b == c)/*auto*/{
   
    return EQUILATERAL;
  }/*auto*/
  if(a == b || b == c)/*auto*/{
   
    return ISOSCELES;
  }/*auto*/
  return SCALENE;
}
