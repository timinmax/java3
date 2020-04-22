import testSuite.Tester;

public class HW07 {
    public static void main(String[] args) {
        Tester.start(class2test.class);
        Tester.start(wrongClass2test.class.getName());
    }
}
