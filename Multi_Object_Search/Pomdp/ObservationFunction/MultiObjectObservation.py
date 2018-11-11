'''
    Created by awandzel on 11/11/18.
'''

class MultiObjectObservation:

    def __init__(self, observation):
        self.observation = observation

    def __hash__(self):
        hashCode = 0
        for o in self.observation:
            for l in o:
                hashCode += hash(l)
        return hashCode

    def __eq__(self, other):
        if other == None or self.__class__ != other.__class__: return False
        if not isinstance(other, MultiObjectObservation): return False

        for i in range(len(self.observation)):
            for j in range(len(self.observation[i])):
                if self.observation[i][j] != other.observation[i][j]:
                    return False

        return True

    def __str__(self):
        string = ""
        for object in self.observation:
            string += str(object)
        return string

