package utils;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * RetryTransformer - The "Injector"
 * Automatically applies the RetryAnalyzer to every @Test method in the framework
 * so you don't have to add it manually to each test class.
 */
public class RetryAnnotationTransformer implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
}