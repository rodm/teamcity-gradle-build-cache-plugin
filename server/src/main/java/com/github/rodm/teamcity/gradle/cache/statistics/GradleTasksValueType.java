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

package com.github.rodm.teamcity.gradle.cache.statistics;

import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.statistics.ChartSettings;
import jetbrains.buildServer.serverSide.statistics.ValueProviderRegistry;
import jetbrains.buildServer.serverSide.statistics.build.BuildDataStorage;
import jetbrains.buildServer.serverSide.statistics.build.CompositeVTB;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class GradleTasksValueType extends CompositeVTB {

    private static Map<String, String> taskOutcomes = new LinkedHashMap<>();

    static {
        taskOutcomes.put("GradleTasksExecuted", "Executed");
        taskOutcomes.put("GradleTasksFromCache", "From Cache");
        taskOutcomes.put("GradleTasksNoSource", "No Source");
        taskOutcomes.put("GradleTasksSkipped", "Skipped");
        taskOutcomes.put("GradleTasksUpToDate", "Up To Date");
    }

    GradleTasksValueType(BuildDataStorage storage, ValueProviderRegistry registry, SBuildServer server) {
        super(storage, registry, server, "GradleTasks");
    }

    @Override
    public String[] getSubKeys() {
        Set<String> keys = taskOutcomes.keySet();
        return keys.toArray(new String[keys.size()]);
    }

    @NotNull
    @Override
    public String getDescription(ChartSettings chartSettings) {
        return "Gradle Build Cache";
    }

    @Override
    public String getSeriesName(String subKey, int idx) {
        return taskOutcomes.get(subKey);
    }

    @Override
    public String getSeriesGenericName() {
        return "Task outcome";
    }
}
