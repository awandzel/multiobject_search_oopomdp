package searchWorld.src.BeliefRoadMap;

import searchWorld.src.Experiments.WorldMaps;
import searchWorld.src.Pomcp.Location;

import java.util.*;

/**
 * Created by awandzel on 7/30/18.
 */
public class Graph {
  public Map<Location, List<Edge>> nodes = new HashMap<>();
  public Graph(){}

  public void printGraph(int[][] selectedMap, Location START_STATE){
    Map<Integer, Integer> edgeStatsitics = new TreeMap<>();
    for (List<Edge> e : this.nodes.values()) {
      if (!edgeStatsitics.containsKey(e.size())) {
        edgeStatsitics.put(e.size(), 1);
      } else {
        edgeStatsitics.put(e.size(), edgeStatsitics.get(e.size()) + 1);
      }
    }
    for (Map.Entry<Integer, Integer> e : edgeStatsitics.entrySet()) {
      System.out.println(e.getKey() + " : " + e.getValue());
    }


    int[][] m = new int[selectedMap.length][selectedMap[0].length];
    for (int x = 0; x < selectedMap.length; x++) {
      for (int y = 0; y < selectedMap[0].length; y++) {
        int token;
        Location currentLocation = new Location(x, y);
        if (currentLocation.x == START_STATE.x && currentLocation.y == START_STATE.y) token = -3;
        else if (this.nodes.containsKey(currentLocation)) token = -1;
        else token = selectedMap[x][y];
        m[x][y] = token;
      }
    }

    int[][] mRotated = WorldMaps.rotateToLeft(m);

    for (int x = 0; x < selectedMap.length; x++) {
      System.out.print("{");
      for (int y = 0; y < selectedMap[0].length; y++) {
        String token;
        if (mRotated[x][y] == -3) token = "A";
        else if (mRotated[x][y] == -2) token = "W";
        else if (mRotated[x][y] == -1) token = "X";
        else token = Integer.toString(mRotated[x][y]);
        System.out.print(token + ", ");
      }
      System.out.print("}\n");
    }
  }

  public void setGraph() {
    nodes.put(new Location(0, 0),
            new ArrayList<>(Arrays.asList(new Edge(new Location(1, 2), null))));

    nodes.put(new Location(1, 2), new ArrayList<>(Arrays.asList(
            new Edge(new Location(0, 0), null),
            new Edge(new Location(3, 1), null),
            new Edge(new Location(3, 3), null)
    )));
    nodes.put(new Location(3, 1), new ArrayList<>(Arrays.asList(
            new Edge(new Location(1, 2), null),
            new Edge(new Location(3, 3), null)
    )));
    nodes.put(new Location(3, 3), new ArrayList<>(Arrays.asList(
            new Edge(new Location(1, 2), null),
            new Edge(new Location(3, 1), null),
            new Edge(new Location(2, 5), null),
            new Edge(new Location(5, 2), null),
            new Edge(new Location(5, 4), null)
    )));
    nodes.put(new Location(2, 5), new ArrayList<>(Arrays.asList(
            new Edge(new Location(3, 3), null)
    )));
    nodes.put(new Location(5, 2), new ArrayList<>(Arrays.asList(
            new Edge(new Location(2, 5), null),
            new Edge(new Location(3, 3), null)
    )));
    nodes.put(new Location(5, 4), new ArrayList<>(Arrays.asList(
            new Edge(new Location(5, 4), null),
            new Edge(new Location(3, 3), null)
    )));
  }
}

