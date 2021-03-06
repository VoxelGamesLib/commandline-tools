package com.voxelgameslib.commandlinetools;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.TextProgressMonitor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkeletonGenerator {

    public static final String classPrefix = "Skeleton";
    public static final String packageName = "me.minidigger.voxelgameslib.skeleton";
    private String time = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    public void generate(File projectFolder, String projectName, String groupId, String author, boolean useKotlin) {
        File folder = new File(projectFolder, projectName);
        if (folder.exists()) {
            System.err.println("THere is already a folder named " + projectName + "!");
            return;
        }
        System.out.println("Cloning skeleton...");
        clone(folder, useKotlin);
        System.out.println("Done");

        System.out.println("Renaming folder");
        String packageString = groupId + "." + projectName.toLowerCase();
        String classPrefix;
        String packageName;
        String packagePathOld;
        String newPackagePath;
        if (useKotlin) {
            classPrefix = "SkeletonKT";
            packageName = "me.minidigger.voxelgameslib.skeletonkt";
            packagePathOld = "src/main/kotlin/me/minidigger/voxelgameslib/skeletonkt/";
            newPackagePath = "src/main/kotlin/" + packageString.replace(".", "/");
        } else {
            classPrefix = "Skeleton";
            packageName = "me.minidigger.voxelgameslib.skeleton";
            packagePathOld = "src/main/java/me/minidigger/voxelgameslib/skeleton/";
            newPackagePath = "src/main/java/" + packageString.replace(".", "/");
        }
        File gitFolder = new File(folder, ".git");
        File packageFolder = new File(folder, newPackagePath);
        File packageFolderParent = packageFolder.getParentFile();
        packageFolderParent.mkdirs();
        File oldPackageFolder = new File(folder, packagePathOld);
        System.out.println("Trying to rename: ");
        System.out.println(oldPackageFolder.getAbsolutePath() + "(" + oldPackageFolder.exists() + ")");
        System.out.println(packageFolder.getAbsolutePath() + "(" + packageFolder.exists() + ")");
        System.out.println(packageFolderParent.getAbsolutePath() + "(" + packageFolderParent.exists() + ")");
        try {
            Files.move(oldPackageFolder.toPath(), packageFolder.toPath());
            Files.delete(oldPackageFolder.getParentFile().toPath());
            Files.walk(gitFolder.toPath(), FileVisitOption.FOLLOW_LINKS)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Done");

        System.out.println("Renaming files");
        String[] names = new String[]{"Feature", "Game", "Phase", "Plugin"};
        String extention = useKotlin ? ".kt" : ".java";
        for (String name : names) {
            name += extention;
            File oldFile = new File(packageFolder, classPrefix + name);
            File newFile = new File(packageFolder, projectName + name);
            if (oldFile.renameTo(newFile)) {
                System.out.println("renamed " + classPrefix + name + " to " + projectName + name);
            } else {
                System.err.println("Didn't rename " + classPrefix + name + " to " + projectName + name);
                return;
            }
        }
        System.out.println("Done");

        System.out.println("Filling out templates...");
        int i = 0;
        i += replace(new File(folder, "scripts/deploy-build-results.sh"), new HashMap<String, String>() {{
            put(classPrefix, projectName);
        }});
        i += replace(new File(folder, "LICENSE"), new HashMap<String, String>() {{
            put("VoxelGamesLib", author);
        }});
        i += replace(new File(folder, "README.md"), new HashMap<String, String>() {{
            put(classPrefix, projectName);
            put("For use with the VGL CLI tools", "Generated with VGL CLI on " + time + "\nTODO: Add a new readme");
        }});
        i += replace(new File(folder, "build.gradle"), new HashMap<String, String>() {{
            put(packageName, groupId);
            put(classPrefix + "-1.0-SNAPSHOT.jar", projectName + "-1.0-SNAPSHOT.jar");
        }});
        i += replace(new File(folder, "settings.gradle"), new HashMap<String, String>() {{
            put(classPrefix, projectName);
        }});
        i += replace(new File(folder, "src/main/resources/plugin.yml"), new HashMap<String, String>() {{
            put(classPrefix, projectName);
            put("MiniDigger", author);
            put(packageName + "." + classPrefix + "Plugin", groupId + "." + projectName.toLowerCase() + "." + projectName + "Plugin");
        }});
        i += replace(new File(packageFolder, projectName + "Plugin" + extention), new HashMap<String, String>() {{
            put(classPrefix, projectName);
            put("MiniDigger", author);
            put("package " + packageName, "package " + packageString);
        }});
        i += replace(new File(packageFolder, projectName + "Phase" + extention), new HashMap<String, String>() {{
            put(classPrefix + "Phase", projectName + "Phase");
            put(classPrefix + "Feature", projectName + "Feature");
            put("oneVsOneFeature", projectName.toLowerCase() + "Feature");
            put("package " + packageName, "package " + packageString);
        }});
        i += replace(new File(packageFolder, projectName + "Game" + extention), new HashMap<String, String>() {{
            put(classPrefix, projectName);
            put("MiniDigger", author);
            put("package " + packageName, "package " + packageString);
        }});
        i += replace(new File(packageFolder, projectName + "Feature" + extention), new HashMap<String, String>() {{
            put(classPrefix, projectName);
            put("MiniDigger", author);
            put("package " + packageName, "package " + packageString);
        }});
        System.out.println("Replaced " + i + " vars");

        System.out.println("adding project to workspace settings.gradle...");
        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(projectFolder, "settings.gradle"), true))) {
            writer.println("include '" + projectName + "'");
            System.out.println("Done");
        } catch (IOException e) {
            System.err.println("Error while writing to settings.gralde");
            e.printStackTrace();
        }
    }

    private void clone(File outputFolder, boolean useKotlin) {
        String uri;
        if (useKotlin) {
            uri = "https://github.com/VoxelGamesLib/skeletonkt.git";
        } else {
            uri = "https://github.com/VoxelGamesLib/skeleton.git";
        }
        try {
            Git.cloneRepository().setURI(uri).setDirectory(outputFolder).setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out))).call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    private int replace(File file, Map<String, String> replacements) {
        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            System.err.println("Error while reading " + file.getAbsolutePath());
            e.printStackTrace();
            return 0;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            int i = 0;
            for (String line : lines) {
                for (String key : replacements.keySet()) {
                    if (line.contains(key)) {
                        i++;
                        line = line.replace(key, replacements.get(key));
                    }
                }

                for (String s : line.split("\n")) {
                    writer.println(s);
                }
            }
            return i;
        } catch (IOException e) {
            System.err.println("Error while writing file " + file.getAbsolutePath());
            e.printStackTrace();
            return 0;
        }
    }
}
