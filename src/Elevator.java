import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import static java.lang.Thread.sleep;

public class Elevator implements Runnable {
    public AtomicBoolean getKill() {
        return kill;
    }

    public void setKill(AtomicBoolean kill) {
        this.kill = kill;
    }

    enum directions{
        DOWN,
        UP,
        STAYING;
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
    public int[] destinations;
    public int in;
    private directions direction;
    private int currentFloor;
    private int from;
    private int numberOfElevator;
    private AtomicBoolean called;
    private AtomicBoolean kill;
    public void setFrom(int from) {
        this.from = from;
    }
    public void setCalled(AtomicBoolean called) {
        this.called = called;
    }
    public AtomicBoolean getCalled() {
        return called;
    }
    public int getCurrentFloor() {
        return this.currentFloor;
    }
    Elevator(int height,int number){
        currentFloor = 0;
        called = new AtomicBoolean(false);
        direction = directions.STAYING;
        destinations = new int[height];
        kill = new AtomicBoolean(false);
        this.numberOfElevator = number;
    }
    public boolean isStaying(){
        return direction == directions.STAYING;
    }

    @Override
    public void run(){
        while (true){
            if (getKill().get()){
                break;
            }
            if (isStaying() && getCalled().get()){
                called = new AtomicBoolean(false);
                try{
                    System.out.println("elevator №"+numberOfElevator+" called from "+currentFloor+" to "+from);
                    if (currentFloor<from){
                        direction = directions.UP;
                        while (currentFloor<from){
                            Thread.sleep(1000);
                            currentFloor++;
                            System.out.println("elevator №"+numberOfElevator+" going up "+currentFloor);
                        }
                    }
                    else if (currentFloor >from){
                        direction = directions.DOWN;
                        while (currentFloor>from){
                            sleep(1000);
                            currentFloor--;
                            System.out.println("elevator №"+numberOfElevator+" going down "+currentFloor);
                        }
                    }

                    synchronized (ElevatorSimulation.PeopleOnFloor){
                        synchronized (ElevatorSimulation.PeopleOnFloor.get(currentFloor)) {
                            if (ElevatorSimulation.PeopleOnFloor.get(currentFloor).isEmpty()){
                                System.out.println("elevator №"+numberOfElevator+" arrived at empty floor");
                                direction = directions.STAYING;
                                continue;
                            }
                            int to = ElevatorSimulation.PeopleOnFloor.get(currentFloor).peek();
                            if (to<from){
                                direction = directions.DOWN;
                            }
                            else{
                                direction = directions.UP;
                            }
                            takePeopleFromFloor();
                        }
                        while (in != 0){
                            if (direction == directions.DOWN){
                                sleep(1000);
                                currentFloor--;
                                if (destinations[currentFloor]>0){
                                    in -= destinations[currentFloor];
                                    destinations[currentFloor] = 0;
                                }
                                takePeopleFromFloor();
                            }
                            if (direction == directions.UP){
                                sleep(1000);
                                currentFloor++;
                                if (destinations[currentFloor]>0){
                                    in -= destinations[currentFloor];
                                    destinations[currentFloor] = 0;
                                }
                                takePeopleFromFloor();
                            }
                            System.out.println("elevator №"+numberOfElevator+" moving "+direction+", currently on "+currentFloor+" floor with "+in+" people in elevator");
                        }
                        direction = directions.STAYING;
                    }

                }
                catch (Exception e){
                    System.out.println(e.getMessage());
                }

            }
        }
    }
    public void takePeopleFromFloor(){
        synchronized (ElevatorSimulation.PeopleOnFloor){
            synchronized (ElevatorSimulation.PeopleOnFloor.get(currentFloor)){
                ArrayList<Integer> staying = new ArrayList<Integer>();
                for (Integer to:ElevatorSimulation.PeopleOnFloor.get(currentFloor)){

                    if (to<currentFloor && direction == directions.DOWN){
                        destinations[to]++;
                        System.out.println("man gets into the elevator №"+numberOfElevator+" to go from "+currentFloor+" to "+to);
                        in++;
                    }
                    else if (to>currentFloor && direction == directions.UP){
                        destinations[to]++;
                        System.out.println("man gets into the elevator №"+numberOfElevator+" to go from "+currentFloor+" to "+to);
                        in++;
                    }
                    else{
                        staying.add(to);
                    }

                }
                ElevatorSimulation.PeopleOnFloor.get(currentFloor).clear();
                for (int person: staying){
                    ElevatorSimulation.PeopleOnFloor.get(currentFloor).add(person);
                }
            }
        }


    }

}


























