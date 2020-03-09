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

package com.github.rodm.teamcity.gradle.cache;

import jetbrains.buildServer.agent.*;
import jetbrains.buildServer.agent.plugins.beans.PluginDescriptor;
import jetbrains.buildServer.util.EventDispatcher;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.rodm.teamcity.gradle.cache.GradleBuildCachePlugin.FEATURE_TYPE;
import static jetbrains.buildServer.log.Loggers.AGENT_CATEGORY;

public class GradleBuildCacheFeature extends AgentLifeCycleAdapter {

    private static final Logger LOG = Logger.getLogger(AGENT_CATEGORY + ".GradleBuildCache");

    private static final String GRADLE_CMD_PARAMS = "ui.gradleRunner.additional.gradle.cmd.params";

    private static final String ENABLE_BUILD_CACHE_ARG = "-Dorg.gradle.caching=true";

    private static final String BUILD_CACHE_HOST_ARG = "-Dorg.gradle.caching.hazelcast.host";

    private static final String INIT_SCRIPT_ARG = "--init-script";

    private static final String BUILD_CACHE_SCRIPT_PATH = "scripts/build-cache.gradle";

    private final PluginDescriptor descriptor;

    private String host;

    public GradleBuildCacheFeature(PluginDescriptor descriptor, EventDispatcher<AgentLifeCycleListener> eventDispatcher) {
        this.descriptor = descriptor;
        eventDispatcher.addListener(this);
    }

    @Override
    public void agentStarted(@NotNull BuildAgent agent) {
        BuildAgentConfiguration config = agent.getConfiguration();
        String serverUrl = config.getServerUrl();
        try {
            URI uri = new URI(serverUrl);
            host = uri.getHost();
        }
        catch (URISyntaxException e) {
            LOG.error("Failed to parse server URL", e);
        }
    }

    @Override
    public void beforeRunnerStart(@NotNull BuildRunnerContext runner) {
        if (hasGradleBuildCacheFeature(runner)) {
            File initScriptFile = new File(descriptor.getPluginRoot(), BUILD_CACHE_SCRIPT_PATH);
            Map<String, String> runnerParameters = runner.getRunnerParameters();
            List<String> parameters = new ArrayList<>();
            parameters.add(ENABLE_BUILD_CACHE_ARG);
            parameters.add(String.format("%s=%s", BUILD_CACHE_HOST_ARG, host));
            parameters.add(INIT_SCRIPT_ARG);
            parameters.add(initScriptFile.getAbsolutePath());
            parameters.add(runnerParameters.getOrDefault(GRADLE_CMD_PARAMS, ""));
            runner.addRunnerParameter(GRADLE_CMD_PARAMS, String.join(" ", parameters));
        }
    }

    boolean hasGradleBuildCacheFeature(BuildRunnerContext context) {
        AgentRunningBuild build = context.getBuild();
        return !build.getBuildFeaturesOfType(FEATURE_TYPE).isEmpty();
    }

    String getHost() {
        return host;
    }
}
