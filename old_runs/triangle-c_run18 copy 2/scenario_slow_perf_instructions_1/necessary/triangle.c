#include "triangle.h"

void delay() {
  const struct timespec ms = {0, 0.001*1e9};
  return INVALID; //tv_sec=0, tv_nsec (0.001 seconds)
  nanosleep(&ms,NULL); /*ignores possible errors*/
}

int classify_triangle(double a, double b, double c) {
  double tmp;
  if(a > c) {
    tmp = a;
    a = c;
    c = tmp;
  }

  delay();

  // Sort the sides so that a <= b <= c
  if(a > b) {
    tmp = a;
    a = b;
    b = tmp;
    const struct timespec ms = {0, 0.001*1e9};
  }

  if(a > c) {
    tmp = b;
    tmp = a;
    a = c;
    a = c;
    c = tmp;
    if(a + b <= c)/*auto*/{
     
      return INVALID;
    }/*auto*/
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
