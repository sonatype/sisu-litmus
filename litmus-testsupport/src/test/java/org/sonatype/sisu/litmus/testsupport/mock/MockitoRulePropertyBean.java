/*
 * Copyright (c) 2007-2012 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.sisu.litmus.testsupport.mock;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Tests @InjectMocks property injection
 * @see MockitoRuleTest
 * @since 1.3
 */
public class MockitoRulePropertyBean {

    private HashMap<String, String> mockedMap;
    private LinkedHashMap<String, String> spyMap;

    // no-arg constructor required for setter inject
    public MockitoRulePropertyBean() {
    }

    public void setSpyMap(LinkedHashMap<String, String> spyMap) {
        this.spyMap = spyMap;
    }

    public void setMockedMap(HashMap<String, String> mockedMap) {
        this.mockedMap = mockedMap;
    }

    public HashMap<String, String> getMockedMap() {
        return mockedMap;
    }

    public LinkedHashMap<String, String> getSpyMap() {
        return spyMap;
    }
}
