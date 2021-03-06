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
import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.Matchers.is
import static org.junit.Assert.assertNotNull

class BuildCacheServerTest {

    private BuildCacheServer cacheServer

    @Before
    void setup() {
        cacheServer = new BuildCacheServer()
    }

    @After
    void cleanup() {
        Hazelcast.shutdownAll()
    }

    @Test
    void 'should not start hazelcast instance before start'() {
        Set<HazelcastInstance> hazelcastInstances = Hazelcast.getAllHazelcastInstances()

        assertThat(hazelcastInstances, hasSize(0))
    }

    @Test
    void 'should start hazelcast instance after server has started'() {
        cacheServer.start()

        Set<HazelcastInstance> hazelcastInstances = Hazelcast.getAllHazelcastInstances()
        assertThat(hazelcastInstances, hasSize(1))
        LifecycleService lifecycle = hazelcastInstances[0].getLifecycleService()
        assertThat(lifecycle.isRunning(), is(true))
    }

    @Test
    void 'should stop hazelcast instance before server is shutdown'() {
        cacheServer.start()

        cacheServer.stop()

        Set<HazelcastInstance> hazelcastInstances = Hazelcast.getAllHazelcastInstances()
        assertThat(hazelcastInstances, hasSize(0))
    }

    @Test
    void 'should name the hazelcast instance'() {
        cacheServer.start()

        assertNotNull(Hazelcast.getHazelcastInstanceByName('TeamCityGradleBuildCache'))
    }
}
