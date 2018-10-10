package searchWorld.src.LanguageCommands;

import searchWorld.src.Pomcp.Location;
import searchWorld.src.State.Rooms;

import java.util.*;

/**
 * Created by awandzel on 9/3/18.
 */
public class LanguageCommands {
  //set vocabulary
  //parseLanguageCommand:
  //  LC --> Room Ref (R)
  //  R --> languageToRoomMapping
  //languageObservation:
  //  set double per location via equation
  //beliefUpdate
  //  piecewise multiplication

  public Map<Set<String>, Integer> languageToRoomMapping; //robot
  public Map<Set<String>, Integer> languageToClassMapping; //robot
  public List<Set<Integer>> roomReferences;
  public Map<Integer, Map<Location, Double>> languageObservation;

  double psi;
  Random rmRn;
  Rooms rooms;
  List<Integer> classToObject;
  List<Integer> classToRooms;

  public LanguageCommands(double p, Rooms rm, List<Integer> co, List<Integer> cr, boolean b) {
    this.psi = p;
    this.rooms = rm;
    this.classToObject = co;
    this.classToRooms = cr;
    this.languageToRoomMapping = new HashMap<>();
    this.languageToClassMapping = new HashMap<>();
    this.languageObservation = new HashMap<>();
    this.roomReferences = new ArrayList<>();

    if (classToObject.size() != classToRooms.size()) {
      throw new RuntimeException("Error: Parameters not set correctly: classes must match rooms");
    }
    for (int r = 0; r < classToRooms.size(); r++) {
      int numRooms = classToRooms.get(r);
      if (b && (numRooms == 0 || numRooms == rooms.totalRoomsInEnvironment)){
        throw new RuntimeException("Error: Cannot set antagonistic with a uniform distribution");
      }
      if (numRooms > rooms.totalRoomsInEnvironment) {
        throw new RuntimeException("Error: Referenced rooms exceed total rooms in environment (check parameter roomsPerClass)");
      }
    }
  }

  public LanguageCommands(double p, Rooms r, List<Integer> c) {
    this.psi = p;
    this.rooms = r;
    this.classToObject = c;
    this.languageToRoomMapping = new HashMap<>();
    this.languageToClassMapping = new HashMap<>();
    this.languageObservation = new HashMap<>();
    this.roomReferences = new ArrayList<>();
  }


  //note can only parse single words
  public void setVocabularyForRooms() {
    Set<String> storage = new HashSet<>(Arrays.asList("storage", "pantry"));
    Set<String> library = new HashSet<>(Arrays.asList("library", "study"));
    Set<String> kitchen = new HashSet<>(Arrays.asList("kitchen"));
    Set<String> livingRoom = new HashSet<>(Arrays.asList("living", "family", "lounge")); //as in living room
    Set<String> roboticsRoom = new HashSet<>(Arrays.asList("robotics", "lab"));
    languageToRoomMapping.put(storage, 0);
    languageToRoomMapping.put(library, 1);
    languageToRoomMapping.put(kitchen, 2);
    languageToRoomMapping.put(livingRoom, 3);
    languageToRoomMapping.put(roboticsRoom, 4);
  }

  public void setVocabularyForClass() {
    Set<String> mug = new HashSet<>(Arrays.asList("mug", "mugs"));
    Set<String> book = new HashSet<>(Arrays.asList("book", "books"));
    Set<String> key = new HashSet<>(Arrays.asList("key", "keys"));
    Set<String> robot = new HashSet<>(Arrays.asList("robot"));
    Set<String> marker = new HashSet<>(Arrays.asList("marker"));
    languageToClassMapping.put(mug, 0);
    languageToClassMapping.put(book, 1);
    languageToClassMapping.put(key, 2);
    languageToClassMapping.put(robot, 3);
    languageToClassMapping.put(marker, 4);
  }

  //String objecsPerClass, int numberOfDefaultRooms, Rooms roomAbstractions
  public void parseLanguageCommandSimulation(Random rn) {
    rmRn = rn;

    //set rooms
    for (int r = 0; r < classToRooms.size(); r++) {
      int numRooms = classToRooms.get(r);
      Set<Integer> roomRef = new HashSet<>();

      //uniform condition
      if (numRooms == 0) numRooms = rooms.totalRoomsInEnvironment;

      while (roomRef.size() < numRooms) {
        int roomIndex = rmRn.nextInt(rooms.totalRoomsInEnvironment);
        if (!roomRef.contains(roomIndex)) roomRef.add(roomIndex);
      }
      this.roomReferences.add(roomRef);
    }
  }

  //cases:
  //0 words
  //1 words
  //two classes 2 words each
  //what happens when no words following a class?
  public void parseLanguageCommandReal(String languageCommand) {
    setVocabularyForClass();
    setVocabularyForRooms();

    //process langauge command
    String[] command = languageCommand.split("\\s+");
    Set<Integer> roomRefTemp = new HashSet<>();
    boolean firstTime = true;
    for (String s : command) {

      for (Map.Entry<Set<String>, Integer> c : languageToClassMapping.entrySet()) {
        if (c.getKey().contains(s)) {
          if (!firstTime) this.roomReferences.add(new HashSet<>(roomRefTemp));
          firstTime = false;
          roomRefTemp.clear();
        }
      }

      for (Map.Entry<Set<String>, Integer> r : languageToRoomMapping.entrySet()) {
        if (r.getKey().contains(s)) {
          roomRefTemp.add(r.getValue());
        }
      }
    }
    this.roomReferences.add(new HashSet<>(roomRefTemp));

    if (roomReferences.size() < classToObject.size()) {
      throw new RuntimeException("Error: missing referenced class in language");
    }

    //postprocess eliminate null strings
    for (int i = 0; i < roomReferences.size(); i++) {
      if (roomReferences.get(i).isEmpty()) {
        Set<Integer> set = new HashSet<>();
        for (Map.Entry<Set<String>, Integer> e : languageToRoomMapping.entrySet()) {
          set.add(e.getValue());
        }
        roomReferences.set(i, set);
      }
    }
  }

  public void languageObservation() {
    int O = 0;
    for (int c = 0; c < classToObject.size(); c++) {
      int normRefRooms = 0;
      int normNonRefRooms = 0;
      for (int i = 0; i < rooms.totalRoomsInEnvironment; i++) {
        if (roomReferences.get(c).contains(i)) normRefRooms += rooms.mappingFromRoomToCells.get(i).size();
        else normNonRefRooms += rooms.mappingFromRoomToCells.get(i).size();
      }
      Map<Location, Double> objObservation = objectLanguageObservation(c, normRefRooms, normNonRefRooms);
      for (int o = 0; o < classToObject.get(c); o++) {
        Map<Location, Double> addHeatMap = new HashMap<>(objObservation);
        languageObservation.put(O, addHeatMap);
        O++;
      }
    }
  }

  public Map<Location, Double> objectLanguageObservation(int classIndex, int Nrr, int Nnrr) {
    Map<Location, Double> objObservation = new HashMap<>();
    for (int i = 0; i < rooms.totalRoomsInEnvironment; i++) {
      for (Location l : rooms.mappingFromRoomToCells.get(i)) {
        double prob = (roomReferences.get(classIndex).contains(i)) ? (1.0 - psi) / Nrr : (psi) / Nnrr;
        objObservation.put(l, prob);
      }
    }
    return objObservation;
  }

  public Map<Integer, Map<Location, Double>> beliefUpdate(int numberOfObjects, Map<Integer, Map<Location, Double>> beliefPrior) {
    Map<Integer, Map<Location, Double>> updatedBelief = new HashMap<>();
    for (int o = 0; o < numberOfObjects; o++) {
      Map<Location, Double> updatedObjBelief = new HashMap<>();
      for (Location l : languageObservation.get(o).keySet()) {
        double prob = beliefPrior.get(o).get(l) * languageObservation.get(o).get(l);
        updatedObjBelief.put(l, prob);
      }
      //normalize
      Double normalize = 0.0;
      for (Double d : updatedObjBelief.values()) normalize += d;
      for (Location l : updatedObjBelief.keySet()) updatedObjBelief.put(l, updatedObjBelief.get(l) / normalize);
      updatedBelief.put(o, updatedObjBelief);
    }
    return updatedBelief;
  }
}
