package edu.purdue.cs.encourse.service.helper;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Helper object for a test script on a separate thread
 * Primarily used to be able to run tests with the ability to kill them if they take too much time
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
public class TestExecuter implements Runnable {
    @Getter
    private volatile String visibleResult;
    @Getter
    private volatile String hiddenResult;

    private String testingDirectory;
    private String testCaseDirectory;
    private String hiddenTestCaseDirectory;
    private String courseAccount;

    public TestExecuter(String courseID, String testingDirectory, String testCaseDirectory, String hiddenTestCaseDirectory) {
        this.courseAccount = courseID + "-account";
        this.testingDirectory = testingDirectory;
        this.testCaseDirectory = testCaseDirectory;
        this.hiddenTestCaseDirectory = hiddenTestCaseDirectory;
    }

    @Override
    public void run() {
        try {
            String command = "/home/" + courseAccount + "/testall.sh " + testingDirectory + " " + testCaseDirectory;
            Process process = Runtime.getRuntime().exec("./src/main/bash/runTestallAsCourseAccount.sh " + courseAccount + " " + command  + " 2> /dev/null");
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String error = null;
            while ((error = stdError.readLine()) != null) {
                System.out.println("Error: " + error);
            }
            visibleResult = stdInput.readLine();
            command = "/home/" + courseAccount + "/testall.sh " + testingDirectory + " " + hiddenTestCaseDirectory;
            process = Runtime.getRuntime().exec("./src/main/bash/runTestallAsCourseAccount.sh " + courseAccount + " " + command  + " 2> /dev/null");
            stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            hiddenResult = stdInput.readLine();
            while ((error = stdError.readLine()) != null) {
                System.out.println("Error: " + error);
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }


}
