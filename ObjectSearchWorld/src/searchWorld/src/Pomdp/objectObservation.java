package searchWorld.src.Pomdp;

import burlap.mdp.core.state.State;
import burlap.mdp.core.state.UnknownKeyException;
import burlap.statehashing.HashableState;

import java.util.Collections;
import java.util.List;

import static searchWorld.src.SearchDomain.*;

/**
 * Search Observation class
 */
public class objectObservation implements State, HashableState {
  public String objectPresent;
  
  /**
   * Constructor for objectObservation
   * @param s - A string.  "object1", ... "objectN", "notObject".
   */
  public objectObservation(String s) {
	this.objectPresent = s;
  }

  private final static List<Object> keys = Collections.singletonList(VAR_OBSERVATION);

  @Override
  public List<Object> variableKeys() {
	return keys;
  }

  public String name(){
    return objectPresent;
  }
  
  @Override
  public Object get(Object o) {
	if (o.equals(VAR_OBSERVATION)) {
	  return objectPresent;
	}
	throw new UnknownKeyException(o);
  }
  
  @Override
  public State copy() {
    String newObjectPresent = this.objectPresent;
    return new objectObservation(newObjectPresent);
  }
  
  @Override
  public String toString() {
	return "{objectPresent='" + objectPresent + "\'}";
  }

  @Override
  public State s() {
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true; // If they have the same address in memory
    if (o == null || getClass() != o.getClass()) return false;
    if (!(o instanceof objectObservation)) return false;

    objectObservation that = (objectObservation) o;
    return (this.objectPresent.equals(that.objectPresent));
  }

  @Override
  public int hashCode() {
    int result = 7;
    for (int i = 0; i < this.objectPresent.length(); i++) {
      result = result * 31 + objectPresent.charAt(i);
    }
    return result;
  }
}
