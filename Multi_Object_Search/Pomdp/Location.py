'''
    Created by awandzel on 10/30/18.
'''

class Location:
    def __init__(self, x, y):
        self.x = x
        self.y = y

    def __str__(self):
        return "x=" + str(self.x) + ", " + "y=" + str(self.y)



