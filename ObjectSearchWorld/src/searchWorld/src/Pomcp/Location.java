package searchWorld.src.Pomcp;

import searchWorld.src.State.SearchState;

/**
 * Created by awandzel on 7/17/18.
 */
public class Location implements Comparable<Location>{
  public Integer x;
  public Integer y;

  public Location(Integer nx, Integer ny) {
    this.x = nx;
    this.y = ny;
  }

  public Location(Location l) {
    this.x = l.x;
    this.y = l.y;
  }

  public Location copy(){
    return new Location(this.x, this.y);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true; // If they have the same address in memory
    if (o == null || getClass() != o.getClass()) return false;
    if (!(o instanceof Location)) return false;

    Location that = (Location) o;

    return (that.x == this.x) && (that.y == this.y);
  }

  @Override
  public int hashCode() {
    return 37 * this.x + this.y;
  }

  @Override
  public String toString() {
    return "x=" + this.x + ", " + "y=" + this.y;
  }

  @Override
  public int compareTo(Location other) {
    return Integer.compare((37 * this.x)+this.y, (37*other.x)+other.y);
  }
}