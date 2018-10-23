package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.domain.relations.*;
import edu.purdue.cs.encourse.service.ProfessorService;
import edu.purdue.cs.encourse.util.ConfigurationManager;
import edu.purdue.cs.encourse.util.JSONReturnable;
import org.aspectj.weaver.patterns.IfPointcut;
import org.codehaus.jackson.map.util.JSONPObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Executors;
import lombok.NonNull;

import javax.persistence.criteria.CriteriaBuilder;

@Service(value = ProfessorServiceImpl.NAME)
public class ProfessorServiceImpl implements ProfessorService {

    public final static String NAME = "ProfessorService";
    private final static String pythonPath = "src/main/python/";
    private final static String tailFilePath = "src/main/temp/";
    private final static int RATE = 3600000;
    private final static Boolean DEBUG = ConfigurationManager.getInstance().debug;
    private final static Boolean OBFUSCATE = false;

    /** Hardcoded for shell project, since shell project test cases use relative paths instead of absolute **/
    final static String testDir = "test-shell";

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

    private int executeBashScript(@NonNull String command) {
        try {
            Process process = Runtime.getRuntime().exec("./src/main/bash/" + command + " 2> /dev/null");
            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
            Executors.newSingleThreadExecutor().submit(streamGobbler);
            process.waitFor();
        }
        catch(Exception e) {
            return -1;
        }
        return 0;
    }

    private boolean isTakingCourse(@NonNull Student student, @NonNull Project project) {
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

    private double parseProgressForProject(@NonNull String projectID, @NonNull String testOutput) {
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
        return (earnedPoints / maxPoints) * 100;
    }

    private void updateTestResults(String result, String studentID, String projectID, boolean isHidden) {
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
    }

    /** Adds a new project to the database, which needs to be done before cloning the project in the course hub **/
    public Project addProject(@NonNull String courseID, @NonNull String semester, @NonNull String projectName, String repoName, String startDate, String dueDate, int testRate) {
        Project project = new Project(courseID, semester, projectName, repoName, startDate, dueDate, testRate);
        if(projectRepository.existsByProjectIdentifier(project.getProjectIdentifier())) {
            return null;
        }
        return projectRepository.save(project);
    }

    /** Assigns a project to all students in the course so that the project starts being tracked **/
    public int assignProject(@NonNull String projectID) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return -1;
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        List<String> completedStudents = new ArrayList<>();
        for(Section s : sections) {
            List<StudentSection> assignments = studentSectionRepository.findByIdSectionIdentifier(s.getSectionIdentifier());
            for(StudentSection a : assignments) {
                if(!(completedStudents.contains(a.getStudentID()))) {
                    studentProjectRepository.save(new StudentProject(a.getStudentID(), project.getProjectIdentifier()));
                    completedStudents.add(a.getStudentID());
                }
            }
        }
        return 0;
    }

    /** Assigns a project to a single student, to account for students being added to course after a project was assigned **/
    public int assignProjectToStudent(@NonNull String projectID, @NonNull String userName) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return -1;
        }
        Student student = studentRepository.findByUserName(userName);
        if(student == null) {
            return -2;
        }
        List<StudentProject> projects = studentProjectRepository.findByIdStudentID(student.getUserID());
        for(StudentProject p : projects) {
            if(p.getProjectIdentifier().equals(projectID)) {
                return -3;
            }
        }
        if(!isTakingCourse(student, project)) {
            return -4;
        }
        studentProjectRepository.save(new StudentProject(student.getUserID(), project.getProjectIdentifier()));
        return 0;
    }

    /** Deletes project and all relations referring to project **/
    public int deleteProject(@NonNull String projectID) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return -1;
        }
        List<ProjectTestScript> testScripts = projectTestScriptRepository.findByIdProjectIdentifier(projectID);
        for(ProjectTestScript t : testScripts) {
            projectTestScriptRepository.delete(t);
        }
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifier(projectID);
        for(StudentProject p : projects) {
            studentProjectRepository.delete(p);
        }
        List<StudentProjectDate> projectDates = studentProjectDateRepository.findByIdProjectIdentifier(projectID);
        for(StudentProjectDate p : projectDates) {
            studentProjectDateRepository.delete(p);
        }
        projectRepository.delete(project);
        return 0;
    }

    /** Modifies project information like start and end dates **/
    // TODO: RACE CONDITION FOR MULTIPLE CALLS SIMULTANEOUSLY
    public int modifyProject(@NonNull String projectID, @NonNull String field, String value) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
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

    public Project getProject(@NonNull String projectID) {
        return projectRepository.findByProjectIdentifier(projectID);
    }

    /** Runs a bash script to initially clone every student's git repository. Each university should supply its own
     bash script, since repo locations will vary **/
    public int cloneProjects(@NonNull String projectID){
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return -1;
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return -2;
        }
        if(sections.get(0).getCourseHub() == null) {
            return -3;
        }
        if(project.getRepoName() == null) {
            return -4;
        }
        if(sections.get(0).getRemotePath() == null) {
            return -5;
        }
        int code = 0;
        for(Section s : sections){
            List<StudentSection> assignments =
                    studentSectionRepository.findByIdSectionIdentifier(s.getSectionIdentifier());
            for(StudentSection a : assignments){
                Student student = studentRepository.findByUserID(a.getStudentID());
                if(!(new File(s.getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName()).exists())) {
                    String destPath = (s.getCourseHub() + "/" + student.getUserName());
                    String repoPath = (s.getRemotePath() + "/" + student.getUserName() + "/" + project.getRepoName() + ".git");
                    if(executeBashScript("cloneRepositories.sh " + destPath + " " + repoPath) == -1) {
                        code = -6;
                    }
                }
            }
        }
        if(executeBashScript("setPermissions.sh " + sections.get(0).getCourseID()) == -1) {
            return -7;
        }
        return code;
    }

    /** Pulls the designated project within every students directory under the course hub **/
    public int pullProjects(@NonNull String projectID){
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return -1;
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return -2;
        }
        if(sections.get(0).getCourseHub() == null) {
            return -3;
        }
        if(project.getRepoName() == null) {
            return -4;
        }
        int code = 0;
        List<String> completedStudents = new ArrayList<>();
        for(Section s : sections){
            List<StudentSection> assignments =
                    studentSectionRepository.findByIdSectionIdentifier(s.getSectionIdentifier());
            for(StudentSection a : assignments) {
                Student student = studentRepository.findByUserID(a.getStudentID());
                if(!(completedStudents.contains(student.getUserName()))) {
                    String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
                    if(executeBashScript("pullRepositories.sh " + destPath) == -1) {
                        code = -5;
                    }
                    completedStudents.add(student.getUserName());
                }
            }
        }
        if(executeBashScript("setPermissions.sh " + sections.get(0).getCourseID()) == -1) {
            return -6;
        }
        return code;
    }

    public JSONReturnable getStudentProgress(@NonNull String projectID, @NonNull String userName) {
        if(!projectRepository.existsByProjectIdentifier(projectID)) {
            return new JSONReturnable(-1, null);
        }
        Student student = studentRepository.findByUserName(userName);
        if(student == null) {
            return new JSONReturnable(-2, null);
        }
        List<StudentProjectDate> projectDates = studentProjectDateRepository.findByIdProjectIdentifierAndIdStudentID(projectID, student.getUserID());
        String visibleTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_visibleTestDates.txt";
        String hiddenTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_hiddenTestDates.txt";
        try {
            BufferedWriter visibleWriter = new BufferedWriter(new FileWriter(visibleTestFile));
            BufferedWriter hiddenWriter = new BufferedWriter(new FileWriter(hiddenTestFile));
            visibleWriter.write("Start " + userName + "\n");
            hiddenWriter.write("Start " + userName + "\n");
            for (StudentProjectDate d : projectDates) {
                visibleWriter.write(d.getDate() + " " + d.getDateVisibleGrade() + "\n");
                hiddenWriter.write(d.getDate() + " " + d.getDateHiddenGrade() + "\n");
            }
            visibleWriter.write("End " + userName + "\n");
            hiddenWriter.write("End " + userName + "\n");
            visibleWriter.close();
            hiddenWriter.close();
        }
        catch(IOException e) {
            return new JSONReturnable(-3, null);
        }
        // TODO: @Ryan adjust python command if needed
        String pyPath = pythonPath + "get_individual_progress.py";
        String command = "python3 " + pyPath + " " + visibleTestFile + " " + hiddenTestFile + " " + userName;
        JSONReturnable json = runPython(command);
        //executeBashScript("cleanDirectory.sh src/main/temp");
        return json;
    }

    public JSONReturnable getClassProgress(@NonNull String projectID) {
        JSONReturnable json = null;
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifier(projectID);
        String visibleTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_visibleTests.txt";
        String hiddenTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_hiddenTests.txt";
        try {
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
        } catch (IOException e) {
            json = new JSONReturnable(-1, null);
        }

        if (DEBUG) {
            visibleTestFile = pythonPath + "/test_datasets/sampleTestCases.txt";
            hiddenTestFile = pythonPath + "/test_datasets/sampleTestCases.txt";
        }

        // TODO: Check that test results work as expected
        String pyPath = pythonPath + "get_class_progress.py";
        String command = "python3 " + pyPath + " " + visibleTestFile + " " + hiddenTestFile;
        json = runPython(command);
        //executeBashScript("cleanDirectory.sh src/main/temp");
        return json;
    }

    public JSONReturnable getTestSummary(@NonNull String projectID) {
        JSONReturnable json = null;
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifier(projectID);
        String visibleTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_visibleTests.txt";
        String hiddenTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_hiddenTests.txt";
        try {
            BufferedWriter visibleWriter = new BufferedWriter(new FileWriter(visibleTestFile));
            BufferedWriter hiddenWriter = new BufferedWriter(new FileWriter(hiddenTestFile));
            for (StudentProject p : projects) {
                Student student = studentRepository.findByUserID(p.getStudentID());
                StringBuilder builder = new StringBuilder();
                builder.append(student.getUserName());
                List<StudentProjectTest> testResults = studentProjectTestRepository.findByIdProjectIdentifierAndIdStudentIDAndIsHidden(p.getProjectIdentifier(), p.getStudentID(), false);
                for(StudentProjectTest t : testResults) {
                    builder.append(";").append(t.getTestResultString());
                }
                String visibleTestResult = builder.toString();
                builder = new StringBuilder();
                builder.append(student.getUserName());
                testResults = studentProjectTestRepository.findByIdProjectIdentifierAndIdStudentIDAndIsHidden(p.getProjectIdentifier(), p.getStudentID(), true);
                for(StudentProjectTest t : testResults) {
                    builder.append(";").append(t.getTestResultString());
                }
                String hiddenTestResult = builder.toString();
                visibleWriter.write(visibleTestResult + "\n");
                hiddenWriter.write(hiddenTestResult + "\n");
            }
            visibleWriter.close();
            hiddenWriter.close();
        }
        catch(IOException e) {
            json = new JSONReturnable(-1, null);
        }

        if (DEBUG) {
            visibleTestFile = pythonPath + "/test_datasets/sampleTestCases.txt";
            hiddenTestFile = pythonPath + "/test_datasets/sampleTestCases.txt";
        }

        // TODO: Check that test results work as expected
        String pyPath = pythonPath + "get_test_summary.py";
        String command = "python3 " + pyPath + " " + visibleTestFile + " " + hiddenTestFile;
        json = runPython(command);
        //executeBashScript("cleanDirectory.sh src/main/temp");
        return json;
    }

    public JSONReturnable getAdditionsAndDeletions(@NonNull String projectID, @NonNull String userName) {
        String dailyCountsFile = countStudentCommitsByDay(projectID, userName);
        String commitLogFile = listStudentCommitsByTime(projectID, userName);
        if(dailyCountsFile == null) {
            return new JSONReturnable(-1, null);
        }
        if(commitLogFile == null) {
            return new JSONReturnable(-2, null);
        }
        String pyPath = pythonPath + "get_add_del.py";
        String command = "python3 " + pyPath + " " + commitLogFile + " " + dailyCountsFile + " " + userName + " -l 1000";
        JSONReturnable json = runPython(command);
        //executeBashScript("cleanDirectory.sh src/main/temp");
        return json;
    }

    public JSONReturnable getStatistics(@NonNull String projectID, @NonNull String userName) {
        String dailyCountsFile = countStudentCommitsByDay(projectID, userName);
        String commitLogFile = listStudentCommitsByTime(projectID, userName);
        if(dailyCountsFile == null) {
            return new JSONReturnable(-1, null);
        }
        if(commitLogFile == null) {
            return new JSONReturnable(-2, null);
        }
        String testResult = null;
        if (DEBUG) {
            testResult = "cutz;Test1:P:1.0;Test2:P:0.5;Test3:P:3.0;Test4:P:1.0;Test5:P:2.0";
        } else {
            Student student = studentRepository.findByUserName(userName);
            StudentProject project = studentProjectRepository.findByIdProjectIdentifierAndIdStudentID(projectID, student.getUserID());
            StringBuilder builder = new StringBuilder();
            builder.append(student.getUserName());
            List<StudentProjectTest> testResults = studentProjectTestRepository.findByIdProjectIdentifierAndIdStudentIDAndIsHidden(project.getProjectIdentifier(), project.getStudentID(), false);
            for(StudentProjectTest t : testResults) {
                builder.append(";").append(t.getTestResultString());
            }
            testResults = studentProjectTestRepository.findByIdProjectIdentifierAndIdStudentIDAndIsHidden(project.getProjectIdentifier(), project.getStudentID(), true);
            for(StudentProjectTest t : testResults) {
                builder.append(";").append(t.getTestResultString());
            }
            testResult = builder.toString();
        }
        String pyPath = pythonPath + "get_statistics.py";
        String command = "python3 " + pyPath + " " + commitLogFile + " " + dailyCountsFile + " " + userName + " " + testResult + " -t 1.0 -l 1000";
        JSONReturnable json = runPython(command);
        if(json == null || json.getJsonObject() == null) {
            return json;
        }
        if (DEBUG) {
            executeBashScript("cleanDirectory.sh src/main/temp");
            return json;
        }
        Student student = studentRepository.findByUserName(userName);
        StudentProject project = studentProjectRepository.findByIdProjectIdentifierAndIdStudentID(projectID, student.getUserID());
        JSONArray array = (JSONArray)json.getJsonObject().get("data");
        for(int i = 0; i < array.size(); i++) {
            JSONObject data = (JSONObject)array.get(i);
            if (data.get("stat_name").equals("End Date")) {
                project.setMostRecentCommitDate(data.get("stat_value").toString());
            }
            else if (data.get("stat_name").equals("Additions")) {
                try {
                    project.setTotalLinesAdded(Integer.parseInt(data.get("stat_value").toString().split(" ")[0]));
                }
                catch(NumberFormatException e) {
                    project.setTotalLinesAdded(0);
                }
            }
            else if (data.get("stat_name").equals("Deletions")) {
                try {
                    project.setTotalLinesRemoved(Integer.parseInt(data.get("stat_value").toString().split(" ")[0]));
                }
                catch(NumberFormatException e) {
                    project.setTotalLinesRemoved(0);
                }
            } else if (data.get("stat_name").equals("Commit Count")) {
                try {
                    project.setCommitCount(Integer.parseInt(data.get("stat_value").toString().split(" ")[0]));
                }
                catch(NumberFormatException e) {
                    project.setCommitCount(0);
                }
            }
            else if (data.get("stat_name").equals("Estimated Time Spent")) {
                try {
                    project.setTotalTimeSpent(Double.parseDouble(data.get("stat_value").toString().split(" ")[0]));
                } catch (NumberFormatException e) {
                    project.setTotalTimeSpent(0.0);
                }
            }
        }
        studentProjectRepository.save(project);
        //executeBashScript("cleanDirectory.sh src/main/temp");
        return json;
    }

    public JSONReturnable getCommitCounts(@NonNull String projectID, @NonNull String userName) {
        String commitLogFile = listStudentCommitsByTime(projectID, userName);
        if(commitLogFile == null) {
            return new JSONReturnable(-2, null);
        }
        String pyPath = pythonPath + "get_git_commits.py";
        String command = "python3 " + pyPath + " " + commitLogFile + " " + userName;
        JSONReturnable json = runPython(command);

        //executeBashScript("cleanDirectory.sh src/main/temp");
        return json;
    }

    public JSONReturnable getCommitList(@NonNull String projectID, @NonNull String userName) {
        String commitLogFile = listStudentCommitsByTime(projectID, userName);
        if(commitLogFile == null) {
            return new JSONReturnable(-1, null);
        }
        String pyPath = pythonPath + "get_git_commit_list.py";
        String command = "python3 " + pyPath + " " + commitLogFile + " " + userName;
        JSONReturnable json = runPython(command);
        //executeBashScript("cleanDirectory.sh src/main/temp");
        return json;
    }

    /** Counts the number of commits that every student in the class has made for a project **/
    public String countAllCommits(@NonNull String projectID) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return null;
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return null;
        }
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifier(projectID);
        String fileName = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_counts.txt";
        for(StudentProject s : projects) {
            Student student = studentRepository.findByUserID(s.getStudentID());
            String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
            executeBashScript("countCommits.sh " + destPath + " " + fileName + " " + student.getUserName());
        }
        return fileName;
    }

    /** Counts the total number of commits made each day that the project was active **/
    public String countAllCommitsByDay(@NonNull String projectID) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return null;
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return null;
        }
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifier(projectID);
        String fileName = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_countsByDay.txt";
        for(StudentProject s : projects) {
            Student student = studentRepository.findByUserID(s.getStudentID());
            String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
            executeBashScript("countCommitsByDay.sh " + destPath + " " + fileName + " " + student.getUserName());
        }
        return fileName;
    }

    /** Counts the number of commits that a single student has made for each day that the project is active **/
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

    /** Lists various information about git history, including commit time and dates, and files modified in each commit for all students **/
    public String listAllCommitsByTime(@NonNull String projectID) {
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
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifier(projectID);
        String fileName = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_commitInfo.txt";
        for(StudentProject s : projects) {
            Student student = studentRepository.findByUserID(s.getStudentID());
            String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
            executeBashScript("listCommitsByTime.sh " + destPath + " " + fileName + " " + student.getUserName());
        }
        return fileName;
    }

    /** Lists various information about git history, including commit time and dates, and files modified in each commit for one student **/
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

    /** Uploads a testing script to testcases directory in the course hub **/
    public ProjectTestScript uploadTestScript(@NonNull String projectID, @NonNull String testName, @NonNull String testContents, boolean isHidden, double points) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
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
        executeBashScript("setPermissions.sh " + sections.get(0).getCourseID());
        return projectTestScriptRepository.save(script);
    }

    /** Adds a testing script to the database. Mainly for testing purposes **/
    public ProjectTestScript addTestScript(@NonNull String projectID, @NonNull String testName, boolean isHidden, double points) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
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

    /** Modify an uploaded testing script to either change its contents, point value, or if it is hidden **/
    public int modifyTestScript(@NonNull String projectID, @NonNull String testName, @NonNull String field, @NonNull String value) {
        ProjectTestScript script = projectTestScriptRepository.findByIdProjectIdentifierAndIdTestScriptName(projectID, testName);
        if(script == null) {
            return -1;
        }
        Project project = projectRepository.findByProjectIdentifier(projectID);
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
            if(executeBashScript("setPermissions.sh " + sections.get(0).getCourseID()) == -1) {
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
            if(executeBashScript("setPermissions.sh " + sections.get(0).getCourseID()) == -1) {
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

    /** Runs a generic testall script, which simply checks if nothing is output, which usually means test was passed,
        and assigns a pass or fail to each test case based on if there was no output from test script **/
    public int runTestall(@NonNull String projectID) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return -1;
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return -2;
        }
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifier(projectID);
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
        String date = LocalDate.now().toString();
        int code = 0;
        for(StudentProject p : projects) {
            Student student = studentRepository.findByUserID(p.getStudentID());
            StudentProjectDate projectDate = studentProjectDateRepository.findByIdDateAndIdProjectIdentifierAndIdStudentID(date, projectID, student.getUserID());
            String testingDirectory = sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName();
            try {
                if(executeBashScript("runMakefile.sh " + testingDirectory + " " + makefilePath) == -1) {
                    code = -5;
                }
                TestExecuter tester = new TestExecuter(testingDirectory + "/" + testDir, testCaseDirectory, hiddenTestCaseDirectory);
                Thread thread = new Thread(tester);
                thread.start();
                Thread.sleep(5000);
                thread.interrupt();
                String visibleResult = tester.getVisibleResult();
                String hiddenResult = tester.getHiddenResult();
                if(visibleResult == null) {
                    visibleResult = "";
                }
                if(hiddenResult == null) {
                    hiddenResult = "";
                }
                double visibleGrade = parseProgressForProject(projectID, visibleResult);
                double hiddenGrade = parseProgressForProject(projectID, hiddenResult);
                if(projectDate == null) {
                    StudentProjectDate d = new StudentProjectDate(p.getStudentID(), p.getProjectIdentifier(), date, visibleGrade, hiddenGrade);
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
                    p.setBestVisibleGrade(visibleGrade);
                    p = studentProjectRepository.save(p);
                    updateTestResults(visibleResult, p.getStudentID(), p.getProjectIdentifier(), false);
                }
                if(hiddenGrade > p.getBestHiddenGrade()) {
                    p.setBestHiddenGrade(hiddenGrade);
                    studentProjectRepository.save(p);
                    updateTestResults(hiddenResult, p.getStudentID(), p.getProjectIdentifier(), true);
                }
            }
            catch(Exception e) {
                code = -6;
            }
        }
        return code;
    }

    /** Runs testall for a single student, which is quicker if the professor or TA wants to manually run testall for a student **/
    /** TODO: @reed Fix this once runTestall is finalized **/
    public int runTestallForStudent(@NonNull String projectID, @NonNull String userName) {
        /*
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return -1;
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return -2;
        }
        Student student = studentRepository.findByUserName(userName);
        if(student == null) {
            return -3;
        }
        String date = LocalDate.now().toString();
        List<StudentProject> projects = studentProjectRepository.findByIdStudentID(student.getUserID());
        boolean hasProject = false;
        for(StudentProject p : projects) {
            if(p.getProjectIdentifier().equals(projectID)) {
                List<StudentProjectDate> projectDates = studentProjectDateRepository.findByIdDateAndIdStudentID(date, p.getStudentID());
                String testingDirectory = sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName() + "/" + testDir;
                String testCaseDirectory = sections.get(0).getCourseHub() + "/testcases/" + project.getRepoName();
                String hiddenTestCaseDirectory = sections.get(0).getCourseHub() + "/hidden_testcases/" + project.getRepoName();
                try {
                    Process process = Runtime.getRuntime().exec("./src/main/bash/testall.sh " + testingDirectory + " " + testCaseDirectory + " 2> /dev/null");
                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String visibleResult = stdInput.readLine();
                    process = Runtime.getRuntime().exec("./src/main/bash/testall.sh " + testingDirectory + "/" + testDir + " " + hiddenTestCaseDirectory + " 2> /dev/null");
                    stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String hiddenResult = stdInput.readLine();
                    double visibleGrade = parseProgressForProject(projectID, visibleResult);
                    double hiddenGrade = parseProgressForProject(projectID, hiddenResult);
                    boolean exists = false;
                    for(StudentProjectDate d : projectDates) {
                        if(d.getProjectIdentifier().equals(p.getProjectIdentifier())) {
                            if(visibleGrade > parseProgressForProject(projectID, d.getDateVisibleGrade())) {
                                d.setDateVisibleGrade(visibleResult);
                                d = studentProjectDateRepository.save(d);
                            }
                            if(hiddenGrade > parseProgressForProject(projectID, d.getDateHiddenGrade())) {
                                d.setDateHiddenGrade(hiddenResult);
                                studentProjectDateRepository.save(d);
                            }
                            exists = true;
                            break;
                        }
                    }
                    if(!exists) {
                        StudentProjectDate projectDate = new StudentProjectDate(p.getStudentID(), p.getProjectIdentifier(), date, visibleResult, hiddenResult);
                        studentProjectDateRepository.save(projectDate);
                    }
                    if(visibleGrade > parseProgressForProject(projectID, p.getBestVisibleGrade())) {
                        p.setBestVisibleGrade(visibleResult);
                        p = studentProjectRepository.save(p);
                    }
                    if(hiddenGrade > parseProgressForProject(projectID, p.getBestHiddenGrade())) {
                        p.setBestHiddenGrade(hiddenResult);
                        studentProjectRepository.save(p);
                    }
                    hasProject = true;
                    break;
                }
                catch(Exception e) {
                    return -4;
                }
            }
        }
        if(!hasProject) {
            return -5;
        }
        return 0;
        */
        return 0;
    }

    /** Pulls and tests every project in the database on one hour intervals **/
    @Scheduled(fixedDelay = RATE)
    public int pullAndTestAllProjects() {
        int code = 0;
        for(Project project : projectRepository.findAll()) {
            if(project.getTestRate() > 0 && project.getTestCount() <= 0) {
                if (pullProjects(project.getProjectIdentifier()) < 0) {
                    code -= 1;
                }
                if (runTestall(project.getProjectIdentifier()) < 0) {
                    code -= 1;
                }
                project.setTestCount(project.getTestRate() - 1);
            }
            else {
                project.setTestCount(project.getTestCount() - 1);
            }
        }
        return code;
    }

    public int testPythonDirectory() {

        // This hardcoded path will undoubtedly cause us difficulty in the future.
        String filePath = pythonPath + "hello.py";
        String dataFilePath = pythonPath + "testData.txt";
        //BufferedWriter stdOutput = new BufferedWriter(new OutputStreamWriter());

        try {
            // Run `python hello.py testData.txt` at correct directory
            Process process = Runtime.getRuntime().exec("python3 " + filePath + " " + dataFilePath);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String input = null;
            String error = null;
            while ((error = stdError.readLine()) != null) {
                System.out.println(error);
            }
            while ((input = stdInput.readLine()) != null) {
                //System.out.println(input);
                if (input.equals("Hello World")) {
                    return 1;
                }
            }
            return -1;
        } catch (IOException e) {
            e.printStackTrace();
            return -2;
        }
    }

    /** Makes a new assignment between teaching assistant and student. Can have multiple students assigned to same TA
     or multiple TAs assigned to the same student **/
    public int assignTeachingAssistantToStudent(@NonNull String teachAssistUserName, @NonNull String studentUserName) {
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(teachAssistUserName);
        if(teachingAssistant == null) {
            return -1;
        }
        Student student = studentRepository.findByUserName(studentUserName);
        if(student == null) {
            return -2;
        }
        TeachingAssistantStudent assignment = new TeachingAssistantStudent(teachingAssistant.getUserID(), student.getUserID());
        if(teachingAssistantStudentRepository.save(assignment) == null) {
            return -3;
        }
        return 0;
    }

    public JSONReturnable runPython(@NonNull String command) {
        if (OBFUSCATE) {
            command += " -O";
        }
        /*File file= new File (tailFilePath, "tail.txt");
        FileWriter tailWriter;
        try {
            if (file.exists())
            {
                tailWriter = new FileWriter(file,true);
            }
            else
            {
                file.createNewFile();
                tailWriter = new FileWriter(file);
            }

            String arr[] = command.split(" ", 3);
            tailWriter.write(arr[0] + arr[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        System.out.println(command);
        JSONReturnable json = null;
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String input = null;
            String error = null;
            while ((error = stdError.readLine()) != null) {
                System.out.println(error);
            }
            while ((input = stdInput.readLine()) != null) {
                JSONParser jsonParser = new JSONParser();
                Object obj = null;
                try {
                    obj = jsonParser.parse(input);
                } catch (ParseException e) {
                    e.printStackTrace();
                    json =  new JSONReturnable(-3, null);
                }
                if (obj != null) {
                    System.out.println(obj);
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
            json =  new JSONReturnable(-2, null);
        }
        return json;
    }
}
