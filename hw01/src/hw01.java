import java.util.ArrayList;
import java.util.Arrays;

public class hw01 {

    public static void main(String[] args) {
        //1. Написать метод, который меняет два элемента массива местами.
        ArrayReplacer<String> ArrayReplacer = new ArrayReplacer<>(new String[]{"I","drink","want to"});
        if(ArrayReplacer.replace(1,2)) {
            System.out.println(Arrays.toString(ArrayReplacer.getArray2Operate()));
        }
        ArrayReplacer<Integer> ArrayReplacer2 = new ArrayReplacer<>(new Integer[]{1,3,2});
        if(ArrayReplacer2.replace(1,2)) {
            System.out.println(Arrays.toString(ArrayReplacer2.getArray2Operate()));
        }

        //2. Написать метод, который преобразует массив в ArrayList;
        Array2ArrayList<Integer> Array2ArrayList = new Array2ArrayList<>(new Integer[]{11,12,13,14});
        ArrayList<Integer> transformedArray = Array2ArrayList.getArrayAsList();
        System.out.println(transformedArray.getClass().getName() + ": " + transformedArray.toString());


        //Задача про фрукты в корзинах

        System.out.println("\n\nFruits in boxes");
        Box<Apple> appleBox1 = new Box<>();
            appleBox1.addFruit(new Apple(Apple.getDefaultWeight()));
            appleBox1.addFruit(new Apple(Apple.getDefaultWeight()));
            appleBox1.addFruit(new Apple(Apple.getDefaultWeight()));
        Box<Apple> appleBox2 = new Box<>();
            appleBox2.addFruit(new Apple(Apple.getDefaultWeight()));
            appleBox2.addFruit(new Apple(Apple.getDefaultWeight()));
            appleBox2.addFruit(new Apple(Apple.getDefaultWeight()));
        Box<Orange> orangeBox1 = new Box<>();
            orangeBox1.addFruit(new Orange(Orange.getDefaultWeight()));
            orangeBox1.addFruit(new Orange(Orange.getDefaultWeight()));
        Box<Orange> orangeBox2 = new Box<>();
            orangeBox2.addFruit(new Orange(Orange.getDefaultWeight()));


        Box<Apple> appleBox3 = new Box<>();
        appleBox1.moveFruits(appleBox3);
        System.out.printf("Applebox1 weight: %f\n Applebox2 weight: %f\n Applebox3 weight: %f\n ",
                appleBox1.getWeight(), appleBox2.getWeight(), appleBox3.getWeight());
        System.out.println("orangebox1 compare applebox1: " + orangeBox1.compare(appleBox1));
    }


}
