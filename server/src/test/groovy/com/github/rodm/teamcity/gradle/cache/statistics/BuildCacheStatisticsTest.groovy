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

package com.github.rodm.teamcity.gradle.cache.statistics

import jetbrains.buildServer.serverSide.SBuildServer
import jetbrains.buildServer.serverSide.statistics.ValueProviderRegistry
import jetbrains.buildServer.serverSide.statistics.build.BuildDataStorage
import jetbrains.buildServer.serverSide.statistics.build.SimpleBuildMetricVT
import org.junit.Before
import org.junit.Test

import static org.hamcrest.Matchers.arrayWithSize
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.hasItemInArray
import static org.hamcrest.MatcherAssert.assertThat
import static org.mockito.Mockito.mock

class BuildCacheStatisticsTest {

    private GradleTasksValueType valueType

    @Before
    void setup() {
        BuildDataStorage storage = mock(BuildDataStorage)
        ValueProviderRegistry registry = mock(ValueProviderRegistry)
        SBuildServer server = mock(SBuildServer)

        valueType = new GradleTasksValueType(storage, registry, server)
    }

    @Test
    void 'provides series keys for Gradle task counts'() {
        assertThat(this.valueType.getSubKeys(), arrayWithSize(6))
        assertThat(this.valueType.getSubKeys(), hasItemInArray('GradleTasksTotal'))
        assertThat(this.valueType.getSubKeys(), hasItemInArray('GradleTasksExecuted'))
        assertThat(this.valueType.getSubKeys(), hasItemInArray('GradleTasksFromCache'))
        assertThat(this.valueType.getSubKeys(), hasItemInArray('GradleTasksNoSource'))
        assertThat(this.valueType.getSubKeys(), hasItemInArray('GradleTasksSkipped'))
        assertThat(this.valueType.getSubKeys(), hasItemInArray('GradleTasksUpToDate'))
    }

    @Test
    void 'provides series names for Gradle task counts'() {
        assertThat(valueType.getSeriesName('GradleTasksTotal', 0), equalTo('Total'))
        assertThat(valueType.getSeriesName('GradleTasksExecuted', 0), equalTo('Executed'))
        assertThat(valueType.getSeriesName('GradleTasksFromCache', 0), equalTo('From Cache'))
        assertThat(valueType.getSeriesName('GradleTasksNoSource', 0), equalTo('No Source'))
        assertThat(valueType.getSeriesName('GradleTasksSkipped', 0), equalTo('Skipped'))
        assertThat(valueType.getSeriesName('GradleTasksUpToDate', 0), equalTo('Up To Date'))
    }

    @Test
    void 'provides series descriptions for Gradle task counts'() {
        assertThat(valueProviderDescription(valueType, 'GradleTasksTotal'), equalTo('Total tasks'))
        assertThat(valueProviderDescription(valueType, 'GradleTasksExecuted'), equalTo('Executed tasks'))
        assertThat(valueProviderDescription(valueType, 'GradleTasksFromCache'), equalTo('From cache tasks'))
        assertThat(valueProviderDescription(valueType, 'GradleTasksNoSource'), equalTo('No source tasks'))
        assertThat(valueProviderDescription(valueType, 'GradleTasksSkipped'), equalTo('Skipped tasks'))
        assertThat(valueProviderDescription(valueType, 'GradleTasksUpToDate'), equalTo('Up to date tasks'))
    }

    private static String valueProviderDescription(GradleTasksValueType valueProvider, String subKey) {
        return (valueProvider.createValueProviderForSubkey(subKey) as SimpleBuildMetricVT).getDescription(null)
    }
}
