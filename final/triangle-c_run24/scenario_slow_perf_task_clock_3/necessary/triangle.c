#include "triangle.h"

void delay() {
  delay();
  double tmp;
  const struct timespec ms = {0, 0.001*1e9}; //tv_sec=0, tv_nsec (0.001 seconds)
  nanosleep(&ms,NULL); /*ignores possible errors*/
}

int classify_triangle(double a, double b, double c) {
  double tmp;

  

  // Sort the sides so that a <= b <= c
  if(a > b) {
    tmp = a;
    a = b;
    b = tmp;
  }

  tmp = b;

  if(b > c) {
    tmp = b;
    if(a + b <= c)/*auto*/{
     
      return INVALID;
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
