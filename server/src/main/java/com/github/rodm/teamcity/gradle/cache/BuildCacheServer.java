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

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import jetbrains.buildServer.serverSide.BuildServerAdapter;
import jetbrains.buildServer.serverSide.SBuildServer;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class BuildCacheServer extends BuildServerAdapter {

    private static final String INSTANCE_NAME = "TeamCityGradleBuildCache";

    private static final Logger LOGGER = Logger.getLogger("jetbrains.buildServer.SERVER");

    private HazelcastInstance hazelcastInstance;

    public BuildCacheServer(@NotNull SBuildServer server) {
        server.addListener(this);
    }

    @Override
    public void serverStartup() {
        LOGGER.info(getClass().getSimpleName() + " started");
        java.util.logging.Logger.getLogger("com.hazelcast").setLevel(Level.WARNING);
        Config config = new Config();
        config.setInstanceName(INSTANCE_NAME);
        config.getNetworkConfig()
                .setPort(5701)
                .getJoin()
                .getMulticastConfig()
                .setEnabled(false);
        hazelcastInstance = Hazelcast.newHazelcastInstance(config);
    }

    @Override
    public void serverShutdown() {
        hazelcastInstance.shutdown();
        LOGGER.info(getClass().getSimpleName() + " stopped");
    }
}
