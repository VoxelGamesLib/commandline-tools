package com.voxelgameslib.commandlinetools;

import com.google.gson.annotations.Expose;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.TextProgressMonitor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.voxelgameslib.voxelgameslib.VoxelGamesLib;
import com.voxelgameslib.voxelgameslib.feature.FeatureInfo;
import com.voxelgameslib.voxelgameslib.feature.features.AutoRespawnFeature;
import com.voxelgameslib.voxelgameslib.phase.Phase;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;

public class DocsGenerator {

    private Git git;
    private File outDir;
    private List<Class<?>> features = new ArrayList<>();
    private List<Class<? extends Phase>> phases = new ArrayList<>();

    public DocsGenerator(File outDir) {
        this.outDir = outDir;
    }

    public void generate() {
        System.out.println("cloning wiki into " + outDir.getAbsolutePath() + "...");
        cloneWiki();
        System.out.println("collect features...");
        collectFeatures();
        System.out.println("modify features.md...");
        modify(new File(outDir, "/docs/components/features.md"));
        System.out.println("modify games.md...");
        modify(new File(outDir, "/docs/components/games.md"));
        System.out.println("modify phases.md...");
        modify(new File(outDir, "/docs/components/phases.md"));
        System.out.println("modify victoryconditions.md...");
        modify(new File(outDir, "/docs/components/victoryconditions.md"));
        System.out.println("commit wiki...");
        commitWiki();
        System.out.println("MAKE SURE YOU PUSH THE CHANGES!");
    }

    private void collectFeatures() {
        try {
            Class.forName(VoxelGamesLib.class.getName());
            Class.forName(AutoRespawnFeature.class.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        features = new FastClasspathScanner().scan().getNamesOfClassesWithAnnotation(FeatureInfo.class).parallelStream().sorted().map(name -> {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        System.out.println("Found " + features.size() + " features");
    }

    private void modify(File file) {
        System.out.println(file.getAbsolutePath());
        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try (PrintWriter pw = new PrintWriter(file)) {
            for (String line : lines) {
                System.out.println(line);
                pw.println(line);
                if (line.contains("[features]")) {
                    writeFeatures(pw);
                    break;
                } else if (line.contains("[phases]")) {
                    writePhases(pw);
                    break;
                } else if (line.contains("[games]")) {
                    writeGames(pw);
                    break;
                } else if (line.contains("[conditions]")) {
                    writeConditions(pw);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeFeatures(PrintWriter pw) {
        features.forEach(clazz -> {
            FeatureInfo featureInfo = clazz.getAnnotation(FeatureInfo.class);

            List<String> dependencies = new ArrayList<>();
            try {
                //noinspection unchecked
                dependencies = ((List<Class>) clazz.getMethod("getDependencies").invoke(clazz.newInstance())).stream().map(Class::getName).collect(Collectors.toList());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("could not collect dependencies for clazz " + clazz.getSimpleName());
            }

            List<String> softDependencies = new ArrayList<>();
            try {
                //noinspection unchecked
                softDependencies = ((List<Class>) clazz.getMethod("getSoftDependencies").invoke(clazz.newInstance())).stream().map(Class::getName).collect(Collectors.toList());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("could not collect soft dependencies for clazz " + clazz.getSimpleName());
            }

            List<String> params = new ArrayList<>();
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Expose.class)) {
                    params.add(field.getName() + " (" + field.getType().getName() + ")");
                }
            }

            pw.println("### " + featureInfo.name() + " (" + clazz.getName() + ") v" + featureInfo.version() + "  ");
            pw.println("Author: " + featureInfo.author() + "  ");
            pw.println("Description: " + featureInfo.description() + "  ");
            pw.println("#### Params  ");
            params.forEach(param -> pw.println(param + "  "));
            pw.println("#### Dependencies  ");
            dependencies.forEach(dependency -> pw.println(dependency + "  "));
            pw.println("#### Soft Dependencies  ");
            softDependencies.forEach(dependency -> pw.println(dependency + "  "));
            pw.println("  ");
        });
    }

    private void writePhases(PrintWriter pw) {
        pw.println("todo write phases");
    }

    private void writeGames(PrintWriter pw) {
        pw.println("todo write games");
    }

    private void writeConditions(PrintWriter pw) {
        pw.println("todo write conditions");
    }

    private void cloneWiki() {
        try {
            if (outDir.exists()) {
                git = Git.open(outDir);
                git.pull().call();
            } else {
                git = Git.cloneRepository().setURI("https://github.com/VoxelGamesLib/docs.git").setDirectory(outDir).setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out))).call();
            }
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
    }

    private void commitWiki() {
        try {
            git.add().addFilepattern(".").call();
            git.commit().setMessage("Bot Update " + new Date().toString()).setAuthor("VoxelGamesLibBot", "vglbot@minidigger.me").call();
            // git.push().sert TODO PUSH
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }
}
