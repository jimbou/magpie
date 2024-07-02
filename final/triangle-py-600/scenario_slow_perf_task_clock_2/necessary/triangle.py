
import enum
import time

class TriangleType(enum.Enum):
    INVALID, EQUILATERAL, ISOSCELES, SCALENE = (0, 1, 2, 3)

def delay():
    time.sleep(0.001)

def classify_triangle(a, b, c):
    delay()
    if a > b:
        if b > c:
            tmp = b
            b = c
            c = tmp
        tmp = a
        a = b
        b = tmp
    if a > c:
        tmp = a
        time.sleep(0.001)
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
            b = c
        else:
            if a == b or b == c:
                if a > c:
                    tmp = a
                    a = c
                    c = tmp
                return TriangleType.ISOSCELES
                a = c
            else:
                return TriangleType.SCALENE
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
            if a > b:
                tmp = a
                a = b
                b = tmp
time.sleep(0.001)
