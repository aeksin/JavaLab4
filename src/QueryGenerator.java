import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class QueryGenerator implements Runnable {
    private int height;
    /**
     * probability both from and to != 0
     */
    private float notFromToFirstProbability; //
    /**
     * probability that from<to
     */
    private float upProbability;
    private AtomicBoolean generate;
    private AtomicBoolean kill;

    public void setGenerate(AtomicBoolean generate) {
        this.generate = generate;
    }

    public AtomicBoolean getGenerate() {
        return this.generate;
    }

    QueryGenerator() {
        this.height = 8;
        notFromToFirstProbability = 10f;
        upProbability = 50f;
        generate = new AtomicBoolean(false);
        kill = new AtomicBoolean(false);
    }

    QueryGenerator(int height, float notFromToFirstProbability, float upProbability) {
        this.height = height;
        this.notFromToFirstProbability = notFromToFirstProbability;
        this.upProbability = upProbability;
    }

    public void generateQuery() throws InterruptedException {
        Random rand = new Random();

        int from;
        int to;
        if (Math.random() * 100 <= notFromToFirstProbability) {
            from = rand.nextInt(this.height);
            to = rand.nextInt(this.height);
        } else if (Math.random() * 100 <= upProbability) {
            from = 0;
            to = rand.nextInt(this.height);
        } else {
            from = rand.nextInt(this.height);
            to = 0;
        }
        if (from == to) {
            return;
        }
        System.out.println("someone wants to go from " + from + " to " + to);
        synchronized (ElevatorSimulation.PeopleOnFloor) {
            synchronized (ElevatorSimulation.PeopleOnFloor.get(from)) {
                ElevatorSimulation.PeopleOnFloor.get(from).add(to);
            }
        }

    }

    @Override
    public void run() {
        while (true) {
            if (getKill().get()) {
                break;
            }
            if (getGenerate().get()) {
                generate = new AtomicBoolean(false);
                try {
                    generateQuery();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public AtomicBoolean getKill() {
        return kill;
    }

    public void setKill(AtomicBoolean kill) {
        this.kill = kill;
    }
}
