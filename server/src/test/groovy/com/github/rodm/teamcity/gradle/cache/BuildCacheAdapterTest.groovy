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

import jetbrains.buildServer.serverSide.SBuildServer
import org.junit.Before
import org.junit.Test

import static org.mockito.Mockito.eq
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify

class BuildCacheAdapterTest {

    private SBuildServer buildServer

    private BuildCacheServer cacheServer

    private BuildCacheAdapter cacheAdapter

    @Before
    void setup() {
        buildServer = mock(SBuildServer)
        cacheServer = mock(BuildCacheServer)
        cacheAdapter = new BuildCacheAdapter(this.cacheServer, buildServer)
    }

    @Test
    void 'should register as a build server listener'() {
        verify(buildServer).addListener(eq(cacheAdapter))
    }

    @Test
    void 'should start build cache server after server has started'() {
        cacheAdapter.serverStartup()

        verify(cacheServer).start()
    }

    @Test
    void 'should stop build cache server before server is shutdown'() {
        cacheAdapter.serverShutdown()

        verify(cacheServer).stop()
    }
}
