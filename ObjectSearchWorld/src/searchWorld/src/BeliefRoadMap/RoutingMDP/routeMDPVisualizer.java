package searchWorld.src.BeliefRoadMap.RoutingMDP;

import burlap.mdp.core.state.State;
import burlap.visualizer.StatePainter;
import burlap.visualizer.StateRenderLayer;
import burlap.visualizer.Visualizer;
import searchWorld.src.Pomcp.Location;
import searchWorld.src.Visualization.SearchMdpVisualizer;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import static searchWorld.src.SearchDomain.EMPTY;
import static searchWorld.src.SearchDomain.WALL;

/**
 * Created by awandzel on 8/6/18.
 */
public class routeMDPVisualizer {

    private static int[][] map;
    Location goal;
    Location start;

    public routeMDPVisualizer(int[][] m, Location g, Location s) {
      map = m;
      goal = g;
      start = s;
    }

    /**
     * @return - Gives us the StateRenderLayer for the world and our agent.
     */
    private StateRenderLayer getStateRenderLayer() {
      StateRenderLayer rl = new StateRenderLayer();
      rl.addStatePainter(new routeMDPVisualizer.WorldPainter());
      rl.addStatePainter(new routeMDPVisualizer.AgentPainter());
      return rl;
    }

    /**
     * @return - Gives us the Visualizer for our SearchDomain
     */
    public Visualizer getVisualizer() {
      return new Visualizer(getStateRenderLayer());
    }

    public Color selectColor(int num) {
      switch (num) {
//        case 1:
//          return Color.BLUE;
        case 1:
          return Color.RED;
        case 2:
          return Color.MAGENTA;
        case 3:
          return Color.CYAN;
        case 4:
          return Color.YELLOW;
        case 5:
          return Color.GREEN;
        case 6:
          return Color.PINK;
        case 7:
          return Color.ORANGE;
      }
      return Color.white;
    }

    /**
     * Paints our world
     */
    class WorldPainter implements StatePainter {

      WorldPainter() {
      }

      @Override
      public void paint(Graphics2D g2, State s, float cWidth, float cHeight) {
        routeState ns = ((routeState) s);

        float fWidth = map.length;
        float fHeight = map[0].length;

        float width = cWidth / fWidth;
        float height = cHeight / fHeight;

        // Goes through everything in map.  Painting walls, objects, and empty spaces separately
        for (int x = 0; x < map.length; x++) {
          for (int y = 0; y < map[0].length; y++) {
            float rx = x * width;
            float ry = cHeight - height - y * height;

            if (map[x][y] == WALL) {
              g2.setColor(Color.yellow);
              g2.fill(new Rectangle2D.Float(rx, ry, width, height));
            } else if ((x == start.x && y == start.y)
                    || (x == goal.x && y == goal.y)) {
              g2.setColor(Color.blue);
              g2.fill(new Rectangle2D.Float(rx, ry, width, height));
            } else if (map[x][y] == EMPTY) {
              g2.setColor(Color.WHITE);
              g2.fill(new Rectangle2D.Float(rx, ry, width, height));
            } else {
              g2.setColor(selectColor(map[x][y]));
              g2.fill(new Rectangle2D.Float(rx, ry, width, height));
            }
          }
        }
      }
    }

    /**
     * Paints our agent
     */
    class AgentPainter implements StatePainter {

      AgentPainter() {
      }

      @Override
      public void paint(Graphics2D g2, State s, float cWidth, float cHeight) {
        routeState ns = ((routeState) s);

        g2.setColor(Color.cyan);

        int ax = ns.currentPosition.x;
        int ay = ns.currentPosition.y;

        float fWidth = map.length;
        float fHeight = map[0].length;

        float width = cWidth / fWidth;
        float height = cHeight / fHeight;

        float rx = ax * width;
        float ry = cHeight - height - ay * height;

        g2.setColor(Color.cyan);
        g2.fill(new Ellipse2D.Float(rx, ry, width, height));

      }
    }
  }
