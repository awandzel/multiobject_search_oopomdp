'''
    Created by awandzel on 10/31/18.
'''
from sys import maxsize
import numpy as np
import copy
import time
import math
import Multi_Object_Search.Pomdp.OOState.Location as Loc
import Multi_Object_Search.Core.Environment as env
from simple_rl.tasks import GridWorldMDP
from simple_rl.planning.ValueIterationClass import ValueIteration

#hard problems:
#timing
#VI

class RRT():
    graph = {} #location --> list[locations]

    def __init__(self, rand):
        self.randomSeed = rand

    def euclideanDistance(self, l1, l2):
        return np.linalg.norm(np.array([l1.x,l1.y]) - np.array([l2.x, l2.y]))
        #return np.sqrt(np.power(np.abs(l1.x - l2.x),2) + np.power(np.abs(l1.y - l2.y),2))

    def buildGraph(self, Maps, centerOfRoom, cellsInRoom, visionDepth, numberOfSeconds):
        self.graph[centerOfRoom] = [] #initialize graph with center of room
        distanceBetweenVertices = 2 * visionDepth #look from vertex A & look from vertex B < 2*visionDepth
        numberOfVertices = 2; count = 0

        timeout = time.time() + numberOfSeconds
        while (time.time() < timeout):
        #while (count < numberOfVertices):
            randomConfig = self.sampleRandomConfiguration(cellsInRoom)
            nearestNode = self.nearestVertex(randomConfig)
            kStepConfig = self.kLengthStep(randomConfig, nearestNode, distanceBetweenVertices)
            distanceToKStepNode = self.euclideanDistance(kStepConfig, nearestNode)

            if (not Maps.occupancyMap[kStepConfig.x][kStepConfig.y] == env.WALL
                and not kStepConfig in self.graph
                and not distanceToKStepNode <= visionDepth):

                #if self.planFromAtoB(Maps, nearestNode, kStepConfig): #assuming path planner for obstacle avoidance

                count += 1
                self.graph[nearestNode].append(kStepConfig) #add edge nearestNode --> kStepConfig
                self.graph[kStepConfig] = [nearestNode]  #add back link kStepConfig --> nearestNode

            time.sleep(1/10) #rest for 100 milliseconds
        print(str(count))

    #sample random location in room
    def sampleRandomConfiguration(self, cellsInRoom):
        while True:
            testConfig = cellsInRoom[self.randomSeed.randrange(len(cellsInRoom))]
            if testConfig not in self.graph:
                return testConfig

    #find the closest vertex in the graph to the new random location
    def nearestVertex(self, randomConfig):
        minimum = maxsize
        nearestVertex = None
        for l in self.graph.keys():
            distance = self.euclideanDistance(l, randomConfig)
            if (distance < minimum):
                minimum = distance
                nearestVertex = l
        return nearestVertex

    #restrict the distance of the nearestNode and randomConfig to k
    def kLengthStep(self, randomConfig, nearestNode, k):
        if (self.euclideanDistance(randomConfig, nearestNode) < k):
            return randomConfig
        else:
            theta = math.atan2(randomConfig.y - nearestNode.y, randomConfig.x - nearestNode.x)
            x = nearestNode.x + round(k * math.cos(theta))
            y = nearestNode.y + round(k * math.sin(theta))
            return Loc.Location(x,y)

    #run VI to determine if path is free from obstructions if so return true
    def planFromAtoB(self, Maps, nearestVertex, kStepConfig):

        # if not self.computedMDP:
        #     self.wallLocations = []
        #     for x in range(len(self.Maps.occupancyMap)):
        #         for y in range(len(self.Maps.occupancyMap[x])):
        #             if self.Maps.occupancyMap[x][y] == env.WALL:
        #                 self.wallLocations.append(Loc.Location(x,y))
        #     self.computedMDP = True

        mdp = GridWorldMDP(width=len(Maps.occupancyMap), height=len(Maps.occupancyMap[0]),
                           init_loc=(nearestVertex.x, nearestVertex.y), goal_locs=[(kStepConfig.x, kStepConfig.y)],
                           gamma=0.95, walls=self.wallLocations)
        vi = ValueIteration(mdp)
        vi.run_vi()
        action_seq, state_seq = vi.plan()

        #check if conflict
        for s in state_seq:
            if Maps.occupancyMap[s[0], s[1]] == env.WALL:
                return False
        return True

    def debugPrint(self, Maps):
        tempMap = copy.deepcopy(Maps.occupancyMap)
        for n in self.graph:
            tempMap[n.x][n.y] = np.nan
        print("\nRRT Nodes in Map: \n", np.matrix(env.rotateToLeft(tempMap)))












