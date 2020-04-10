import java.util.concurrent.CountDownLatch;

public class Car implements Runnable {
    private static int CARS_COUNT;
    static {
        CARS_COUNT = 0;
    }
    private Race race;
    private int speed;
    private String name;
    private CountDownLatch startFlag,finishFlag;

    public String getName() {
        return name;
    }
    public int getSpeed() {
        return speed;
    }
    public Car(Race race, CountDownLatch startFlag,CountDownLatch finishFlag, int speed) {
        this.race = race;
        this.speed = speed;
        this.startFlag = startFlag;
        this.finishFlag = finishFlag;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
    }
    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int)(Math.random() * 800));
            System.out.println(this.name + " готов");
        } catch (Exception e) {
            e.printStackTrace();
        }
        startFlag.countDown();
        try {
            startFlag.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);
        }
        if (finishFlag.getCount() == CARS_COUNT){
            System.out.println(getName() + " - WIN");
            //на одном из прогонов программы я получил 2 WIN.
            //Больше это не удалось воспроизвести.
        }
        finishFlag.countDown();
    }
}