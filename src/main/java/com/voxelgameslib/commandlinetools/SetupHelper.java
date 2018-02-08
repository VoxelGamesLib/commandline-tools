package com.voxelgameslib.commandlinetools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;

public class SetupHelper {


    // ask if want to setup testserver or new project
    // testserver? clone testserver repo, fill out templates
    // new project? clone skeleton repo, foll out templates

    private void cloneTestServer(File outputFolder) {

    }

    public void setupTestServer(File serverFolder, boolean shouldDownload, String jarFileName) {
        if (!serverFolder.exists()) {
            boolean result = serverFolder.mkdirs();
            if (!result) {
                System.err.println("Error while creating server folder!");
                return;
            }
        }

        File jarFile = new File(serverFolder, jarFileName);

        if (shouldDownload) {
            System.out.println("Downloading paperclip into " + jarFile.getAbsolutePath() + "...");
            if (download(jarFile)) {
                System.out.println("Done!");
            } else {
                return;
            }
        }

        Scanner scanner = new Scanner(System.in);
        while (!jarFile.exists()) {
            System.out.println("Serverjar " + jarFile.getName() + " doesn't exist!");
            System.out.println("Either provide it now or restart with the -downloadJar option!");
            System.out.println("Type exit to exit, check to recheck");
            if (scanner.next().equals("exit")) {
                scanner.close();
                return;
            }
        }
        scanner.close();

        // todo setup templates

        // todo setup idea run configs?
    }

    private boolean download(File output) {
        URL url;
        try {
            url = new URL("http://ci.destroystokyo.com/job/Paper/lastSuccessfulBuild/artifact/paperclip.jar");
            //TODO check why this is giving a 403...
        } catch (MalformedURLException e) {
            System.err.println("Malformed url?!");
            e.printStackTrace();
            return false;
        }

        try {
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream(output);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            return true;
        } catch (IOException e) {
            System.err.println("Error while downloading file: ");
            e.printStackTrace();
            return false;
        }
    }
}
