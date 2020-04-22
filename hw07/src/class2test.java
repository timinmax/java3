import testSuite.AfterSuite;
import testSuite.BeforeSuite;
import testSuite.Test;

public class class2test {
    @BeforeSuite
    public void beforeSuiteMethod(){
        System.out.println("Before suite method invoked");
    }
    @AfterSuite
    public void AfterSuite(){
        System.out.println("After suite method invoked");
    }
    @Test(invokeOrder = 9)
    public void test91(){
        System.out.println("test 9.1 invoked");
    }
    @Test(invokeOrder = 9)
    public void test92(){
        System.out.println("test 9.2 invoked");
    }
    @Test(invokeOrder = 4)
    public void test4(){
        System.out.println("test 4 invoked");
    }
    @Test
    public void testDefaultOrder(){
        System.out.println("test default order invoked");
    }

}
