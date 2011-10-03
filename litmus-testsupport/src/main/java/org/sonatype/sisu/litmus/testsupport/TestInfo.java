package org.sonatype.sisu.litmus.testsupport;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * Information about the currently executing test.
 *
 * @author plynch
 * @since 1.0
 */
public interface TestInfo {

    /**
	 * @return the annotation of type annotationType that is attached to this description node,
	 * or null if none exists
	 */
	<T extends Annotation> T getAnnotation(Class<T> annotationType);

	/**
	 * @return all of the annotations attached to this description node
	 */
	Collection<Annotation> getAnnotations();

    /**
	 * @return If this describes a method invocation,
	 * the name of the class of the test instance
	 */
    String getClassName();

    /**
	 * @return a user-understandable label
	 */
    String getDisplayName();

    /**
	 * @return If this describes a method invocation,
	 * the name of the method (or null if not)
	 */
    String getMethodName();

    /**
	 * @return If this describes a method invocation,
	 * the class of the test instance.
	 */
    Class<?> getTestClass();
}
