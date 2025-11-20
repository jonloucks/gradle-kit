package io.github.jonloucks.gradle.kit.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Tools {
    /**
     * Asserts that a class can NOT be instantiated
     *
     * @param theClass the class to check
     */
    public static void assertInstantiateThrows(Class<?> theClass) {
        final Throwable thrown = assertThrows(Throwable.class, () -> {
            final Constructor<?> constructor = theClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });
        
        assertTrue(thrown instanceof IllegalAccessException ||
                thrown instanceof InaccessibleObjectException ||
                thrown instanceof InvocationTargetException ||
                thrown instanceof NoSuchMethodException ||
                thrown instanceof AssertionError
            , "Exception thrown not expected " + thrown.getClass().getName() + ".");
    }
}
