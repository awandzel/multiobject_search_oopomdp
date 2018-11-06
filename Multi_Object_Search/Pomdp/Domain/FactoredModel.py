'''
    Created by awandzel on 03/04/18.
'''
import Multi_Object_Search.Pomdp.Domain.StaticConstants as Constants
import Multi_Object_Search.Pomdp.OOState.Location as Loc
import Multi_Object_Search.Pomdp.OOState.OOState as State
import random
import copy

class FactoredModel:

    def __init__(self, util, PomdpParameters):
        self.reward_function = self.RewardFunction(util, PomdpParameters)
        self.TerminationFunction = self.TerminationFunction()
        self.TransitionFunction = self.TransitionFunction()

    class RewardFunction:

        def __init__(self, util, PomdpParameters):
            self.util = util
            self.PomdpParameters = PomdpParameters

        def reward(self, s, a, s_=None):
            params = Constants.parseAction(a) #{name, x, y}

            #Find action
            if params[0] == Constants.ACTION_FIND:
                findLocation = Loc.Location(params[1], params[2])

                #iterate over all objects to check if findLocation corresponds to object location
                for i in range(len(s.searchObjects)):
                    a = (findLocation == s.searchObjects[i])
                    b = (not s.hasChosen[i])
                    if findLocation == s.searchObjects[i] and not s.hasChosen[i]:
                        return self.PomdpParameters.objectReward
                return -self.PomdpParameters.objectReward

            #Move actions
            elif params[0] == Constants.ACTION_MOVE or params[0] == Constants.ACTION_MOVE_ROOM:
                moveLocation = Loc.Location(params[1], params[2])
                return self.PomdpParameters.actionCost + -self.util.euclideanDistance(s.agent, moveLocation)

            #Look action
            else:
                return self.PomdpParameters.actionCost

    class TerminationFunction:

        def isTerminal(self, s):
            for i in range(len(s.hasChosen)):
                if (not s.hasChosen(i)):
                    return False
            return True

    class TransitionFunction:

        def sample(self, s, a, deterministic=True):
            stateTransitions = self.stateTransitions(s, a)

            if deterministic:
                return next(iter(stateTransitions))
            else:
                curSum = 0.
                roll = random.random()
                for s_ in stateTransitions:
                    curSum += stateTransitions[s_]
                    if (roll <= curSum):
                        return s_
                raise Exception("Probabilities don't sum to 1.0: " + str(curSum))


        def stateTransitions(self, s, a):
            params = Constants.parseAction(a)  # {name, x, y}
            scopy = copy.deepcopy(s) #copy new state

            if params[0] == Constants.ACTION_FIND:
                findLocation = Loc.Location(params[1], params[2])

                #if at correct location then modify at index
                for i in range(len(s.searchObjects)):
                    if findLocation == s.searchObjects[i] and not s.hasChosen[i]:
                        scopy.hasChosen[i] = True
                        return {scopy : 1.0}

                #if at incorrect location then modify first false index
                for i in range(len(scopy.hasChosen)):
                    if not s.hasChosen[i]:
                        scopy.hasChosen[i] = True
                        return {scopy: 1.0}

            elif params[0] == Constants.ACTION_MOVE or params[0] == Constants.ACTION_MOVE_ROOM:
                scopy.agent.x = int(params[1])
                scopy.agent.y = int(params[2])
                return {scopy: 1.0}
                #State.OOState(Loc.Location(params[1], params[2]), scopy.searchObjects, scopy.hasChosen)

            elif params[0] == Constants.ACTION_LOOK:
                return {scopy: 1.0}

            else:
                raise Exception("Error: no existing action named " + params[0])

