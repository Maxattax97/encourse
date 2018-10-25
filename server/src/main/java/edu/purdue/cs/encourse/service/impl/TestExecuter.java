package edu.purdue.cs.encourse.service.impl;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestExecuter implements Runnable {
    @Getter
    private volatile String visibleResult;
    @Getter
    private volatile String hiddenResult;

    private String testingDirectory;
    private String testCaseDirectory;
    private String hiddenTestCaseDirectory;
    private String courseID;

    public TestExecuter(String courseID, String testingDirectory, String testCaseDirectory, String hiddenTestCaseDirectory) {
        this.courseID = courseID;
        this.testingDirectory = testingDirectory;
        this.testCaseDirectory = testCaseDirectory;
        this.hiddenTestCaseDirectory = hiddenTestCaseDirectory;
    }

    @Override
    public void run() {
        try {
            String command = "/home/" + courseID + "/testall.sh " + testingDirectory + " " + testCaseDirectory + " 2> /dev/null";
            Process process = Runtime.getRuntime().exec("./src/main/bash/runCommandAsCourseAccount.sh " + courseID + " " + command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            visibleResult = stdInput.readLine();
            command = "/home/" + courseID + "/testall.sh " + testingDirectory + " " + hiddenTestCaseDirectory + " 2> /dev/null";
            process = Runtime.getRuntime().exec("./src/main/bash/runCommandAsCourseAccount.sh " + courseID + " " + command);
            stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            hiddenResult = stdInput.readLine();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }


}
