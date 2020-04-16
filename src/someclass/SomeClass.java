package someclass;

import annotation.AfterSuite;
import annotation.BeforeSuite;
import annotation.Test;

public class SomeClass {

    @Test(priority = 1)
    public void method1(){
        System.out.println("method1, priority 1");
    }

    @Test(priority = 12)
    public void method2(){
        System.out.println("method2, priority 12");
    }

    @Test(priority = 7)
    public void method3(){
        System.out.println("method3, priority 7");
    }

    @Test(priority = 10)
    public void method4(){
        System.out.println("method4, priority 10");
    }

    @Test(priority = 7)
    public void method5(){
        System.out.println("method5, priority 7");
    }

    @Test(priority = 3)
    public void method6(){
        System.out.println("method6, priority 3");
    }

    @BeforeSuite
    public void beforeMethod(){
        System.out.println("beforeMethod");
    }

    @AfterSuite
    public void afterMethod(){
        System.out.println("afterMethod");
    }

//    @AfterSuite
//    public void afterMethod2(){
//        System.out.println("afterMethod");
//    }
}
