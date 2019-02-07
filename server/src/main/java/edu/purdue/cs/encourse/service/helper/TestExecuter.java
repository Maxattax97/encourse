package edu.purdue.cs.encourse.service.helper;

import edu.purdue.cs.encourse.domain.TestScript;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Helper object for a test script on a separate thread
 * Primarily used to be able to run tests with the ability to kill them if they take too much time
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
public class TestExecuter extends Thread {
    @Getter
    private volatile String visibleResult;
    @Getter
    private volatile String hiddenResult;
    
    @Getter
    private double visiblePoints;
    @Getter
    private double hiddenPoints;
    
    @Getter
    private List<Long> passedTests;

    private String testingDirectory;
    private String testCaseDirectory;
    private String hiddenTestCaseDirectory;
    private String courseAccount;

    public TestExecuter(String courseID, String testingDirectory, String testCaseDirectory, String hiddenTestCaseDirectory) {
        super();
        this.courseAccount = courseID + "-account";
        this.testingDirectory = testingDirectory;
        this.testCaseDirectory = testCaseDirectory;
        this.hiddenTestCaseDirectory = hiddenTestCaseDirectory;
    }
    
    private String runTestAll(String directory) throws IOException {
        String command = "/home/" + courseAccount + "/testall.sh " + testingDirectory + " " + directory;
        
        Process process = Runtime.getRuntime().exec("./src/main/bash/runTestallAsCourseAccount.sh " + courseAccount + " " + command  + " 2> /dev/null");
        
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        
        String error;
        while ((error = stdError.readLine()) != null) {
            System.out.println("Error: " + error);
        }
        
        return stdInput.readLine();
    }

    @Override
    public void run() {
        try {
            visibleResult = runTestAll(testCaseDirectory);
            hiddenResult = runTestAll(hiddenTestCaseDirectory);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    private double parseResult(Map<String, TestScript> pointMap, String result) {
        String[] testResults = result.split(";");
        double earnedPoints = 0.0;
        
        for(String r : testResults) {
            String testName = r.split(":")[0];
    
            TestScript testScript = pointMap.get(testName);
            
            if(testScript != null && r.endsWith("P")) {
                earnedPoints += testScript.getValue();
                passedTests.add(testScript.getId());
            }
        }
        return earnedPoints;
    }

    public void parse(Map<String, TestScript> pointMap) {
        passedTests = new ArrayList<>();
        
        if(visibleResult == null)
            visiblePoints = 0;
        else
            visiblePoints = parseResult(pointMap, visibleResult);
        
        if(hiddenResult == null)
            hiddenPoints = 0;
        else
            hiddenPoints = parseResult(pointMap, hiddenResult);
    }

}
