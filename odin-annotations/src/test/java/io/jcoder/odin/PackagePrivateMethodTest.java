/**
 *  Copyright 2019 JCoder Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.jcoder.odin;

import static io.jcoder.odin.annotation.builder.AnnotationAwareRegistrationBuilder.annotated;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.jcoder.odin.inner.GardenDoor;

/**
 * This test covers the case of "overridden" package private methods that are injected in class hierarchies where the
 * parent and child classes are in different packages. In this case, overriding doesn't actually occur and both methods
 * should be injected.
 *
 * @author Camilo Gonzalez
 */
public class PackagePrivateMethodTest {

    @Test
    public void testPackagePrivate() throws Exception {
        InjectionContext context = new DefaultInjectionContext();
        context.register(annotated(GardenDoor.class));
        context.initialize();

        GardenDoor gardenDoor = context.get(GardenDoor.class);
        assertTrue(gardenDoor.isSuperInjected());
        assertTrue(gardenDoor.isSubInjected());
    }

}
