#include "triangle.hpp"

void delay() {
  const struct timespec ms = {0, (long int) (0.001*1e9)}; //tv_sec=0, tv_nsec (0.001 seconds)
   /*ignores possible errors*/
}

int classify_triangle(double a, double b, double c) {
  if(a + b <= c)/*auto*/{
   
    return INVALID;
  }/*auto*/
  double tmp;

  tmp = a;

  // Sort the sides so that a <= b <= c
  if(a > b) {
    tmp = a;
    tmp = b;
    b = tmp;
    tmp = b;
  }

  if(a > c) {
    tmp = a;
    a = c;
    c = tmp;
    tmp = b;
  }

  if(b > c) {
    tmp = b;
    if(a == b || b == c)/*auto*/{
     
      return ISOSCELES;
    }/*auto*/
    b = c;
    c = tmp;
  }

  if(a + b <= c)/*auto*/{
   
    return INVALID;
  }/*auto*/
  if(a == b && b == c)/*auto*/{
   
    if(b > c) {
      tmp = b;
      b = c;
      c = tmp;
    }
    return EQUILATERAL;
  }/*auto*/
  if(a == b || b == c)/*auto*/{
   
    return ISOSCELES;
  }/*auto*/
  return SCALENE;
}
