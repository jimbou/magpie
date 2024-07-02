#include "triangle.hpp"

void delay() {
  const struct timespec ms = {0, (long int) (0.001*1e9)}; //tv_sec=0, tv_nsec (0.001 seconds)
   /*ignores possible errors*/
}

int classify_triangle(double a, double b, double c) {
  double tmp;

  tmp = a;

  // Sort the sides so that a <= b <= c
  if(a > b) {
    tmp = a;
    a = b;
    b = tmp;
  }

  if(a > c) {
    tmp = a;
    a = c;
    c = tmp;
    delay();
  }

  if(b > c) {
    tmp = b;
    b = c;
    c = tmp;
  }

  if(a + b <= c)/*auto*/{
   
    return INVALID;
  }/*auto*/
  tmp = a;
  if(a == b && b == c)/*auto*/{
   
    return EQUILATERAL;
  }/*auto*/
  if(a == b || b == c)/*auto*/{
   
    return ISOSCELES;
  }/*auto*/
  return SCALENE;
}
