/*配置插件仓库的 通过plugins块引入的Gradle插件 */
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        maven {
            url  = uri("https://maven.aliyun.com/nexus/content/groups/public/")
        }
        maven {
            url  = uri("https://maven.aliyun.com/repository/google")
        }

        maven {
            url  = uri("https://maven.aliyun.com/repository/public")
        }
        maven {
            url  = uri("https://maven.aliyun.com/repository/jcenter")
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
/*配置项目依赖的仓库*/
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url  = uri("https://maven.aliyun.com/nexus/content/groups/public/")
        }
        maven {
            url  = uri("https://maven.aliyun.com/repository/google")
        }
        maven {
            url  = uri("https://maven.aliyun.com/repository/public")
        }
        maven {
            url  = uri("https://maven.aliyun.com/repository/jcenter")
        }
        maven { url  = uri("https://esri.jfrog.io/artifactory/arcgis") }
    }
}

rootProject.name = "Map"
include(":app")
include(":imap")
include(":imap-arcgis10")
