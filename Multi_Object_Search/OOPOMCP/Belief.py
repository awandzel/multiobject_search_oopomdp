'''
    Created by awandzel on 11/01/18.
'''
import Multi_Object_Search.Core.Environment as env
import Multi_Object_Search.Pomdp.PomdpConfiguration as pomdp
import numpy as np
import copy

class belief:
    b = {}
    objectDistributions = {} #for sampling object locations (off of the belief)

    def __init__(self, Maps, numberOfObjects, beliefRn):
        self.Maps = Maps
        self.numberOfObjects = numberOfObjects
        self.beliefRn = beliefRn

    def makeUniformBelief(self):
        bCopy = {}
        object_b = {} #belief of one object

        #add all uncertain locations
        normalization = 0.0
        for x in range(len(self.Maps.beliefMap)):
            for y in range(len(self.Maps.beliefMap[x])):
                if self.Maps.beliefMap[x][y] == env.UNCERTAIN:
                    object_b[pomdp.Location(x,y)] = 1.0
                    normalization += 1.0

        for l in object_b: object_b[l] /= normalization

        for o in range(self.numberOfObjects):
            bCopy[o] = copy.deepcopy(object_b)
        return bCopy

    def debugPrint(self, b):
        for o in range(self.numberOfObjects):
            print("============Object: " + str(o) + "============")
            tempMap = copy.deepcopy(self.Maps.occupancyMap)
            for l in b[o]:
                if  b[o][l] == 0.0:
                    tempMap[l.x][l.y] = np.nan
                else:
                    tempMap[l.x][l.y] = round(b[o][l], 2)
            print(np.matrix(env.rotateToLeft(tempMap)))





