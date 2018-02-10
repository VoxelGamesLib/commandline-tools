package com.voxelgameslib.commandlinetools;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.TextProgressMonitor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class WorkspaceGenerator {

    public void generate(File workspaceFolder, boolean shouldIncludeAddons, boolean shouldIncludeOtherProjects) {
        if (workspaceFolder.exists()) {
            System.err.println("The workspace folder already exist, this is a bad idea, please delete the folder or use another folder");
            return;
        }

        System.out.println("Cloning stuff...");
        clone(new File(workspaceFolder, "VoxelGamesLib"), "https://github.com/VoxelGamesLib/VoxelGamesLibv2.git");
        if (shouldIncludeAddons) {
            clone(new File(workspaceFolder, "1vs1"), "https://github.com/VoxelGamesLib/1vs1.git");
            clone(new File(workspaceFolder, "Hub"), "https://github.com/VoxelGamesLib/Hub.git");
            clone(new File(workspaceFolder, "Deathmatch"), "https://github.com/VoxelGamesLib/Deathmatch.git");
        }
        if (shouldIncludeOtherProjects) {
            clone(new File(workspaceFolder, "assets"), "https://github.com/VoxelGamesLib/assets.git");
            clone(new File(workspaceFolder, "commandline-tools"), "https://github.com/VoxelGamesLib/commandline-tools.git");
            clone(new File(workspaceFolder, "translation"), "https://github.com/VoxelGamesLib/translation.git");
            clone(new File(workspaceFolder, "KVGL"), "https://github.com/VoxelGamesLib/KVGL.git");
        }
        System.out.println("Done!");

        System.out.println("Writing settings.gradle...");
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(new File(workspaceFolder, "settings.gradle")));
            writer.println("include 'VoxelGamesLib'");
            if (shouldIncludeAddons) {
                writer.println("include '1vs1'");
                writer.println("include 'Hub'");
                writer.println("include 'Deathmatch'");
            }
            if (shouldIncludeAddons) {
                writer.println("include 'assets'");
                writer.println("include 'commandline-tools'");
                writer.println("include 'translation'");
                writer.println("include 'KVGL'");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Done");

        System.out.println("You may now open " + workspaceFolder.getAbsolutePath() + " in intellij");
        // TODO promt to open idea, then close, then generate run configs in workspace.xml
        //TODO create run configs
    }

    private void clone(File outputFolder, String uri) {
        try {
            Git.cloneRepository().setURI(uri).setDirectory(outputFolder).setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out))).call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }
}
