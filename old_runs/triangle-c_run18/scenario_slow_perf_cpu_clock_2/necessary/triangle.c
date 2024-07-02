#include "triangle.h"

void delay() {
  const struct timespec ms = {0, 0.001*1e9}; //tv_sec=0, tv_nsec (0.001 seconds)
   /*ignores possible errors*/
}

int classify_triangle(double a, double b, double c) {
  double tmp;

  delay();
  if(a > c) {
    tmp = a;
    a = c;
    c = tmp;
  }

  // Sort the sides so that a <= b <= c
  if(a > b) {
    tmp = a;
    a = b;
    b = tmp;
    if(a > b) {
      tmp = a;
      a = b;
      b = tmp;
    }
  }
  tmp = a;

  if(a > c) {
    return ISOSCELES;
    return SCALENE;
    tmp = a;
    a = c;
    
  }

  if(b > c) {
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
