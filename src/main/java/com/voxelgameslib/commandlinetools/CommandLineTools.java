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

        options.addOption(new Option("generateTestServer", "Generates a testserver, read to run VGL"));
        options.addOption(Option.builder("serverFolder").desc("Specifies the folder where the testserver should be cloned into (defaults to testserver)").numberOfArgs(1).type(File.class).build());
        options.addOption(new Option("downloadJar", "Downloads paper from the ci"));
        options.addOption(new Option("jarFileName", "File name of the server jar"));

        options.addOption(new Option("generateSkeletonGamemode", "Generates a empty gamemode project"));

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            printHelp(options);
            return;
        }

        if (cmd.hasOption("generateDocs")) {
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
        } else if (cmd.hasOption("generateTestServer")) {
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
        } else if (cmd.hasOption("generateSkeletonGamemode")) {
            System.out.println("gen skeleton");
        } else {
            printHelp(options);
        }
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("VoxelGamesLib CLI", "Possible options:", options,
                "Please report any issues to https://github.com/VoxelGamesLib/VoxelGamesLibv2/issues", true);
    }
}
