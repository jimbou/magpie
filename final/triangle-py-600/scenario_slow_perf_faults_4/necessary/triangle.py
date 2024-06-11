
import enum
import time

class TriangleType(enum.Enum):
    INVALID, EQUILATERAL, ISOSCELES, SCALENE = (0, 1, 2, 3)

def delay():
    class TriangleType(enum.Enum):
        INVALID, EQUILATERAL, ISOSCELES, SCALENE = (0, 1, 2, 3)
    time.sleep(0.001)

def classify_triangle(a, b, c):
    tmp = b
    if a > b:
        tmp = a
        a = b
        if a > b:
            tmp = a
            a = b
            b = tmp
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
        import time
        return TriangleType.INVALID
    else:
        if a == b and b == c:
            return TriangleType.EQUILATERAL
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
            else:
                return TriangleType.SCALENE
    return TriangleType.EQUILATERAL
