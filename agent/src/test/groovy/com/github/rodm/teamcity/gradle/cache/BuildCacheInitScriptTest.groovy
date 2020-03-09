/*
 * Copyright 2019 Rod MacKenzie.
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

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import java.nio.file.Path
import java.nio.file.Paths

import static org.hamcrest.CoreMatchers.containsString
import static org.hamcrest.CoreMatchers.not
import static org.hamcrest.MatcherAssert.assertThat

class BuildCacheInitScriptTest {

    @Rule
    public final TemporaryFolder projectDir = new TemporaryFolder()

    @Test
    void 'applying build cache script with Gradle 4.0 applies Hazelcast plugin v0.11'() {
        BuildResult result = executeBuild('4.0.2')

        assertThat(result.getOutput(), containsString('Applying Hazelcast plugin version: 0.11'))
    }

    @Test
    void 'applying build cache script with Gradle 3.5 applies Hazelcast plugin v0.9'() {
        BuildResult result = executeBuild('3.5.1')

        assertThat(result.getOutput(), containsString('Applying Hazelcast plugin version: 0.9'))
    }

    @Test
    void 'applying build cache script with Gradle 3.4 does not apply Hazelcast plugin'() {
        BuildResult result = executeBuild('3.4.1')

        assertThat(result.getOutput(), not(containsString('Applying Hazelcast plugin version:')))
        assertThat(result.getOutput(), containsString('Gradle Build Cache is disabled'))
    }

    @Test
    void 'applying build cache script with Gradle 5.x applies Hazelcast plugin'() {
        BuildResult result = executeBuild('5.6.4')

        assertThat(result.getOutput(), containsString('Applying Hazelcast plugin version'))
    }

    @Test
    void 'applying build cache script with Gradle 6.x applies Hazelcast plugin'() {
        BuildResult result = executeBuild('6.2.2')

        assertThat(result.getOutput(), containsString('Applying Hazelcast plugin version'))
    }

    private BuildResult executeBuild(String version) {
        Path initScriptPath = Paths.get('scripts', 'build-cache.gradle').toAbsolutePath()
        BuildResult result = GradleRunner.create()
            .withProjectDir(projectDir.getRoot())
            .withArguments('--info', '--init-script', initScriptPath.toString(), 'tasks')
            .withGradleVersion(version)
            .forwardOutput()
            .build()
        return result
    }
}
