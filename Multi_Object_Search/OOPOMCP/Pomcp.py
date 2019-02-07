'''
    Created by awandzel on 11/11/18.
'''
import math
import Multi_Object_Search.Pomdp.Domain.StaticConstants as Constants

class Pomcp:

    def __init__(self, domain, PomcpParameters, belief, POMCPRn):
        self.PARAMETERS = PomcpParameters
        self.RANDOM = POMCPRn
        self.DOMAIN = domain
        self.POMCP_DEPTH = 0
        self.MAX_DEPTH_REACHED = 0
        self.belief = belief #may not need this

    def initializePomcp(self, initialState, beliefEstimate):
        root = VNode(None,None)
        root.initializeVNode(initialState, self.DOMAIN)
        root.beliefEstimate = beliefEstimate
        return root

    def selectAction(self, root):
        for i in range(self.PARAMETERS.maxSamples):
            self.POMCP_DEPTH = 0
            particle = self.RANDOM.choice(root.beliefEstimate)
            self.simulateV(root, particle)
        # print(self.MAX_DEPTH_REACHED)
        return self.greedyUCB(root, False)

    def beliefUpdate(self, root, action, observation):
        qNode = root.children[action]
        vNode = qNode.get(observation)

        if vNode == None:
            qNode.children[observation] = vNode.vNode(action, observation)
            vNode = qNode.children[observation]

        if len(vNode.beliefEstimate):
            particle = self.RANDOM.choice(vNode.beliefEstimate)
        else:
            return "particleDepletion"

        if self.PARAMETERS.pomcpReinvigorateSamples > 0:
            self.particleReinvigoration(root, action, observation)

        return self.initializePomcp(particle, vNode.beliefEstimate)


    def particleReinvigoration(self, root, action, observation):
        added = 0

        #add extra particles
        while (added < self.PARAMETERS.pomcpReinvigorateSamples):

            #run model to get possible next state (consistent with real action)
            particle = self.RANDOM.choice(root.beliefEstimate)
            outcome = self.sampleGenerativeModel(particle, action)
            transform = root.beliefEstimate.sampleState(outcome)

            # ensures that transformation is consistent with last real observation
            params = Constants.parseAction(action)
            if params[0] == Constants.ACTION_LOOK:
                nextObservation = self.DOMAIN.ObservationModel.sample(transform, action)
                if observation == nextObservation:
                    root.beliefEstimate.append(transform)




    def greedyUCB(self, vNode, ucb):

        qValues = []
        maxQ = -float("inf")
        for qn in vNode.children.values():
            cumulativeReward = sum(qn.rewardEstimate)
            qvalue = cumulativeReward / len(qn.rewardEstimate) if len(qn.rewardEstimate) else 0

            if ucb:
                if len(qn.rewardEstimate):
                    qvalue += self.PARAMETERS.pomcpExploration * \
                              math.sqrt(
                        math.log(len(vNode.rewardEstimate) + 1) / len(qn.rewardEstimate))
                else:
                    qvalue = float("inf")

            if qvalue > maxQ:
                maxQ = qvalue
                action = qn.nextAction
            qValues.append(qvalue)

        return action


    def simulateV(self, vNode, state):
        if self.POMCP_DEPTH >= self.PARAMETERS.pomcpDepth:
            return 0.0

        action = self.greedyUCB(vNode, True)

        if self.POMCP_DEPTH == 1:
            vNode.beliefEstimate.append(state)

        qNode = vNode.children[action]
        qValue = self.simulateQ(qNode, state, action)
        vNode.rewardEstimate.append(qValue)

        return qValue

    def simulateQ(self, qNode, state, action):

        [s_, r, terminated, o] = self.sampleGenerativeModel(state, action)

        vNode = qNode.children.get(o)
        if vNode == None:#sampling o from Z : pseudo initialize vNode
            qNode.children[o] = vNode.vNode(action, o)
            vNode = qNode.children[o]

        #initialize vNode if at least one rollout for qNode reward
        if not terminated and not vNode.children and qNode.rewardEstimate >= 1:
            qNode.children[o].initializeVNode(s_, self.DOMAIN)

        delayedReward = 0
        global POMCP_DEPTH, MAX_DEPTH_REACHED
        if not terminated:
            POMCP_DEPTH += 1
            if POMCP_DEPTH > MAX_DEPTH_REACHED: MAX_DEPTH_REACHED = POMCP_DEPTH  # debug code

            if vNode.children:
                delayedReward = self.simulateV(vNode, state)
            else:
                delayedReward = self.rollout(state, 0, 0, 1.0, False)
            POMCP_DEPTH -= 1

        rewardTotal = round(r + self.PARAMETERS.pomcpDiscount * delayedReward, 3)
        qNode.rewardEstimate.append(rewardTotal)
        return rewardTotal

    def rollout(self, state, reward, depth, discount, terminated):
        if (depth >= self.PARAMETERS.pomcpDepth or terminated):
            return reward

        action = self.DOMAIN.selectRandomAction(state, self.RANDOM)
        [s_, r, terminated, o] = self.sampleGenerativeModel(state, action)

        reward += r * discount
        # discount = round(PARAMETERS.pomcpDiscount * discount, 3)
        depth += 1
        return self.rollout(state, reward, depth, discount, terminated) # modify so BFS over DFS

    def sampleGenerativeModel(self, state, action):
        s_ = self.DOMAIN.FactoredModel.TransitionFunction.sample(state, action)
        r = self.DOMAIN.FactoredModel.RewardFunction.reward(state, action, s_)
        t = self.DOMAIN.FactoredModel.TerminationFunction.isTerminal(s_)
        o = self.DOMAIN.ObservationModel.sample(s_, action)

        return [s_, r, t, o]

class VNode:

    def __init__(self, action, observation):
        self.action = action
        self.observation = observation
        self.rewardEstimate = []
        self.beliefEstimate = []
        self.children = {}

    def initializeVNode(self, state, domain):
        for action in domain.applicableActions(state):
            qNode = QNode(action)
            self.children[action] = qNode

class QNode():

    def __init__(self, action):
        self.nextAction = action
        self.rewardEstimate = []
        self.children = {}












