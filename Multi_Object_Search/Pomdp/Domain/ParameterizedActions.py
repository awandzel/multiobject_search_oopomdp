'''
    Created by awandzel on 03/04/18.
'''
class ParameterizedActions:
        def __init__(self, type):
            self.type = type

        def applicableActions(self):
            pass

        def returnName(self):
            print("My name is " + self.type)

class A(ParameterizedActions):

    def applicableActions(self):
        print("HelloA!")

class B(ParameterizedActions):

    def applicableActions(self):
        print("HelloB!")


