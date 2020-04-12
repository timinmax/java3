import java.util.concurrent.Semaphore;

public class Tunnel extends Stage {
    private Semaphore tunnelSemaphore;
    public Tunnel(int tunnelCapacity) {
        this.length = 80;
        this.description = "Тоннель " + length + " метров";
        //"вместимость можно было бы объявить в классе Stage, но согласно "ТЗ" это свойство только у тоннеля.
        tunnelSemaphore = new Semaphore(tunnelCapacity, true);
    }
    @Override
    public void go(Car c) {
        try {
            try {
                System.out.println(c.getName() + " готовится к этапу(ждет): " + description);
                tunnelSemaphore.acquire();
                System.out.println(c.getName() + " начал этап: " + description);
                Thread.sleep(length / c.getSpeed() * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println(c.getName() + " закончил этап: " + description);
                tunnelSemaphore.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}