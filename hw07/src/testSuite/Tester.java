package testSuite;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


public class Tester {
    public static void start(Class classTest){
        doTest(classTest);
    }

    public static void start(String classTestName){
        try {
            Class classTest = Class.forName(classTestName);
            doTest(classTest);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    static void doTest(Class classTest){
        System.out.println("Test started for " + classTest);

        if(checkTest(classTest)) {
            try {
                Object instance2test = classTest.newInstance();
                runBeforeAfterSuiteAnnotations(instance2test,BeforeSuite.class);
                runTests(instance2test);
                runBeforeAfterSuiteAnnotations(instance2test,AfterSuite.class);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }

    private static boolean checkTest(Class classTest) throws RuntimeException {
        if (getAnnotatedMethods(classTest, BeforeSuite.class).size()>1){
            throw new RuntimeException("Multiple BeforeSuite annotations declared. Max 1 allowed.");
        }else if (getAnnotatedMethods(classTest, AfterSuite.class).size()>1){
            throw new RuntimeException("Multiple AfterSuite annotations declared. Max 1 allowed.");
        }
        return true;
    }

    private static void runBeforeAfterSuiteAnnotations(Object instance2test, Class annotationType) {
        ArrayList<Method>  methods = getAnnotatedMethods(instance2test, annotationType);
        if (methods.size() == 1){
            try {
                methods.get(0).invoke(instance2test);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private static void runTests( Object instance2test) {
        ArrayList<Method> methods = getAnnotatedMethods(instance2test, Test.class);
        methods.sort(Comparator.comparing(Tester::getMethodTestOrder));
        for (Method m:methods) {
            try {
                m.invoke(instance2test);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private static ArrayList<Method> getAnnotatedMethods(Object instance2test, Class annotationClass){
        return getAnnotatedMethods(instance2test.getClass(),annotationClass);
    }
    private static ArrayList<Method> getAnnotatedMethods(Class testClass, Class annotationClass){
        ArrayList<Method> listOfMethods = new ArrayList<>();
        Method[] methods = testClass.getDeclaredMethods();

        for (Method method: methods){
            if(method.getAnnotation(annotationClass)!=null){
                listOfMethods.add(method);
            }
        }

        return listOfMethods;
    }

    private static Integer getMethodTestOrder(Method method){
        return method.getAnnotation(Test.class).invokeOrder();
    }
}
