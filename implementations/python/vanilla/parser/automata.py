# Automata.py

class DFA:

    def __init__(self, states, accepts, start = 0):
        self.states = states
        self.accepts = accepts
        self.start = start

    def recognize (self, inVec, pos = 0): # greedy = True
        crntState = self.start
        lastAccept = False
        i = pos
        for i in range(pos, len(inVec)):
            item = inVec[i]
            # arcMap, accept = self.states[crntState]
            arcMap = self.states[crntState]
            accept = self.accepts[crntState]
            if item in arcMap:
                crntState = arcMap[item]
            elif DEFAULT in arcMap:
                crntState = arcMap[DEFAULT]
            elif accept:
                return i
            elif lastAccept:
                # This is now needed b/c of exception cases where there are
                # transitions to dead states
                return i - 1
            else:
                return -1
            lastAccept = accept
        # if self.states[crntState][1]:
        if self.accepts[crntState]:
            return i + 1
        elif lastAccept:
            return i
        else:
            return -1


class NonGreedyDFA (DFA):
    def recognize (self, inVec, pos = 0):
        crntState = self.start
        i = pos
        for item in inVec[pos:]:
            # arcMap, accept = self.states[crntState]
            arcMap = self.states[crntState]
            accept = self.accepts[crntState]
            if accept:
                return i
            elif item in arcMap:
                crntState = arcMap[item]
            elif DEFAULT in arcMap:
                crntState = arcMap[DEFAULT]
            else:
                return -1
            i += 1
        # if self.states[crntState][1]:
        if self.accepts[crntState]:
            return i
        else:
            return -1

