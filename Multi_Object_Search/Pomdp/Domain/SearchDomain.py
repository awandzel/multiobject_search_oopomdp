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
        pass



