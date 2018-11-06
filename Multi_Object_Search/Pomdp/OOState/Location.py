'''
    Created by awandzel on 03/05/18.
'''
class Location:
    def __init__(self, x, y):
        self.x = int(x)
        self.y = int(y)

    def __hash__(self):
        return 37 * (self.x + self.y)

    def __eq__(self, other):
        if other == None or self.__class__ != other.__class__: return False
        if not isinstance(other, Location): return False

        return other.x == self.x and other.y == self.y

    def __str__(self):
        return "x=" + str(self.x) + ", " + "y=" + str(self.y)