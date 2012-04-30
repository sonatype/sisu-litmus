package org.sonatype.sisu.litmus.testsupport.junit;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Test {@link org.sonatype.sisu.litmus.testsupport.junit.TestInfoRule}
 *
 * @since 1.3
 */
@TestInfoRuleTest.ValuedAnnotation("test class annotation for testing TestInfoRule.getAnnotation()")
public class TestInfoRuleTest {

    /**
     * Annotation for testing TestInfoRule.getAnnotation(Class c)
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ValuedAnnotation {
        String value();
    }

    @Rule
    public TestInfoRule infoRule = new TestInfoRule();

    @ClassRule
    public static TestInfoRule classInfoRule = new TestInfoRule();

    /**
     * The goal here is not to test {@link org.junit.runner.Description}, but that {@link TestInfoRule} derives values from it as we expect
     */
    @Test
    @ValuedAnnotation("test method annotation for testing TestInfoRule.getAnnotation()")
    public void testRule() {
        assertThat(infoRule.getClassName(), equalTo("org.sonatype.sisu.litmus.testsupport.junit.TestInfoRuleTest"));
        assertThat(infoRule.getMethodName(), equalTo("testRule"));
        assertThat(infoRule.getDisplayName(), equalTo("testRule(org.sonatype.sisu.litmus.testsupport.junit.TestInfoRuleTest)"));
        assertThat(infoRule.getTestClass().getCanonicalName(), equalTo("org.sonatype.sisu.litmus.testsupport.junit.TestInfoRuleTest"));
        assertThat(infoRule.getAnnotations(), hasSize(2)); // checking for annotations on this test method
        assertThat(infoRule.getAnnotation(Test.class), notNullValue());
        assertThat(infoRule.getAnnotation(ValuedAnnotation.class).value(),
                equalTo("test method annotation for testing TestInfoRule.getAnnotation()"));
    }

    @Test
    public void testClassRule() {
        assertThat(classInfoRule.getClassName(), equalTo("org.sonatype.sisu.litmus.testsupport.junit.TestInfoRuleTest"));
        assertThat(classInfoRule.getMethodName(), nullValue());
        assertThat(classInfoRule.getDisplayName(), equalTo("org.sonatype.sisu.litmus.testsupport.junit.TestInfoRuleTest"));
        assertThat(classInfoRule.getTestClass().getCanonicalName(), equalTo("org.sonatype.sisu.litmus.testsupport.junit.TestInfoRuleTest"));
        assertThat(classInfoRule.getAnnotations(), hasSize(1)); //annotations on this test class
        assertThat(classInfoRule.getAnnotation(ValuedAnnotation.class).value(), equalTo("test class annotation for testing TestInfoRule.getAnnotation()"));
    }

}
