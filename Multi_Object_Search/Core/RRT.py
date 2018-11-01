'''
    Created by awandzel on 10/31/18.
'''
from sys import maxsize

#hard problems:
#timing
#VI

class RRT():
    graph = {} #location --> list[locations]

    def __init__(self, Maps, rand):
        self.Maps = Maps
        self.randomSeed = rand

    def euclideanDistance(self, l1, l2):
        #    return Math.sqrt(Math.pow(Math.abs(compare1.x - compare2.x), 2) + Math.pow(Math.abs(compare1.y - compare2.y), 2));

    def buildGraph(self, startLocation, cellsInRoom, visionDepth, numberOfSeconds):
        randomConfig = self.sampleRandomConfiguration(cellsInRoom)
        nearestNode = self.nearestVertex(randomConfig)

    def sampleRandomConfiguration(self, cellsInRoom):
        while True:
            testConfig = cellsInRoom[self.randomSeed.randrange(len(cellsInRoom))]
            if testConfig not in self.graph:
                return testConfig

    def nearestVertex(self, randomConfig):
        minimum = -maxsize
        for l in self.graph.keys():
            distance =

