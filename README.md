# commandline-tools
Small tools to automate stuff and things

# Download

You can download the latest version [here](https://github.com/VoxelGamesLib/VoxelGamesLibv2/blob/gh-pages/commandline-tools/tools/commandline-tools-1.0-SNAPSHOT-all.jar?raw=true)

# Usage

```
usage: java -jar commandline-tools.jar [-author <arg>] [-docsFolder <arg>]
       [-downloadJar] [-generateDocs] [-generateSkeletonGamemode]
       [-generateTestServer] [-generateWorkspace] [-groupId <arg>]
       [-includeAddons] [-includeOtherProjects] [-jarFileName]
       [-projectFolder <arg>] [-projectName <arg>] [-serverFolder <arg>]
       [-test] [-useKotlin] [-workspaceFolder <arg>]
Possible options:
 -author <arg>               Your name
 -docsFolder <arg>           Specifies the folder where the docs should be
                             cloned into (defaults to docs)
 -downloadJar                Downloads paper from the ci
 -generateDocs               Regenerates the docs repo with the latest
                             data
 -generateSkeletonGamemode   Generates a empty gamemode project
 -generateTestServer         Generates a testserver, ready to run VGL
 -generateWorkspace          Generates a workspace, ready to enable
                             working on VGL itself
 -groupId <arg>              The groupid of your gamemode
 -includeAddons              Also setup all default addons
 -includeOtherProjects       Also setup other misc projects
 -jarFileName                File name of the server jar
 -projectFolder <arg>        The folder the project should be located in
 -projectName <arg>          The name of your gamemode
 -serverFolder <arg>         Specifies the folder where the testserver
                             should be cloned into (defaults to
                             testserver)
 -test                       Test stuff, pls ignore
 -useKotlin                  If the gamemode should be written in kotlin
 -workspaceFolder <arg>      The folder where the workspace should be
                             located in (defaults to VoxelGamesLib)
Please report any issues to
https://github.com/VoxelGamesLib/VoxelGamesLibv2/issues
```

Example to generate a workspace with a testserver:
```
java -jar commandline-tools.jar -generateWorkspace -workspaceFolder testWorkspace -includeAddons -includeOtherProjects -generateTestServer -serverFolder testWorkspace/testserver -downloadJar
```
Example to generate a testserver
```
java -jar commandline-tools.jar -generateTestServer -serverFolder testserver -downloadJar
```
Example to generate a project
```
java -jar commandline-tools.jar -generateSkeletonGamemode -author yourName -projectFolder projectName -projectName projectName -groupId com.yourpackage [-useKotlin]
```
