package searchWorld.src.State;

import burlap.domain.singleagent.lunarlander.LLVisualizer;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.annotations.DeepCopyState;

import java.util.Arrays;
import java.util.List;

import static searchWorld.src.SearchDomain.*;

@DeepCopyState
public class SearchAgent implements ObjectInstance {

	// The cartesian coordinates of the SearchAgent
	public int x, y, theta;

	private final static List<Object> keys = Arrays.asList(VAR_AGENT_X, VAR_AGENT_Y, VAR_AGENT_THETA);

	/**
	 * Constructor for SearchAgent
	 *
	 * @param x - The x-coordinate of our SearchAgent
	 * @param y - The y-coordinate of our SearchAgent
	 */
	public SearchAgent(int x, int y, int t) {
		this.x = x;
		this.y = y;
		this.theta = t;
	}

	@Override
	public String className() {
		return CLASS_AGENT;
	}

	@Override
	public String name() {
		return CLASS_AGENT;
	}

	@Override
	public SearchAgent copyWithName(String objectName) {
		return this.copy();
	}

	@Override
	public List<Object> variableKeys() {
		return keys;
	}

	@Override
	public Object get(Object variableKey) {
		if (!(variableKey instanceof String)) {
			throw new RuntimeException("Key must be a string");
		}

		String key = (String) variableKey;
		if (key.equals(VAR_AGENT_X)) {
			return x;
		} else if (key.equals(VAR_AGENT_Y)) {
			return y;
		} else if (key.equals(VAR_AGENT_THETA)) {
			return theta;
		}

		throw new RuntimeException("Unknown key " + key);
	}

	@Override
	public SearchAgent copy() {
		return new SearchAgent(this.x, this.y, this.theta);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!(o instanceof SearchAgent)) return false;
		SearchAgent that = (SearchAgent) o;
		return (this.x == that.x && this.y == that.y && this.theta == that.theta);
	}

	@Override
	public int hashCode() {
		int result = x;
		result = 37 * result + y;
		result = 37 * result + theta;
		return result;
	}

	@Override
	public String toString() {
		String direction = "";
		if (this.theta == 0) {
			direction = "north";
		} else if (this.theta == 1) {
			direction = "south";
		} else if (this.theta == 2) {
			direction = "east";
		} else if (this.theta == 3) {
			direction = "west";
		}

		return "{t=" + theta + ", x=" + x + ", y=" + y +'}';
	}
}
