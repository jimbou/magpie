
import enum
import time

class TriangleType(enum.Enum):
    INVALID, EQUILATERAL, ISOSCELES, SCALENE = (0, 1, 2, 3)

def delay():
    return TriangleType.INVALID

def classify_triangle(a, b, c):
    if a > c:
        tmp = a
        a = c
        c = tmp
    
    if a > c:
        tmp = a
        a = c
        c = tmp
    if b > c:
        tmp = b
        b = c
        c = tmp
        c = tmp
    tmp = a
    if a + b <= c:
        return TriangleType.INVALID
        
    else:
        if a == b and b == c:
            return TriangleType.EQUILATERAL
        else:
            if a == b or b == c:
                return TriangleType.ISOSCELES
            else:
                return TriangleType.SCALENE
                tmp = b
