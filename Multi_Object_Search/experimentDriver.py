import click #Command Line Interface Creation Kit
import random
import Multi_Object_Search.Maps as envMap
from Multi_Object_Search.Pomdp import Location as utils

@click.command()
@click.option('--exp', required=True, type=str, help='Name of experiment')
@click.option('--mem', required=True, type=str, help='Class-object membership (e.g. \'1,2,1\' 3 classes of 1,2,1 objects)')
@click.option('--mem2', required=True, type=str, help='Class-room membership (e.g. \'0,1,2\' 3 classes in 0,1,2 rooms)')
@click.option('--sam', required=True, type=int, help='Number of Monte-Carlo samples')
@click.option('--act', required=True, type=int, help='Max number of actions')
@click.option('--map', required=True, type=str, help='Name of map')
@click.option('--obs', required=True, type=float, help='Accuracy of observation model')
@click.option('--sdv', required=True, type=float, help='Standard deviation of event A in observation model')
@click.option('--dep', required=True, type=int, help='Vision cone depth')
@click.option('--adv', required=True, type=bool, help='Adversarial command')
@click.option('--psi', required=True, type=float, help='Psi language error')
@click.option('--itr', required=True, type=int, help='Number of experiment iterations')

#Example: experimentDriver.py --exp=test --mem=2 --mem2=0 --sam=10000 --act=10 --map=arthur --obs=1.0 --sdv=.0001 --dep=2 --adv=False --psi=0 --itr=1

def experimentDriver(exp, mem, mem2, sam, act, map, obs, sdv, dep, adv, psi, itr):
    fileName = exp + "__" + "Mo-" + mem + "_Mc-" + mem2 + "_S-" + str(sam) + "_A-" + str(act) \
               + "_M-" + map + "_Oa-" + str(obs) + "_Os-" + str(sdv) + "_D-" + str(dep) + "_Ad-" + str(adv) \
               + "_P-" + str(psi) + ".txt";
    #omit itr for aggregating over iterations
    print(fileName)

    # //////////////////////////////////////////////////////////////////////////////////////
    # ////////////////////////////// PARAMETERS ////////////////////////////////////////////
    # //////////////////////////////////////////////////////////////////////////////////////

    #POMCP parameters
    pomcpHeight = 25
    pomcpReinvigorate = True
    pomcpReinvigorateSamples = 1000
    pomcpExploration = 999
    pomcpDiscount = .95

    #POMDP parameters
    objectReward = 1000
    actionReward = -10

    #Observation model parameters (V in visionCone / NV not in visionCone
    betaV = (1.0 - obs) / 2.0
    gammaV = (1.0 - obs) / 2.0
    alphaV = obs
    betaNV = (1.0 - obs) / 2.0
    gammaNV = obs
    alphaNV = (1.0 - obs) / 2.0

    #-----------Program------------
    startState = utils.Location(1,1) #8,13 ROBOT EXPERIMENT
    #set true if manually specifying goal location via map
    manualMapSpecification = True
    ROBOTEXPERIMENT = False

    #-----------RRT------------
    numberOfSecondsForRRTSamples = 10

    #-----------Debug----------
    debugPrintOuts = 4 #{0:none, 1:env steps, 2:belief, 3:RRT, 4:other}
    issuePomdpVisualizer = True

    #sets random seed constant for goal placement & RRT nodes.
    preprocessingConsistency = True

    #sets random seed constant for POMDP algorithm (sampling / action selection)
    algorithmConsistency = True

    #No uncertianity POMCP for checking if agent can reach goals with RRT nodes
    issueNoUncertainityPOMCP = False;

    # //////////////////////////////////////////////////////////////////////////////////////
    # ////////////////////////////// EXPERIMENT ////////////////////////////////////////////
    # //////////////////////////////////////////////////////////////////////////////////////
    #//----------------------------Set Randomness------------------------------------
    POMCPRn = random.Random(0) if algorithmConsistency else random.Random()
    observationRn = random.Random(0) if algorithmConsistency else random.Random()
    objDistraRn = random.Random(0) if algorithmConsistency else random.Random()

    objectRn = random.Random(0) if preprocessingConsistency else random.Random()
    languageRn = random.Random(0) if preprocessingConsistency else random.Random()
    RRTRn = random.Random(0) if preprocessingConsistency else random.Random()

    # //----------------------------Set Maps------------------------------------
    Maps = envMap.selectMap(map);
    totalNumberOfUncertainCells = 0
    for x in range(len(Maps.semanticMap)):
        for y in range(len(Maps.semanticMap[x])):
            if Maps.semanticMap[x][y] > envMap.E and Maps.occupancyMap[x][y] != envMap.Y:
                totalNumberOfUncertainCells += 1
                Maps.beliefMap[x][y] = envMap.U
    


    # if (searchCommand.equals("searchRoom")) {
    #   int totalNumberOfUncertainCells = 0;
    #   System.out.print(" " + semanticMap[0].length + "," + semanticMap.length);
    #   for (int x = 0; x < semanticMap.length; x++) {
    #     for (int y = 0; y < semanticMap[0].length; y++) {
    #       if (semanticMap[x][y] > EMPTY && occupancyMap[x][y] != MAPCENTER) {
    #         totalNumberOfUncertainCells++;
    #         beliefMap[x][y] = UNCERTAIN;
    #       }
    #     }
    #   }
    #   System.out.print(" " + totalNumberOfUncertainCells + "\n");
    # }





if __name__ == '__main__':
    experimentDriver()



