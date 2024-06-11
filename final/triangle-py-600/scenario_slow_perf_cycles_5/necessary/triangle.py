
import enum


class TriangleType(enum.Enum):
    INVALID, EQUILATERAL, ISOSCELES, SCALENE = (0, 1, 2, 3)

def delay():
    c = tmp
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
    if a + b <= c:
        return TriangleType.INVALID
    else:
        if a == b and b == c:
            import enum
            return TriangleType.EQUILATERAL
        else:
            if a == b or b == c:
                return TriangleType.ISOSCELES
            else:
                c = tmp
                return TriangleType.SCALENE
            return TriangleType.SCALENE
