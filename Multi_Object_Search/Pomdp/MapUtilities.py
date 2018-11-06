'''
    Created by awandzel on 10/31/18.
'''

import Multi_Object_Search.Pomdp.OOState.Location as Loc
import Multi_Object_Search.Core.Environment as env
import numpy as np

class mapUtilities:
    def __init__(self, Maps, Rooms=None, RRT=None):
        self.Maps = Maps
        self.Rooms = Rooms
        self.RRT = RRT

    def isConflictInCardinalDirections(self, currentLocation):
        for i in range(4):
            moveLocation = self.moveInDirection(currentLocation.x, currentLocation.y, i)
            if not self.checkInBounds(moveLocation.x, moveLocation.y):
                return True
        return False

    def moveInDirection(self, x, y, d):
        if (d == 0): #north
            y += 1
        elif (d == 1): #south
            y -= 1
        elif (d == 2): #east
            x += 1
        elif (d == 3): #west
            x -= 1
        return Loc.Location(x,y)

    def checkInBounds(self, x, y):
        return not (x < 0 or x >= len(self.Maps.occupancyMap) or y < 0
               or y >= len(self.Maps.occupancyMap[0]) or self.Maps.occupancyMap[x][y] == env.WALL)

    def euclideanDistance(self, l1, l2):
        return np.linalg.norm(np.array([l1.x,l1.y]) - np.array([l2.x, l2.y]))


