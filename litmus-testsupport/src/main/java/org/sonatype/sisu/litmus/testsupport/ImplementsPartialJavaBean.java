/*
 * Sonatype Overlord (TM)
 * Copyright (C) 2011 Sonatype, Inc. All rights reserved.
 * Includes the third-party code listed at http://www.sonatype.com/products/overlord/attributions/.
 * "Sonatype" and "Sonatype Overlord" are trademarks of Sonatype, Inc.
 */

package org.sonatype.sisu.litmus.testsupport;

import com.google.common.collect.Maps;
import org.mockito.internal.stubbing.defaultanswers.ReturnsMoreEmptyValues;
import org.mockito.invocation.InvocationOnMock;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * ???
 *
 * @since 1.0
 */
public class ImplementsPartialJavaBean
    extends ReturnsMoreEmptyValues
    implements Serializable
{
    private final Map<String, Object> methodToValueMap = Maps.newConcurrentMap();

    @Override
    public Object answer(final InvocationOnMock invocation) throws Throwable {
        Method method = invocation.getMethod();
        if (method.getName().startsWith("set") && (method.getParameterTypes().length == 1)) {
            if (invocation.getArguments()[0] != null) {
                methodToValueMap.put(method.getName().substring(3), invocation.getArguments()[0]);
            }
            else {
                methodToValueMap.remove(method.getName().substring(3));
            }
            return null;
        }
        else if (method.getName().startsWith("is") && (method.getParameterTypes().length == 0)) {
            Object retval = methodToValueMap.get(method.getName().substring(2));
            if (retval != null) {
                return retval;
            }
        }
        else if (method.getName().startsWith("get") && (method.getParameterTypes().length == 0)) {
            Object retval = methodToValueMap.get(method.getName().substring(3));
            if (retval != null) {
                return retval;
            }
        }

        return super.answer(invocation);
    }
}
