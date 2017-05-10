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

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.monitor.LocalMapStats;
import jetbrains.buildServer.controllers.admin.AdminPage;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.PositionConstraint;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.github.rodm.teamcity.gradle.cache.BuildCacheServer.INSTANCE_NAME;
import static com.github.rodm.teamcity.gradle.cache.GradleBuildCachePlugin.PLUGIN_NAME;

public class BuildCachePage extends AdminPage {

    private static final String INCLUDE_URL = "buildCache.jsp";

    private static final String TITLE = "Gradle Build Cache";

    private static final String TASK_CACHE_NAME = "gradle-task-cache";

    public BuildCachePage(PagePlaces pagePlaces, PluginDescriptor descriptor) {
        super(pagePlaces);
        setPluginName(PLUGIN_NAME);
        setIncludeUrl(INCLUDE_URL);
        setTabTitle(TITLE);
        setPosition(PositionConstraint.last());
        addJsFile(descriptor.getPluginResourcesPath("buildCache.js"));
        register();
    }

    @NotNull
    @Override
    public String getGroup() {
        return PROJECT_RELATED_GROUP;
    }

    public void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request) {
        HazelcastInstance instance = Hazelcast.getHazelcastInstanceByName(INSTANCE_NAME);
        if (instance != null) {
            IMap<String, byte[]> taskCache = instance.getMap(TASK_CACHE_NAME);
            LocalMapStats statistics = taskCache.getLocalMapStats();
            model.put("statistics", statistics);
        }
    }
}
