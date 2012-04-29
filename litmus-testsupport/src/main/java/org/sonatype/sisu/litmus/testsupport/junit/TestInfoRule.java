package org.sonatype.sisu.litmus.testsupport.junit;

import com.google.common.base.Preconditions;
import java.lang.annotation.Annotation;
import java.util.Collection;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.sonatype.sisu.litmus.testsupport.TestInfo;

/**
 * A JUnit {@link org.junit.Rule} implementation of {@link TestInfo}, with values derived from {@link Description}
 *
 * @see Description
 * @since 1.3
 */
public class TestInfoRule extends TestWatcher implements TestInfo
{
    private Description d;

	@Override
	protected void starting(final Description d) {
        this.d = Preconditions.checkNotNull(d);
	}

	/**
	 * {@inheritDoc }
	 */
    @Override
	public String getMethodName() {
		return d.getMethodName();
	}

    /**
     * {@inheritDoc }
	 */
    @Override
    public String getDisplayName() {
        return d.getDisplayName();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getClassName() {
        return d.getClassName();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<?> getTestClass() {
        return d.getTestClass();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return d.getAnnotation(annotationType);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Annotation> getAnnotations() {
        return d.getAnnotations();
    }

}
