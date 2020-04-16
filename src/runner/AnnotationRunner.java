package runner;

import annotation.AfterSuite;
import annotation.BeforeSuite;
import annotation.Test;
import someclass.SomeClass;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class AnnotationRunner {

    final static int MIN_PRIORITY = 1;
    final static int MAX_PRIORITY = 10;

    public static void main(String[] args) {
        AnnotationRunner.start(SomeClass.class);
    }

    public static void start(Class clazz){
        try {
            Object object = clazz.newInstance();
            Map<Integer, Set<Method>> methodMap = getMethodMap(clazz);
            for (Map.Entry<Integer, Set<Method>> pair: methodMap.entrySet())
            {
                Set<Method> methodSet = pair.getValue();
                for (Method method : methodSet){
                    try {
                        method.invoke(object);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static Map<Integer, Set<Method>> getMethodMap(Class clazz){
        Map<Integer, Set<Method>> methodMap = new HashMap<>();

        for(Method method : clazz.getMethods()){
            if (method.getAnnotation(BeforeSuite.class) != null){
                setBeforeSuite(methodMap, method);
            }
            if (method.getAnnotation(Test.class) != null){
                Test test = method.getAnnotation(Test.class);
                setTest(methodMap, method, test);
            }
            if (method.getAnnotation(AfterSuite.class) != null){
                setAfterSuite(methodMap, method);
            }
        }
        return methodMap;
    }

    private static void setAfterSuite(Map<Integer, Set<Method>> methodMap, Method method) {
        Set<Method> methodSet;
        if (methodMap.get(MAX_PRIORITY+1) == null){
            methodSet = new HashSet<>();
            methodSet.add(method);
            methodMap.put(MAX_PRIORITY+1, methodSet);
        } else {
            throw new RuntimeException();
        }
    }

    private static void setBeforeSuite(Map<Integer, Set<Method>> methodMap, Method method) {
        Set<Method> methodSet;
        if (methodMap.get(MIN_PRIORITY-1) == null){
            methodSet = new HashSet<>();
            methodSet.add(method);
            methodMap.put(MIN_PRIORITY-1, methodSet);
        } else {
            throw new RuntimeException();
        }
    }

    private static void setTest(Map<Integer, Set<Method>> methodMap, Method method, Test test) {
        Set<Method> methodSet;
        if (test.priority() < MIN_PRIORITY || test.priority() > MAX_PRIORITY){
            System.out.println(method.getName() + " неккоректный приоритет выполнения тест-метода. Минимальный приоритет 1, максимальный - 10.");
            return;
        }
        if (methodMap.get(test.priority()) == null){
            methodSet = new HashSet<>();
            methodSet.add(method);
            methodMap.put(test.priority(), methodSet);
        } else {
            methodMap.get(test.priority()).add(method);
        }
    }
}
