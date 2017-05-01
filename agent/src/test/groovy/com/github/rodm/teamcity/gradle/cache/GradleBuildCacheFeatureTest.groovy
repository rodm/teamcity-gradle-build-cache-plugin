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

import jetbrains.buildServer.agent.AgentBuildFeature
import jetbrains.buildServer.agent.AgentLifeCycleListener
import jetbrains.buildServer.agent.AgentRunningBuild
import jetbrains.buildServer.agent.BuildAgent
import jetbrains.buildServer.agent.BuildAgentConfiguration
import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.plugins.beans.PluginDescriptor
import jetbrains.buildServer.util.EventDispatcher
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor

import static org.hamcrest.CoreMatchers.containsString
import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.CoreMatchers.is
import static org.mockito.ArgumentMatchers.contains
import static org.mockito.ArgumentMatchers.endsWith
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.ArgumentMatchers.matches
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.never
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

class GradleBuildCacheFeatureTest {

    public static final String GRADLE_CMD_PARAMS = 'ui.gradleRunner.additional.gradle.cmd.params'

    private PluginDescriptor descriptor

    private EventDispatcher<AgentLifeCycleListener> eventDispatcher

    private GradleBuildCacheFeature feature

    private boolean hasGradleBuildCacheFeature = true

    @Before
    void setup() {
        descriptor = mock(PluginDescriptor)
        eventDispatcher = EventDispatcher.create(AgentLifeCycleListener)
        feature = new GradleBuildCacheFeature(descriptor, eventDispatcher) {
            @Override
            boolean hasGradleBuildCacheFeature(BuildRunnerContext context) {
                return hasGradleBuildCacheFeature;
            }
        }
    }

    @Test
    void 'feature is enabled if build has feature of type gradle-build-cache'() {
        GradleBuildCacheFeature feature = new GradleBuildCacheFeature(descriptor, eventDispatcher)

        AgentRunningBuild build = mock(AgentRunningBuild)
        when(build.getBuildFeaturesOfType(eq('gradle-build-cache'))).thenReturn([mock(AgentBuildFeature)])

        BuildRunnerContext context = mock(BuildRunnerContext)
        when(context.getBuild()).thenReturn(build)

        assertThat(feature.hasGradleBuildCacheFeature(context), is(true))
    }

    @Test
    void 'feature is disabled if build does not have a feature type of gradle-build-cache'() {
        GradleBuildCacheFeature feature = new GradleBuildCacheFeature(descriptor, eventDispatcher)

        AgentRunningBuild build = mock(AgentRunningBuild)
        when(build.getBuildFeaturesOfType(eq('gradle-build-cache'))).thenReturn([])

        BuildRunnerContext context = mock(BuildRunnerContext)
        when(context.getBuild()).thenReturn(build)

        assertThat(feature.hasGradleBuildCacheFeature(context), is(false))
    }

    @Test
    void 'when feature is enabled an additional --init-script arg is passed to Gradle'() {
        BuildRunnerContext runner = mock(BuildRunnerContext)

        feature.beforeRunnerStart(runner)

        verify(runner).addRunnerParameter(eq(GRADLE_CMD_PARAMS), contains('--init-script'))
    }

    @Test
    void 'when feature is enabled the --init-script parameter value should be the path to an init script'() {
        when(descriptor.getPluginRoot()).thenReturn(new File('pluginDir'))
        BuildRunnerContext runner = mock(BuildRunnerContext)

        feature.beforeRunnerStart(runner)

        verify(runner).addRunnerParameter(eq(GRADLE_CMD_PARAMS), matches('.*--init-script .*/scripts/build-cache.gradle.*'))
    }

    @Test
    void 'when feature is disabled no addtional --init-script arg is passed to Gradle'() {
        hasGradleBuildCacheFeature = false
        BuildRunnerContext runner = mock(BuildRunnerContext)

        feature.beforeRunnerStart(runner)

        verify(runner, never()).addRunnerParameter(eq(GRADLE_CMD_PARAMS), contains('--init-script'))
    }

    @Test
    void 'when feature is enabled an additional argument to enable the build cache is passed to Gradle'() {
        BuildRunnerContext runner = mock(BuildRunnerContext)

        feature.beforeRunnerStart(runner)

        verify(runner).addRunnerParameter(eq(GRADLE_CMD_PARAMS), contains('-Dorg.gradle.caching=true'))
    }

    @Test
    void 'when feature is disabled no additional argument to enable the build cache is passed to Gradle'() {
        hasGradleBuildCacheFeature = false
        BuildRunnerContext runner = mock(BuildRunnerContext)

        feature.beforeRunnerStart(runner)

        verify(runner, never()).addRunnerParameter(eq(GRADLE_CMD_PARAMS), contains('-Dorg.gradle.caching=true'))
    }

    @Test
    void 'enable cache and init script parameters should be before additional Gradle parameters'() {
        BuildRunnerContext runner = mock(BuildRunnerContext)
        when(runner.getRunnerParameters()).thenReturn([(GRADLE_CMD_PARAMS): '-Pproperty=value'])

        feature.beforeRunnerStart(runner)

        verify(runner).addRunnerParameter(eq(GRADLE_CMD_PARAMS), endsWith('-Pproperty=value'))
    }

    @Test
    void 'when agent starts the cache host name is established'() {
        BuildAgentConfiguration configuration = mock(BuildAgentConfiguration)
        when(configuration.getServerUrl()).thenReturn('https://teamcity-server.local:8111/')
        BuildAgent agent = mock(BuildAgent)
        when(agent.getConfiguration()).thenReturn(configuration)

        feature.agentStarted(agent)

        assertThat(feature.getHost(), equalTo('teamcity-server.local'))
    }

    @Test
    void 'when feature is enabled an additional argument with the cache hostname is passed to Gradle'() {
        BuildAgentConfiguration configuration = mock(BuildAgentConfiguration)
        when(configuration.getServerUrl()).thenReturn('https://teamcity-server.local:8111/')
        BuildAgent agent = mock(BuildAgent)
        when(agent.getConfiguration()).thenReturn(configuration)
        BuildRunnerContext runner = mock(BuildRunnerContext)

        feature.agentStarted(agent)
        feature.beforeRunnerStart(runner)

        verify(runner).addRunnerParameter(eq(GRADLE_CMD_PARAMS), contains('-Dorg.gradle.caching.hazelcast.host=teamcity-server.local'))
    }

    @Test
    void 'when feature is disabled no additional argument with the cache hostname is passed to Gradle'() {
        hasGradleBuildCacheFeature = false
        BuildRunnerContext runner = mock(BuildRunnerContext)

        feature.beforeRunnerStart(runner)

        verify(runner, never()).addRunnerParameter(eq(GRADLE_CMD_PARAMS), contains('-Dorg.gradle.caching.hazelcast.host'))
    }

    @Test
    void 'check additional and existing command line arguments are correctly separated'() {
        when(descriptor.getPluginRoot()).thenReturn(new File('pluginDir'))
        BuildAgentConfiguration configuration = mock(BuildAgentConfiguration)
        when(configuration.getServerUrl()).thenReturn('https://teamcity-server.local:8111/')
        BuildAgent agent = mock(BuildAgent)
        when(agent.getConfiguration()).thenReturn(configuration)
        BuildRunnerContext runner = mock(BuildRunnerContext)
        when(runner.getRunnerParameters()).thenReturn([(GRADLE_CMD_PARAMS): '-Pproperty=value'])

        feature.agentStarted(agent)
        feature.beforeRunnerStart(runner)

        ArgumentCaptor<String> parametersCaptor = ArgumentCaptor.forClass(String)
        verify(runner).addRunnerParameter(eq(GRADLE_CMD_PARAMS), parametersCaptor.capture())

        List<String> parameters = parametersCaptor.value.split(' ')
        assertThat(parameters[0], equalTo('-Dorg.gradle.caching=true'))
        assertThat(parameters[1], equalTo('-Dorg.gradle.caching.hazelcast.host=teamcity-server.local'))
        assertThat(parameters[2], equalTo('--init-script'))
        assertThat(parameters[3], containsString('pluginDir/scripts/build-cache.gradle'))
        assertThat(parameters[4], equalTo('-Pproperty=value'))
    }
}
