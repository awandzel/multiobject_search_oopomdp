package searchWorld.src.State;

import searchWorld.src.Pomcp.Location;

import java.util.*;

import static searchWorld.src.SearchDomain.*;

/**
 * Created by awandzel on 7/18/18.
 */
public class Rooms {
  public boolean[][] roomAdjacencyMatrix;
  public Map<Location, Integer> mappingFromAgentToRoom = new HashMap<>();
  public Map<Integer, Location> transitionMatrix = new TreeMap<>();
  public Map<Integer, List<Location>> mappingFromRoomToCells = new HashMap<>();
  public int totalRoomsInEnvironment = 0;
  public String map;

  public Rooms(String m){
    this.map = m;
  }

  public int[][] setTransitionMatrix(boolean print, int [][] occupancyMap, int[][] semanticMap){
    int[][] updatedMap = new int[occupancyMap.length][occupancyMap[0].length];
    for (int x = 0; x < occupancyMap.length; x++) {
      for (int y = 0; y < occupancyMap[0].length; y++) {
        updatedMap[x][y] = occupancyMap[x][y];
        if (occupancyMap[x][y] == ROOMCENTER || occupancyMap[x][y] == MAPCENTER) {
          transitionMatrix.put(semanticMap[x][y] - NAME_OFFSET, new Location(x, y));
          updatedMap[x][y] = EMPTY;
        }
      }
    }
    if (print){
      for (Map.Entry<Integer, Location> e : transitionMatrix.entrySet()){
        System.out.println(e.getKey() + " : " + e.getValue().toString());
      }
    }
    totalRoomsInEnvironment = transitionMatrix.size() - 1; //exclude center
    return updatedMap;
  }

  public void setRoomAdjacencyMatrix() {
    //hand encoded 4 room setup
    if (map.equals("toy")){
      boolean[][] roomAdjacencyMatrixCopy = {
              {false, true},
              {true, false},
      };
      this.roomAdjacencyMatrix = roomAdjacencyMatrixCopy;
    }
    else if  (map.equals("arthur") || map.equals("baxter") || map.equals("rasputin"))  {
      boolean[][] roomAdjacencyMatrixCopy = {
              {false, true, false, true, false},
              {true, false, true, false, false},
              {false, true, false, true, false},
              {true, false, true, false, false},
              {false, false, false, false, false},
      };
      this.roomAdjacencyMatrix = roomAdjacencyMatrixCopy;
    }  else if (map.equals("A") || map.equals("B") || map.equals("C") || map.equals("D")) {
      boolean[][] roomAdjacencyMatrixCopy = {
              {false, true, false, false, false, false, false, true, true},
              {true, false, true, false, false, false, false, false, true},
              {false, true, false, true, false, false, false, false, true},
              {false, false, true, false, true, false, false, false, true},
              {false, false, false, true, false, true, false, false, true},
              {false, false, false, false, true, false, true, false, true},
              {false, false, false, false, false, true, false, true, true},
              {true, false, false, false, false, false, true, false, true},
              {true, true, true, true, true, true, true, true, false},
      };
      this.roomAdjacencyMatrix = roomAdjacencyMatrixCopy;
    } else if (map.equals("robot")) {
      boolean[][] roomAdjacencyMatrixCopy = {
              {false, false, false, true, false, true},
              {false, false, true, true, false, true},
              {false, true, false, true, false, true},
              {true, true, true, false, true, false},
              {false, false, false, true, false, true},
              {true, true, true, true, true, false},
      };
      this.roomAdjacencyMatrix = roomAdjacencyMatrixCopy;
    }

  }

  public void setMappingFromAgentToRoom(int[][] roomsInHome) {
    for (int x = 0; x < roomsInHome.length; x++) {
      for (int y = 0; y < roomsInHome[0].length; y++) {
        if (roomsInHome[x][y] > EMPTY) {
          mappingFromAgentToRoom.put(new Location(x, y), roomsInHome[x][y] - NAME_OFFSET);
        }
      }
    }
  }

  public void setMappingFromRoomToCells(int[][] roomsInHome) {
    for (int x = 0; x < roomsInHome.length; x++) {
      for (int y = 0; y < roomsInHome[0].length; y++) {
        if (roomsInHome[x][y] > EMPTY) {
          if (mappingFromRoomToCells.containsKey(roomsInHome[x][y] - NAME_OFFSET)) {
            mappingFromRoomToCells.get(roomsInHome[x][y] - NAME_OFFSET).add(new Location(x, y));
          } else {
            mappingFromRoomToCells.put(roomsInHome[x][y] - NAME_OFFSET, new ArrayList<>(Arrays.asList(new Location(x, y))));
          }
        }
      }
    }
  }

  public List<Integer> connectedRooms(Integer index) {
    List<Integer> connectivity = new ArrayList<>();
    for (int i = 0; i < roomAdjacencyMatrix[index].length; i++) {
      if (roomAdjacencyMatrix[index][i]) {
        connectivity.add(i);
      }
    }
    return connectivity;
  }
}
