package com.voxelgameslib.commandlinetools;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.File;

public class CommandLineTools {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption(new Option("generateDocs", "Regenerates the docs repo with the latest data"));
        options.addOption(Option.builder("docsFolder").desc("Specifies the folder where the docs should be cloned into (defaults to docs)").numberOfArgs(1).type(File.class).build());

        options.addOption(new Option("generateTestServer", "Generates a testserver, ready to run VGL"));
        options.addOption(Option.builder("serverFolder").desc("Specifies the folder where the testserver should be cloned into (defaults to testserver)").numberOfArgs(1).type(File.class).build());
        options.addOption(new Option("downloadJar", "Downloads paper from the ci"));
        options.addOption(new Option("jarFileName", "File name of the server jar"));

        options.addOption(new Option("generateSkeletonGamemode", "Generates a empty gamemode project"));

        options.addOption(new Option("generateWorkspace", "Generates a workspace, ready to start working on VGL itself"));
        options.addOption(Option.builder("workspaceFolder").desc("The folder where the workspace should be located in (defaults to VoxelGamesLib)").numberOfArgs(1).type(File.class).build());
        options.addOption(new Option("includeAddons", "Also setup all default addons"));
        options.addOption(new Option("includeOtherProjects", "Also setup other misc projects"));

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            printHelp(options);
            return;
        }

        boolean hasRun = false;
        if (cmd.hasOption("generateWorkspace")) {
            hasRun = true;
            File workspaceFolder = new File("testserver");
            if (cmd.hasOption("workspaceFolder")) {
                try {
                    workspaceFolder = (File) cmd.getParsedOptionValue("workspaceFolder");
                } catch (ParseException e) {
                    System.err.println("Could not parse " + cmd.getOptionValue("workspaceFolder") + " into an file!");
                    e.printStackTrace();
                    return;
                }
            }

            boolean shouldIncludeAddons = cmd.hasOption("includeAddons");
            boolean shouldIncludeOtherProjects = cmd.hasOption("includeOtherProjects");
            new WorkspaceGenerator().generate(workspaceFolder, shouldIncludeAddons, shouldIncludeOtherProjects);
        }
        if (cmd.hasOption("generateTestServer")) {
            hasRun = true;
            File serverFolder = new File("testserver");
            if (cmd.hasOption("serverFolder")) {
                try {
                    serverFolder = (File) cmd.getParsedOptionValue("serverFolder");
                } catch (ParseException e) {
                    System.err.println("Could not parse " + cmd.getOptionValue("serverFolder") + " into an file!");
                    e.printStackTrace();
                    return;
                }
            }
            boolean shouldDownloadJar = cmd.hasOption("downloadJar");
            String jarFileName = cmd.getOptionValue("jarFileName", "paperclip.jar");

            new TestServerGenerator().generate(serverFolder, shouldDownloadJar, jarFileName);
        }

        if (!hasRun && cmd.hasOption("generateSkeletonGamemode")) {
            hasRun = true;
            System.out.println("gen skeleton");
        } else if (!hasRun && cmd.hasOption("generateDocs")) {
            hasRun = true;
            File docsFolder = new File("docs");
            if (cmd.hasOption("docsFolder")) {
                try {
                    docsFolder = (File) cmd.getParsedOptionValue("docsFolder");
                } catch (ParseException e) {
                    System.err.println("Could not parse " + cmd.getOptionValue("docsFolder") + " into an file!");
                    e.printStackTrace();
                    return;
                }
            }
            new DocsGenerator(docsFolder).generate();
        }

        if (!hasRun) {
            printHelp(options);
        }
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar commandline-tools.jar", "Possible options:", options,
                "Please report any issues to https://github.com/VoxelGamesLib/VoxelGamesLibv2/issues", true);
    }
}
