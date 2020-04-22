import testSuite.AfterSuite;
import testSuite.BeforeSuite;
import testSuite.Test;

public class wrongClass2test {
        @BeforeSuite
        public void beforeSuiteMethod(){
            System.out.println("Before suite method invoked");
        }
        @AfterSuite
        public void AfterSuite(){
            System.out.println("After suite method invoked");
        }
        @AfterSuite
        public void AfterSuite2(){
            System.out.println("After suite method invoked");
        }
        @Test
        public void test91(){
            System.out.println("test 9.1 invoked");
        }
        @Test
        public void test92(){
            System.out.println("test 9.2 invoked");
        }
        @Test
        public void test4(){
            System.out.println("test 4 invoked");
        }
}
