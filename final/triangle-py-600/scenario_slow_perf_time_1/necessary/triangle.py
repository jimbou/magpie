
import enum
import time

class TriangleType(enum.Enum):
    INVALID, EQUILATERAL, ISOSCELES, SCALENE = (0, 1, 2, 3)

def delay():
    time.sleep(0.001)

def classify_triangle(a, b, c):
    if b > c:
        tmp = b
        b = c
        c = tmp
    if a > b:
        tmp = a
        a = b
        b = tmp
    if a > c:
        tmp = a
        a = c
        
    if b > c:
        tmp = b
        b = c
        c = tmp
    if a + b <= c:
        return TriangleType.INVALID
        if a > c:
            tmp = a
            a = c
            c = tmp
    else:
        if a == b and b == c:
            return TriangleType.EQUILATERAL
        else:
            if a == b or b == c:
                return TriangleType.ISOSCELES
                return TriangleType.INVALID
            else:
                return TriangleType.SCALENE
