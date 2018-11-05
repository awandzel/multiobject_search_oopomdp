'''
    Created by awandzel on 03/04/18.
'''
import Multi_Object_Search.Pomdp.Domain.ParameterizedActions as Action

class SearchDomain:
    #Domain general data members
    ActionTypes = [] #list of parameterized actions
    FactoredModel = None

    def __init__(self, util, PomdpParameters, ObservationModelParameters, observationRn):
        self.util = util
        self.PomdpParameters = PomdpParameters
        self.ObservationModelParameters = ObservationModelParameters
        self.observationRn = observationRn

    def generateDomain(self):
        self.ActionTypes.append(Action.MoveRoom(self.util, "MoveRoom"))
        self.ActionTypes.append(Action.Move(self.util, "Move"))

    def applicableActions(self, s):
        applicableActions = []
        for action in self.ActionTypes:
            applicableActions += action.applicableActions(s)
        return applicableActions







