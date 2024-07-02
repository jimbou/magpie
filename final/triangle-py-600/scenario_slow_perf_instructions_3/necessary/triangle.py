
import enum


class TriangleType(enum.Enum):
    INVALID, EQUILATERAL, ISOSCELES, SCALENE = (0, 1, 2, 3)

def delay():
    class TriangleType(enum.Enum):
        INVALID, EQUILATERAL, ISOSCELES, SCALENE = (0, 1, 2, 3)
    a = b

def classify_triangle(a, b, c):
    INVALID, EQUILATERAL, ISOSCELES, SCALENE = (0, 1, 2, 3)
    if a > b:
        tmp = a
        a = b
        b = tmp
    if a > c:
        tmp = a
        a = c
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
        else:
            if a == b or b == c:
                if a > c:
                    tmp = a
                    a = c
                    c = tmp
                return TriangleType.ISOSCELES
                return TriangleType.SCALENE
                a = b
            else:
                return TriangleType.SCALENE
            delay()
