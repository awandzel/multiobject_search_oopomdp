'''
    Created by awandzel on 03/06/18.
'''

import Multi_Object_Search.Pomdp.OOState.Location as Loc
import Multi_Object_Search.Pomdp.Domain.StaticConstants as Constants


def locationsInVisionBoundingBox(util, s, a=None):
    sensingRegion = []
    visionDepth = util.RRT.visionDepth

    #bounding box around agent
    for x in range(s.agent.x - visionDepth, s.agent.x + visionDepth+1):
        for y in range(s.agent.y - visionDepth, s.agent.y + visionDepth+1):
            if util.checkInBounds(x, y):
                sensingRegion.append(Loc.Location(x, y))

    return sensingRegion


def locationsInVisionCone(util, s, a):
    sensingRegion = []
    params = Constants.parseAction(a)
    lookDirection = int(params[1])
    visionDepth = util.RRT.visionDepth

    # add current position
    sensingRegion.append(Loc.Location(s.agent.x, s.agent.y))

    # move one step forward
    l = util.moveInDirection(s.agent.x, s.agent.y, lookDirection)

    if lookDirection == 0:  # north
        leftRange = rightRange = s.agent.x #sensor width
        for y in range(l.y, l.y + visionDepth): #direction

            #expand sensor width each iteration
            leftRange -= 1
            rightRange += 1

            #add locations to sensing region
            for x in range(leftRange, rightRange+1):
                if util.checkInBounds(x, y): #check if wall / out of map
                    sensingRegion.append(Loc.Location(x, y))

    elif lookDirection == 1:  # south
        leftRange = rightRange = s.agent.x
        for y in range(l.y, l.y - visionDepth, -1):
            leftRange -= 1
            rightRange += 1
            for x in range(leftRange, rightRange+1):
                if util.checkInBounds(x, y):
                    sensingRegion.append(Loc.Location(x, y))

    elif lookDirection == 2:  # east
        leftRange = rightRange = s.agent.y
        for x in range(l.x, l.x + visionDepth):
            leftRange -= 1
            rightRange += 1
            for y in range(leftRange, rightRange+1):
                if util.checkInBounds(x, y):
                    sensingRegion.append(Loc.Location(x, y))

    else:  # west
        leftRange = rightRange = s.agent.y
        for x in range(l.x, l.x - visionDepth, -1):
            leftRange -= 1
            rightRange += 1
            for y in range(leftRange, rightRange+1):
                if util.checkInBounds(x, y):
                    sensingRegion.append(Loc.Location(x, y))

    return sensingRegion