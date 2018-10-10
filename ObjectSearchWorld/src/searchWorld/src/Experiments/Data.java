package searchWorld.src.Experiments;

import burlap.behavior.singleagent.Episode;
import searchWorld.src.Environments.pomdpEpisode;

import java.util.List;

/**
 * Created by arthurwandzel on 11/14/17.
 */
public class Data {
     public pomdpEpisode episode;
   	 public double timeInSeconds;
   	 public int numberOfObjectsFound;

   	 public Data(){}

   	public Data(pomdpEpisode e, double t, int o){
   		 this.episode = e;
   		 this.timeInSeconds = t;
   		 this.numberOfObjectsFound = o;
   	 }

	static public int calculateNumberOfObjectsFound(List<Double> rewardSequence, double goalReward, double pickReward){
		int numberOfObjectsFound = 0;
		for (double r : rewardSequence) {
			if (r == goalReward) numberOfObjectsFound++;
			if (r == pickReward) numberOfObjectsFound--;
		}
		return numberOfObjectsFound;
	 }

	 static public void printData(double elapsedTime, Episode e, int numberOfObjectsFound){
		 System.out.println("\nProgram End!");
		 System.out.println("Time: " + elapsedTime);
		 double totalReward = 0;
		 for (double r : e.rewardSequence) totalReward += r;
		 System.out.println("Reward: " + totalReward);
		 System.out.println("Actions: " + e.actionSequence.size());
		 System.out.println("Objects: " + numberOfObjectsFound);
	 }
}

  /*
      //Baxter 5 obj, 1 lookDepth, 5 experiments, 150 actions, 10RRT, randomseed 1
Program End!
Time: 238.242
Reward: 1530.0
Actions: 150

Program End!
Time: 263.819
Reward: 1530.0
Actions: 150

Program End! --> much faster with less of a rollout!
Time: 111.356
Reward: 1530.0
Actions: 150

Time: 219.547 --> better with BFS vs DFS
Reward: 2260.0
Actions: 150

Program End! --> better with fully factored observations!
Time: 111.779
Reward: 3950.0
Actions: 70

Program End!
Time: 163.064
Reward: 3960.0
Actions: 69

Program End!
Time: 82.438 --> faster with rollouts set to 25 (but fails if set to 10)
Reward: 4340.0
Actions: 47

Program End!
Time: 78.278 --> faster w/o multipleObjsObservation
Reward: 4340.0
Actions: 47

Program End!
Time: 70.346 --> faster with object, location ordering
Reward: 4340.0
Actions: 47

Program End! --> depth 3 vision sensor....-__-
Time: 86.58
Reward: 4660.0
Actions: 27

Program End! --> broken...
Time: 254.796
Reward: 2180.0
Actions: 150

Program End! --> sequential!
Time: 148.728
Reward: 630.0
Actions: 123
Objects: 2

Program End! --> multiobject!
Time: 101.038
Reward: 1090.0
Actions: 77
Objects: 2
   */