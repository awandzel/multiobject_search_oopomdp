'''
    Created by awandzel on 03/06/18.
'''

DELIMITER = "_"
ACTION_MOVE = "Move"
ACTION_MOVE_ROOM = "MoveRoom"
ACTION_LOOK = "Look"
ACTION_FIND = "Find"

NULL_OBSERVATION = "doNotCare"


#domain general function
def parseAction(action):
    return action.split(DELIMITER) #assumes actions are specified as "Parameter_Parameter_..."