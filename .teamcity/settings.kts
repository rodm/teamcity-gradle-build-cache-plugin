
import jetbrains.buildServer.configs.kotlin.v2019_2.Template
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2019_2.project
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.VcsTrigger.QuietPeriodMode.USE_DEFAULT
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot
import jetbrains.buildServer.configs.kotlin.v2019_2.version

version = "2020.1"

project {

    val vcsId = "GradleBuildCache"
    val vcsRoot = GitVcsRoot {
        id(vcsId)
        name = "gradle-build-cache"
        url = "https://github.com/rodm/teamcity-gradle-build-cache-plugin.git"
        useMirrors = false
    }
    vcsRoot(vcsRoot)

    val buildTemplate = Template {
        id("Build")
        name = "build"

        vcs {
            root(vcsRoot)
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
                quietPeriodMode = USE_DEFAULT
                branchFilter = ""
                triggerRules = """
                    -:.github/**
                    -:README.adoc
                """.trimIndent()
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
    }
    template(buildTemplate)

    val build1 = buildType {
        templates(buildTemplate)
        id("Build1")
        name = "Build - TeamCity 2018.1"
    }

    val build2 = buildType {
        templates(buildTemplate)
        id("Build2")
        name = "Build - TeamCity 2020.1"

        params {
            param("gradle.opts", "-Pteamcity.api.version=2020.1")
        }
    }

    val reportCodeQuality = buildType {
        templates(buildTemplate)
        id("ReportCodeQuality")
        name = "Report - Code Quality"

        params {
            param("gradle.opts", "%sonar.opts%")
            param("gradle.tasks", "clean build sonarqube")
        }
    }

    buildTypesOrder = arrayListOf(build1, build2, reportCodeQuality)
}
