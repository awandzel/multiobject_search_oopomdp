package searchWorld.src.State;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.oo.ObjectParameterizedAction;
import searchWorld.src.BeliefRoadMap.Graph;
import searchWorld.src.Pomcp.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static searchWorld.src.SearchDomain.*;

/**
 * Created by awandzel on 12/28/17.
 */
public class Utilities {
  public int[][] worldMap;
  public Rooms roomAbstractions;
  public Graph roadMap;
  public Integer visionDepth;

  public Utilities(int[][] wm, Rooms r, Graph rm, Integer d) {
    this.worldMap = wm;
    this.roomAbstractions = r;
    this.roadMap = rm;
    this.visionDepth = d;
  }

  public Utilities(int[][] wm){
    this.worldMap = wm;
  }

  public List<Location> locationsInVisionBoundingBox(SearchState ns) {
    List<Location> visionCone = new ArrayList<>();
    Location currentLocation = new Location(ns.agent.x, ns.agent.y);

    if (visionDepth == 0){
      for (int d = 0; d < 4; d++){
        Location moveInDirection = util.moveInDirection(currentLocation.x, currentLocation.y, d);
        if (util.checkIfInBounds(moveInDirection.x, moveInDirection.y)) {
          visionCone.add(new Location(moveInDirection.x, moveInDirection.y));
        }
      }
    }

    //construct bounding box
    for (int x = currentLocation.x - visionDepth; x < currentLocation.x + visionDepth *2; x++){
      for (int y = currentLocation.y - visionDepth; y < currentLocation.y + visionDepth *2; y++){
        if (util.checkIfInBounds(x, y)) {
          visionCone.add(new Location(x,y));
        }
      }
    }
    return visionCone;
  }

    public double euclideanDistance(Location compare, int x, int y) {
    return Math.sqrt(Math.pow(Math.abs(compare.x - x), 2) + Math.pow(Math.abs(compare.y - y), 2));
  }

  //Returns list of (x,y) locations in robots vision cone
  public List<Location> locationsInVisionCone(SearchState ns, Action action) {
    String[] params = ((ObjectParameterizedAction) action).getObjectParameters();
    List<Location> visionCone = new ArrayList<>();
    int lookDirection = util.directionStoI(params[0]);

    int count = 1;
    visionCone.add(new Location(ns.agent.x, ns.agent.y));

    //visionDepth = 0; base case (single cell lookahead)
    if (visionDepth == 0) {
      List<Integer> agentsInitialLookPosition = util.actWithinBounds(ns, lookDirection);
      visionCone.add(new Location(agentsInitialLookPosition.get(0), agentsInitialLookPosition.get(1)));
    }

    //progress agent one step forward for cone shape
    Location moveInDirection = util.moveInDirection(ns.agent.x, ns.agent.y, lookDirection);
    int x = moveInDirection.x;
    int y = moveInDirection.y;

    if (!util.checkIfInBounds(x, y)) {
      return visionCone;
    }

    if (lookDirection == 0) { //north
      int leftRange = x;
      int rightRange = x;
      for (int ny = y; ny < y + visionDepth; ny++) {
        count++;
        leftRange--;
        rightRange++;
        for (int nx = leftRange; nx <= rightRange; nx++) {
          if (util.checkIfInBounds(nx, ny)) {
            visionCone.add(new Location(nx, ny));
          }
        }
//        count++;
//        leftRange--;
//        rightRange++;
      }
    } else if (lookDirection == 1) { //south
      int leftRange = x;
      int rightRange = x;
      for (int ny = y; ny > y - visionDepth; ny--) {
        count++;
        leftRange--;
        rightRange++;
        for (int nx = leftRange; nx <= rightRange; nx++) {
          if (util.checkIfInBounds(nx, ny)) {
            visionCone.add(new Location(nx, ny));
          }
        }
//        count++;
//        leftRange--;
//        rightRange++;
      }
    } else if (lookDirection == 2) { //east
      int leftRange = y;
      int rightRange = y;
      for (int nx = x; nx < x + visionDepth; nx++) {
        count++;
        leftRange--;
        rightRange++;
        for (int ny = leftRange; ny <= rightRange; ny++) {
          if (util.checkIfInBounds(nx, ny)) {
            visionCone.add(new Location(nx, ny));
          }
        }
//        count++;
//        leftRange--;
//        rightRange++;
      }
    } else {
      int leftRange = y;
      int rightRange = y;
      for (int nx = x; nx > x - visionDepth; nx--) {
        count++;
        leftRange--;
        rightRange++;
        for (int ny = leftRange; ny <= rightRange; ny++) {
          if (util.checkIfInBounds(nx, ny)) {
            visionCone.add(new Location(nx, ny));
          }
        }
//        count++;
//        leftRange--;
//        rightRange++;
      }
    }
    return visionCone;
  }

  public String directionItoS(int at) {
    if (at == 0) { //north
      return NORTH;
    } else if (at == 1) { //south
      return SOUTH;
    } else if (at == 2) { //east
      return EAST;
    } else if (at == 3) { //west
      return WEST;
    }
    return "";
  }

  public int directionStoI(String at) {
    if (at.equals(NORTH)) { //north
      return 0;
    } else if (at.equals(SOUTH)) { //south
      return 1;
    } else if (at.equals(EAST)) { //east
      return 2;
    } else if (at.equals(WEST)) { //west
      return 3;
    }
    return -1;
  }

  public Location moveInDirection(int nx, int ny, int at) {
    if (at == 0) { //north
      ny++;
    } else if (at == 1) { //south
      ny--;
    } else if (at == 2) { //east
      nx++;
    } else if (at == 3) { //west
      nx--;
    }
    return new Location(nx, ny);
  }

  public boolean checkIfInBounds(int nx, int ny) {
    return !(nx < 0 || nx >= this.worldMap.length || ny < 0 || ny >= this.worldMap[0].length || this.worldMap[nx][ny] == WALL);
  }

  public List<Integer> actWithinBounds(SearchState ns, int at) {
    Location updatedAgent = moveInDirection(ns.agent.x, ns.agent.y, at);

    int nx = updatedAgent.x;
    int ny = updatedAgent.y;

    if (checkIfInBounds(nx, ny)) {
      return new ArrayList<>(Arrays.asList(nx, ny));
    } else {
      return new ArrayList<>(Arrays.asList(ns.agent.x, ns.agent.y));
    }
  }

  public boolean ObjectInLookAnyLocation(SearchState ns, String objName, Location location) {
    SearchObject obj = ns.searchableObjects.get(ns.objectIndex(objName));
    int nx = location.x;
    int ny = location.y;
    return checkIfInBounds(nx, ny) && nx == obj.x && ny == obj.y;
  }

  public boolean inBoundsForForwardLocation(SearchState ns, int at) {
    Location action = moveInDirection(ns.agent.x, ns.agent.y, at);
    return checkIfInBounds(action.x, action.y);
  }

  public boolean ObjectInLookForwardLocation(SearchState ns, String objName, int at) {
    Location action = moveInDirection(ns.agent.x, ns.agent.y, at);
    return ObjectInLookAnyLocation(ns, objName, action);
  }

  public boolean checkIfConflict(Location currentLocation){
    for (int d = 0; d < 4; d++) {
      Location moveInDirection = moveInDirection(currentLocation.x, currentLocation.y, d);
      if (!checkIfInBounds(moveInDirection.x, moveInDirection.y)) {
        return true;
      }
    }
    return false;
  }
}
