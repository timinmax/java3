import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class tester14 {
    private static HW06 hw06;
    private int[] arr2test;
    private boolean resultExp;

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
                {new int[]{1, 1, 1, 4, 4, 1, 4, 4 },    true},
                {new int[]{1, 1, 1, 1, 1, 1 },          false},
                {new int[]{4, 4, 4, 4},                 false},
                {new int[]{1, 4, 4, 1, 1, 4, 3},        false}

        });
    }

    public tester14(int[] arr2test, boolean resultExp) {
        this.arr2test = arr2test;
        this.resultExp = resultExp;
    }

    @Test
    public void testGetArrayTail(){
        Assert.assertEquals(hw06.checkArray14(arr2test),resultExp);
    }

}
