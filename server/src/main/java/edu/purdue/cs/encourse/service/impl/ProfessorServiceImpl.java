package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.domain.relations.*;
import edu.purdue.cs.encourse.service.HelperService;
import edu.purdue.cs.encourse.service.ProfessorService;
import edu.purdue.cs.encourse.service.helper.*;
import edu.purdue.cs.encourse.util.JSONReturnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalTime;
import java.util.*;
import java.time.LocalDate;
import lombok.NonNull;

/**
 * Contains implementations for all services which are used by professors for managing a class
 * Primarily used by endpoints called for professors
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
@Service(value = ProfessorServiceImpl.NAME)
public class ProfessorServiceImpl implements ProfessorService {

    final static String NAME = "ProfessorService";
    
    @Autowired
    private HelperService helperService;

    @Autowired
    private CourseServiceImpl courseService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeachingAssistantRepository teachingAssistantRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private ProfessorCourseRepository professorCourseRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private StudentSectionRepository studentSectionRepository;

    @Autowired
    private StudentProjectRepository studentProjectRepository;

    @Autowired
    private StudentProjectDateRepository studentProjectDateRepository;

    @Autowired
    private TeachingAssistantStudentRepository teachingAssistantStudentRepository;

    @Autowired
    private ProjectTestScriptRepository projectTestScriptRepository;

    @Autowired
    private StudentProjectTestRepository studentProjectTestRepository;

    @Autowired
    private TeachingAssistantCourseRepository teachingAssistantCourseRepository;

    @Autowired
    private TeachingAssistantSectionRepository teachingAssistantSectionRepository;

    /**
     * Adds a new project to the database
     *
     * @param courseID      Identifier for course that is associated with the project
     * @param semester      Semester that the project is assigned
     * @param projectName   Name of the project for display purposes
     * @param repoName      Name of the remote repository to clone
     * @param startDate     Date that project will start for students
     * @param dueDate       Date that project will be due for students
     * @param testRate      Rate at which project will be pulled, tested, and analyzed automatically
     *                      testRate is currently not used since automatic loop is run as often as possible
     * @return              The object representing the created project
     */
    public Project addProject(@NonNull String courseID, @NonNull String semester, @NonNull String projectName, String repoName, String startDate, String dueDate, int testRate) {
        Project project = new Project(courseID, semester, projectName, repoName, startDate, dueDate, testRate);
        if(projectRepository.existsByProjectID(project.getProjectID())) {
            return null;
        }
        return projectRepository.save(project);
    }

    /**
     * Assigns a project so that students can now see and be graded for the project
     * Primarily used for a professor that wishes to add projects in advance but not assign them until later
     *
     * @param projectID Identifier for the project being assigned
     * @return          Error code
     */
    public int assignProject(@NonNull String projectID) {
        Project project = projectRepository.findByProjectID(projectID);
        if(project == null) {
            return -1;
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        List<String> completedStudents = new ArrayList<>();
        for(Section s : sections) {
            List<StudentSection> assignments = studentSectionRepository.findByIdSectionID(s.getSectionID());
            for(StudentSection a : assignments) {
                if(!(completedStudents.contains(a.getStudentID()))) {
                    studentProjectRepository.save(new StudentProject(a.getStudentID(), project.getProjectID(), "testall"));
                    helperService.initTestResults(a.getStudentID(), project.getProjectID());
                    completedStudents.add(a.getStudentID());
                }
            }
        }
        return 0;
    }

    /**
     * Assigns a project to a particular student so that they can now see and be graded for the project
     * Primarily used to assign a project to a student who joined the course after the project was already assigned
     *
     * @param projectID Identifier for the project being assigned
     * @param userName  Front-end identifier for student being assigned a project
     * @return          Error code
     */
    public int assignProjectToStudent(@NonNull String projectID, @NonNull String userName) {
        Project project = projectRepository.findByProjectID(projectID);
        if(project == null) {
            return -1;
        }
        Student student = studentRepository.findByUserName(userName);
        if(student == null) {
            return -2;
        }
        List<StudentProject> projects = studentProjectRepository.findByIdStudentID(student.getUserID());
        for(StudentProject p : projects) {
            if(p.getProjectID().equals(projectID)) {
                return -3;
            }
        }
        if(!helperService.isTakingCourse(student, project)) {
            return -4;
        }
        studentProjectRepository.save(new StudentProject(student.getUserID(), project.getProjectID(), "testall"));
        return 0;
    }

    /**
     * Deletes a project and all relations involving it
     *
     * @param projectID Identifier for project being deleted
     * @return          Error code
     */
    public int deleteProject(@NonNull String projectID) {
        Project project = projectRepository.findByProjectID(projectID);
        if(project == null) {
            return -1;
        }
        List<ProjectTestScript> testScripts = projectTestScriptRepository.findByIdProjectID(projectID);
        for(ProjectTestScript t : testScripts) {
            projectTestScriptRepository.delete(t);
        }
        List<StudentProject> projects = studentProjectRepository.findByIdProjectID(projectID);
        for(StudentProject p : projects) {
            studentProjectRepository.delete(p);
        }
        List<StudentProjectDate> projectDates = studentProjectDateRepository.findByIdProjectID(projectID);
        for(StudentProjectDate p : projectDates) {
            studentProjectDateRepository.delete(p);
        }
        projectRepository.delete(project);
        return 0;
    }

    /**
     * Modifies certain project information
     *
     * @param projectID Identifier for project being modified
     * @param field     Field to modify. Can be startDate, dueDate, repoName, or testRate
     * @param value     New value to update the project with. Must fit format of field
     * @return          Error code
     */
    public int modifyProject(@NonNull String projectID, @NonNull String field, String value) {
        Project project = projectRepository.findByProjectID(projectID);
        if(project == null) {
            return -1;
        }
        switch(field) {
            case "startDate": project.setStartDate(value); break;
            case "dueDate": project.setDueDate(value); break;
            case "repoName": project.setRepoName(value); break;
            case "testRate":
                try {
                    project.setTestRate(Integer.parseInt(value));
                    project.setTestCount(Integer.parseInt(value) - 1);
                    break;
                }
                catch(NumberFormatException e) {
                    return -2;
                }
            default: return -3;
        }
        projectRepository.save(project);
        return 0;
    }

    /**
     * Runs a bash script to clone projects for each student from a remote repository
     *
     * @param projectID Identifier for the project being cloned
     * @return          Error code
     */
    public int cloneProjects(@NonNull String projectID){
        Project project = projectRepository.findByProjectID(projectID);
        if(project == null) {
            return -1;
        }
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIDAndIdSuite(projectID, "testall");
        if(projects.isEmpty()) {
            return -2;
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return -3;
        }
        if(sections.get(0).getCourseHub() == null) {
            return -4;
        }
        if(project.getRepoName() == null) {
            return -5;
        }
        if(sections.get(0).getRemotePath() == null) {
            return -6;
        }
        int code = 0;
        for(StudentProject p : projects) {
            Student student = studentRepository.findByUserID(p.getStudentID());
            if(!(new File(sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName()).exists())) {
                String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName());
                String repoPath = (sections.get(0).getRemotePath() + "/" + student.getUserName() + "/" + project.getRepoName() + ".git");
                if (helperService.executeBashScript("cloneRepositories.sh " + destPath + " " + repoPath) == -1) {
                    code = -7;
                }
            }
        }
        if(helperService.executeBashScript("setPermissions.sh " + sections.get(0).getCourseID()) == -1) {
            return -8;
        }
        return code;
    }

    /**
     * Runs a bash script to pull projects for each student from a remote repository
     *
     * @param projectID Identifier for the project being pulled
     * @return          Error code
     */
    public int pullProjects(@NonNull String projectID){
        Project project = projectRepository.findByProjectID(projectID);
        if(project == null) {
            return -1;
        }
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIDAndIdSuite(projectID, "testall");
        if(projects.isEmpty()) {
            return -2;
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return -3;
        }
        if(sections.get(0).getCourseHub() == null) {
            return -4;
        }
        if(project.getRepoName() == null) {
            return -5;
        }
        int code = 0;
        int count = 0;
        long start = System.currentTimeMillis();
        project.setSyncing(true);
        project = projectRepository.save(project);
        for(StudentProject p : projects){
            count++;
            Student student = studentRepository.findByUserID(p.getStudentID());
            String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
            if(helperService.executeBashScript("pullRepositories.sh " + destPath) == -1) {
                code = -6;
            }
            helperService.updateStudentInformation(p.getProjectID(), student.getUserName());
            if(count % 10 == 0) {
                project.setOperationProgress(1.0 * count / projects.size());
                project.setOperationTime((System.currentTimeMillis() - start) / 1000);
                project = projectRepository.save(project);
            }
        }
        if(helperService.executeBashScript("setPermissions.sh " + sections.get(0).getCourseID()) == -1) {
            return -7;
        }
        project.setSyncing(false);
        project.setSyncDate(LocalDate.now().toString());
        project.setSyncTime(LocalTime.now().toString());
        project.setOperationProgress(1.0 * count / projects.size());
        project.setOperationTime((System.currentTimeMillis() - start) / 1000);
        projectRepository.save(project);
        return code;
    }

    /**
     * Obtains analysis on an entire class for potential cheating on a project
     * Primarily used in the professor's summary for academic dishonesty
     *
     * @param projectID Identifier for the project being analyzed
     * @return          JSON for front-end to parse
     */
    public JSONReturnable getClassCheating(@NonNull String projectID) {
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIDAndIdSuite(projectID, "testall");
        List<String> userNames = helperService.getStudentUserNames(projects);
        return courseService.getCheating(projectID, userNames);
    }

    /**
     * Obtains commit history for an entire class for a project
     * Primarily used in the professor's summary for a project
     *
     * @param projectID Identifier for the project being analyzed
     * @return          JSON for front-end to parse
     */
    public JSONReturnable getClassCommitList(@NonNull String projectID) {
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIDAndIdSuite(projectID, "testall");
        List<String> userNames = helperService.getStudentUserNames(projects);
        return courseService.getCommitList(projectID, userNames);
    }

    /**
     * Obtains progress history for an entire class for a project
     * Primarily used in the professor's summary for a project
     *
     * @param projectID Identifier for the project being analyzed
     * @return          JSON for front-end to parse
     */
    public JSONReturnable getClassProgress(@NonNull String projectID) {
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIDAndIdSuite(projectID, "testall");
        List<String> userNames = helperService.getStudentUserNames(projects);
        return courseService.getProgress(projectID, userNames);
    }

    /**
     * Obtains analysis on similarities between each student's project in an entire class
     * Primarily used in the professor's summary for academic dishonesty
     *
     * @param projectID Identifier for the project being analyzed
     * @return          JSON for front-end to parse
     */
    public JSONReturnable getClassSimilar(@NonNull String projectID) {
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIDAndIdSuite(projectID, "testall");
        List<String> userNames = helperService.getStudentUserNames(projects);
        return courseService.getSimilar(projectID, userNames);
    }

    /**
     * Obtains statistics, such as additions and deletions, for an entire class for a project
     * Primarily used in the professor's summary for a project
     *
     * @param projectID Identifier for the project being analyzed
     * @return          JSON for front-end to parse
     */
    public JSONReturnable getClassStatistics(@NonNull String projectID) {
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIDAndIdSuite(projectID, "testall");
        List<String> userNames = helperService.getStudentUserNames(projects);
        return courseService.getStatistics(projectID, userNames);
    }

    /**
     * Obtains a summary of passed test cases for an entire class for a project
     * Primarily used in the professor's summary for a project
     *
     * @param projectID Identifier for the project being analyzed
     * @return          JSON for front-end to parse
     */
    public JSONReturnable getClassTestSummary(@NonNull String projectID) {
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIDAndIdSuite(projectID, "testall");
        List<String> userNames = helperService.getStudentUserNames(projects);
        return courseService.getTestSummary(projectID, userNames);
    }

    /**
     * Adds a test script to the database and project repository so that it can be ran on projects
     *
     * @param projectID     Identifier for project that test script is made for
     * @param testName      Filename for the test script
     * @param testContents  The entire content of the test script file
     * @param isHidden      True if test script is part of a student's hidden grade, false otherwise
     * @param points        Points given to a student for passing the script
     * @return              Object representing the new test script
     */
    public ProjectTestScript uploadTestScript(@NonNull String projectID, @NonNull String testName, @NonNull String testContents, boolean isHidden, double points) {
        Project project = projectRepository.findByProjectID(projectID);
        if(project == null) {
            return null;
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return null;
        }
        String filePath;
        if(isHidden) {
            new File(sections.get(0).getCourseHub() + "/hidden_testcases/" + project.getRepoName()).mkdirs();
            filePath = sections.get(0).getCourseHub() + "/hidden_testcases/" + project.getRepoName() + "/" + testName;
        }
        else {
            new File(sections.get(0).getCourseHub() + "/testcases/" + project.getRepoName()).mkdirs();
            filePath = sections.get(0).getCourseHub() + "/testcases/" + project.getRepoName() + "/" + testName;
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(testContents);
            writer.close();
        }
        catch(IOException e) {
            return null;
        }
        ProjectTestScript script = new ProjectTestScript(projectID, testName, isHidden, points);
        helperService.executeBashScript("setPermissions.sh " + sections.get(0).getCourseID());
        return projectTestScriptRepository.save(script);
    }

    /**
     * Adds a test script to the database without adding it to the repository
     * Primarily used for registering a test case that is already on the server for a project
     *
     * @param projectID Identifier for project that test script is made for
     * @param testName  Filename for the test script
     * @param isHidden  True if test script is part of a student's hidden grade, false otherwise
     * @param points    Points given to a student for passing the script
     * @return          Object representing the new test script
     */
    public ProjectTestScript addTestScript(@NonNull String projectID, @NonNull String testName, boolean isHidden, double points) {
        Project project = projectRepository.findByProjectID(projectID);
        if(project == null) {
            return null;
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return null;
        }
        ProjectTestScript script = new ProjectTestScript(projectID, testName, isHidden, points);
        return projectTestScriptRepository.save(script);
    }

    /**
     * Modify an existing test script in the database
     *
     * @param projectID Identifier for the project having a test script modified
     * @param testName  Filename of the test script
     * @param field     Field being modified in the test script. Can be "content", "points", or "value"
     * @param value     Value that is used to update the test script
     * @return          Error code
     */
    public int modifyTestScript(@NonNull String projectID, @NonNull String testName, @NonNull String field, @NonNull String value) {
        ProjectTestScript script = projectTestScriptRepository.findByIdProjectIDAndIdTestScriptName(projectID, testName);
        if(script == null) {
            return -1;
        }
        Project project = projectRepository.findByProjectID(projectID);
        if(project == null) {
            return -2;
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return -3;
        }
        if(field.equals("content")) {
            String filePath;
            if(script.isHidden()) {
                new File(sections.get(0).getCourseHub() + "/hidden_testcases/" + project.getRepoName()).mkdirs();
                filePath = sections.get(0).getCourseHub() + "/hidden_testcases/" + project.getRepoName() + "/" + testName;
            }
            else {
                new File(sections.get(0).getCourseHub() + "/testcases/" + project.getRepoName()).mkdirs();
                filePath = sections.get(0).getCourseHub() + "/testcases/" + project.getRepoName() + "/" + testName;
            }
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
                writer.write("value");
                writer.close();
            }
            catch(IOException e) {
                return -4;
            }
            if(helperService.executeBashScript("setPermissions.sh " + sections.get(0).getCourseID()) == -1) {
                return -5;
            }
            return 0;
        }
        else if(field.equals("hidden")) {
            try {
                boolean isHidden = Boolean.parseBoolean(value);
                if(script.isHidden() == isHidden) {
                    return 0;
                }
                script.setHidden(isHidden);
            }
            catch(Exception e) {
                return -6;
            }
            String newFilePath;
            String oldFilePath;
            if(script.isHidden()) {
                new File(sections.get(0).getCourseHub() + "/hidden_testcases/" + project.getRepoName()).mkdirs();
                newFilePath = sections.get(0).getCourseHub() + "/hidden_testcases/" + project.getRepoName() + "/" + testName;
                oldFilePath = sections.get(0).getCourseHub() + "/testcases/" + project.getRepoName() + "/" + testName;
            }
            else {
                new File(sections.get(0).getCourseHub() + "/testcases/" + project.getRepoName()).mkdirs();
                newFilePath = sections.get(0).getCourseHub() + "/testcases/" + project.getRepoName() + "/" + testName;
                oldFilePath = sections.get(0).getCourseHub() + "/hidden_testcases/" + project.getRepoName() + "/" + testName;
            }
            try {
                BufferedReader reader = new BufferedReader(new FileReader(oldFilePath));
                BufferedWriter writer = new BufferedWriter(new FileWriter(newFilePath));
                String line;
                while((line = reader.readLine()) != null) {
                    writer.write(line + "\n");
                }
                reader.close();
                writer.close();
                new File(oldFilePath).delete();
            }
            catch(IOException e) {
                return -7;
            }
            if(helperService.executeBashScript("setPermissions.sh " + sections.get(0).getCourseID()) == -1) {
                return -8;
            }
        }
        else if(field.equals("points")) {
            try {
                script.setPointsWorth(Integer.parseInt(value));
            }
            catch(NumberFormatException e) {
                return -9;
            }
        }
        else {
            return -10;
        }
        projectTestScriptRepository.save(script);
        return 0;
    }

    /**
     * Adds a testing suite to a project
     *
     * @param projectID Identifier for project
     * @param suite     Name of the suite. Can be a new or existing suite
     * @return          Error code
     */
    public int addSuiteToProject(@NonNull String projectID, @NonNull String suite) {
        Project project = projectRepository.findByProjectID(projectID);
        if(project == null) {
            return -1;
        }
        if(!project.hasSuite(suite)) {
            project.addSuite(suite);
        }
        projectRepository.save(project);
        return 0;
    }

    /**
     * Adds a test script to a testing suite
     *
     * @param projectID Identifier for project that test script is made for
     * @param testName  Filename of the test script being added to a suite
     * @param suite     Name of the suite. Can be a new or existing suite
     * @return          Error code
     */
    public int addTestScriptToSuite(@NonNull String projectID, @NonNull String testName, @NonNull String suite) {
        ProjectTestScript testScript = projectTestScriptRepository.findByIdProjectIDAndIdTestScriptName(projectID, testName);
        if(testScript == null) {
            return -1;
        }
        if(!testScript.hasSuite(suite)) {
            testScript.addSuite(suite);
        }
        Project project = projectRepository.findByProjectID(projectID);
        if(project == null) {
            return -2;
        }
        if(!project.hasSuite(suite)) {
            project.addSuite(suite);
        }
        projectTestScriptRepository.save(testScript);
        projectRepository.save(project);
        return 0;
    }

    /**
     * Runs a generic testall on every student's work for a project so far
     * Primarily used in the automatic pull and test interval but can also be initiated manually
     *
     * @param projectID Identifier for the project being tested
     * @return          Error code
     */
    public int runTestall(@NonNull String projectID) {
        Project project = projectRepository.findByProjectID(projectID);
        if(project == null) {
            return -1;
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return -2;
        }
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIDAndIdSuite(projectID, "testall");
        String testCaseDirectory = sections.get(0).getCourseHub() + "/testcases/" + project.getRepoName();
        String hiddenTestCaseDirectory = sections.get(0).getCourseHub() + "/hidden_testcases/" + project.getRepoName();
        String makefilePath = sections.get(0).getCourseHub() + "/makefiles/" + project.getRepoName() + "/Makefile";
        File directory = new File(testCaseDirectory);
        if(!directory.isDirectory() || directory.listFiles().length == 0) {
            return -3;
        }
        File file = new File(makefilePath);
        if(!file.exists()) {
            return -4;
        }
        int code = 0;
        int count = 0;
        long start = System.currentTimeMillis();
        project.setTesting(true);
        projectRepository.save(project);
        String fileName = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_gitHashes.txt";
        for(StudentProject p : projects) {
            count++;
            Student student = studentRepository.findByUserID(p.getStudentID());
            String testingDirectory = sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName();
            helperService.executeBashScript("listTestUpdateHistory.sh " + testingDirectory + " " + fileName);
            try {
                BufferedReader reader = new BufferedReader(new FileReader(fileName));
                String line = reader.readLine();
                helperService.executeBashScript("checkoutPreviousCommit.sh " + testingDirectory + " origin");
                String[] commitInfo = line.split(" ");
                String date = commitInfo[2];
                helperService.executeBashScript("checkoutPreviousCommit.sh " + testingDirectory + " " + commitInfo[1]);
                if(helperService.executeBashScript("runMakefile.sh " + testingDirectory + " " + makefilePath) == -1) {
                    code = -5;
                }
                TestExecuter tester = new TestExecuter(project.getCourseID(), testingDirectory + "/" + helperService.getTestDir(), testCaseDirectory, hiddenTestCaseDirectory);
                Thread thread = new Thread(tester);
                thread.start();
                Thread.sleep(project.getMaximumRuntime());
                thread.interrupt();
                helperService.executeBashScript("killProcesses.sh " + project.getCourseID());
                String visibleResult = tester.getVisibleResult();
                String hiddenResult = tester.getHiddenResult();
                if(visibleResult == null) {
                    visibleResult = "";
                }
                if(hiddenResult == null) {
                    hiddenResult = "";
                }
                double visibleGrade = helperService.parseProgressForProject(p.getProjectID(), visibleResult);
                double hiddenGrade = helperService.parseProgressForProject(p.getProjectID(), hiddenResult);
                helperService.updateTestResults(hiddenResult, p.getStudentID(), p.getProjectID(), true);
                StudentProjectDate projectDate = studentProjectDateRepository.findByIdDateAndIdProjectIDAndIdStudentID(date, projectID, student.getUserID());
                if(projectDate == null) {
                    StudentProjectDate d = new StudentProjectDate(p.getStudentID(), p.getProjectID(), date, visibleGrade, hiddenGrade);
                    studentProjectDateRepository.save(d);
                }
                else {
                    if(visibleGrade > projectDate.getDateVisibleGrade()) {
                        projectDate.setDateVisibleGrade(visibleGrade);
                        studentProjectDateRepository.save(projectDate);
                    }
                    if(hiddenGrade > projectDate.getDateHiddenGrade()) {
                        projectDate.setDateHiddenGrade(hiddenGrade);
                        studentProjectDateRepository.save(projectDate);
                    }
                }
                if(visibleGrade > p.getBestVisibleGrade()) {
                    helperService.updateTestResults(visibleResult, p.getStudentID(), p.getProjectID(), false);
                }
                if(hiddenGrade > p.getBestHiddenGrade()) {
                    helperService.updateTestResults(hiddenResult, p.getStudentID(), p.getProjectID(), true);
                }
                p = studentProjectRepository.findByIdProjectIDAndIdStudentIDAndIdSuite(p.getProjectID(), p.getStudentID(), "testall");
                line = reader.readLine();
                commitInfo = line.split(" ");
                date = commitInfo[2];
                helperService.executeBashScript("checkoutPreviousCommit.sh " + testingDirectory + " " + commitInfo[1]);
                if(helperService.executeBashScript("runMakefile.sh " + testingDirectory + " " + makefilePath) == -1) {
                    code = -5;
                }
                tester = new TestExecuter(project.getCourseID(), testingDirectory + "/" + helperService.getTestDir(), testCaseDirectory, hiddenTestCaseDirectory);
                thread = new Thread(tester);
                thread.start();
                Thread.sleep(project.getMaximumRuntime());
                thread.interrupt();
                helperService.executeBashScript("killProcesses.sh " + project.getCourseID());
                visibleResult = tester.getVisibleResult();
                hiddenResult = tester.getHiddenResult();
                if(visibleResult == null) {
                    visibleResult = "";
                }
                if(hiddenResult == null) {
                    hiddenResult = "";
                }
                visibleGrade = helperService.parseProgressForProject(projectID, visibleResult);
                hiddenGrade = helperService.parseProgressForProject(projectID, hiddenResult);
                projectDate = studentProjectDateRepository.findByIdDateAndIdProjectIDAndIdStudentID(date, projectID, student.getUserID());
                if(projectDate == null) {
                    StudentProjectDate d = new StudentProjectDate(p.getStudentID(), p.getProjectID(), date, visibleGrade, hiddenGrade);
                    studentProjectDateRepository.save(d);
                }
                else {
                    if(visibleGrade > projectDate.getDateVisibleGrade()) {
                        projectDate.setDateVisibleGrade(visibleGrade);
                        studentProjectDateRepository.save(projectDate);
                    }
                    if(hiddenGrade > projectDate.getDateHiddenGrade()) {
                        projectDate.setDateHiddenGrade(hiddenGrade);
                        studentProjectDateRepository.save(projectDate);
                    }
                }
                if(visibleGrade > p.getBestVisibleGrade()) {
                    helperService.updateTestResults(visibleResult, p.getStudentID(), p.getProjectID(), false);
                }
                if(hiddenGrade > p.getBestHiddenGrade()) {
                    helperService.updateTestResults(hiddenResult, p.getStudentID(), p.getProjectID(), true);
                }
                helperService.executeBashScript("checkoutPreviousCommit.sh " + testingDirectory + " origin");
                reader.close();
                project.setOperationProgress(1.0 * count / projects.size());
                project.setOperationTime((System.currentTimeMillis() - start) / 1000);
                projectRepository.save(project);
            }
            catch(Exception e) {
                code = -6;
                project.setOperationProgress(1.0 * count / projects.size());
                project.setOperationTime((System.currentTimeMillis() - start) / 1000);
                projectRepository.save(project);
                System.out.println("\nException at testall\n");
                helperService.executeBashScript("checkoutPreviousCommit.sh " + testingDirectory + " origin");
            }
        }
        project.setTesting(false);
        project.setTestDate(LocalDate.now().toString());
        project.setTestTime(LocalTime.now().toString());
        project.setOperationProgress(1.0 * count / projects.size());
        project.setOperationTime((System.currentTimeMillis() - start) / 1000);
        projectRepository.save(project);
        return code;
    }

    /**
     * Runs a generic testall on every student's work for a project using two commits from each day that they worked
     * Primarily used for populating the database with progress for a project that already finished
     *
     * @param projectID Identifier for the project being tested
     * @return          Error code
     */
    public int runHistoricTestall(@NonNull String projectID) {
        Project project = projectRepository.findByProjectID(projectID);
        if(project == null) {
            return -1;
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return -2;
        }
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIDAndIdSuite(projectID, "testall");
        String testCaseDirectory = sections.get(0).getCourseHub() + "/testcases/" + project.getRepoName();
        String hiddenTestCaseDirectory = sections.get(0).getCourseHub() + "/hidden_testcases/" + project.getRepoName();
        String makefilePath = sections.get(0).getCourseHub() + "/makefiles/" + project.getRepoName() + "/Makefile";
        File directory = new File(testCaseDirectory);
        if(!directory.isDirectory() || directory.listFiles().length == 0) {
            return -3;
        }
        File file = new File(makefilePath);
        if(!file.exists()) {
            return -4;
        }
        int code = 0;
        String fileName = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_gitHashes.txt";
        for(StudentProject p : projects) {
            Student student = studentRepository.findByUserID(p.getStudentID());
            String testingDirectory = sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName();
            helperService.executeBashScript("checkoutPreviousCommit.sh " + testingDirectory + " origin");
            helperService.executeBashScript("listTestUpdateHistory.sh " + testingDirectory + " " + fileName);
            try {
                BufferedReader reader = new BufferedReader(new FileReader(fileName));
                String line;
                String commitDate = "";
                while((line = reader.readLine()) != null && !line.equals("")) {
                    String[] commitInfo = line.split(" ");
					System.out.println("Student: " + student.getUserName() + " Date: " + commitInfo[2]);
                    if(commitInfo[2].equals(commitDate)) {
                        continue;
                    }
                    commitDate = commitInfo[2];
                    String date = commitInfo[2];
                    helperService.executeBashScript("checkoutPreviousCommit.sh " + testingDirectory + " " + commitInfo[1]);
                    if(helperService.executeBashScript("runMakefile.sh " + testingDirectory + " " + makefilePath) == -1) {
                        code = -5;
                    }
                    TestExecuter tester = new TestExecuter(project.getCourseID(), testingDirectory + "/" + helperService.getTestDir(), testCaseDirectory, hiddenTestCaseDirectory);
                    Thread thread = new Thread(tester);
                    thread.start();
                    Thread.sleep(project.getMaximumRuntime());
                    thread.interrupt();
                    helperService.executeBashScript("killProcesses.sh " + project.getCourseID());
                    String visibleResult = tester.getVisibleResult();
                    String hiddenResult = tester.getHiddenResult();
                    if(visibleResult == null) {
                        visibleResult = "";
                    }
                    if(hiddenResult == null) {
                        hiddenResult = "";
                    }
                    double visibleGrade = helperService.parseProgressForProject(projectID, visibleResult);
                    double hiddenGrade = helperService.parseProgressForProject(projectID, hiddenResult);
                    StudentProjectDate projectDate = studentProjectDateRepository.findByIdDateAndIdProjectIDAndIdStudentID(date, projectID, student.getUserID());
                    if(projectDate == null) {
                        StudentProjectDate d = new StudentProjectDate(p.getStudentID(), p.getProjectID(), date, visibleGrade, hiddenGrade);
                        studentProjectDateRepository.save(d);
                    }
                    else {
                        if(visibleGrade > projectDate.getDateVisibleGrade()) {
                            projectDate.setDateVisibleGrade(visibleGrade);
                            studentProjectDateRepository.save(projectDate);
                        }
                        if(hiddenGrade > projectDate.getDateHiddenGrade()) {
                            projectDate.setDateHiddenGrade(hiddenGrade);
                            studentProjectDateRepository.save(projectDate);
                        }
                    }
                    if(visibleGrade > p.getBestVisibleGrade()) {
                        helperService.updateTestResults(visibleResult, p.getStudentID(), p.getProjectID(), false);
                    }
                    if(hiddenGrade > p.getBestHiddenGrade()) {
                        helperService.updateTestResults(hiddenResult, p.getStudentID(), p.getProjectID(), true);
                    }
                    p = studentProjectRepository.findByIdProjectIDAndIdStudentIDAndIdSuite(p.getProjectID(), p.getStudentID(), "testall");
                    line = reader.readLine();
                    commitInfo = line.split(" ");
                    date = commitInfo[2];
                    helperService.executeBashScript("checkoutPreviousCommit.sh " + testingDirectory + " " + commitInfo[1]);
                    if(helperService.executeBashScript("runMakefile.sh " + testingDirectory + " " + makefilePath) == -1) {
                        code = -5;
                    }
                    tester = new TestExecuter(project.getCourseID(), testingDirectory + "/" + helperService.getTestDir(), testCaseDirectory, hiddenTestCaseDirectory);
                    thread = new Thread(tester);
                    thread.start();
                    Thread.sleep(project.getMaximumRuntime());
                    thread.interrupt();
                    helperService.executeBashScript("killProcesses.sh " + project.getCourseID());
                    visibleResult = tester.getVisibleResult();
                    hiddenResult = tester.getHiddenResult();
                    if(visibleResult == null) {
                        visibleResult = "";
                    }
                    if(hiddenResult == null) {
                        hiddenResult = "";
                    }
                    visibleGrade = helperService.parseProgressForProject(projectID, visibleResult);
                    hiddenGrade = helperService.parseProgressForProject(projectID, hiddenResult);
                    projectDate = studentProjectDateRepository.findByIdDateAndIdProjectIDAndIdStudentID(date, projectID, student.getUserID());
                    if(projectDate == null) {
                        StudentProjectDate d = new StudentProjectDate(p.getStudentID(), p.getProjectID(), date, visibleGrade, hiddenGrade);
                        studentProjectDateRepository.save(d);
                    }
                    else {
                        if(visibleGrade > projectDate.getDateVisibleGrade()) {
                            projectDate.setDateVisibleGrade(visibleGrade);
                            studentProjectDateRepository.save(projectDate);
                        }
                        if(hiddenGrade > projectDate.getDateHiddenGrade()) {
                            projectDate.setDateHiddenGrade(hiddenGrade);
                            studentProjectDateRepository.save(projectDate);
                        }
                    }
                    if(visibleGrade > p.getBestVisibleGrade()) {
                        helperService.updateTestResults(visibleResult, p.getStudentID(), p.getProjectID(), false);
                    }
                    if(hiddenGrade > p.getBestHiddenGrade()) {
                        helperService.updateTestResults(hiddenResult, p.getStudentID(), p.getProjectID(), true);
                    }
                    p = studentProjectRepository.findByIdProjectIDAndIdStudentIDAndIdSuite(p.getProjectID(), p.getStudentID(), "testall");
                }
                helperService.executeBashScript("checkoutPreviousCommit.sh " + testingDirectory + " origin");
                reader.close();
            }
            catch(Exception e) {
                code = -6;
                System.out.println("\nException at historic testall\n");
                helperService.executeBashScript("checkoutPreviousCommit.sh " + testingDirectory + " origin");
            }
        }
        project.setTestDate(LocalDate.now().toString());
        projectRepository.save(project);
        return code;
    }

    /**
     * Runs time consuming operations on an automatic interval
     * Primarily used in a scheduled task that runs with a fixed delay between start and finish
     */
    public void pullAndTestAllProjects() {
        for(Project project : projectRepository.findAll()) {
            if(project.getTestRate() > 0 && project.getTestCount() <= 0) {
                System.out.println("Pulling project " + project.getProjectName());
                pullProjects(project.getProjectID());
                System.out.println("Testing project " + project.getProjectName());
                List<ProjectTestScript> tests = projectTestScriptRepository.findByIdProjectID(project.getProjectID());
                if(!tests.isEmpty()) {
                    runTestall(project.getProjectID());
                }
                project.setTestCount(project.getTestRate() - 1);
                project.setOperationProgress(0);
                project.setOperationTime(0);
                projectRepository.save(project);
            }
            else {
                project.setTestCount(project.getTestCount() - 1);
                projectRepository.save(project);
            }
        }
        helperService.executeBashScript("cleanDirectory.sh src/main/temp");
    }

    /**
     * Assigns a teaching assistant to a section without yet assigning students to the TA
     * Primarily used to initially organize TAs into sections
     *
     * @param teachAssistUserName   Front-end identifier for the teaching assistant
     * @param sectionID             Identifier for the section that they are assigned to
     * @return                      Error code
     */
    public int assignTeachingAssistantToSection(@NonNull String teachAssistUserName, @NonNull String sectionID) {
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(teachAssistUserName);
        if(teachingAssistant == null) {
            return -1;
        }
        Section section = sectionRepository.findBySectionID(sectionID);
        if(section == null) {
            return -2;
        }
        TeachingAssistantCourse check = teachingAssistantCourseRepository.findByIdTeachingAssistantIDAndIdSemesterAndIdCourseID(teachingAssistant.getUserID(), section.getSemester(), section.getCourseID());
        if(check == null) {
            return -3;
        }
        TeachingAssistantSection assignment = teachingAssistantSectionRepository.findByIdTeachingAssistantIDAndIdSectionID(teachingAssistant.getUserID(), sectionID);
        if(assignment != null) {
            return -4;
        }
        assignment = new TeachingAssistantSection(teachingAssistant.getUserID(), sectionID);
        teachingAssistantSectionRepository.save(assignment);
        return 0;
    }

    /**
     * Assigns a teaching assistant to a student in a section that they oversee
     * Primarily used in the professor's panel for assigning TAs to students
     *
     * @param teachAssistUserName   Front-end identifier for TA being assigned
     * @param studentUserName       Front-end identifier for student that TA is assigned to
     * @param sectionID             Identifier for the section that assignment is effective for
     * @return                      Error code
     */
    public int assignTeachingAssistantToStudentInSection(@NonNull String teachAssistUserName, @NonNull String studentUserName, @NonNull String sectionID) {
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(teachAssistUserName);
        if(teachingAssistant == null) {
            return -1;
        }
        Student student = studentRepository.findByUserName(studentUserName);
        if(student == null) {
            return -2;
        }
        Section section = sectionRepository.findBySectionID(sectionID);
        if(section == null) {
            return -3;
        }
        TeachingAssistantSection check = teachingAssistantSectionRepository.findByIdTeachingAssistantIDAndIdSectionID(teachingAssistant.getUserID(), sectionID);
        if(check == null) {
            return -4;
        }
        StudentSection checkStudent = studentSectionRepository.findByIdStudentIDAndIdSectionID(student.getUserID(), sectionID);
        if(checkStudent == null) {
            return -5;
        }
        TeachingAssistantStudent assignment = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdStudentIDAndIdCourseID(teachingAssistant.getUserID(), student.getUserID(), section.getCourseID());
        if(assignment != null) {
            return -6;
        }
        assignment = new TeachingAssistantStudent(teachingAssistant.getUserID(), student.getUserID(), section.getCourseID());
        teachingAssistantStudentRepository.save(assignment);
        return 0;
    }

    /**
     * Assigns a teaching assistant to all students in a section that they oversee
     * Primarily used in the professor's panel for quickly assigning a TA to many students
     *
     * @param teachAssistUserName   Front-end identifier for TA being assigned
     * @param sectionID             Identifier for the section that assignments are effective for
     * @return                      Error code
     */
    public int assignTeachingAssistantToAllStudentsInSection(@NonNull String teachAssistUserName, @NonNull String sectionID) {
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(teachAssistUserName);
        if(teachingAssistant == null) {
            return -1;
        }
        TeachingAssistantSection check = teachingAssistantSectionRepository.findByIdTeachingAssistantIDAndIdSectionID(teachingAssistant.getUserID(), sectionID);
        if(check == null) {
            return -2;
        }
        Section section = sectionRepository.findBySectionID(sectionID);
        if(section == null) {
            return -3;
        }
        List<StudentSection> studentSections = studentSectionRepository.findByIdSectionID(sectionID);
        for(StudentSection s : studentSections) {
            TeachingAssistantStudent assignment = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdStudentIDAndIdCourseID(teachingAssistant.getUserID(), s.getStudentID(), section.getCourseID());
            if (assignment == null) {
                assignment = new TeachingAssistantStudent(teachingAssistant.getUserID(), s.getStudentID(), section.getCourseID());
                teachingAssistantStudentRepository.save(assignment);
            }
        }
        return 0;
    }

    /**
     * Obtains data needed for displaying a professor's courses when they log in
     *
     * @param userName  Front-end identifier for the professor
     * @return          JSON for front-end to parse
     */
    public JSONArray getCourseData(@NonNull String userName) {
        Professor professor = professorRepository.findByUserName(userName);
        if(professor == null) {
            return null;
        }
        List<ProfessorCourse> courses = professorCourseRepository.findByIdProfessorID(professor.getUserID());
        if(courses.isEmpty()) {
            return null;
        }
        List<String> courseIDs = new ArrayList<>();
        for(ProfessorCourse course : courses) {
            courseIDs.add(course.getCourseID());
        }
        return courseService.getCourseData(userName, courseIDs);
    }

    /**
     * Obtains data for displaying every student in a professor's course
     *
     * @param semester  Semester that course is being taught
     * @param courseID  Identifier for the course
     * @return          JSON for front-end to parse
     */
    public JSONArray getStudentData(@NonNull String semester, @NonNull String courseID) {
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(semester, courseID);
        List<String> userNames = new ArrayList<>();
        for(Section section : sections) {
            List<StudentSection> studentSections = studentSectionRepository.findByIdSectionID(section.getSectionID());
            for(StudentSection studentSection : studentSections) {
                Student student = studentRepository.findByUserID(studentSection.getStudentID());
                if(!userNames.contains(student.getUserName())) {
                    userNames.add(student.getUserName());
                }
            }
        }
        if(userNames.isEmpty()) {
            return null;
        }
        return courseService.getStudentData(userNames);
    }

    /**
     * Obtains data for displaying every teaching assistant and their assignments
     *
     * @param semester  Semester that course is being taught
     * @param courseID  Identifier for the course
     * @return          JSON for front-end to parse
     */
    public JSONArray getTeachingAssistantData(@NonNull String semester, @NonNull String courseID) {
        List<TeachingAssistantCourse> teachingAssistants = teachingAssistantCourseRepository.findByIdSemesterAndIdCourseID(semester, courseID);
        if(teachingAssistants.isEmpty()) {
            return null;
        }
        JSONArray teachingAssistantsJSON = new JSONArray();
        for(TeachingAssistantCourse t : teachingAssistants) {
            TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserID(t.getTeachingAssistantID());
            JSONObject teachingAssistantJSON = new JSONObject();
            List<TeachingAssistantStudent> assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdCourseID(teachingAssistant.getUserID(), courseID);
            List<String> studentIDs = new ArrayList<>();
            for(TeachingAssistantStudent a : assignments) {
                Student student = studentRepository.findByUserID(a.getStudentID());
                studentIDs.add(student.getUserName());
            }
            List<TeachingAssistantSection> sections = teachingAssistantSectionRepository.findByIdTeachingAssistantID(t.getTeachingAssistantID());
            List<String> sectionIDs = new ArrayList<>();
            for(TeachingAssistantSection s : sections) {
                sectionIDs.add(s.getSectionID());
            }
            teachingAssistantJSON.put("first_name", teachingAssistant.getFirstName());
            teachingAssistantJSON.put("last_name", teachingAssistant.getLastName());
            teachingAssistantJSON.put("id", teachingAssistant.getUserName());
            teachingAssistantJSON.put("assignment_type", 0);
            teachingAssistantJSON.put("students", studentIDs);
            teachingAssistantJSON.put("sections", sectionIDs);
            teachingAssistantsJSON.add(teachingAssistantJSON);
        }
        return teachingAssistantsJSON;
    }

    // TODO JARETT and REED connect new calculateDiffScore.sh
    public JSONReturnable getDiffScore(@NonNull String projectID) {
        JSONReturnable json = null;
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIDAndIdSuite(projectID, "testall");
        // String visibleTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_visibleTests.txt";
        // String hiddenTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_hiddenTests.txt";
        // try {
        //     helperService.createTestFiles(visibleTestFile, hiddenTestFile, projects);
        // } catch (IOException e) {
        //     json = new JSONReturnable(-1, null);
        // }
        //
        // if (helperService.DEBUG) {
        //     visibleTestFile = helperService.getPythonPath() + "/test_datasets/sampleVisibleTestCases.txt";
        //     hiddenTestFile = helperService.getPythonPath() + "/test_datasets/sampleHiddenTestCases.txt";
        // }

        // TODO: Check that test results work as expected
        String pyPath = helperService.getPythonPath() + "get_identical_count.py";
        String command = helperService.getPythonCommand() + " " + pyPath + " " /* + File */;
        json = helperService.runPython(command);
        //helperService.executeBashScript("cleanDirectory.sh src/main/temp");
        return json;
    }
}
