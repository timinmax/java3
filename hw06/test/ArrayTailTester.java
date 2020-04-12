import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ArrayTailTester {

    private static HW06 hw06;
    private int[] arr1;
    private int[] arr2;
    private Class<? extends Exception> expectedException;
    private String expectedExceptionMsg;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @BeforeClass
    public static void initTest(){
        hw06 = new HW06();
    }

    @AfterClass
    public static void finishTest() throws Exception{
        hw06 = null;
    }

    @Parameters
    public static Collection data(){
        return Arrays.asList(new Object[][]{
                {new int[]{1,2,3,4,5},      new int[]{5},       null,                               null},
                {new int[]{3,8,9,8,7,4},    new int[]{},        null,                               null},
                {new int[]{3,8,4,9,8,7,5},  new int[]{9,8,7,5}, null,                               null},
                {new int[]{3,8,9,8,7},      null,               WrongArrayFormatException.class,    "There is no 4 in array"}

        });
    }

    public ArrayTailTester(int[] arr1, int[] arr2, Class<? extends Exception> wrongArrayFormatException, String expectedMessage) {
        this.arr1 = arr1;
        this.arr2 = arr2;
        this.expectedException = wrongArrayFormatException;
        this.expectedExceptionMsg = expectedMessage;
    }

    @Test
    public void testGetArrayTail() throws WrongArrayFormatException{
        if (expectedException != null){
            thrown.expect(expectedException);
            thrown.expectMessage(expectedExceptionMsg);
        }
        Assert.assertArrayEquals(hw06.objGetArrayTail(arr1),arr2);
    }

}
