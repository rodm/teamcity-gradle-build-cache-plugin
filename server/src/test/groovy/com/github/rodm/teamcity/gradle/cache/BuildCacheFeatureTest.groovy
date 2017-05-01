/*
 * Copyright 2017 Rod MacKenzie.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.rodm.teamcity.gradle.cache

import jetbrains.buildServer.web.openapi.PluginDescriptor
import org.junit.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.is
import static org.mockito.Mockito.eq
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class BuildCacheFeatureTest {

    @Test
    void 'gradle build cache feature configuration'() {
        PluginDescriptor descriptor = mock(PluginDescriptor)
        when(descriptor.getPluginResourcesPath(eq('editFeature.jsp'))).thenReturn('plugin/editFeature.jsp')
        BuildCacheFeature feature = new BuildCacheFeature(descriptor)

        assertThat(feature.getType(), equalTo('gradle-build-cache'))
        assertThat(feature.getDisplayName(), equalTo('Gradle Build Cache'))
        assertThat(feature.isMultipleFeaturesPerBuildTypeAllowed(), is(false))
        assertThat(feature.getEditParametersUrl(), equalTo('plugin/editFeature.jsp'))
    }
}
