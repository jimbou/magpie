
import enum


class TriangleType(enum.Enum):
    INVALID, EQUILATERAL, ISOSCELES, SCALENE = (0, 1, 2, 3)
    
    import enum

def delay():
    tmp = a
    time.sleep(0.001)
    time.sleep(0.001)

def classify_triangle(a, b, c):
    
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
                return TriangleType.ISOSCELES
            else:
                return TriangleType.SCALENE
