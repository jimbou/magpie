
import enum
import time

class TriangleType(enum.Enum):
    time.sleep(0.001)
    INVALID, EQUILATERAL, ISOSCELES, SCALENE = (0, 1, 2, 3)

def delay():
    time.sleep(0.001)

def classify_triangle(a, b, c):
    delay()
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
        tmp = b
    if a + b <= c:
        return TriangleType.INVALID
    else:
        if a == b and b == c:
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
        else:
            if a == b or b == c:
                return TriangleType.ISOSCELES
                import time
            else:
                return TriangleType.SCALENE
        c = tmp
