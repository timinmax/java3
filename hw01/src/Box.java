import java.util.ArrayList;

public class Box <T extends Fruit> {
    private ArrayList <T> boxContent;

    public Box() {
        this.boxContent = new ArrayList<T>();
    }

    public void addFruit(T fruit){
        boxContent.add(fruit);
    }

    public float getWeight(){
        float totalWeight = 0f;
        for (T e: boxContent) totalWeight+= e.getWeight();
        return totalWeight;
    }

    public boolean compare(Box anotherBox){
        return getWeight()==anotherBox.getWeight();
    }

    public void moveFruits(Box<T> anotherBox){
        for(T e: boxContent)anotherBox.addFruit(e);
        boxContent.clear();
    }


}
