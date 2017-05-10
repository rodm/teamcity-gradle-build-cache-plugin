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

import jetbrains.buildServer.web.openapi.ControllerAction
import jetbrains.buildServer.web.openapi.WebControllerManager
import org.jdom.Element
import org.junit.Before
import org.junit.Test

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.CoreMatchers.is
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.ArgumentMatchers.isA
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

class BuildCacheControllerTest {

    private BuildCacheServer cacheServer

    @Before
    void setup() {
        cacheServer = mock(BuildCacheServer)
    }

    @Test
    void 'should register with controller manager'() {
        WebControllerManager controllerManager = mock(WebControllerManager)
        BuildCacheController controller = new BuildCacheController(controllerManager, cacheServer)

        verify(controllerManager).registerController(eq('/admin/buildCache.html'), eq(controller))
    }

    @Test
    void 'should register a clear cache action'() {
        WebControllerManager controllerManager = mock(WebControllerManager)

        BuildCacheController controller = new BuildCacheController(controllerManager, cacheServer)

        verify(controllerManager).registerAction(eq(controller), isA(BuildCacheController.ClearCacheAction))
    }

    @Test
    void 'action should process clear cache actions'() {
        ControllerAction action = new BuildCacheController.ClearCacheAction(cacheServer)
        HttpServletRequest request = mock(HttpServletRequest)
        when(request.getParameter(eq('action'))).thenReturn('clear')

        assertThat(action.canProcess(request), is(true))
    }

    @Test
    void 'action should not process other actions'() {
        ControllerAction action = new BuildCacheController.ClearCacheAction(cacheServer)
        HttpServletRequest request = mock(HttpServletRequest)
        when(request.getParameter(eq('action'))).thenReturn('someOtherAction')

        assertThat(action.canProcess(request), is(false))
    }

    @Test
    void 'clear action clears the build cache'() {
        HttpServletRequest request = mock(HttpServletRequest)
        HttpServletResponse response = mock(HttpServletResponse)
        ControllerAction action = new BuildCacheController.ClearCacheAction(cacheServer)

        action.process(request, response, new Element("response"))

        verify(cacheServer).clear()
    }
}
