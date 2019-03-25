package TeamCityPlugins_GradleBuildCache

import jetbrains.buildServer.configs.kotlin.v2018_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2018_2.CheckoutMode
import jetbrains.buildServer.configs.kotlin.v2018_2.Template
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2018_2.project
import jetbrains.buildServer.configs.kotlin.v2018_2.projectFeatures.VersionedSettings
import jetbrains.buildServer.configs.kotlin.v2018_2.projectFeatures.versionedSettings
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2018_2.vcs.GitVcsRoot
import jetbrains.buildServer.configs.kotlin.v2018_2.version

version = "2018.2"

project {
    uuid = "8ea62758-8300-4649-9118-ce67379ce790"
    id("TeamCityPlugins_GradleBuildCache")
    parentId("TeamCityPlugins")
    name = "Gradle Build Cache"

    val vcsId = "TeamCityPlugins_GradleBuildCache_GradleBuildCache"
    val vcsRoot = GitVcsRoot({
        uuid = "81b63059-b4bd-4714-b9a7-5c169039423a"
        id(vcsId)
        name = "gradle-build-cache"
        url = "https://github.com/rodm/teamcity-gradle-build-cache-plugin.git"
        useMirrors = false
    })
    vcsRoot(vcsRoot)

    features {
        versionedSettings {
            id = "PROJECT_EXT_8"
            mode = VersionedSettings.Mode.ENABLED
            rootExtId = vcsId
            showChanges = true
            settingsFormat = VersionedSettings.Format.KOTLIN
            buildSettingsMode = VersionedSettings.BuildSettingsMode.PREFER_SETTINGS_FROM_VCS
        }
    }

    val buildTemplate = Template({
        uuid = "1a61b842-422b-4f54-9f7e-435d0b646b36"
        id("TeamCityPlugins_GradleBuildCache_Build")
        name = "build"

        vcs {
            root(vcsRoot)
            checkoutMode = CheckoutMode.ON_SERVER
        }

        steps {
            gradle {
                id = "RUNNER_30"
                tasks = "%gradle.tasks%"
                buildFile = ""
                gradleParams = "%gradle.opts%"
                useGradleWrapper = true
                enableStacktrace = true
                jdkHome = "%java8.home%"
            }
        }

        triggers {
            vcs {
                id = "vcsTrigger"
                branchFilter = ""
            }
        }

        features {
            feature {
                id = "perfmon"
                type = "perfmon"
            }
        }

        params {
            param("gradle.opts", "")
            param("gradle.tasks", "clean build")
        }
    })
    template(buildTemplate)

    val build = BuildType({
        templates(buildTemplate)
        uuid = "320aa7eb-8962-47dd-bebf-5997a5b2afdc"
        id("TeamCityPlugins_GradleBuildCache_BuildTeamCity100")
        name = "Build - TeamCity 10.0"
    })
    buildType(build)

    val reportCodeQuality = BuildType({
        templates(buildTemplate)
        uuid = "b769f56a-37a7-460b-9ebc-53ab556ea23f"
        id("TeamCityPlugins_GradleBuildCache_ReportCodeQuality")
        name = "Report - Code Quality"

        params {
            param("gradle.opts", "%sonar.opts%")
            param("gradle.tasks", "clean build sonarqube")
        }
    })
    buildType(reportCodeQuality)
}
