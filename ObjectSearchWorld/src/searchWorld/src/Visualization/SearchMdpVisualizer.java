package searchWorld.src.Visualization;

import burlap.mdp.core.state.State;
import burlap.visualizer.StatePainter;
import burlap.visualizer.StateRenderLayer;
import burlap.visualizer.Visualizer;
import searchWorld.src.State.SearchState;

import static searchWorld.src.SearchDomain.*;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by arthurwandzel on 7/18/17.
 */
public class SearchMdpVisualizer {

  private static int[][] map;

  public SearchMdpVisualizer(int[][] m) {
    map = m;
  }

  /**
   * @return - Gives us the StateRenderLayer for the world and our agent.
   */
  private StateRenderLayer getStateRenderLayer() {
    StateRenderLayer rl = new StateRenderLayer();
    rl.addStatePainter(new WorldPainter());
    rl.addStatePainter(new AgentPainter());
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
      case 1:
        return Color.BLUE;
      case 2:
        return Color.RED;
      case 3:
        return Color.MAGENTA;
      case 4:
        return Color.CYAN;
      case 5:
        return Color.GREEN;
      case 6:
        return Color.PINK;
      case 7:
        return Color.ORANGE;
      case 8:
        return new Color(255, 0, 255);
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

      float fWidth = SearchMdpVisualizer.map.length;
      float fHeight = SearchMdpVisualizer.map[0].length;

      float width = cWidth / fWidth;
      float height = cHeight / fHeight;

      // Goes through everything in map.  Painting walls, objects, and empty spaces separately
      for (int x = 0; x < SearchMdpVisualizer.map.length; x++) {
        for (int y = 0; y < SearchMdpVisualizer.map[0].length; y++) {

          if (SearchMdpVisualizer.map[x][y] == WALL) {
            float rx = x * width;
            float ry = cHeight - height - y * height;
            g2.setColor(Color.BLACK);
            g2.fill(new Rectangle2D.Float(rx, ry, width, height));
          } else if (SearchMdpVisualizer.map[x][y] == EMPTY) {
            float rx = x * width;
            float ry = cHeight - height - y * height;
            g2.setColor(Color.WHITE);
            g2.fill(new Rectangle2D.Float(rx, ry, width, height));
          } else {
            float rx = x * width;
            float ry = cHeight - height - y * height;
            g2.setColor(selectColor(SearchMdpVisualizer.map[x][y]));
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
      SearchState ns = ((SearchState) s);

      g2.setColor(Color.cyan);

      int at = ns.agent.theta;
      int ax = ns.agent.x;
      int ay = ns.agent.y;

      float fWidth = SearchMdpVisualizer.map.length;
      float fHeight = SearchMdpVisualizer.map[0].length;

      float width = cWidth / fWidth;
      float height = cHeight / fHeight;

      float rx = ax * width;
      float ry = cHeight - height - ay * height;

      g2.setColor(Color.cyan);
      g2.fill(new Ellipse2D.Float(rx, ry, width, height));

      //paint the triangle
//      Polygon poly;
//      if (at == 0) {
//          poly = new Polygon(new int[]{(int) rx, (int) (rx + width / 2), (int) (rx + width)}, new int[]{(int) (ry + height), (int) ry, (int) (ry + height)}, 3);
//      }
//      else if (at == 1) {
//        poly = new Polygon(new int[]{(int) rx, (int) (rx + width / 2), (int) (rx + width)}, new int[]{(int) ry, (int) (ry + height), (int) ry}, 3);
//      }
//      else if (at == 2) {
//        poly = new Polygon(new int[]{(int) rx, (int) rx, (int) (rx + width)}, new int[]{(int) ry, (int) (ry + height), (int) (ry + height / 2)}, 3);
//      } else {
//          poly = new Polygon(new int[]{(int) rx, (int) (rx+width), (int) (rx+width)}, new int[]{(int) (ry + height / 2), (int) ry, (int) (ry + height)}, 3);
//      }
//      g2.fill(poly);
    }
  }


}



