package searchWorld.src.State;

import burlap.mdp.core.oo.state.ObjectInstance;

import java.util.Arrays;
import java.util.List;

import static searchWorld.src.SearchDomain.*;

/**
 * A class for the object we want our agent to find.
 */
public class SearchObject implements ObjectInstance {
  
  // The cartesian coordinates
  public int x, y;
  public String name;

  private final static List<Object> keys = Arrays.asList(VAR_OBJECT_X, VAR_OBJECT_Y);
  
  /**
   * Constructor for SearchObject
   * @param x - The x-coordinate of our object
   * @param y - The y-coordinate of our object
   */
  public SearchObject(int x, int y, String n) {
	this.x = x;
	this.y = y;
	this.name = n;
  }
  
  @Override
  public String className() {
	return CLASS_OBJECTS;
  }
  
  @Override
  public String name() {
	return name;
  } //object:1
  
  @Override
  public ObjectInstance copyWithName(String newName) {
    return new SearchObject(x, y, newName);
  }
  
  @Override
  public List<Object> variableKeys() {
	return keys;
  }
  
  @Override
  public Object get(Object variableKey) {
	if (!(variableKey instanceof String)) {
	  throw new RuntimeException("Variable key must be a String.\n");
	}
	
	String key = (String) variableKey;
	if (key.equals(VAR_OBJECT_X)) {
	  return this.x;
	}
	else if (key.equals(VAR_OBJECT_Y)) {
	  return this.y;
	}
	
	throw new RuntimeException("No variable key named: " + key + '\n');
  }
  
  @Override public SearchObject copy() {
	return new SearchObject(this.x, this.y, this.name);
  }
  
  @Override
  public boolean equals(Object o) {
	if (this == o) return true;
	if (o == null || getClass() != o.getClass()) return false;
	if (!(o instanceof SearchObject)) return false;
	SearchObject that = (SearchObject) o;
	return (this.x == that.x && this.y == that.y && this.name.equals(that.name));
  }
  
  @Override
  public int hashCode() {
	int result = y;
	result = 37 * result + x;
	result = 37 * result + name.hashCode();
	return result;
  }
  
  @Override
  public String toString() {
	return "{" + name + ", x=" + x + ", y=" + y + '}';
  }
}
