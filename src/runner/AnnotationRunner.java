package runner;

import annotation.AfterSuite;
import annotation.BeforeSuite;
import annotation.Test;
import someclass.SomeClass;

import java.lang.annotation.Annotation;
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
        Set<Method> methodSet;

        for(Method method : clazz.getMethods()){
            for(Annotation annotation : method.getAnnotations()){
                if (method.getAnnotation(BeforeSuite.class) != null){
                    if (methodMap.get(MIN_PRIORITY-1) == null){
                        methodSet = new HashSet<>();
                        methodSet.add(method);
                        methodMap.put(MIN_PRIORITY-1, methodSet);
                    } else {
                        throw new RuntimeException();
                    }
                }
                if (method.getAnnotation(Test.class) != null){
                    Test test = method.getAnnotation(Test.class);
                    methodSet = getTestSet(methodMap, method, test);
                    if (methodSet == null) {
                        System.out.println(method.getName() + " неккоректный приоритет выполнения тест-метода. Минимальный приоритет 1, максимальный - 10.");
                        break;
                    }
                    methodMap.put(test.priority(), methodSet);
                }
                if (method.getAnnotation(AfterSuite.class) != null){
                    if (methodMap.get(MAX_PRIORITY+1) == null){
                        methodSet = new HashSet<>();
                        methodSet.add(method);
                        methodMap.put(MAX_PRIORITY+1, methodSet);
                    } else {
                        throw new RuntimeException();
                    }
                }
            }
        }
        return methodMap;
    }

    private static Set<Method> getTestSet(Map<Integer, Set<Method>> methodMap, Method method, Test test) {
        Set<Method> methodSet;
        if (test.priority() < MIN_PRIORITY || test.priority() > MAX_PRIORITY){
            return null;
        }
        if (methodMap.get(test.priority()) == null){
            methodSet = new HashSet<>();
        } else {
            methodSet = methodMap.get(test.priority());
            methodMap.remove(test.priority());
        }
        methodSet.add(method);
        return methodSet;
    }
}
