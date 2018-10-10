package searchWorld.src.Model;

import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;
import searchWorld.src.State.SearchObject;
import searchWorld.src.State.SearchState;

/**
 * The terminal function class for the Search Domain
 */
public class SearchTF implements TerminalFunction {

    @Override
    public boolean isTerminal(State s) {
        // If the agent has picked all objects then terminate
        SearchState ns = ((SearchState) s).copy();
        for (Boolean b : ns.hasChosen) {
            if (!b)  return false;
        }
        return true;
    }
}
