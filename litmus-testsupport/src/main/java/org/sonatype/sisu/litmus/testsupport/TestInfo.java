package org.sonatype.sisu.litmus.testsupport;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * Information about the currently executing test.
 *
 * @since 1.3
 */
public interface TestInfo {

    /**
     * @return the annotation of type annotationType that is attached to the test,
     *         or null if none exists
     */
    <T extends Annotation> T getAnnotation(Class<T> annotationType);

    /**
     * @return all of the annotations attached to the test
     */
    Collection<Annotation> getAnnotations();

    /**
     * @return If this describes a method invocation,
     *         the name of the class of the test instance
     */
    String getClassName();

    /**
     * @return a user-understandable label describing the test
     */
    String getDisplayName();

    /**
     * @return If this describes a method invocation,
     *         the name of the method (or null if not)
     */
    String getMethodName();

    /**
     * @return the class of the test instance, if this describes a method invocation,
     *         .
     */
    Class<?> getTestClass();
}
