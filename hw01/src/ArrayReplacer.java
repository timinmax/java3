public class ArrayReplacer<T> {
    private T[] array2Operate;

    public ArrayReplacer(T[] array2Operate) {
        this.array2Operate = array2Operate;
    }

    public T[] getArray2Operate() {
        return array2Operate;
    }

    public boolean replace(int idxFrom, int idxTo){
        try {
            T tmp = array2Operate[idxFrom];
            array2Operate[idxFrom] = array2Operate[idxTo];
            array2Operate[idxTo] = tmp;
        }catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Указан некорректный индекс массива.");
            System.out.println(e.getStackTrace());
            return false;
        }
        return true;
    }
}
