'''
    Created by awandzel on 11/12/18.
'''

class Data:

    def __init__(self, episode, elapsedTime, PomdpParameters):
        self.episode = episode
        self.elapsedTime = elapsedTime
        self.numberOfObjectsFound = self.calculateNumberOfObjectsFound(PomdpParameters, episode.rewardSequence)

    def printData(self):
        print("Program End!")
        print("Time: " + str(self.elapsedTime))
        print("Reward: " + str(sum(self.episode.rewardSequence)))
        print("Actions: " + str(len(self.episode.actionSequence)))
        print("Objects Found: " + self.numberOfObjectsFound)

    def calculateNumberOfObjectsFound(self, PomdpParameters, rewardSeqence):
        numberOfObjectsFound = 0
        for r in rewardSeqence:
            if r == PomdpParameters.objectReward: numberOfObjectsFound += 1
            if r == -PomdpParameters.objectReward: numberOfObjectsFound -= 1

        return numberOfObjectsFound