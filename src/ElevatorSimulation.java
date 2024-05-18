import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.sleep;
public class ElevatorSimulation {
    private int height;
    /**probability of query being generated per tick*/
    private float queryProbability;
    private int ticks;
    private Elevator[] elevators;
    private int numElevators;
    public static ConcurrentSkipListMap<Integer, ConcurrentLinkedQueue<Integer>> PeopleOnFloor;
    ElevatorSimulation(){
        this.height = 8;
        this.ticks = 100;
        numElevators = 2;
        queryProbability = 40f;
        this.elevators = new Elevator[numElevators];
        for (int i=0;i<numElevators;i++){
            elevators[i] = new Elevator(height,i);
        }
        PeopleOnFloor = new ConcurrentSkipListMap<>();

        for (int i=0;i<height;i++) {
            PeopleOnFloor.put(i, new ConcurrentLinkedQueue<>());
        }
    }
    public void runSimulation(){
        QueryGenerator generator  = new QueryGenerator();
        Thread queryGeneratorThread = new Thread(generator);
        Thread[] elevatorThreads = new Thread[numElevators];
        for (int elevator=0;elevator<numElevators;elevator++){
            elevatorThreads[elevator] = new Thread(elevators[elevator]);
        }
        queryGeneratorThread.start();
        for (int elevatorThread=0;elevatorThread<numElevators;elevatorThread++){
            elevatorThreads[elevatorThread].start();
        }
        for (int tick = 0;tick<ticks;tick++){
            if (Math.random()*100.0 <= queryProbability){
                generator.setGenerate(new AtomicBoolean(true));
                ArrayList<Pair<Integer,Integer>> emptyElevatorList = new ArrayList<>();
                for (int curElevator=0 ;curElevator<numElevators;curElevator++){
                    if (elevators[curElevator].isStaying()){
                        emptyElevatorList.add(new Pair<>(curElevator,elevators[curElevator].getCurrentFloor()));
                    }
                }
                Collections.sort(emptyElevatorList, new SortBySecond());
                ArrayList<Integer> NonEmptyFloorsList = new ArrayList<>();
                for (int floor=0;floor<height;floor++){
                    if (!PeopleOnFloor.get(floor).isEmpty()){
                        NonEmptyFloorsList.add(floor);
                    }
                }

                int down = 0;
                int up = NonEmptyFloorsList.size()-1;
                int downElevator = 0;
                int upElevator = emptyElevatorList.size()-1;
                int request = 0;
                if (NonEmptyFloorsList.isEmpty()){
                    continue;
                }
                while (down<=up  && downElevator<=upElevator){
                    if (request == 0){
                        int distFromDownToDownElevator = Math.abs(NonEmptyFloorsList.get(down) - emptyElevatorList.get(downElevator).getSecond());
                        int distFromDownToUpElevator = Math.abs(NonEmptyFloorsList.get(down) - emptyElevatorList.get(downElevator).getSecond());
                        if (distFromDownToDownElevator<=distFromDownToUpElevator){
                            elevators[emptyElevatorList.get(downElevator).getFirst()].setFrom(NonEmptyFloorsList.get(down));
                            elevators[emptyElevatorList.get(downElevator).getFirst()].setCalled(new AtomicBoolean(true));
                            downElevator++;

                        }
                        else{
                            elevators[emptyElevatorList.get(upElevator).getFirst()].setFrom(NonEmptyFloorsList.get(down));
                            elevators[emptyElevatorList.get(upElevator).getFirst()].setCalled(new AtomicBoolean(true));
                            upElevator--;
                        }

                        down++;
                    }
                    else if (request == 1){
                        int distFromUpToDownElevator = Math.abs(NonEmptyFloorsList.get(down) - emptyElevatorList.get(downElevator).getSecond());
                        int distFromUpToUpElevator = Math.abs(NonEmptyFloorsList.get(down) - emptyElevatorList.get(downElevator).getSecond());

                        if (distFromUpToDownElevator<distFromUpToUpElevator){
                            elevators[emptyElevatorList.get(downElevator).getFirst()].setFrom(NonEmptyFloorsList.get(up));
                            elevators[emptyElevatorList.get(downElevator).getFirst()].setCalled(new AtomicBoolean(true));
                            downElevator++;
                        }
                        else{
                            elevators[emptyElevatorList.get(upElevator).getFirst()].setFrom(NonEmptyFloorsList.get(up));
                            elevators[emptyElevatorList.get(upElevator).getFirst()].setCalled(new AtomicBoolean(true));
                            upElevator--;
                        }
                        up--;
                    }
                    request +=1;
                    request%=2;
                }
            }
            try{
                sleep(1000);

            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        generator.setKill(new AtomicBoolean(true));
        for (int elevatorThread=0;elevatorThread<numElevators;elevatorThread++){
            elevators[elevatorThread].setKill(new AtomicBoolean(true));
        }
    }
}
