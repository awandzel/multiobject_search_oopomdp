'''
    Created by awandzel on 03/05/18.
'''

class OOState:

    def __init__(self, agent, searchObjects, hasChosen):
        self.agent = agent
        self.searchObjects = searchObjects
        self.hasChosen = hasChosen

    def searchObjectsHash(self):
        hash = 0
        for i in range(len(self.searchObjects)):
            hash += 37*self.searchObjects[i].__hash__()
        return hash

    def hasChosenHash(self):
        hash = 0
        for i in range(len(self.hasChosen)):
            hash += 37 * self.hasChosen[i]
        return hash

    def __hash__(self):
        return 37 * self.agent.__hash__() + self.searchObjectsHash() + self.hasChosenHash()

    def searchObjectsEquals(self, other):
        for i in range(len(self.searchObjects)):
            if not self.searchObjects.__eq__(other):
                return False
        return True

    def hasChosenEquals(self, other):
        for i in range(len(self.hasChosen)):
            if self.hasChosen[i] != other.hasChosen[i]:
                return False
        return True

    def __eq__(self, other):
        if other == None or self.__class__ != other.__class__: return False
        if not isinstance(other, OOState): return False

        a = self.agent.__eq__(other.agent)
        b = self.searchObjectsEquals(other)
        c = self.hasChosenEquals(other)

        return self.agent.__eq__(other.agent) and self.searchObjectsEquals(other) and self.hasChosenEquals(other)

    def searchObjectsToString(self):
        string = ""
        for i in range(len(self.searchObjects)):
            string += "(" + self.searchObjects[i].__str__() + ")"
        return string

    def hasChosenToString(self):
        string = ""
        for i in range(len(self.hasChosen)):
            string += "(True)" if self.hasChosen[i] else "(False)"
        return string

    def __str__(self):
        string = "{agent=" + self.agent.__str__()  + ", searchObjects=" + self.searchObjectsToString() \
                 + ", hasChosen=" + self.hasChosenToString() + "}"
        return string


