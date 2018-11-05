'''
    Created by awandzel on 03/04/18.
'''
import Multi_Object_Search.Pomdp.OOState.Location as Loc

class ParameterizedActions:
        def __init__(self, util, type):
            self.util = util
            self.type = type

        def applicableActions(self, s):
            pass

        def returnType(self):
            return self.type

class Move(ParameterizedActions):

    def applicableActions(self, s):
        actions = []
        currentLocation = Loc.Location(s.agent.x, s.agent.y)
        if currentLocation in self.util.RRT.graph:
            transitionLocations = self.util.RRT.graph[currentLocation]
            for l in transitionLocations:
                actions.append("Move_" + str(l.x) + "_" + str(l.y))
        return actions

class MoveRoom(ParameterizedActions):

    def applicableActions(self, s):
        actions = []
        currentLocation = Loc.Location(s.agent.x, s.agent.y)
        currentRoom = self.util.Rooms.agentToRoomMapping[currentLocation]
        connectedRooms = self.util.Rooms.connectedRooms(currentRoom)
        for r in connectedRooms:
            l = self.util.Rooms.transitionMatrix[r]
            actions.append("MoveRoom" + str(r) + "_" + str(l.x) + "_" + str(l.y))
        return actions


