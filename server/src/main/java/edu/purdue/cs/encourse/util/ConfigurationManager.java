package edu.purdue.cs.encourse.util;

import java.io.*;
import java.nio.Buffer;

public class ConfigurationManager {
    private static ConfigurationManager sharedInstance = null;
    public Boolean debug = false;
    public final static String ßuserID = "cutz";
    public final static String projectID = "cs252";

    private ConfigurationManager() {
        File configFile = new File("src/config.txt");
        System.out.println("CONFIGURATION:");
        BufferedReader configReader = null;
        try {
            configReader = new BufferedReader(new FileReader(configFile));
            String line;
            while ((line = configReader.readLine()) != null) {
                String[] words = line.split("=");
                if (words[0].equals("DEBUG")) {
                    debug = words[1].equals("1") ? true : false;
                    System.out.println("DEBUG = " + debug);
                }
            }
        } catch (IOException e) {
            System.out.println("Configuration File not found at src/config.txt");
        }
    }

    public static ConfigurationManager getInstance() {
        if (sharedInstance == null) {
            sharedInstance = new ConfigurationManager();
        }
        return sharedInstance;
    }
}
