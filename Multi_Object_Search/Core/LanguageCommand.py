'''
    Created by awandzel on 11/01/18.
'''
import copy


class LanguageCommand:
    languageToRoom = {}
    languageToClass = {}
    classToRooms = {}
    languageObservation = {}

    def __init__(self, Rooms, objectClasses):
        self.Rooms = Rooms
        self.objectClasses = objectClasses

    def setVocabularyforClass(self):
        self.languageToClass[("mug", "mugs")] = 0
        self.languageToClass[("book", "books")] = 1
        self.languageToClass[("key", "keys")] = 2
        self.languageToClass[("remote", "remotes")] = 3
        self.languageToClass[("person", "persons")] = 4

    def setVocabularyForRooms(self):
        self.languageToRoom["library"] = 0
        self.languageToRoom["kitchen"] = 1
        self.languageToRoom["storage"] = 2
        self.languageToRoom["livingroom"] = 3
        self.languageToRoom["robotics"] = 4
        self.languageToRoom["classroomA"] = 5
        self.languageToRoom["classroomB"] = 6
        self.languageToRoom["classroomC"] = 7
        self.languageToRoom["classroomD"] = 8

    # Language command parsing structure:
    # for c classes : extract sequential pair (class, <rooms>)
    def parseLanguageCommand(self, command, randomRoom = None):
        self.setVocabularyforClass()
        self.setVocabularyForRooms()

        #initialize mapping from class to room to []
        for c in range(len(self.objectClasses)):
            self.classToRooms[c] = []

        # e.g. mug in <room1> or <room2> and book in <room1>
        currentClass = ""
        words = command.split()
        for w in words:

            for c in self.languageToClass:
                if  w in c:
                    currentClass = self.languageToClass[c]
                    if self.languageToClass[c] >= len(self.objectClasses): raise Exception("Error: referenced class exceeds number of object classes")

            if w in self.languageToRoom:
                # Randomize room for simulation
                if randomRoom is not None:
                    self.classToRooms[currentClass].append(randomRoom.randrange(self.Rooms.numberOfRooms))
                else:
                    self.classToRooms[currentClass].append(self.languageToRoom[w])
                if self.languageToRoom[w] >= self.Rooms.numberOfRooms: raise Exception("Error: referenced room exceeds number of rooms")

        #for not mentioned classes assume all rooms (no information)
        for c in self.classToRooms:
            if not self.classToRooms[c]:
                self.classToRooms[c] = [i for i in range(self.Rooms.numberOfRooms)]


    def translateToObservation(self, psi):
        numberOfObjects = 0

        #iterate over all referenced classes
        for c in self.classToRooms:
            normRefRooms, normNotRefRooms = self.normalizationConstant(self.classToRooms[c])
            objObservation = self.objectLanguageObservation(self.classToRooms[c], psi, normRefRooms, normNotRefRooms)

            #ground to object i
            for o in range(self.objectClasses[c]):
                self.languageObservation[numberOfObjects] = copy.deepcopy(objObservation)
                numberOfObjects += 1

    #for each set of referenced rooms count number of locations in each
    def normalizationConstant(self, referencedRooms):
        normRefRooms = normNotRefRooms = 0
        for r in range(self.Rooms.numberOfRooms):
            if r in referencedRooms:
                normRefRooms += len(self.Rooms.roomToLocationsMapping[r])
            else:
                normNotRefRooms += len(self.Rooms.roomToLocationsMapping[r])
        return normRefRooms, normNotRefRooms


    # for each room: if referenced: 1.0 - psi / #roomsReferenced, else: psi / #roomsNotReferenced
    def objectLanguageObservation(self, referencedRooms, psi, normRefRooms, normNotRefRooms):
        objObservation = {}
        for r in range(self.Rooms.numberOfRooms):
            for l in self.Rooms.roomToLocationsMapping[r]:
                #only translate into observation if informative (i.e. not uniform)
                if len(referencedRooms) < self.Rooms.numberOfRooms:
                    prob = (1.0 - psi) / normRefRooms if r in referencedRooms else psi / normNotRefRooms
                else:
                    prob = 1.0
                objObservation[l] = prob
        return objObservation

    #iterate over each object / location and perform a pairwise multiplication w/ languageObservation
    def beliefUpdate(self, beliefPrior):
        updatedBelief = {}

        for o in range(len(beliefPrior)):
            updatedObjBelief = {}
            normalization = 0.0

            for l in beliefPrior[o]:
                prob = beliefPrior[o][l] * self.languageObservation[o][l] #pairwise multiplication
                normalization += prob
                updatedObjBelief[l] = prob

            for l in updatedObjBelief: updatedObjBelief[l] /= normalization
            updatedBelief[o] = updatedObjBelief
        return updatedBelief
