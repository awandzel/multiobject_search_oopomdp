'''
    Created by awandzel on 11/01/18.
'''
import Multi_Object_Search.Core.Environment as env
import Multi_Object_Search.Pomdp.PomdpConfiguration as pomdp
import Multi_Object_Search.Pomdp.OOState.Location as Loc
import Multi_Object_Search.Pomdp.OOState.OOState as State
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
                    object_b[Loc.Location(x,y)] = 1.0
                    normalization += 1.0

        for l in object_b: object_b[l] /= normalization

        for o in range(self.numberOfObjects):
            bCopy[o] = copy.deepcopy(object_b)
        return bCopy

    def sampleState(self, s):
        objects = []
        for o in range(self.numberOfObjects):
            if s.hasChosen[o]:
                objects.append(s.searchObjects[o])
            else:
                objects.append(self.sampleLocation(o))
        newState = State.OOState(s.agent, objects, s.hasChosen)
        return copy.deepcopy(newState)

    def sampleLocation(self, o):
        curSum = 0.
        roll = self.beliefRn.random()
        for l in self.b[o]:
            curSum += self.b[o][l]
            if (roll <= curSum):
                return l
        raise Exception("Probabilities don't sum to 1.0: " + str(curSum))


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





