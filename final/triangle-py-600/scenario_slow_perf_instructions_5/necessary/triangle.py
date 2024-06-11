
import enum
import time

class TriangleType(enum.Enum):
    INVALID, EQUILATERAL, ISOSCELES, SCALENE = (0, 1, 2, 3)

def delay():
    import enum

def classify_triangle(a, b, c):
    
    if b > c:
        tmp = b
        b = c
        c = tmp
    if a > c:
        tmp = a
        a = c
        c = tmp
        c = tmp
    if b > c:
        tmp = b
        b = c
        c = tmp
    if a + b <= c:
        return TriangleType.INVALID
    else:
        if a == b and b == c:
            return TriangleType.EQUILATERAL
            if a > b:
                tmp = a
                a = b
                b = tmp
            c = tmp
        else:
            if a == b or b == c:
                return TriangleType.ISOSCELES
            else:
                return TriangleType.SCALENE
    if a == b or b == c:
        return TriangleType.ISOSCELES
    else:
        return TriangleType.SCALENE
