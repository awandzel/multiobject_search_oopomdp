package searchWorld.src.Experiments;

import searchWorld.src.Pomcp.Location;

import java.util.*;

import static searchWorld.src.SearchDomain.*;

/**
 * Created by arthurwandzel on 11/14/17.
 */

public class WorldMaps {
  public static int W = WALL; //wall
  public static int U = UNCERTAIN; //belief
  public static int X = ROOMCENTER;
  public static int Y = MAPCENTER;
  //objects 1-100

  /**
   * Rotates an array to the right
   *
   * @param m - The array you would like rotated
   * @return - The rotated array
   */
  public static int[][] rotateToRight(int[][] m) {
    //System.out.println(m.length + " , " + m[0].length);
    int[][] t = new int[m.length][m[0].length];
    for (int x = 0; x < m.length; x++) {
      for (int y = 0; y < m[0].length; y++) {
        t[x][y] = m[m.length - y - 1][x];
      }
    }
    return t;
  }
  //get locations

  public static double[][] rotateToLeft(double[][] m) {
    double[][] t = new double[m.length][m[0].length];
    for (int x = 0; x < m.length; x++) {
      for (int y = 0; y < m[0].length; y++) {
        t[m.length - 1 - x][m.length - 1 - y] = m[m.length - 1 - y][x];
      }
    }
    return t;
  }

  public static int[][] rotateToLeft(int[][] m) {
    int[][] t = new int[m.length][m[0].length];
    for (int x = 0; x < m.length; x++) {
      for (int y = 0; y < m[0].length; y++) {
        t[m.length - 1 - x][m.length - 1 - y] = m[m.length - 1 - y][x];
      }
    }
    return t;
  }


  public static int[][] updateSelectedMap(SortedMap<Integer, Location> goals, int[][] m) {
    int[][] updatedMap = new int[m.length][m[0].length];
    for (int x = 0; x < m.length; x++) {
      for (int y = 0; y < m[0].length; y++) {
        updatedMap[x][y] = m[x][y];
        if (m[x][y] != WALL && m[x][y] != EMPTY) {
          updatedMap[x][y] = EMPTY;
        }
      }
    }

    for (Map.Entry<Integer, Location> entry : goals.entrySet()) {
      Integer object = entry.getKey();
      Location cell = entry.getValue();
      updatedMap[cell.x][cell.y] = object;
    }

    return updatedMap;
  }


  //Select map & rotate so that visualization corresponds to topology of selectedMap.
  //Experiment setup
  public static int[][] selectMap(String map) {
    if (map.equals("two")) {
      return WorldMaps.rotateToRight(WorldMaps.Cleanup2x2TwoRooms);
    } else if (map.equals("three")) {
      return WorldMaps.rotateToRight(WorldMaps.Cleanup3x3TwoRooms);
    } else if (map.equals("four")) {
      return WorldMaps.rotateToRight(WorldMaps.Cleanup4x4TwoRooms);
    } else if (map.equals("rasputin")) {
      return WorldMaps.rotateToRight(WorldMaps.rasputinHome);
    } else if (map.equals("baxter")) {
      return WorldMaps.rotateToRight(WorldMaps.baxterHome);
    } else if (map.equals("arthur")) {
      return WorldMaps.rotateToRight(WorldMaps.arthurHome);
    } else if (map.equals("toy")) {
      return WorldMaps.rotateToRight(WorldMaps.toyHome);
    } else if (map.equals("A")) {
      return WorldMaps.rotateToRight(WorldMaps.A);
    } else if (map.equals("B")) {
      return WorldMaps.rotateToRight(WorldMaps.B);
    } else if (map.equals("C")) {
      return WorldMaps.rotateToRight(WorldMaps.C);
    }  else if (map.equals("D")) {
      return WorldMaps.rotateToRight(WorldMaps.D);
    } else if (map.equals("robot")) {
      return WorldMaps.rotateToRight(WorldMaps.robotDomain);
    }
    {
      throw new RuntimeException("No known map exists for key: " + map + '\n');
    }
  }

  public static int[][] selectRoomMap(String map) {
    if (map.equals("rasputin")) {
      return WorldMaps.rotateToRight(WorldMaps.roomsInRasputinHome);
    } else if (map.equals("baxter")) {
      return WorldMaps.rotateToRight(WorldMaps.roomsInBaxterHome);
    } else if (map.equals("arthur")) {
      return WorldMaps.rotateToRight(WorldMaps.roomsInArthurHome);
    } else if (map.equals("toy")) {
      return WorldMaps.rotateToRight(WorldMaps.roomsInToyHome);
    } else if (map.equals("A")) {
      return WorldMaps.rotateToRight(WorldMaps.roomsInA);
    } else if (map.equals("B")) {
      return WorldMaps.rotateToRight(WorldMaps.roomsInB);
    } else if (map.equals("C")) {
      return WorldMaps.rotateToRight(WorldMaps.roomsInC);
    } else if (map.equals("D")) {
      return WorldMaps.rotateToRight(WorldMaps.roomsInD);
    } else if (map.equals("robot")) {
      return WorldMaps.rotateToRight(WorldMaps.roomsInRobotDomain);
    }
    else {
      throw new RuntimeException("No known belief map exists for key: " + map + '\n');
    }

  }

  // 3 is agent.  Make room class containing coordinates.  Two rooms here.  Only set goal
  // location for the two rooms.
  public static int[][] Cleanup2x2TwoRooms = new int[][]{
          {W, 0},
          {0, 1},
  };
  //  public static int[][] Cleanup3x3TwoRooms = new int[][]{
//          {W, W, W},
//          {W, W, W},
//          {0, 0, 1},
//  };
//  public static int[][] Cleanup3x3TwoRooms = new int[][]{
//          {W, W, 0},
//          {0, 0, 0},
//          {W, W, 1},
//  };
  public static int[][] Cleanup3x3TwoRooms = new int[][]{
          {W, 0, 0},
          {1, W, 0},
          {0, 0, 0},
  };
  public static int[][] Cleanup4x4TwoRooms = new int[][]{
          {W, 0, 0, 1},
          {0, W, 0, 0},
          {2, 0, 0, 0},
          {0, 0, 0, W},
  };
  public static int[][] toyHome = new int[][]{
          {0, 0, 0, W, W, W, W, W, W},
          {0, X, 0, W, W, W, W, W, W},
          {0, 0, 0, W, W, W, W, W, W},
          {0, 0, 0, W, W, W, W, W, W},
          {0, 0, 0, W, Y, W, W, W, W},
          {0, 0, 0, W, W, W, W, W, W},
          {0, 0, 0, W, W, W, W, W, W},
          {0, X, 0, W, W, W, W, W, W},
          {0, 0, 0, W, W, W, W, W, W}
  };
  public static int[][] roomsInToyHome = new int[][]{
          {1, 1, 1, W, W, W, W, W, W},
          {1, 1, 1, W, W, W, W, W, W},
          {1, 1, 1, W, W, W, W, W, W},
          {1, 1, 1, W, W, W, W, W, W},
          {1, 1, 1, W, W, W, W, W, W},
          {2, 2, 2, W, W, W, W, W, W},
          {2, 2, 2, W, W, W, W, W, W},
          {2, 2, 2, W, W, W, W, W, W},
          {2, 2, 2, W, W, W, W, W, W}
  };


//  public static int[][] toyHome = new int[][]{
//          {W, 0, W, W, W, W, W, W, W},
//          {0, 0, W, W, W, W, W, W, W},
//          {0, 0, W, W, W, W, W, W, W},
//          {X, 0, W, W, W, W, W, W, W},
//          {0, 0, W, W, Y, W, W, W, W},
//          {0, 0, W, W, W, W, W, W, W},
//          {X, 0, W, W, W, W, W, W, W},
//          {0, 0, W, W, W, W, W, W, W},
//          {0, 0, W, W, W, W, W, W, W}
//  };
//  public static int[][] roomsInToyHome = new int[][]{
//          {W, 1, W, W, W, W, W, W, W},
//          {1, 1, W, W, W, W, W, W, W},
//          {1, 1, W, W, W, W, W, W, W},
//          {1, 1, W, W, W, W, W, W, W},
//          {1, 1, W, W, W, W, W, W, W},
//          {2, 2, W, W, W, W, W, W, W},
//          {2, 2, W, W, W, W, W, W, W},
//          {2, 2, W, W, W, W, W, W, W},
//          {2, 2, W, W, W, W, W, W, W}
//  };

  public static int[][] arthurHome = new int[][]{
          {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
          {0, X, 2, 0, 0, 0, 0, 0, 0, X, 0},
          {3, 0, 0, W, W, W, W, W, 0, 0, 0},
          {0, 0, W, W, W, W, W, W, W, 0, 0},
          {0, 0, W, W, W, W, W, W, W, 0, 0},
          {0, 0, W, W, W, Y, W, W, W, 0, 0},
          {0, 0, W, W, W, W, W, W, W, 0, 0},
          {0, 0, W, W, W, W, W, W, W, 0, 0},
          {0, 0, 0, W, W, W, W, W, 0, 0, 0},
          {0, X, 0, 0, 0, 0, 0, 0, 0, X, 0},
          {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
  };
  public static int[][] roomsInArthurHome = new int[][]{
          {2, 2, 2, 0, 0, 0, 0, 0, 3, 3, 3},
          {2, 2, 2, 0, 0, 0, 0, 0, 3, 3, 3},
          {2, 2, 2, W, W, W, W, W, 3, 3, 3},
          {0, 0, W, W, W, W, W, W, W, 0, 0},
          {0, 0, W, W, W, W, W, W, W, 0, 0},
          {0, 0, W, W, W, 5, W, W, W, 0, 0},
          {0, 0, W, W, W, W, W, W, W, 0, 0},
          {0, 0, W, W, W, W, W, W, W, 0, 0},
          {1, 1, 1, W, W, W, W, W, 4, 4, 4},
          {1, 1, 1, 0, 0, 0, 0, 0, 4, 4, 4},
          {1, 1, 1, 0, 0, 0, 0, 0, 4, 4, 4},
  };

  public static int[][] baxterHome = new int[][]{
          {0, 0, 0, 0, 0, W, W, W, W, W, 0, 0, 0, 0, 0},
          {0, 0, 0, 0, 0, W, W, W, W, W, 0, 0, 0, 0, 0},
          {0, 0, X, 1, 0, 0, 0, 0, 0, 0, 0, 0, X, 0, 0},
          {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
          {0, 0, 0, 0, 0, W, W, W, W, W, 0, 0, 0, 0, 0},
          {W, W, 0, 0, W, W, W, W, W, W, W, 0, 0, W, W},
          {W, W, 0, 0, W, W, W, W, W, W, W, 0, 0, W, W},
          {W, W, 0, 0, W, W, W, Y, W, W, W, 0, 0, W, W},
          {W, W, 0, 0, W, W, W, W, W, W, W, 0, 0, W, W},
          {W, W, 0, 0, W, W, W, W, W, W, W, 0, 0, W, W},
          {0, 0, 0, 0, 0, W, W, W, W, W, 0, 0, 0, 0, 0},
          {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
          {0, 0, X, 0, 0, 0, 0, 0, 0, 0, 0, 0, X, 0, 0},
          {0, 0, 0, 0, 0, W, W, W, W, W, 0, 0, 0, 0, 0},
          {0, 0, 0, 0, 0, W, W, W, W, W, 0, 0, 0, 0, 0},
  };
  public static int[][] roomsInBaxterHome = new int[][]{
          {2, 2, 2, 2, 2, W, W, W, W, W, 3, 3, 3, 3, 3},
          {2, 2, 2, 2, 2, W, W, W, W, W, 3, 3, 3, 3, 3},
          {2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3},
          {2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3},
          {2, 2, 2, 2, 2, W, W, W, W, W, 3, 3, 3, 3, 3},
          {W, W, 0, 0, W, W, W, W, W, W, W, 0, 0, W, W},
          {W, W, 0, 0, W, W, W, W, W, W, W, 0, 0, W, W},
          {W, W, 0, 0, W, W, W, 5, W, W, W, 0, 0, W, W},
          {W, W, 0, 0, W, W, W, W, W, W, W, 0, 0, W, W},
          {W, W, 0, 0, W, W, W, W, W, W, W, 0, 0, W, W},
          {1, 1, 1, 1, 1, W, W, W, W, W, 4, 4, 4, 4, 4},
          {1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 4, 4, 4, 4, 4},
          {1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 4, 4, 4, 4, 4},
          {1, 1, 1, 1, 1, W, W, W, W, W, 4, 4, 4, 4, 4},
          {1, 1, 1, 1, 1, W, W, W, W, W, 4, 4, 4, 4, 4},
  };

//  public static int[][] robotDomain = new int[][]{
//          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
//          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
//          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
//          {W, 0, 0, W, 0, 0, 0, 0, 0, W, W, W, W, W, W, W, 0, 0, W, W},
//          {W, 0, 0, W, W, 0, 0, X, 0, 0, 0, X, 0, W, W, 0, X, 0, W, W},
//          {W, X, 0, W, W, 0, 0, 0, 0, 0, W, W, W, W, 0, 0, 0, 0, W, W},
//          {W, 0, 0, W, W, W, W, 0, Y, 0, W, W, W, 0, 0, 0, W, 0, 0, W},
//          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, W, W, W},
//          {W, 0, 0, 0, 0, 0, 0, 0, X, 0, 0, 0, 0, 0, W, W, W, W, W, W},
//          {W, 0, 0, 0, 0, 0, 0, W, W, W, W, 0, W, W, W, W, W, W, W, W},
//          {W, W, 0, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
//          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
//          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
//          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
//          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
//          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
//          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
//          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
//          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
//          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
//  };
//  public static int[][] roomsInRobotDomain = new int[][]{
//          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
//          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
//          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
//          {W, 1, 1, W, 2, 2, 2, 2, 2, W, W, W, W, W, W, W, 0, 0, W, W},
//          {W, 1, 1, W, W, 2, 2, 2, 2, 0, 3, 3, 3, W, W, 5, 5, 5, W, W},
//          {W, 1, 1, W, W, 2, 2, 2, 2, 0, W, W, W, W, 0, 5, 5, 5, W, W},
//          {W, 1, 1, W, W, W, W, 0, 6, 0, W, W, W, 0, 0, W, W, 0, 0, W},
//          {W, 1, 1, 0, 0, 0, 4, 4, 4, 4, 4, 0, 0, 0, 0, W, W, W, W, W},
//          {W, 0, 0, 0, 0, 0, 4, 4, 4, 4, 4, 0, 0, 0, W, W, W, W, W, W},
//          {W, W, 0, 0, 0, 0, 4, W, W, W, W, 0, W, W, W, W, W, W, W, W},
//          {W, W, 0, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
//          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
//          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
//          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
//          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
//          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
//          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
//          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
//          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
//          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
//  };

  public static int[][] robotDomain = new int[][]{
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, W, W, W, 0, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, W},
          {0, 0, 0, 0, 0, X, 0, 0, 0, 0, 0, 0, 0, W, W, W, 0, 0, 0, W},
          {0, X, 0, 0, 0, 0, 0, 0, 0, 0, 0, X, 0, W, W, 0, 0, X, 0, W},
          {0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, W},
          {0, 0, 0, W, W, W, 0, 0, Y, 0, W, W, W, 0, 0, 0, W, 0, 0, W},
          {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, W, W, W},
          {W, 0, 0, 0, 0, 0, 0, 0, X, 0, 0, 0, 0, 0, W, W, W, W, W, W},
          {W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, W, W, W, W, W, W},
          {W, W, 0, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
  };
  public static int[][] roomsInRobotDomain = new int[][]{
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, W, W, W, 2, 2, 2, 2, 0, W, W, W, W, W, W, W, W, W, W, W},
          {1, 1, 1, 2, 2, 2, 2, 2, 0, 3, 3, 3, 3, W, W, W, 5, 5, 5, W},
          {1, 1, 1, 2, 2, 2, 2, 2, 0, 3, 3, 3, 3, W, W, 0, 5, 5, 5, W},
          {1, 1, 1, W, 2, 2, 2, 2, 0, 3, 3, 3, 3, W, 0, 0, 5, 5, 5, W},
          {1, 1, 1, W, W, W, 0, 0, 6, 0, W, W, W, 0, 0, W, W, 0, 0, W},
          {1, 1, 1, 0, 0, 0, 4, 4, 4, 4, 4, 0, 0, 0, 0, W, W, W, W, W},
          {W, 0, 0, 0, 0, 0, 4, 4, 4, 4, 4, 0, 0, 0, W, W, W, W, W, W},
          {W, W, 0, 0, 0, 0, 4, 4, 4, 4, 4, 0, W, W, W, W, W, W, W, W},
          {W, W, 0, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
  };


  public static int[][] rasputinHome = new int[][]{
          {0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0},
          {0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0},
          {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
          {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
          {0, 0, 0, 0, X, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, X, 0, 0, 0, 0},
          {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
          {0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0},
          {0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0},
          {0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0},
          {W, W, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, W, W},
          {W, W, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, W, W},
          {W, W, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, W, W},
          {W, W, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, W, W},
          {W, W, 0, 0, 0, 0, W, W, W, W, W, W, W, W, Y, W, W, W, W, W, W, W, 0, 0, 0, 0, W, W},
          {W, W, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, W, W},
          {W, W, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, W, W},
          {W, W, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, W, W},
          {W, W, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, W, W},
          {W, W, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, W, W},
          {0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0},
          {0, 0, 0, 0, 1, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0},
          {0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0},
          {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
          {0, 0, 0, 0, X, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, X, 0, 0, 0, 0},
          {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
          {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
          {0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0},
          {0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0},
  };
  //9*9 + 9*9 + 9*9 + 9*9 = 324
  public static int[][] roomsInRasputinHome = new int[][]{
          {2, 2, 2, 2, 2, 2, 2, 2, 2, W, W, W, W, W, W, W, W, W, W, 3, 3, 3, 3, 3, 3, 3, 3, 3},
          {2, 2, 2, 2, 2, 2, 2, 2, 2, W, W, W, W, W, W, W, W, W, W, 3, 3, 3, 3, 3, 3, 3, 3, 3},
          {2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3},
          {2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3},
          {2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3},
          {2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3},
          {2, 2, 2, 2, 2, 2, 2, 2, 2, W, W, W, W, W, W, W, W, W, W, 3, 3, 3, 3, 3, 3, 3, 3, 3},
          {2, 2, 2, 2, 2, 2, 2, 2, 2, W, W, W, W, W, W, W, W, W, W, 3, 3, 3, 3, 3, 3, 3, 3, 3},
          {2, 2, 2, 2, 2, 2, 2, 2, 2, W, W, W, W, W, W, W, W, W, W, 3, 3, 3, 3, 3, 3, 3, 3, 3},
          {W, W, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, W, W},
          {W, W, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, W, W},
          {W, W, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, W, W},
          {W, W, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, W, W},
          {W, W, 0, 0, 0, 0, W, W, W, W, W, W, W, W, 5, W, W, W, W, W, W, W, 0, 0, 0, 0, W, W},
          {W, W, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, W, W},
          {W, W, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, W, W},
          {W, W, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, W, W},
          {W, W, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, W, W},
          {W, W, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, W, W},
          {1, 1, 1, 1, 1, 1, 1, 1, 1, W, W, W, W, W, W, W, W, W, W, 4, 4, 4, 4, 4, 4, 4, 4, 4},
          {1, 1, 1, 1, 1, 1, 1, 1, 1, W, W, W, W, W, W, W, W, W, W, 4, 4, 4, 4, 4, 4, 4, 4, 4},
          {1, 1, 1, 1, 1, 1, 1, 1, 1, W, W, W, W, W, W, W, W, W, W, 4, 4, 4, 4, 4, 4, 4, 4, 4},
          {1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 4, 4, 4, 4, 4, 4, 4},
          {1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 4, 4, 4, 4, 4, 4, 4},
          {1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 4, 4, 4, 4, 4, 4, 4},
          {1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 4, 4, 4, 4, 4, 4, 4},
          {1, 1, 1, 1, 1, 1, 1, 1, 1, W, W, W, W, W, W, W, W, W, W, 4, 4, 4, 4, 4, 4, 4, 4, 4},
          {1, 1, 1, 1, 1, 1, 1, 1, 1, W, W, W, W, W, W, W, W, W, W, 4, 4, 4, 4, 4, 4, 4, 4, 4},
  };



  //1XL, 1L, 2M, 4S
  //2L, 4M, 2S
  //2L, 4M, 2S
  //8M

  //2L, 4M, 2S
  public static int[][] A = new int[][]{
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, X, 0, 0, 0, 0, 0, W, W, 0, 0, 0, X, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, X, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, W, W, W, W, W, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, W, W, W, W, W, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, Y, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, W, W, W, W, W, 0, 0, W, W, W, W, W, W, W, W, 0, 0, 0, W, W, W, W, 0, 0, W, W, W},
          {W, 0, 0, W, W, W, W, W, 0, 0, W, W, W, W, W, W, W, W, 0, 0, 0, W, W, W, W, 0, 0, W, W, W},
          {W, 0, 0, W, W, W, W, W, 0, 0, W, W, W, W, W, W, W, W, 0, 0, 0, W, W, W, W, 0, 0, W, W, W},
          {W, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, X, 0, 0, W, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, W, W, W, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, X, 0, 0, W, W, 0, 0, 0, X, 0, 0, 0, W, W, 0, 0, 0, X, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, W},
          {W, 0, X, 0, 0, W, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, W},
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
  };

  public static int[][] roomsInA = new int[][]{
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, W, W, 2, 2, 2, 2, 2, 2, 2, 2, W, W, 3, 3, 3, 3, 3, 3, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, W, W, 3, 3, 3, 3, 3, 3, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, W, W, 3, 3, 3, 3, 3, 3, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, W, W, 3, 3, 3, 3, 3, 3, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, W, W, 2, 2, 2, 2, 2, 2, 2, 2, W, W, 3, 3, 3, 3, 3, 3, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, W, W, 2, 2, 2, 2, 2, 2, 2, 2, W, W, 3, 3, 3, 3, 3, 3, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, W, W, 2, 2, 2, 2, 2, 2, 2, 2, W, W, 3, 3, 3, 3, 3, 3, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, W, W, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 3, 3, 3, 3, 3, 3, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, W, W, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 3, 3, 3, 3, 3, 3, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, W, W, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 3, 3, 3, 3, 3, 3, W},
          {W, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, W, W, W, W, W, 3, 3, 3, 3, 3, 3, W},
          {W, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, W, 0, 0, 0, W, W, W, W, W, 3, 3, 3, 3, 3, 3, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, 3, 3, 3, 3, 3, 3, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 0, 0, 0, 0, 0, 0, W, W, 3, 3, 3, 3, 3, 3, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, 3, 3, 3, 3, 3, 3, W},
          {W, 0, 0, W, W, W, W, W, 0, 0, W, W, W, W, W, W, W, W, 0, 0, 0, W, W, W, W, 0, 0, W, W, W},
          {W, 0, 0, W, W, W, W, W, 0, 0, W, W, W, W, W, W, W, W, 0, 0, 0, W, W, W, W, 0, 0, W, W, W},
          {W, 0, 0, W, W, W, W, W, 0, 0, W, W, W, W, W, W, W, W, 0, 0, 0, W, W, W, W, 0, 0, W, W, W},
          {W, 8, 8, 8, 8, W, 6, 6, 6, 6, 6, 6, W, W, 5, 5, 5, 5, 5, 5, 5, 0, 0, 4, 4, 4, 4, 4, 4, W},
          {W, 8, 8, 8, 8, W, 6, 6, 6, 6, 6, 6, W, W, 5, 5, 5, 5, 5, 5, 5, 0, 0, 4, 4, 4, 4, 4, 4, W},
          {W, 8, 8, 8, 8, W, 6, 6, 6, 6, 6, 6, W, W, 5, 5, 5, 5, 5, 5, 5, W, W, 4, 4, 4, 4, 4, 4, W},
          {W, 8, 8, 8, 8, W, 6, 6, 6, 6, 6, 6, W, W, 5, 5, 5, 5, 5, 5, 5, W, W, 4, 4, 4, 4, 4, 4, W},
          {W, 0, 0, W, W, W, 6, 6, 6, 6, 6, 6, W, W, 5, 5, 5, 5, 5, 5, 5, W, W, 4, 4, 4, 4, 4, 4, W},
          {W, 7, 7, 7, 7, 0, 6, 6, 6, 6, 6, 6, W, W, 5, 5, 5, 5, 5, 5, 5, W, W, 4, 4, 4, 4, 4, 4, W},
          {W, 7, 7, 7, 7, 0, 6, 6, 6, 6, 6, 6, W, W, 5, 5, 5, 5, 5, 5, 5, W, W, 4, 4, 4, 4, 4, 4, W},
          {W, 7, 7, 7, 7, W, 6, 6, 6, 6, 6, 6, W, W, 5, 5, 5, 5, 5, 5, 5, W, W, 4, 4, 4, 4, 4, 4, W},
          {W, 7, 7, 7, 7, W, 6, 6, 6, 6, 6, 6, W, W, 5, 5, 5, 5, 5, 5, 5, W, W, 4, 4, 4, 4, 4, 4, W},
          {W, 7, 7, 7, 7, W, 6, 6, 6, 6, 6, 6, W, W, 5, 5, 5, 5, 5, 5, 5, W, W, 4, 4, 4, 4, 4, 4, W},
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
  };

  public static int[][] B = new int[][]{
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, X, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, X, 0, 0, 0, W, W, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, W, W, 0, 0, W, W, W, W, W, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, W, W, W, 0, 0, W, W},
          {W, W, W, 0, 0, W, W, W, W, W, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, W, W, W, 0, 0, W, W},
          {W, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 5, 0, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, X, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, Y, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, X, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, W, W, W, W, W},
          {W, W, W, 0, 0, W, W, W, W, W, 0, 0, W, W, W, W, W, 0, 0, W, W, 0, 0, W, W, W, W, W, W, W},
          {W, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, W, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, W, 0, 7, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, X, 0, 0, 0, W, W, 0, 0, 0, 0, W, 0, 0, 0, 0, W, W, 0, 0, 6, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, X, 0, W, 0, X, 0, 0, W, W, 0, 0, 0, 0, X, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, W, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, W, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
  };

  public static int[][] roomsInB = new int[][]{
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, 1, 1, 1, 1, 1, 1, 1, W, W, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, W},
          {W, 1, 1, 1, 1, 1, 1, 1, W, W, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, W},
          {W, 1, 1, 1, 1, 1, 1, 1, W, W, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, W},
          {W, 1, 1, 1, 1, 1, 1, 1, W, W, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, W},
          {W, 1, 1, 1, 1, 1, 1, 1, W, W, 0, 0, 0, W, W, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, W},
          {W, 1, 1, 1, 1, 1, 1, 1, W, W, 0, 0, 0, W, W, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, W},
          {W, 1, 1, 1, 1, 1, 1, 1, W, W, 0, 0, 0, W, W, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, W},
          {W, 1, 1, 1, 1, 1, 1, 1, W, W, 0, 0, 0, W, W, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, W, W, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, W, W, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, W, W, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, W, W, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, W, W, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, W},
          {W, 1, 1, 1, 1, 1, 1, 1, W, W, 0, 0, 0, W, W, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, W},
          {W, W, W, 0, 0, W, W, W, W, W, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, W, W, W, 0, 0, W, W},
          {W, W, W, 0, 0, W, W, W, W, W, 0, 0, 0, W, W, W, W, W, W, W, W, W, W, W, W, W, 0, 0, W, W},
          {W, 8, 8, 8, 8, 8, 8, 8, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, 3, 3, 3, 3, 3, 3, W},
          {W, 8, 8, 8, 8, 8, 8, 8, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, 3, 3, 3, 3, 3, 3, W},
          {W, 8, 8, 8, 8, 8, 8, 8, 0, 0, 0, 0, 0, 0, 0, 9, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, W},
          {W, 8, 8, 8, 8, 8, 8, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, W},
          {W, 8, 8, 8, 8, 8, 8, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, W, W, W, W, W, W},
          {W, W, W, 0, 0, W, W, W, W, W, 0, 0, W, W, W, W, W, 0, 0, W, W, 0, 0, W, W, W, W, W, W, W},
          {W, 7, 7, 7, 7, 7, 7, 7, W, W, 6, 6, 6, 6, W, 5, 5, 5, 5, W, W, 4, 4, 4, 4, 4, 4, 4, 4, W},
          {W, 7, 7, 7, 7, 7, 7, 7, W, W, 6, 6, 6, 6, W, 5, 5, 5, 5, W, W, 4, 4, 4, 4, 4, 4, 4, 4, W},
          {W, 7, 7, 7, 7, 7, 7, 7, W, W, 6, 6, 6, 6, W, 5, 5, 5, 5, W, W, 4, 4, 4, 4, 4, 4, 4, 4, W},
          {W, 7, 7, 7, 7, 7, 7, 7, W, W, 6, 6, 6, 6, W, 5, 5, 5, 5, W, W, 4, 4, 4, 4, 4, 4, 4, 4, W},
          {W, 7, 7, 7, 7, 7, 7, 7, W, W, 6, 6, 6, 6, W, 5, 5, 5, 5, W, W, 4, 4, 4, 4, 4, 4, 4, 4, W},
          {W, 7, 7, 7, 7, 7, 7, 7, W, W, 6, 6, 6, 6, W, 5, 5, 5, 5, W, W, 4, 4, 4, 4, 4, 4, 4, 4, W},
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
  };


  //2L, 2M, 3S
    public static int[][] C = new int[][]{
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, X, 0, 0, 0, 0, 0, 0, 0, 0, 0, X, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, W, W, 0, 0, 0, 0, 0, 0, W},
          {W, W, W, 0, 0, W, W, W, W, W, W, W, W, W, W, W, W, 0, 0, W, W, W, W, 0, 0, 0, X, 0, 0, W},
          {W, W, W, 0, 0, W, W, W, W, W, W, W, W, W, W, W, W, 0, 0, W, W, W, W, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, W, W, 0, 0, W, W, W, W, W, 0, 0, 0, W, W, W, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W},
          {W, W, W, 0, 0, W, W, W, W, W, 0, 0, 0, W, W, W, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, X, 0, 0, 0, 0, W, 0, Y, 0, 0, 0, 0, 0, X, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, W},
          {W, W, W, 0, 0, 0, W, W, W, W, 0, 0, 0, W, W, W, W, W, W, W, W, W, 0, 0, 0, 0, 0, 0, 0, W},
          {W, W, W, 0, 0, 0, W, W, W, W, 0, 0, 0, W, W, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, W, W, 0, 0, 0, W, W, W, W, 0, 0, 0, W, W, W, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, X, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, X, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, X, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
  };


  public static int[][] roomsInC = new int[][]{
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, W, 2, 2, 2, 2, 2, 2, 2, W, W, 3, 3, 3, 3, 3, 3, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, W, 2, 2, 2, 2, 2, 2, 2, W, W, 3, 3, 3, 3, 3, 3, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 2, 2, 2, 2, 2, 2, 0, 0, 3, 3, 3, 3, 3, 3, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 2, 2, 2, 2, 2, 2, 0, 0, 3, 3, 3, 3, 3, 3, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, W, 2, 2, 2, 2, 2, 2, 2, W, W, 3, 3, 3, 3, 3, 3, W},
          {W, W, W, 0, 0, W, W, W, W, W, W, W, W, W, W, W, W, 0, 0, W, W, W, W, 3, 3, 3, 3, 3, 3, W},
          {W, W, W, 0, 0, W, W, W, W, W, W, W, W, W, W, W, W, 0, 0, W, W, W, W, 3, 3, 3, 3, 3, 3, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, W},
          {W, W, W, 0, 0, W, W, W, W, W, 0, 0, 0, W, W, W, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W},
          {W, W, W, 0, 0, W, W, W, W, W, 0, 0, 0, W, W, W, 0, 0, 0, 0, W, W, W, W, W, W, W, W, W, W},
          {W, 8, 8, 8, 8, 8, 8, 8, 8, W, 0, 0, 0, W, 4, 4, 4, 4, 4, 4, 4, W, W, W, W, W, W, W, W, W},
          {W, 8, 8, 8, 8, 8, 8, 8, 8, W, 0, 0, 0, W, 4, 4, 4, 4, 4, 4, 4, W, 5, 5, 5, 5, 5, 5, 5, W},
          {W, 8, 8, 8, 8, 8, 8, 8, 8, W, 0, 0, 0, W, 4, 4, 4, 4, 4, 4, 4, W, 5, 5, 5, 5, 5, 5, 5, W},
          {W, 8, 8, 8, 8, 8, 8, 8, 8, W, 0, 9, 0, W, 4, 4, 4, 4, 4, 4, 4, 0, 5, 5, 5, 5, 5, 5, 5, W},
          {W, 8, 8, 8, 8, 8, 8, 8, 8, W, 0, 0, 0, W, 4, 4, 4, 4, 4, 4, 4, 0, 5, 5, 5, 5, 5, 5, 5, W},
          {W, 8, 8, 8, 8, 8, 8, 8, 8, W, 0, 0, 0, W, 4, 4, 4, 4, 4, 4, 4, 0, 5, 5, 5, 5, 5, 5, 5, W},
          {W, 8, 8, 8, 8, 8, 8, 8, 8, W, 0, 0, 0, W, 4, 4, 4, 4, 4, 4, 4, W, 5, 5, 5, 5, 5, 5, 5, W},
          {W, W, W, 0, 0, 0, W, W, W, W, 0, 0, 0, W, W, W, W, W, W, W, W, W, 5, 5, 5, 5, 5, 5, 5, W},
          {W, W, W, 0, 0, 0, W, W, W, W, 0, 0, 0, W, W, W, W, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, W},
          {W, W, W, 0, 0, 0, W, W, W, W, 0, 0, 0, W, W, W, W, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, W},
          {W, 7, 7, 7, 7, 7, 7, 7, 7, W, 6, 6, 6, 6, 6, 6, W, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, W},
          {W, 7, 7, 7, 7, 7, 7, 7, 7, 0, 6, 6, 6, 6, 6, 6, 0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, W},
          {W, 7, 7, 7, 7, 7, 7, 7, 7, 0, 6, 6, 6, 6, 6, 6, 0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, W},
          {W, 7, 7, 7, 7, 7, 7, 7, 7, 0, 6, 6, 6, 6, 6, 6, 0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, W},
          {W, 7, 7, 7, 7, 7, 7, 7, 7, W, 6, 6, 6, 6, 6, 6, W, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, W},
          {W, 7, 7, 7, 7, 7, 7, 7, 7, W, 6, 6, 6, 6, 6, 6, W, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, W},
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
  };

  public static int[][] D = new int[][]{
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, 0, X, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, X, 0, 0, 0, 0, W, 0, 0, X, 0, 0, W, W, W, 0, 0, W, W, W, W, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, X, 0, 0, 0, W, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, W},
          {W, W, W, 0, 0, 0, 0, W, W, W, W, W, 0, 0, 0, W, W, W, W, 0, 0, W, W, W, W, W, 0, 0, W, W},
          {W, W, W, 0, 0, 0, 0, W, W, W, W, W, 0, 0, 0, W, W, W, W, 0, 0, W, W, W, W, W, 0, 0, W, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, Y, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, W, 0, 0, W, W, W, W, W, 0, 0, W, W, W, W, W, 0, 0, W, W, W, W, W, W, 0, 0, W, W, W, W},
          {W, W, 0, 0, W, W, W, W, W, 0, 0, W, W, W, W, W, 0, 0, W, W, W, W, W, W, 0, 0, W, W, W, W},
          {W, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, X, 0, 0, 0, 0, 0, 0, X, 0, 0, 0, W, 0, 0, X, 0, 0, 0, 0, 0, 0, 0, X, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, W, 0, 0, 0, 0, 0, 0, 0, W},
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
  };


  public static int[][] roomsInD = new int[][]{
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 1, 1, W, 2, 2, 2, 2, 2, W, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 1, 1, W, 2, 2, 2, 2, 2, W, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 2, 2, 2, 2, W, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 2, 2, 2, 2, W, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 2, 2, 2, 2, W, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 1, 1, W, 2, 2, 2, 2, 2, W, W, W, 0, 0, W, W, W, W, 3, 3, 3, 3, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 1, 1, W, 2, 2, 2, 2, 2, W, 4, 4, 4, 4, 4, 4, 4, W, 3, 3, 3, 3, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 1, 1, W, 2, 2, 2, 2, 2, 0, 4, 4, 4, 4, 4, 4, 4, W, 3, 3, 3, 3, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 1, 1, W, 2, 2, 2, 2, 2, 0, 4, 4, 4, 4, 4, 4, 4, W, 3, 3, 3, 3, W},
          {W, 1, 1, 1, 1, 1, 1, 1, 1, 1, W, 2, 2, 2, 2, 2, W, 4, 4, 4, 4, 4, 4, 4, W, 3, 3, 3, 3,W},
          {W, 1, 1, 1, 1, 1, 1, 1, 1, 1, W, 2, 2, 2, 2, 2, W, 4, 4, 4, 4, 4, 4, 4, W, 3, 3, 3, 3,W},
          {W, W, W, 0, 0, 0, 0, W, W, W, W, W, 0, 0, 0, W, W, W, W, 0, 0, W, W, W, W, W, 0, 0, W, W},
          {W, W, W, 0, 0, 0, 0, W, W, W, W, W, 0, 0, 0, W, W, W, W, 0, 0, W, W, W, W, W, 0, 0, W, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, W},
          {W, W, 0, 0, W, W, W, W, W, 0, 0, W, W, W, W, W, 0, 0, W, W, W, W, W, W, 0, 0, W, W, W, W},
          {W, W, 0, 0, W, W, W, W, W, 0, 0, W, W, W, W, W, 0, 0, W, W, W, W, W, W, 0, 0, W, W, W, W},
          {W, 8, 8, 8, 8, 8, 8, W, 7, 7, 7, 7, 7, 7, W, 6, 6, 6, 6, 6, 6, W, 5, 5, 5, 5, 5, 5, 5, W},
          {W, 8, 8, 8, 8, 8, 8, W, 7, 7, 7, 7, 7, 7, W, 6, 6, 6, 6, 6, 6, W, 5, 5, 5, 5, 5, 5, 5, W},
          {W, 8, 8, 8, 8, 8, 8, W, 7, 7, 7, 7, 7, 7, W, 6, 6, 6, 6, 6, 6, W, 5, 5, 5, 5, 5, 5, 5, W},
          {W, 8, 8, 8, 8, 8, 8, W, 7, 7, 7, 7, 7, 7, W, 6, 6, 6, 6, 6, 6, W, 5, 5, 5, 5, 5, 5, 5, W},
          {W, 8, 8, 8, 8, 8, 8, W, 7, 7, 7, 7, 7, 7, W, 6, 6, 6, 6, 6, 6, W, 5, 5, 5, 5, 5, 5, 5, W},
          {W, 8, 8, 8, 8, 8, 8, W, 7, 7, 7, 7, 7, 7, W, 6, 6, 6, 6, 6, 6, W, 5, 5, 5, 5, 5, 5, 5, W},
          {W, 8, 8, 8, 8, 8, 8, W, 7, 7, 7, 7, 7, 7, W, 6, 6, 6, 6, 6, 6, W, 5, 5, 5, 5, 5, 5, 5, W},
          {W, 8, 8, 8, 8, 8, 8, W, 7, 7, 7, 7, 7, 7, W, 6, 6, 6, 6, 6, 6, W, 5, 5, 5, 5, 5, 5, 5, W},
          {W, 8, 8, 8, 8, 8, 8, W, 7, 7, 7, 7, 7, 7, W, 6, 6, 6, 6, 6, 6, W, 5, 5, 5, 5, 5, 5, 5, W},
          {W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W},
  };
}
