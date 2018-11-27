package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.domain.relations.*;
import edu.purdue.cs.encourse.service.HelperService;
import edu.purdue.cs.encourse.service.helper.StreamGobbler;
import edu.purdue.cs.encourse.util.ConfigurationManager;
import edu.purdue.cs.encourse.util.JSONReturnable;
import lombok.NonNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Contains implementations for all services which are used internally as helper functions for other services
 * Primarily used internally by other services
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
@Service(value = HelperServiceImpl.NAME)
public class HelperServiceImpl implements HelperService {

    public final static String NAME = "HelperService";

    public final Boolean DEBUG = ConfigurationManager.getInstance().debug;
    public final Boolean OBFUSCATE = false;
    public final String pythonCommand = DEBUG ? "/anaconda3/bin/python" : "python3";
    public final String pythonPath = "src/main/python/";
    public final String tailFilePath = "src/main/temp/";
    public final String testDir = "test-shell";

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private StudentSectionRepository studentSectionRepository;

    @Autowired
    private StudentProjectRepository studentProjectRepository;

    @Autowired
    private ProjectTestScriptRepository projectTestScriptRepository;

    @Autowired
    private StudentProjectTestRepository studentProjectTestRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public boolean getDebug() {
        return DEBUG;
    }

    public boolean getObfuscate() {
        return OBFUSCATE;
    }

    public String getPythonCommand() {
        return pythonCommand;
    }

    public String getPythonPath() {
        return pythonPath;
    }

    public String getTailFilePath() {
        return tailFilePath;
    }

    public String getTestDir() {
        return testDir;
    }

    /**
     * Executes a bash script using the project's directory for bash scripts
     * Primarily used for connecting Java to bash scripts
     *
     * @param command   Script and arguments to execute, format is "{ScriptName} {Argument1} {Argument2} ..."
     * @return          Error code
     */
    public int executeBashScript(@NonNull String command) {
        try {
            Process process = Runtime.getRuntime().exec("./src/main/bash/" + command + " 2> /dev/null");
            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
            Executors.newSingleThreadExecutor().submit(streamGobbler);
            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), System.out::println);
            Executors.newSingleThreadExecutor().submit(errorGobbler);
            process.waitFor();
        }
        catch(Exception e) {
            return -1;
        }
        return 0;
    }

    /**
     * Executes a python script and gives the return value of the script
     * Primarily used for connecting Java to Python scripts
     *
     * @param command   Script and arguments to execute, format is "{PythonDir}/{ScriptName} {Argument1} {Argument2} ..."
     * @return          Return value of the script
     */
    public JSONReturnable runPython(@NonNull String command) {
        System.out.println(command);
        JSONReturnable json = null;
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String output;
            String error;
            while ((error = stdError.readLine()) != null) {
                System.out.println("Error: " + error);
            }
            while ((output = stdInput.readLine()) != null) {
                System.out.println("Output: " + output);
                JSONParser jsonParser = new JSONParser();
                Object obj = null;
                try {
                    obj = jsonParser.parse(output);
                } catch (ParseException e) {
                    e.printStackTrace();
                    json =  new JSONReturnable(-3, null);
                }
                if (obj != null) {
                    JSONObject jsonObject = null;
                    if (obj.getClass() == JSONObject.class) {
                        jsonObject = (JSONObject)obj;
                    } else if (obj.getClass() == JSONArray.class) {
                        jsonObject = new JSONObject();
                        JSONArray jsonArray = (JSONArray)obj;
                        jsonObject.put("data", jsonArray);
                    } else {
                        json = new JSONReturnable(-4, null);
                    }
                    json = new JSONReturnable(1, jsonObject);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            json = new JSONReturnable(-2, null);
        }
        return json;
    }

    /**
     * Checks if a student is taking a course that a project is assigned for
     *
     * @param student   Object representing the student being checked
     * @param project   Object representing a project for a course
     * @return          True if the student is in the course, false otherwise
     */
    public boolean isTakingCourse(@NonNull Student student, @NonNull Project project) {
        List<StudentSection> studentSections = studentSectionRepository.findByIdStudentID(student.getUserID());
        boolean isTaking = false;
        for(StudentSection s : studentSections) {
            Section section = sectionRepository.findBySectionIdentifier(s.getSectionIdentifier());
            if(section.getCourseID().equals(project.getCourseID()) && section.getSemester().equals(project.getSemester())) {
                isTaking = true;
                break;
            }
        }
        return isTaking;
    }

    /**
     * Takes a string representing the test results for a project and converts it to a grade
     * Primarily used for setting the best grade earned for a project on a particular date
     *
     * @param projectID     Identifier for project that was tested
     * @param testOutput    Output of the testall for the project
     * @return              Grade on a scale from 0 to 100, rounded to nearest integer
     */
    public double parseProgressForProject(@NonNull String projectID, @NonNull String testOutput) {
        String[] testResults = testOutput.split(";");
        double earnedPoints = 0.0;
        double maxPoints = 0.0;
        for(String r : testResults) {
            String testName = r.split(":")[0];
            ProjectTestScript testScript = projectTestScriptRepository.findByIdProjectIdentifierAndIdTestScriptName(projectID, testName);
            if(testScript == null) {
                continue;
            }
            maxPoints += testScript.getPointsWorth();
            if(r.endsWith("P")) {
                earnedPoints += testScript.getPointsWorth();
            }
        }
        if(maxPoints == 0.0) {
            return 0.0;
        }
        return Math.round((earnedPoints / maxPoints) * 100);
    }

    /**
     * Updates each individual test script to indicate if it passed or failed in the test result
     * Also updates the grade for every suite based on the scores for each test script within the suite
     * Primarily used to record the best grade from the testall
     *
     * @param result    Output of the testall for the project
     * @param studentID Back-end identifier for the student tested
     * @param projectID Identifier for the project that was tested
     * @param isHidden  Indicated if results should be hidden from students
     */
    public void updateTestResults(String result, String studentID, String projectID, boolean isHidden) {
        String[] testResults = result.split(";");
        for(String s : testResults) {
            String testName = s.split(":")[0];
            String testScore = s.split(":")[1];
            boolean isPassing = testScore.equals("P");
            StudentProjectTest studentProjectTest =
                    studentProjectTestRepository.findByIdProjectIdentifierAndIdTestScriptNameAndIdStudentID(projectID, testName, studentID);
            if(studentProjectTest == null) {
                ProjectTestScript testScript = projectTestScriptRepository.findByIdProjectIdentifierAndIdTestScriptName(projectID, testName);
                studentProjectTest = new StudentProjectTest(studentID, projectID, testName, isPassing, isHidden, testScript.getPointsWorth());
                studentProjectTestRepository.save(studentProjectTest);
            }
            else {
                studentProjectTest.setPassing(isPassing);
                studentProjectTestRepository.save(studentProjectTest);
            }
        }
        Project project = projectRepository.findByProjectIdentifier(projectID);
        String[] suites = project.getSuites().split(",");
        for(String s : suites) {
            StudentProject suiteProject = studentProjectRepository.findByIdProjectIdentifierAndIdStudentIDAndIdSuite(projectID, studentID, s);
            List<StudentProjectTest> testScores = studentProjectTestRepository.findByIdProjectIdentifierAndIdStudentIDAndIsHidden(projectID, studentID, isHidden);
            double earnedPoints = 0.0;
            double maxPoints = 0.0;
            for(StudentProjectTest t : testScores) {
                if(t.isPassing()) {
                    earnedPoints += t.getPointsWorth();
                }
                maxPoints += t.getPointsWorth();
            }
            double grade = Math.round((earnedPoints / maxPoints) * 100);
            if(isHidden) {
                suiteProject.setBestHiddenGrade(grade);
                studentProjectRepository.save(suiteProject);
            }
            else {
                suiteProject.setBestVisibleGrade(grade);
                studentProjectRepository.save(suiteProject);
            }
        }
    }

    /**
     * Creates temporary files containing visible and hidden test results for Python scripts to parse
     * Primarily used for services which analyze test scores for a project
     *
     * @param visibleTestFile   Name of the file to contain visible test results
     * @param hiddenTestFile    Name of the file to contain hidden test results
     * @param projects          All of the students and associated projects to write test results for
     * @throws IOException      Opens, closes, and writes to files
     */
    public void createTestFiles(String visibleTestFile, String hiddenTestFile, List<StudentProject> projects) throws IOException {
        BufferedWriter visibleWriter = new BufferedWriter(new FileWriter(visibleTestFile));
        BufferedWriter hiddenWriter = new BufferedWriter(new FileWriter(hiddenTestFile));
        for (StudentProject p : projects) {
            Student student = studentRepository.findByUserID(p.getStudentID());
            StringBuilder builder = new StringBuilder();
            builder.append(student.getUserName());
            List<StudentProjectTest> testResults = studentProjectTestRepository.findByIdProjectIdentifierAndIdStudentIDAndIsHidden(p.getProjectIdentifier(), p.getStudentID(), false);
            for (StudentProjectTest t : testResults) {
                builder.append(";").append(t.getTestResultString());
            }
            String visibleTestResult = builder.toString();
            builder = new StringBuilder();
            builder.append(student.getUserName());
            testResults = studentProjectTestRepository.findByIdProjectIdentifierAndIdStudentIDAndIsHidden(p.getProjectIdentifier(), p.getStudentID(), true);
            for (StudentProjectTest t : testResults) {
                builder.append(";").append(t.getTestResultString());
            }
            String hiddenTestResult = builder.toString();
            visibleWriter.write(visibleTestResult + "\n");
            hiddenWriter.write(hiddenTestResult + "\n");
        }
        visibleWriter.close();
        hiddenWriter.close();
    }

    /**
     * Creates temporary files containing visible and hidden test results for assigned students for Python scripts to parse
     * Primarily used for services which analyze test scores for a project for a TA
     *
     * @param visibleTestFile   Name of the file to contain visible test results
     * @param hiddenTestFile    Name of the file to contain hidden test results
     * @param assignments       All of the assigned students to write test results for
     * @param projectID         Identifier for the project that test results pertain to
     * @throws IOException      Opens, closes, and writes to files
     */
    public void createTestFilesTA(String visibleTestFile, String hiddenTestFile, List<TeachingAssistantStudent> assignments, String projectID) throws IOException {
        BufferedWriter visibleWriter = new BufferedWriter(new FileWriter(visibleTestFile));
        BufferedWriter hiddenWriter = new BufferedWriter(new FileWriter(hiddenTestFile));
        for (TeachingAssistantStudent s : assignments) {
            Student student = studentRepository.findByUserID(s.getStudentID());
            if(studentProjectRepository.findByIdProjectIdentifierAndIdStudentID(projectID, student.getUserID()) == null) {
                continue;
            }
            StringBuilder builder = new StringBuilder();
            builder.append(student.getUserName());
            List<StudentProjectTest> testResults = studentProjectTestRepository.findByIdProjectIdentifierAndIdStudentIDAndIsHidden(projectID, student.getUserID(), false);
            for (StudentProjectTest t : testResults) {
                builder.append(";").append(t.getTestResultString());
            }
            String visibleTestResult = builder.toString();
            builder = new StringBuilder();
            builder.append(student.getUserName());
            testResults = studentProjectTestRepository.findByIdProjectIdentifierAndIdStudentIDAndIsHidden(projectID, student.getUserID(), true);
            for (StudentProjectTest t : testResults) {
                builder.append(";").append(t.getTestResultString());
            }
            String hiddenTestResult = builder.toString();
            visibleWriter.write(visibleTestResult + "\n");
            hiddenWriter.write(hiddenTestResult + "\n");
        }
        visibleWriter.close();
        hiddenWriter.close();
    }

    /**
     * Obtains all project for a list of students
     *
     * @param projectID Identifier for the project
     * @param userNames Front-end identifiers for all students that projects are retrieved for
     * @return          Projects associated with each username
     */
    public List<StudentProject> getStudentProjects(String projectID, List<String> userNames) {
        List<StudentProject> projects = new ArrayList<>();
        for(String userName: userNames) {
            Student student = studentRepository.findByUserName(userName);
            StudentProject project = studentProjectRepository.findByIdProjectIdentifierAndIdStudentIDAndIdSuite(projectID, student.getUserID(), "testall");
            if(project != null) {
                projects.add(project);
            }
        }
        return projects;
    }

    /**
     * Obtains front-end identifiers for students that work on the specified projects
     *
     * @param studentProjects   Projects that identifiers are being obtained for
     * @return                  Usernames associated with each project
     */
    public List<String> getStudentUserNames(List<StudentProject> studentProjects) {
        List<String> userNames = new ArrayList<>();
        for(StudentProject project : studentProjects) {
            Student student = studentRepository.findByUserID(project.getStudentID());
            userNames.add(student.getUserName());
        }
        return userNames;
    }

    /**
     * Obtains front-end identifiers for students that are assigned to a TA
     *
     * @param assignments   Assignments that identifiers are being obtained for
     * @return              Student usernames associated with each TA assignment
     */
    public List<String> getStudentUserNamesForTA(List<TeachingAssistantStudent> assignments) {
        List<String> userNames = new ArrayList<>();
        for(TeachingAssistantStudent assignment : assignments) {
            Student student = studentRepository.findByUserID(assignment.getStudentID());
            userNames.add(student.getUserName());
        }
        return userNames;
    }

    /**
     * Counts the commits for each specified student's project, and stores result in a temporary file
     * Primarily used to relay information to Python via temporary file
     *
     * @param projectID Identifier for project that git information is obtained from
     * @param projects  List of student projects to count commits for
     * @return          Filename for the temporary file created to store commit counts
     */
    public String countAllCommits(@NonNull String projectID, List<StudentProject> projects) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return null;
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return null;
        }
        String fileName = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_counts.txt";
        for(StudentProject s : projects) {
            Student student = studentRepository.findByUserID(s.getStudentID());
            String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
            executeBashScript("countCommits.sh " + destPath + " " + fileName + " " + student.getUserName());
        }
        return fileName;
    }

    /**
     * Counts the commits for each day for each specified student's project, and stores result in a temporary file
     * Primarily used to relay information to Python via temporary file
     *
     * @param projectID Identifier for project that git information is obtained from
     * @param projects  List of student projects to count commits for by day
     * @return          Filename for the temporary file created to store daily commit counts
     */
    public String countAllCommitsByDay(@NonNull String projectID, List<StudentProject> projects) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return null;
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return null;
        }
        String fileName = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_countsByDay.txt";
        for(StudentProject s : projects) {
            Student student = studentRepository.findByUserID(s.getStudentID());
            String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
            executeBashScript("countCommitsByDay.sh " + destPath + " " + fileName + " " + student.getUserName());
        }
        return fileName;
    }

    /**
     * Counts the commits for each day for a student's project, and stores result in a temporary file
     * Primarily used to relay information to Python via temporary file
     *
     * @param projectID Identifier for project that git information is obtained from
     * @param userName  Front-end identifier for student whose project is being analyzed
     * @return          Filename for the temporary file created to store daily commit count
     */
    public String countStudentCommitsByDay(@NonNull String projectID, @NonNull String userName) {
        if (DEBUG) {
            return pythonPath + "test_datasets/sampleCountsDay.txt";
        }

        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return null;
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return null;
        }
        Student student = studentRepository.findByUserName(userName);
        if(student == null) {
            return null;
        }
        if(!isTakingCourse(student, project)) {
            return null;
        }
        String fileName = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_studentCountsByDay.txt";
        String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
        if(executeBashScript("countCommitsByDay.sh " + destPath + " " + fileName + " " + student.getUserName()) == -1) {
            return null;
        }
        return fileName;
    }

    /**
     * Lists various git information, like additions and deletions, for each commit made
     * for each specified student's project, and stores result in a temporary file
     * Primarily used to relay information to Python via temporary file
     *
     * @param projectID Identifier for project that git information is obtained from
     * @param projects  List of student projects to list commit information for
     * @return          Filename for the temporary file created to store commit information
     */
    public String listAllCommitsByTime(@NonNull String projectID, List<StudentProject> projects) {
        if (DEBUG) {
            return pythonPath + "test_datasets/sampleCommitList.txt";
        }

        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return null;
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return null;
        }
        String fileName = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_commitInfo.txt";
        for(StudentProject s : projects) {
            Student student = studentRepository.findByUserID(s.getStudentID());
            String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
            executeBashScript("listCommitsByTime.sh " + destPath + " " + fileName + " " + student.getUserName());
        }
        return fileName;
    }

    /**
     * Lists various git information, like additions and deletions,
     * for each commit in a student's project, and stores result in a temporary file
     * Primarily used to relay information to Python via temporary file
     *
     * @param projectID Identifier for project that git information is obtained from
     * @param userName  Front-end identifier for student whose project is being analyzed
     * @return          Filename for the temporary file created to store commit information
     */
    public String listStudentCommitsByTime(@NonNull String projectID, @NonNull String userName) {
        if (DEBUG) {
            return pythonPath + "test_datasets/sampleCommitList.txt";
        }

        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return null;
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return null;
        }
        Student student = studentRepository.findByUserName(userName);
        if(student == null) {
            return null;
        }
        if(!isTakingCourse(student, project)) {
            return null;
        }
        String fileName = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_studentCommitInfo.txt";
        String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
        if(executeBashScript("listCommitsByTime.sh " + destPath + " " + fileName + " " + student.getUserName()) == -1) {
            return null;
        }
        return fileName;
    }
}
