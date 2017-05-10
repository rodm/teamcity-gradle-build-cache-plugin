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

import com.hazelcast.core.Hazelcast
import jetbrains.buildServer.serverSide.SBuildServer
import jetbrains.buildServer.web.openapi.PagePlace
import jetbrains.buildServer.web.openapi.PagePlaces
import jetbrains.buildServer.web.openapi.PlaceId
import jetbrains.buildServer.web.openapi.PluginDescriptor
import org.junit.After
import org.junit.Before
import org.junit.Test

import javax.servlet.http.HttpServletRequest

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.hasKey
import static org.hamcrest.Matchers.not
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.any
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class BuildCachePageTest {

    private BuildCachePage page

    @Before
    void setup() {
        PagePlaces pagePlaces = mock(PagePlaces)
        when(pagePlaces.getPlaceById(any(PlaceId))).thenReturn(mock(PagePlace))
        PluginDescriptor descriptor = mock(PluginDescriptor)
        when(descriptor.getPluginResourcesPath(eq('buildCache.js'))).thenReturn('pluginResourcesPath/buildCache.js')
        page = new BuildCachePage(pagePlaces, descriptor)
    }

    @After
    void cleanup() {
        Hazelcast.shutdownAll()
    }

    @Test
    void 'should configure page'() {
        assertThat(page.getPluginName(), equalTo('gradleBuildCache'))
        assertThat(page.getTabTitle(), equalTo('Gradle Build Cache'))
        assertThat(page.getIncludeUrl(), equalTo('buildCache.jsp'))
    }

    @Test
    void 'page fills model with cache statistics'() {
        SBuildServer server = mock(SBuildServer)
        BuildCacheServer cacheServer = new BuildCacheServer(server)
        cacheServer.serverStartup()
        HttpServletRequest request = mock(HttpServletRequest)
        Map<String, Object> model = [:]

        page.fillModel(model, request)

        assertThat(model, hasKey('statistics'))
    }

    @Test
    void 'page does not fill model with statistics if cache not found'() {
        HttpServletRequest request = mock(HttpServletRequest)
        Map<String, Object> model = [:]

        page.fillModel(model, request)

        assertThat(model, not(hasKey('statistics')))
    }
}
