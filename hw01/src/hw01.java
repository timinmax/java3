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
    }
}
