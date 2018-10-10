#!/bin/bash

#chmod +x theScript.sh
#scp ObjectSearchWorld.jar theScript.sh awandzel@ssh.cs.brown.edu:/home/awandzel/Beefy5Layer/OOOPOMCP
#scp -r awandzel@ssh.cs.brown.edu:/home/awandzel/Documents/Experiments/out .
#qsub -l vlong -l vf=60G -cwd -m bes theScript.sh
#qstat -u "*"
#qdel

map="A"
condition="Objects"
actions="1"
S="10000"
I="1"
vision="3"
accuracy="1.0"
SD=".001"
solution="Pomcp" #Pomcp Random
title=OOPOMCP"_$map""_$condition"$accuracy

# echo $title
# exit 0

samp=(10 100 1000 10000)
obj=(2 4 6 8)

for o in "${obj[@]}"
do
  java -jar -Xms60g -Xmx60g ObjectSearchWorld.jar $title $solution $o 10000 $actions $map $accuracy $SD $vision false 0 0 $I
  sleep 15s
done
# java -jar -Xms60g -Xmx60g ObjectSearchWorld.jar $title $solution 4 $S $actions $map $accuracy $SD $vision true .05 3  $I
# sleep 15s
# java -jar -Xms60g -Xmx60g ObjectSearchWorld.jar $title $solution 4 $S $actions $map $accuracy $SD $vision false .05 0  $I
# sleep 15s
# java -jar -Xms60g -Xmx60g ObjectSearchWorld.jar $title $solution 4 $S $actions $map $accuracy $SD $vision false .05 3  $I
# sleep 15s
# java -jar -Xms60g -Xmx60g ObjectSearchWorld.jar $title $solution 4 $S $actions $map $accuracy $SD $vision false .05 1  $I

              # "Command line arguments must be in order and given as string literals.\n" +
              #         "For example, solutionMethod is set as \"VI\" not -S \"VI\"\n" +
              #         "Help prints this message and exits.\n\n" +
              #         "H - \"help\" \"-h\"\n" +
              #         "N - name of experiment\n" +
              #         "S - solutionMethod {visualMDP, VI, BSS, POMCP}\n" +
              #         "O- class-object membership {e.g. '3' = 1 class, 3 objects, '1,1,1' = 3 classes, 1 object each  \n" +
              #         "SS - samples {-1-1000}\n" +
              #         "MA - Max number of actions {0-5000}\n" +
              #         "M - map {e.g. arthur, toy}\n" +
              #         "A - observationAccuracy {.5-1.0}\n" +
              #         "Sdv - standard deviation of event A in observation model\n" +
              #         "V - visionDepth\n" +
              #         "Ad - adversarial\n" +
              #         "Ps - Psi language error\n" +
              #         "Lc - class-room membership as referenced in language command  (e.g. 0,1,2 corresponding to '1,1,1' = 0 rooms for first Y, 1 room for second class, 3 rooms for last class \n" +
              #         "I - experimentIterations {0-100}\n");
