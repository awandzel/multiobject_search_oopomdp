'''
    Created by awandzel on 03/04/18.
'''
import Multi_Object_Search.Pomdp.OOState.Location as Loc
import Multi_Object_Search.Pomdp.Domain.StaticConstants as Constants
import Multi_Object_Search.Pomdp.ObservationFunction.SensingRegion as SensingRegion


class ParameterizedActions:
        def __init__(self, util, type):
            self.util = util
            self.type = type

        def applicableActions(self, s):
            pass #overriden by action classes

        def returnType(self):
            return self.type

class Find(ParameterizedActions):

    def applicableActions(self, s):
        actions = []

        #calculate sensing region around agent
        sensingRegion = SensingRegion.locationsInVisionBoundingBox(self.util, s)
        for l in sensingRegion:

            #at least one object must be present to be applicable for find action given state (note: does not violate POMDP)
            detect = None
            for o in range(len(s.searchObjects)):
                if l == s.searchObjects[o] and not s.hasChosen[o]:
                    actions.append(Constants.ACTION_FIND + "_" + str(l.x) + "_" + str(l.y))
                    break

        return actions

class Move(ParameterizedActions):

    def applicableActions(self, s):
        actions = []
        currentLocation = Loc.Location(s.agent.x, s.agent.y)

        #calculate all connected locations in RRT graph
        if currentLocation in self.util.RRT.graph:
            transitionLocations = self.util.RRT.graph[currentLocation]
            for l in transitionLocations:
                actions.append(Constants.ACTION_MOVE + "_" + str(l.x) + "_" + str(l.y))
        return actions

class MoveRoom(ParameterizedActions):

    def applicableActions(self, s):
        actions = []
        currentLocation = Loc.Location(s.agent.x, s.agent.y)
        currentRoom = self.util.Rooms.agentToRoomMapping[currentLocation]
        connectedRooms = self.util.Rooms.connectedRooms(currentRoom)

        #calcualte all adjacent rooms for transition
        for r in connectedRooms:
            l = self.util.Rooms.transitionMatrix[r]
            actions.append(Constants.ACTION_MOVE_ROOM + "_" + str(l.x) + "_" + str(l.y) + "_" + str(r))
        return actions

class Look(ParameterizedActions):

    def applicableActions(self, s):
        actions = []
        for d in range(0, 4):
            forwardDirection = self.util.moveInDirection(s.agent.x, s.agent.y, d)

            #must be at least one empty location in look direction
            if self.util.checkInBounds(forwardDirection.x, forwardDirection.y):
                actions.append(Constants.ACTION_LOOK + "_" + str(d))
        return actions


