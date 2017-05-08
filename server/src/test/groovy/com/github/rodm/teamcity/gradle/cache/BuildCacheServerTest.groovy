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
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.LifecycleService
import jetbrains.buildServer.serverSide.SBuildServer
import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.is
import static org.junit.Assert.assertNotNull
import static org.mockito.Mockito.eq
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify

class BuildCacheServerTest {

    private SBuildServer server

    private BuildCacheServer cacheServer

    @Before
    void setup() {
        server = mock(SBuildServer)
        cacheServer = new BuildCacheServer(server)
    }

    @After
    void cleanup() {
        Hazelcast.shutdownAll()
    }

    @Test
    void 'should register as a build server listener'() {
        verify(server).addListener(eq(cacheServer))
    }

    @Test
    void 'should not start hazelcast instance on creation'() {
        Set<HazelcastInstance> hazelcastInstances = Hazelcast.getAllHazelcastInstances()

        assertThat(hazelcastInstances, hasSize(0))
    }

    @Test
    void 'should start hazelcast instance after server has started'() {
        cacheServer.serverStartup()

        Set<HazelcastInstance> hazelcastInstances = Hazelcast.getAllHazelcastInstances()
        assertThat(hazelcastInstances, hasSize(1))
        LifecycleService lifecycle = hazelcastInstances[0].getLifecycleService()
        assertThat(lifecycle.isRunning(), is(true))
    }

    @Test
    void 'should stop hazelcast instance before server is shutdown'() {
        cacheServer.serverStartup()

        cacheServer.serverShutdown()

        Set<HazelcastInstance> hazelcastInstances = Hazelcast.getAllHazelcastInstances()
        assertThat(hazelcastInstances, hasSize(0))
    }

    @Test
    void 'should name the hazelcast instance'() {
        cacheServer.serverStartup()

        assertNotNull(Hazelcast.getHazelcastInstanceByName('TeamCityGradleBuildCache'))
    }
}
