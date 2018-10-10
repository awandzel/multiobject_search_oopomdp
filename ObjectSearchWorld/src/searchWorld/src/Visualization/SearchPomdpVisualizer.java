package searchWorld.src.Visualization;

import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.pomdp.beliefstate.EnumerableBeliefState;
import burlap.visualizer.StatePainter;
import burlap.visualizer.StateRenderLayer;
import burlap.visualizer.Visualizer;
import searchWorld.src.State.SearchState;

import java.awt.*;
import java.awt.List;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Array;
import java.util.*;

import static searchWorld.src.SearchDomain.*;

/**
 * Created by arthurwandzel on 11/27/17.
 */
public class SearchPomdpVisualizer {

  private static int[][] map;

  public SearchPomdpVisualizer(int[][] m) {
    int[][] selectedMap = new int[m.length][];
    for (int i = 0; i < m.length; i++)
      selectedMap[i] = m[i].clone();
    map = selectedMap;
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

  public Color selectColor(int num) {
    switch (num) {
      case 1:
        return Color.RED;//Color.BLUE;
      case 2:
        return Color.RED;//Color.RED;
      case 3:
        return Color.RED;//Color.MAGENTA;
      case 4:
        return Color.RED;//new Color(100, 250, 100);
      case 5:
        return Color.RED;//Color.GREEN;
      case 6:
        return Color.RED;//Color.PINK;
      case 7:
        return Color.RED;//Color.ORANGE;
      case 8:
        return Color.RED;//new Color(100, 0, 255);
    }
    return Color.WHITE;
  }

  /**
   * @return - Gives us the Visualizer for our SearchDomain
   */
  public Visualizer getVisualizer() {
    return new Visualizer(getStateRenderLayer());
  }

  /**
   * Paints our world
   */
  class WorldPainter implements StatePainter {

    WorldPainter() {
    }

    @Override
    public void paint(Graphics2D g2, State s, float cWidth, float cHeight) {
      VizualizeBeliefState ns = ((VizualizeBeliefState) s);

      float fWidth = SearchPomdpVisualizer.map.length;
      float fHeight = SearchPomdpVisualizer.map[0].length;

      float width = cWidth / fWidth;
      float height = cHeight / fHeight;

      if (true) {
        //find the maximum uncertainty of a cell over all object over all beliefs
        HashMap<ArrayList<Integer>, ArrayList<Double>> cellToObject = new HashMap<>();

        //iterate over all objects
        for (int o = 0; o < ns.searchState.numObjects(); o++) {

          //iterate over all beliefs
          for (EnumerableBeliefState.StateBelief sb : ns.stateBelief) {
            SearchState nns = ((SearchState) sb.s);

            Integer x = nns.searchableObjects.get(o).x;
            Integer y = nns.searchableObjects.get(o).y;

            //uncover object location (x,y)
            ArrayList<Integer> cellLocation = new ArrayList<>(Arrays.asList(x, y));

            //adds probability mass to location (x,y)
            if (!cellToObject.containsKey(cellLocation)) {
              cellToObject.put(cellLocation, new ArrayList<>(Arrays.asList(sb.belief)));
            } else {
              cellToObject.get(cellLocation).add(sb.belief);
            }
          }
        }

        for (Map.Entry<ArrayList<Integer>, ArrayList<Double>> entry : cellToObject.entrySet()) {
          ArrayList<Integer> bp = entry.getKey();
          ArrayList<Double> beliefValues = entry.getValue();

          //take maximum belief value for a given cell over objects
          Double beliefValue = 0.0;
          for (int i = 0; i < beliefValues.size(); i++) {
            if (beliefValue < beliefValues.get(i)) {
              beliefValue = beliefValues.get(i);
            }
          }

          Integer x = bp.get(0);
          Integer y = bp.get(1);

          float rx = x * width;
          float ry = cHeight - height - y * height;
          g2.setColor(blend(selectColor(SearchPomdpVisualizer.map[x][y]), new Color(
                  190, 190, 190), (float) (double) beliefValue)); //mix with black so dark denotes uncertainty
          g2.fill(new Rectangle2D.Float(rx, ry, width, height));
        }
      }

      // Goes through everything in map.  Painting walls, objects, and empty spaces separately
      for (int x = 0; x < SearchPomdpVisualizer.map.length; x++) {
        for (int y = 0; y < SearchPomdpVisualizer.map[0].length; y++) {
          if (SearchPomdpVisualizer.map[x][y] == WALL) {
            float rx = x * width;
            float ry = cHeight - height - y * height;
            g2.setColor(Color.black);
            g2.fill(new Rectangle2D.Float(rx, ry, width, height));
          }
//          } else if (SearchPomdpVisualizer.map[x][y] == EMPTY) {
//            float rx = x * width;
//            float ry = cHeight - height - y * height;
//            g2.setColor(Color.WHITE);
//            g2.fill(new Rectangle2D.Float(rx, ry, width, height));
//          } else {
            else if (SearchPomdpVisualizer.map[x][y] > EMPTY) {
            float rx = x * width;
            float ry = cHeight - height - y * height;
            g2.setColor(selectColor(SearchPomdpVisualizer.map[x][y]));
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
    VizualizeBeliefState ns = ((VizualizeBeliefState) s);

    g2.setColor(Color.cyan);

    int at = ns.searchState.agent.theta;
    int ax = ns.searchState.agent.x;
    int ay = ns.searchState.agent.y;

    float fWidth = SearchPomdpVisualizer.map.length;
    float fHeight = SearchPomdpVisualizer.map[0].length;

    float width = cWidth / fWidth;
    float height = cHeight / fHeight;

    float rx = ax * width;
    float ry = cHeight - height - ay * height;

    g2.setColor(new Color(51, 204, 255));



    g2.fill(new Ellipse2D.Float(rx, ry, width, height));

    //paint the triangle
//    Polygon poly;
//    if (at == 0) {
//      poly = new Polygon(new int[]{(int) rx, (int) (rx + width / 2), (int) (rx + width)}, new int[]{(int) (ry + height), (int) ry, (int) (ry + height)}, 3);
//    } else if (at == 1) {
//      poly = new Polygon(new int[]{(int) rx, (int) (rx + width / 2), (int) (rx + width)}, new int[]{(int) ry, (int) (ry + height), (int) ry}, 3);
//    } else if (at == 2) {
//      poly = new Polygon(new int[]{(int) rx, (int) rx, (int) (rx + width)}, new int[]{(int) ry, (int) (ry + height), (int) (ry + height / 2)}, 3);
//    } else {
//      poly = new Polygon(new int[]{(int) rx, (int) (rx + width), (int) (rx + width)}, new int[]{(int) (ry + height / 2), (int) ry, (int) (ry + height)}, 3);
//    }
//    g2.fill(poly);
  }

}

  Color blend(Color base, Color cover, float fAmount) {
    float fInverse = (float) (1.0 - fAmount);

    float afOne[] = new float[3];
    base.getColorComponents(afOne);
    float afTwo[] = new float[3];
    cover.getColorComponents(afTwo);

    float afResult[] = new float[3];
    afResult[0] = afOne[0] * fAmount + afTwo[0] * fInverse;
    afResult[1] = afOne[1] * fAmount + afTwo[1] * fInverse;
    afResult[2] = afOne[2] * fAmount + afTwo[2] * fInverse;

    return new Color(afResult[0], afResult[1], afResult[2]);
  }
}



