import java.util.Arrays;

public class HW06 {

    public int[] objGetArrayTail(int[] startArray) throws WrongArrayFormatException{
        int last4index = -1;

        for (int i=0; i<startArray.length; i++){
            if (startArray[i]==4){
                last4index = i+1;
            }
        }

        if (last4index==-1){
            throw new WrongArrayFormatException("There is no 4 in array");
        }


        int[] returnArray = new int[startArray.length - last4index];
        int j=0;
        for (int i=last4index;i<startArray.length;i++){
            returnArray[j++] = startArray[i];
        }

        return returnArray;
    }

    public boolean checkArray14(int[] arr2test){
        int count1 = (int)Arrays.stream(arr2test).filter(i->i==1).count();
        int count4 = (int)Arrays.stream(arr2test).filter(i->i==4).count();
        return !(count1==0 || count4==0 || count1 + count4 != arr2test.length);
    }

}
