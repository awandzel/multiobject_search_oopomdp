'''
    Created by awandzel on 03/06/18.
'''

import Multi_Object_Search.Pomdp.Domain.StaticConstants as Constants
import Multi_Object_Search.Pomdp.ObservationFunction.SensingRegion as SensingRegion
import Multi_Object_Search.Pomdp.OOState.Location as Loc
from scipy.stats import norm

class FanShapedSensor:

    def __init__(self, util, ObservationModelParameters, observationRn):
        self.util = util
        self.ObsParams = ObservationModelParameters
        self.observationRn = observationRn

    def toString(self, o, a):
        params = Constants.parseAction(a)
        actionName = params[0]
        if params[1] == str(0):
            actionName += "_North"
        elif params[1] == str(1):
            actionName += "_South"
        elif params[1] == str(2):
            actionName += "_East"
        else:
            actionName += "_West"

        print(actionName + ":")
        for object in o:
            print(object)


    def realWorldExperimentObservation(self, message, s, a): #for robot experiments
        locations = SensingRegion.locationsInVisionCone(self.util, s, a)

        #make null observation for all undetected objects
        multiCellObjectObservation = [["notObj" + str(o) for l in locations] for o in range(len(s.searchObjects))]

        objectIndex = 1
        for i in range(int(message[0])):
            id = message[objectIndex]; objectIndex += 1
            x = message[objectIndex]; objectIndex += 1
            y = message[objectIndex]; objectIndex += 1

            indexLocation = Loc.Location(x,y)

            #inject observation into null observation
            for i in range(len(locations)):
                if locations[i] == indexLocation:
                    obsObj = "Obj" + str(id)
                    multiCellObjectObservation[id][i] = obsObj

        return multiCellObjectObservation

    def sample(self, s, a):
        params = Constants.parseAction(a)  # {name, x, y}
        observation = None

        if params[0] == Constants.ACTION_LOOK:
            observation = self.sampleObservationModel(s, a)
        else:
            observation = self.nullObservation(s, a)

        return observation

    def nullObservation(self, s, a):
        return [Constants.NULL_OBSERVATION]

    def sampleObservationModel(self, s, a):
        multiCellObjectObservation = [] # double list:: objects : locations

        locations = SensingRegion.locationsInVisionCone(self.util, s, a) #locations in sensing region
        for o in range(len(s.searchObjects)):
            objectObservation = []

            #observation types
            obsObj = "Obj" + str(o)
            obsNotObj = "notObj" + str(o)

            currentObject = s.searchObjects[o]
            objectInVisionCone = currentObject in locations

            if s.hasChosen[o]: #null observation if object has been chosen
                objectObservation = [Constants.NULL_OBSERVATION for l in locations]
            else:
                roll = self.observationRn.random()
                if objectInVisionCone:
                    if roll <= self.ObsParams.betaV:
                        objectObservation = self.sampleEventB(locations, objectInVisionCone, obsObj, obsNotObj)
                    elif self.ObsParams.betaV < roll and roll <= self.ObsParams.betaV + self.ObsParams.gammaV:
                        objectObservation = self.sampleEventC(locations, obsNotObj)
                    else:
                        objectObservation = self.sampleEventA(locations, objectInVisionCone, obsObj, obsNotObj, currentObject)
                else:
                    if roll <= self.ObsParams.betaNV:
                        objectObservation = self.sampleEventB(locations, objectInVisionCone, obsObj, obsNotObj)
                    elif self.ObsParams.betaNV < roll and roll <= self.ObsParams.betaNV + self.ObsParams.gammaNV:
                        objectObservation = self.sampleEventC(locations, obsNotObj)
                    else:
                        objectObservation = self.sampleEventA(locations, objectInVisionCone, obsObj, obsNotObj, currentObject)


            multiCellObjectObservation.append(objectObservation)
        return multiCellObjectObservation


    def sampleEventB(self, locations, objectInVisionCone, obsObj, obsNotObj):
        objectObservation = []

        #if not in sensing region: return obs not object
        if not objectInVisionCone:
            objectObservation = [obsNotObj for l in locations]

        #return obs with object 1 location sampled from uniform distribution over locations in sensing region
        else:

            #one object observation
            randIndex = self.observationRn.randrange(len(locations))
            for i,l in enumerate(locations):
                if i == randIndex:
                    objectObservation.append(obsObj)
                else:
                    objectObservation.append(obsNotObj)

        return objectObservation

    def sampleEventC(self, locations, obsNotObj):
        return [obsNotObj for l in locations]

    def sampleEventA(self, locations, objectInVisionCone, obsObj, obsNotObj, currentObject):
        objectObservation = []

        #if not in sensing region: return obs not object
        if not objectInVisionCone:
            objectObservation = [obsNotObj for l in locations]

        #return obs with object 1 location sampled from normal distribution over locations in sensing region
        else:
            distribution = {}
            normalization = 0
            for i in range(len(locations)):
                #normal distribution centered on currentObject's true location
                prob = norm.cdf(-self.util.euclideanDistance(locations[i], currentObject), 0, self.ObsParams.sdvForEventA)
                normalization += prob
                distribution[i] = prob

            #normalize
            for l in distribution:
                distribution[l] /= normalization

            #sample random location
            curSum = 0.
            roll = self.observationRn.random()
            randIndex = -1
            for i in distribution:
                curSum += distribution[i]
                if (roll <= curSum):
                    randIndex = i
                    break

            if randIndex == -1:
                raise Exception("Probabilities don't sum to 1.0: " + str(curSum))

            for i, l in enumerate(locations):
                if i == randIndex:
                    objectObservation.append(obsObj)
                else:
                    objectObservation.append(obsNotObj)

        return objectObservation




