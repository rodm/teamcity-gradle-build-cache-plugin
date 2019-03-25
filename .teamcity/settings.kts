
import jetbrains.buildServer.configs.kotlin.v2018_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2018_2.CheckoutMode
import jetbrains.buildServer.configs.kotlin.v2018_2.Template
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2018_2.project
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2018_2.vcs.GitVcsRoot
import jetbrains.buildServer.configs.kotlin.v2018_2.version

version = "2018.2"

project {

    val vcsId = "GradleBuildCache"
    val vcsRoot = GitVcsRoot({
        id(vcsId)
        name = "gradle-build-cache"
        url = "https://github.com/rodm/teamcity-gradle-build-cache-plugin.git"
        useMirrors = false
    })
    vcsRoot(vcsRoot)

    val buildTemplate = Template({
        id("Build")
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
        id("BuildTeamCity100")
        name = "Build - TeamCity 10.0"
    })
    buildType(build)

    val reportCodeQuality = BuildType({
        templates(buildTemplate)
        id("ReportCodeQuality")
        name = "Report - Code Quality"

        params {
            param("gradle.opts", "%sonar.opts%")
            param("gradle.tasks", "clean build sonarqube")
        }
    })
    buildType(reportCodeQuality)
}
