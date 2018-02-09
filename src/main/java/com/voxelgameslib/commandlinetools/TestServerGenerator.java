package com.voxelgameslib.commandlinetools;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.TextProgressMonitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;

public class TestServerGenerator {

    public void generate(File serverFolder, boolean shouldDownload, String jarFileName) {
        if (!serverFolder.exists()) {
            boolean result = serverFolder.mkdirs();
            if (!result) {
                System.err.println("Error while creating server folder!");
                return;
            }
        } else {
            File[] files = serverFolder.listFiles();
            if (files == null || files.length != 0) {
                System.out.println("Serverfolder already exists and isn't empty. We can't clone!");
                return;
            }
        }

        System.out.println("Cloning testserver...");
        cloneTestServer(serverFolder);
        System.out.println("Done");

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

        System.out.println("Setup templates...");
        if (setupTemplates(serverFolder)) {
            System.out.println("Done");
        }
    }

    private boolean setupTemplates(File serverFolder) {
        File[] files = serverFolder.listFiles();
        if (files == null || files.length == 0) {
            System.err.println("No files cloned!?");
            return false;
        }

        for (File file : files) {
            if (file.getName().endsWith(".template")) {
                if (file.renameTo(new File(serverFolder, file.getName().replace(".template", "")))) {
                    System.out.println("Processed " + file.getName());
                } else {
                    System.out.println("Could not rename " + file.getName() + "!");
                }
            }
        }

        return true;
    }

    private void cloneTestServer(File outputFolder) {
        try {
            Git.cloneRepository().setURI("https://github.com/VoxelGamesLib/testserver.git").setDirectory(outputFolder).setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out))).call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    private boolean download(File output) {
        try {
            URL url = new URL("http://ci.destroystokyo.com/job/Paper/lastSuccessfulBuild/artifact/paperclip.jar");
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet get = new HttpGet(url.toURI());
            HttpResponse response = client.execute(get);
            int responseCode = response.getStatusLine().getStatusCode();

            if (responseCode == 200) {
                ReadableByteChannel rbc = Channels.newChannel(response.getEntity().getContent());
                FileOutputStream fos = new FileOutputStream(output);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                rbc.close();
                fos.close();
                ((CloseableHttpResponse) response).close();
                System.out.println("File downloaded");
                return true;

            } else {
                ((CloseableHttpResponse) response).close();
                System.out.println("Error while downloading: " + responseCode);
                return false;
            }
        } catch (MalformedURLException | URISyntaxException e) {
            System.err.println("Malformed url?!");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            System.err.println("Error while downloading file: ");
            e.printStackTrace();
            return false;
        }
    }
}
