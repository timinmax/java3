import java.util.ArrayList;
import java.util.Arrays;

public class Array2ArrayList<T> {
    private T[] simpleArray;

    public Array2ArrayList(T[] simpleArray) {
        this.simpleArray = simpleArray;
    }

    public ArrayList<T> getArrayAsList(){
        ArrayList<T> arrToReturn = new ArrayList();
        Arrays.stream(simpleArray).forEach(e->arrToReturn.add(e));
        return arrToReturn;
    }
}
