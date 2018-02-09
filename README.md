# commandline-tools
Small tools to automate stuff and things

# Download

You can download the latest version [here](https://github.com/VoxelGamesLib/commandline-tools/raw/gh-pages/commandline-tools-1.0-SNAPSHOT-all.jar)

# Usage

```
usage: java -jar commandline-tools.jar [-docsFolder <arg>] [-downloadJar]
       [-generateDocs] [-generateSkeletonGamemode] [-generateTestServer]
       [-generateWorkspace] [-includeAddons] [-includeOtherProjects]
       [-jarFileName] [-serverFolder <arg>] [-workspaceFolder <arg>]
Possible options:
 -docsFolder <arg>           Specifies the folder where the docs should be
                             cloned into (defaults to docs)
 -downloadJar                Downloads paper from the ci
 -generateDocs               Regenerates the docs repo with the latest
                             data
 -generateSkeletonGamemode   Generates a empty gamemode project
 -generateTestServer         Generates a testserver, ready to run VGL
 -generateWorkspace          Generates a workspace, ready to start working
                             on VGL itself
 -includeAddons              Also setup all default addons
 -includeOtherProjects       Also setup other misc projects
 -jarFileName                File name of the server jar
 -serverFolder <arg>         Specifies the folder where the testserver
                             should be cloned into (defaults to
                             testserver)
 -workspaceFolder <arg>      The folder where the workspace should be
                             located in (defaults to VoxelGamesLib)
Please report any issues to
https://github.com/VoxelGamesLib/VoxelGamesLibv2/issues
```

Example to generate a workspace with a testserver:
```
java -jar commandline-tools.jar -generateWorkspace -workspaceFolder testWorkspace -includeAddons -includeOtherProjects -generateTestServer -serverFolder testWorkspace/testserver -downloadJar
```