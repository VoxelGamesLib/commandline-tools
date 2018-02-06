package com.voxelgameslib.commandlinetools;

import com.google.gson.annotations.Expose;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.reflections.Reflections;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.voxelgameslib.voxelgameslib.VoxelGamesLib;
import com.voxelgameslib.voxelgameslib.feature.FeatureInfo;
import com.voxelgameslib.voxelgameslib.feature.features.AutoRespawnFeature;
import com.voxelgameslib.voxelgameslib.phase.Phase;

public class DocsGenerator {

    private Git git;
    private File outDir = new File("docs");
    private Set<Class<?>> features = new HashSet<>();
    private Set<Class<? extends Phase>> phases = new HashSet<>();

    public void generate() {
        System.out.println("cloneing wiki...");
        cloneWiki();
        System.out.println("collect features...");
        collectFeatures();
        System.out.println("modify features.md...");
        modify(new File(outDir, "/docs/components/features.md"));
        System.out.println("commit wiki...");
       // commitWiki();
    }

    private void collectFeatures() {
        try {
            Class.forName(VoxelGamesLib.class.getName());
            Class.forName(AutoRespawnFeature.class.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        features = new Reflections("com.voxelgameslib").getTypesAnnotatedWith(FeatureInfo.class);
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
                if (line.contains("[features]")) {
                    writeFeatures(pw);
                    break;
                } else if (line.contains("[phases]")) {
                    writePhases(pw);
                    break;
                } else if (line.contains("[games]")) {
                    writePhases(pw);
                    break;
                } else {
                    pw.println(line);
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
                Class<?>[] dep = (Class[]) clazz.getMethod("getDependencies").invoke(clazz.newInstance());
                for (Class<?> c : dep) {
                    dependencies.add(c.getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("could not collect dependencies for clazz " + clazz.getSimpleName());
            }

            List<String> softDependencies = new ArrayList<>();
            try {
                Class<?>[] dep = (Class[]) clazz.getMethod("getSoftDependencies").invoke(clazz.newInstance());
                for (Class<?> c : dep) {
                    softDependencies.add(c.getName());
                }
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

            pw.println("### " + featureInfo.name() + "(" + clazz.getName() + ") v" + featureInfo.version() + "  ");
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

    private void cloneWiki() {
        if (outDir.exists()) {
            try {
                git = Git.open(outDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                git = Git.cloneRepository().setURI("https://github.com/VoxelGamesLib/docs.git").setDirectory(outDir).setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out))).call();
            } catch (GitAPIException e) {
                e.printStackTrace();
            }
        }
    }

    private void commitWiki() {
        try {
            git.commit().setMessage("Bot Update " + new Date().toString()).setAuthor("VoxelGamesLibBot", "vglbot@minidigger.me").call();
            // git.push().sert TODO PUSH
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }
}
