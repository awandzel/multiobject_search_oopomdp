'''
    Created by awandzel on 11/11/18.
'''


class Environment:
    def __init__(self, domain, environmentState, debugPrint, discountFactor=.95):
        self.domain = domain
        self.currentState = environmentState
        self.debugPrint = debugPrint
        self.discountFactor = discountFactor
        self.discount = 1.0
        self.episode = self.Episode(environmentState)

    def executeAction(self, a):
        pass

    class Episode:
        def __init__(self, s):
            self.stateSequence = [s]
            self.observationSequence = []
            self.actionSequence = []
            self.rewardSequence = []

        def transition(self, s_, o, a, r):
            self.stateSequence.append(s_)
            self.observationSequence.append(o)
            self.actionSequence.append(a)
            self.rewardSequence.append(r)

class SimulatedEnvironment(Environment):

    def executeAction(self, a):
        s_ = self.domain.FactoredModel.TransitionFunction.sample(self.currentState, a)
        r = self.domain.FactoredModel.RewardFunction.reward(self.currentState, a, s_)
        t = self.domain.FactoredModel.TerminationFunction.isTerminal(s_)
        o = self.domain.ObservationModel.sample(s_, a)

        discountedReward = r * self.discount
        self.discount = round(self.discount * self.discountFactor, 3)

        if (self.debugPrint > 0):
            print("Action: " + a)
            print("Observation: " + self.domain.ObservationModel.toString())
            print("State: " + s_)
            print("Reward: " + r)
            print("Discounted Reward: " + discountedReward)
        else:
            print(".", end="")

        return [s_, o, a, r, t]

class RealEnvironment(Environment):

    def executeAction(self, a):
        pass

