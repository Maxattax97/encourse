package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.domain.relations.*;
import edu.purdue.cs.encourse.service.ProfessorService;
import edu.purdue.cs.encourse.util.ConfigurationManager;
import edu.purdue.cs.encourse.util.JSONReturnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final static Boolean DEBUG = ConfigurationManager.getInstance().debug;
    private final static String pythonCommand = DEBUG ? "/anaconda3/bin/python" : "python3";
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

    @Autowired
    private TeachingAssistantCourseRepository teachingAssistantCourseRepository;

    @Autowired
    private TeachingAssistantSectionRepository teachingAssistantSectionRepository;

    private int executeBashScript(@NonNull String command) {
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

    public JSONReturnable runPython(@NonNull String command) {
        System.out.println(command);
        JSONReturnable json = null;
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String output = null;
            String error = null;
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
            json =  new JSONReturnable(-2, null);
        }
        System.out.println("JSON: " + json.getJsonObject().toJSONString());
        return json;
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

    private void createTestFiles(String visibleTestFile, String hiddenTestFile, List<StudentProject> projects) throws IOException {
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
        project.setSyncDate(LocalDate.now().toString());
        projectRepository.save(project);
        return code;
    }

    public int updateStudentInformation(@NonNull String projectID, @NonNull String userName) {
        String dailyCountsFile = countStudentCommitsByDay(projectID, userName);
        String commitLogFile = listStudentCommitsByTime(projectID, userName);
        if(dailyCountsFile == null) {
            return -1;
        }
        if(commitLogFile == null) {
            return -2;
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
        String command = pythonCommand + " " + pyPath + " " + commitLogFile + " " + dailyCountsFile + " " + userName + " " + testResult + " -t 1.0 -l 200";
        JSONReturnable json = runPython(command);
        if(json == null || json.getJsonObject() == null) {
            return 0;
        }
        if (DEBUG) {
            executeBashScript("cleanDirectory.sh src/main/temp");
            return 0;
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
        return 0;
    }

    public JSONReturnable getStudentProgress(@NonNull String projectID, @NonNull String userName) {
        String visibleTestFile;
        String hiddenTestFile;
        String dailyCountsFile;
        if (DEBUG) {
            visibleTestFile = "src/main/python/test_datasets/sampleTestsDay.txt";
            hiddenTestFile = "src/main/python/test_datasets/sampleTestsDay.txt";
            dailyCountsFile = "src/main/python/test_datasets/sampleCountsDay.txt";
        } else {
            dailyCountsFile = countStudentCommitsByDay(projectID, userName);
            if (!projectRepository.existsByProjectIdentifier(projectID)) {
                return new JSONReturnable(-1, null);
            }
            Student student = studentRepository.findByUserName(userName);
            if (student == null) {
                return new JSONReturnable(-2, null);
            }
            List<StudentProjectDate> projectDates = studentProjectDateRepository.findByIdProjectIdentifierAndIdStudentID(projectID, student.getUserID());
            visibleTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_visibleTestDates.txt";
            hiddenTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_hiddenTestDates.txt";
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
        }

        String pyPath = pythonPath + "get_individual_progress.py";
        String command = pythonCommand + " " + pyPath + " " + visibleTestFile + " " + hiddenTestFile + " " + dailyCountsFile + " " + userName;
        JSONReturnable json = runPython(command);
        //executeBashScript("cleanDirectory.sh src/main/temp");
        return json;
    }

    public JSONReturnable getCommitVelocity(@NonNull String projectID, @NonNull String userName) {
        String dailyCountsFile = countStudentCommitsByDay(projectID, userName);
        String commitLogFile = listStudentCommitsByTime(projectID, userName);
        if(dailyCountsFile == null) {
            return new JSONReturnable(-1, null);
        }
        if(commitLogFile == null) {
            return new JSONReturnable(-2, null);
        }
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
        if (DEBUG) {
            visibleTestFile = "src/main/python/test_datasets/sampleTestsDay.txt";
            hiddenTestFile = "src/main/python/test_datasets/sampleTestsDay.txt";
        }
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

        String pyPath = pythonPath + "get_velocity.py";
        String command = pythonCommand + " " + pyPath + " " + visibleTestFile + " " + hiddenTestFile + " " + commitLogFile + " " + userName;
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
            createTestFiles(visibleTestFile, hiddenTestFile, projects);
        } catch (IOException e) {
            json = new JSONReturnable(-1, null);
        }

        if (DEBUG) {
            visibleTestFile = pythonPath + "/test_datasets/sampleVisibleTestCases.txt";
            hiddenTestFile = pythonPath + "/test_datasets/sampleHiddenTestCases.txt";
        }

        // TODO: Check that test results work as expected
        String pyPath = pythonPath + "get_class_progress.py";
        String command = pythonCommand + " " + pyPath + " " + visibleTestFile + " " + hiddenTestFile;
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
            createTestFiles(visibleTestFile, hiddenTestFile, projects);
        }
        catch(IOException e) {
            json = new JSONReturnable(-1, null);
        }

        if (DEBUG) {
            visibleTestFile = pythonPath + "/test_datasets/sampleVisibleTestCases.txt";
            hiddenTestFile = pythonPath + "/test_datasets/sampleHiddenTestCases.txt";
        }

        // TODO: Check that test results work as expected
        String pyPath = pythonPath + "get_test_summary.py";
        String command = pythonCommand + " " + pyPath + " " + visibleTestFile + " " + hiddenTestFile;
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
        String command = pythonCommand + " " + pyPath + " " + commitLogFile + " " + dailyCountsFile + " " + userName + " -l 200";
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
        String command = pythonCommand + " " + pyPath + " " + commitLogFile + " " + dailyCountsFile + " " + userName + " " + testResult + " -t 1.0 -l 200";
        JSONReturnable json = runPython(command);
        //executeBashScript("cleanDirectory.sh src/main/temp");
        return json;
    }

    public JSONReturnable getClassCheating(@NonNull String projectID) {
        JSONReturnable json = null;

        String commitLogFile = listAllCommitsByTime(projectID);
        if(commitLogFile == null) {
            json = new JSONReturnable(-1, null);
        }
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifier(projectID);
        String visibleTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_visibleTests.txt";
        String hiddenTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_hiddenTests.txt";
        try {
            createTestFiles(visibleTestFile, hiddenTestFile, projects);
        } catch (IOException e) {
            json = new JSONReturnable(-2, null);
        }

        if (DEBUG){
            commitLogFile = pythonPath + "/test_datasets/sampleCommitList.txt";
            visibleTestFile = pythonPath + "/test_datasets/sampleTestsDay.txt";
            hiddenTestFile = pythonPath + "/test_datasets/sampleTestsDay.txt";
        }
        else if (json != null) {
            return json;
        }

        String pyPath = pythonPath + "get_class_cheating.py";
        String command = pythonCommand + " " + pyPath + " " + visibleTestFile + " " + hiddenTestFile + " " + commitLogFile + " -l 1000";
        json = runPython(command);
        //executeBashScript("cleanDirectory.sh src/main/temp");
        return json;
    }


    public JSONReturnable getCommitCounts(@NonNull String projectID, @NonNull String userName) {
        String commitLogFile = listStudentCommitsByTime(projectID, userName);
        if(commitLogFile == null) {
            return new JSONReturnable(-2, null);
        }
        String pyPath = pythonPath + "get_git_commits.py";
        String command = pythonCommand + " " + pyPath + " " + commitLogFile + " " + userName;
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
        String command = pythonCommand + " " + pyPath + " " + commitLogFile + " " + userName;
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
                TestExecuter tester = new TestExecuter(project.getCourseID(), testingDirectory + "/" + testDir, testCaseDirectory, hiddenTestCaseDirectory);
                Thread thread = new Thread(tester);
                thread.start();
                Thread.sleep(5000);
                thread.interrupt();
                executeBashScript("killProcesses.sh " + project.getCourseID());
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
                    p = studentProjectRepository.save(p);
                    updateTestResults(hiddenResult, p.getStudentID(), p.getProjectIdentifier(), true);
                }
            }
            catch(Exception e) {
                code = -6;
            }
        }
        project.setTestDate(LocalDate.now().toString());
        projectRepository.save(project);
        return code;
    }

    /** Runs testall for a single student, which is quicker if the professor or TA wants to manually run testall for a student **/
    public int runTestallForStudent(@NonNull String projectID, @NonNull String userName) {
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
        String testCaseDirectory = sections.get(0).getCourseHub() + "/testcases/" + project.getRepoName();
        String hiddenTestCaseDirectory = sections.get(0).getCourseHub() + "/hidden_testcases/" + project.getRepoName();
        String makefilePath = sections.get(0).getCourseHub() + "/makefiles/" + project.getRepoName() + "/Makefile";
        File directory = new File(testCaseDirectory);
        if(!directory.isDirectory() || directory.listFiles().length == 0) {
            return -4;
        }
        File file = new File(makefilePath);
        if(!file.exists()) {
            return -5;
        }
        String date = LocalDate.now().toString();
        StudentProject studentProject = studentProjectRepository.findByIdProjectIdentifierAndIdStudentID(projectID, student.getUserID());
        if(studentProject == null) {
            return -6;
        }
        StudentProjectDate projectDate = studentProjectDateRepository.findByIdDateAndIdProjectIdentifierAndIdStudentID(date, projectID, student.getUserID());
        String testingDirectory = sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName();
        try {
            if(executeBashScript("runMakefile.sh " + testingDirectory + " " + makefilePath) == -1) {
                return -7;
            }
            TestExecuter tester = new TestExecuter(project.getCourseID(), testingDirectory + "/" + testDir, testCaseDirectory, hiddenTestCaseDirectory);
            Thread thread = new Thread(tester);
            thread.start();
            Thread.sleep(5000);
            thread.interrupt();
            executeBashScript("killProcesses.sh " + project.getCourseID());
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
                StudentProjectDate d = new StudentProjectDate(studentProject.getStudentID(), studentProject.getProjectIdentifier(), date, visibleGrade, hiddenGrade);
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
            if(visibleGrade > studentProject.getBestVisibleGrade()) {
                studentProject.setBestVisibleGrade(visibleGrade);
                studentProject = studentProjectRepository.save(studentProject);
                updateTestResults(visibleResult, studentProject.getStudentID(), studentProject.getProjectIdentifier(), false);
            }
            if(hiddenGrade > studentProject.getBestHiddenGrade()) {
                studentProject.setBestHiddenGrade(hiddenGrade);
                studentProject = studentProjectRepository.save(studentProject);
                updateTestResults(hiddenResult, studentProject.getStudentID(), studentProject.getProjectIdentifier(), true);
            }
        }
        catch(Exception e) {
            return -8;
        }
        return 0;
    }

    /** Runs a testall script using two commits out of every day that a student has worked **/
    public int runHistoricTestall(@NonNull String projectID) {
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
        String fileName = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_gitHashes.txt";
        for(StudentProject p : projects) {
            Student student = studentRepository.findByUserID(p.getStudentID());
            StudentProjectDate projectDate = studentProjectDateRepository.findByIdDateAndIdProjectIdentifierAndIdStudentID(date, projectID, student.getUserID());
            String testingDirectory = sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName();
            executeBashScript("listTestUpdateHistory.sh " + testingDirectory + " " + fileName);
            try {
                BufferedReader reader = new BufferedReader(new FileReader(fileName));
                String line;
                String commitDate = "";
                executeBashScript("checkoutPreviousCommit.sh " + testingDirectory + " origin");
                while((line = reader.readLine()) != null && !line.equals("")) {
                    String[] commitInfo = line.split(" ");
					System.out.println("Student: " + student.getUserName() + " Date: " + commitInfo[2]);
                    if(commitInfo[2].equals(commitDate)) {
                        continue;
                    }
                    commitDate = commitInfo[2];
                    date = commitInfo[2];
                    executeBashScript("checkoutPreviousCommit.sh " + testingDirectory + " " + commitInfo[1]);
                    if(executeBashScript("runMakefile.sh " + testingDirectory + " " + makefilePath) == -1) {
                        code = -5;
                    }
                    TestExecuter tester = new TestExecuter(project.getCourseID(), testingDirectory + "/" + testDir, testCaseDirectory, hiddenTestCaseDirectory);
                    Thread thread = new Thread(tester);
                    thread.start();
                    Thread.sleep(5000);
                    thread.interrupt();
                    executeBashScript("killProcesses.sh " + project.getCourseID());
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
                        p = studentProjectRepository.save(p);
                        updateTestResults(hiddenResult, p.getStudentID(), p.getProjectIdentifier(), true);
                    }
                    line = reader.readLine();
                    commitInfo = line.split(" ");
                    date = commitInfo[2];
                    executeBashScript("checkoutPreviousCommit.sh " + testingDirectory + " " + commitInfo[1]);
                    if(executeBashScript("runMakefile.sh " + testingDirectory + " " + makefilePath) == -1) {
                        code = -5;
                    }
                    tester = new TestExecuter(project.getCourseID(), testingDirectory + "/" + testDir, testCaseDirectory, hiddenTestCaseDirectory);
                    thread = new Thread(tester);
                    thread.start();
                    Thread.sleep(5000);
                    thread.interrupt();
                    executeBashScript("killProcesses.sh " + project.getCourseID());
                    visibleResult = tester.getVisibleResult();
                    hiddenResult = tester.getHiddenResult();
                    if(visibleResult == null) {
                        visibleResult = "";
                    }
                    if(hiddenResult == null) {
                        hiddenResult = "";
                    }
                    visibleGrade = parseProgressForProject(projectID, visibleResult);
                    hiddenGrade = parseProgressForProject(projectID, hiddenResult);
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
                        p = studentProjectRepository.save(p);
                        updateTestResults(hiddenResult, p.getStudentID(), p.getProjectIdentifier(), true);
                    }
                }
                executeBashScript("checkoutPreviousCommit.sh " + testingDirectory + " origin");
                reader.close();
            }
            catch(Exception e) {
                code = -6;
            }
        }
        project.setTestDate(LocalDate.now().toString());
        projectRepository.save(project);
        return code;
    }

    /** Pulls and tests every project in the database on one hour intervals **/
    public void pullAndTestAllProjects() {
        for(Project project : projectRepository.findAll()) {
            if(project.getTestRate() > 0 && project.getTestCount() <= 0) {
                System.out.println("Pulling project " + project.getProjectName());
                pullProjects(project.getProjectIdentifier());
                System.out.println("Testing project " + project.getProjectName());
                runTestall(project.getProjectIdentifier());
                List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifier(project.getProjectIdentifier());
                for(StudentProject p : projects) {
                    Student student = studentRepository.findByUserID(p.getStudentID());
                    updateStudentInformation(p.getProjectIdentifier(), student.getUserName());
                }
                project.setTestCount(project.getTestRate() - 1);
            }
            else {
                project.setTestCount(project.getTestCount() - 1);
            }
        }
        executeBashScript("cleanDirectory.sh src/main/temp");
    }

    public int testPythonDirectory() {

        // This hardcoded path will undoubtedly cause us difficulty in the future.
        String filePath = pythonPath + "hello.py";
        String dataFilePath = pythonPath + "testData.txt";
        //BufferedWriter stdOutput = new BufferedWriter(new OutputStreamWriter());

        try {
            // Run `python hello.py testData.txt` at correct directory
            Process process = Runtime.getRuntime().exec(pythonCommand + " " + filePath + " " + dataFilePath);
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

    /** Assigns a TA to a section in the course. Does not include assignments to students within section **/
    public int assignTeachingAssistantToSection(@NonNull String teachAssistUserName, @NonNull String sectionID) {
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(teachAssistUserName);
        if(teachingAssistant == null) {
            return -1;
        }
        Section section = sectionRepository.findBySectionIdentifier(sectionID);
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

    /** Assigns a TA to a student in a section that they TA for **/
    public int assignTeachingAssistantToStudentInSection(@NonNull String teachAssistUserName, @NonNull String studentUserName, @NonNull String sectionID) {
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(teachAssistUserName);
        if(teachingAssistant == null) {
            return -1;
        }
        Student student = studentRepository.findByUserName(studentUserName);
        if(student == null) {
            return -2;
        }
        TeachingAssistantSection check = teachingAssistantSectionRepository.findByIdTeachingAssistantIDAndIdSectionID(teachingAssistant.getUserID(), sectionID);
        if(check == null) {
            return -3;
        }
        Section section = sectionRepository.findBySectionIdentifier(sectionID);
        if(section == null) {
            return -4;
        }
        TeachingAssistantStudent assignment = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdStudentIDAndIdCourseID(teachingAssistant.getUserID(), student.getUserID(), section.getCourseID());
        if(assignment != null) {
            return -5;
        }
        assignment = new TeachingAssistantStudent(teachingAssistant.getUserID(), student.getUserID(), section.getCourseID());
        teachingAssistantStudentRepository.save(assignment);
        return 0;
    }

    /** Assigns a TA to all students in a course with one command. **/
    public int assignTeachingAssistantToAllStudentsInSection(@NonNull String teachAssistUserName, @NonNull String sectionID) {
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(teachAssistUserName);
        if(teachingAssistant == null) {
            return -1;
        }
        TeachingAssistantSection check = teachingAssistantSectionRepository.findByIdTeachingAssistantIDAndIdSectionID(teachingAssistant.getUserID(), sectionID);
        if(check == null) {
            return -2;
        }
        Section section = sectionRepository.findBySectionIdentifier(sectionID);
        if(section == null) {
            return -3;
        }
        List<StudentSection> studentSections = studentSectionRepository.findByIdSectionIdentifier(sectionID);
        for(StudentSection s : studentSections) {
            TeachingAssistantStudent assignment = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdStudentIDAndIdCourseID(teachingAssistant.getUserID(), s.getStudentID(), section.getCourseID());
            if (assignment == null) {
                assignment = new TeachingAssistantStudent(teachingAssistant.getUserID(), s.getStudentID(), section.getCourseID());
                teachingAssistantStudentRepository.save(assignment);
            }
        }
        return 0;
    }

    /** Gets all courses that a professor supervises **/
    public JSONArray getCourseData(@NonNull String userName) {
        Professor professor = professorRepository.findByUserName(userName);
        if(professor == null) {
            return null;
        }
        List<ProfessorCourse> courses = professorCourseRepository.findByIdProfessorID(professor.getUserID());
        if(courses.isEmpty()) {
            return null;
        }
        JSONArray coursesJSON = new JSONArray();
        for(ProfessorCourse c : courses) {
            JSONObject courseJSON = new JSONObject();
            List<Section> sections = sectionRepository.findByCourseID(c.getCourseID());
            List<String> sectionIDs = new ArrayList<>();
            for(Section s : sections) {
                sectionIDs.add(s.getSectionIdentifier());
            }
            courseJSON.put("course_number", c.getCourseID());
            courseJSON.put("course_name", sections.get(0).getCourseID());
            courseJSON.put("semester", c.getSemester());
            courseJSON.put("id", professor.getUserName());
            courseJSON.put("sections", sectionIDs);
            coursesJSON.add(courseJSON);
        }
        return coursesJSON;
    }

    /** Retrieves basic data for all students in course, including name, userName, and simple project info **/
    public JSONArray getStudentData(@NonNull String semester, @NonNull String courseID) {
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(semester, courseID);
        if(sections.isEmpty()) {
            return null;
        }
        List<String> completedStudents = new ArrayList<>();
        JSONArray studentsJSON = new JSONArray();
        for(Section section : sections) {
            List<StudentSection> studentSections = studentSectionRepository.findByIdSectionIdentifier(section.getSectionIdentifier());
            for(StudentSection studentSection : studentSections) {
                Student student = studentRepository.findByUserID(studentSection.getStudentID());
                if(!(completedStudents.contains(student.getUserID()))) {
                    completedStudents.add(student.getUserID());
                    List<StudentProject> studentProjects = studentProjectRepository.findByIdStudentID(student.getUserID());
                    Map<String, Double> grades = new TreeMap<>();
                    Map<String, Double> hiddenGrades = new TreeMap<>();
                    Map<String, Integer> commitCounts = new TreeMap<>();
                    Map<String, Double> timeSpent = new TreeMap<>();
                    for(StudentProject p : studentProjects) {
                        grades.put(p.getProjectIdentifier(), p.getBestVisibleGrade());
                        hiddenGrades.put(p.getProjectIdentifier(), p.getBestHiddenGrade());
                        commitCounts.put(p.getProjectIdentifier(), p.getCommitCount());
                        timeSpent.put(p.getProjectIdentifier(), p.getTotalTimeSpent());
                    }
                    List<StudentSection> assignedSections = studentSectionRepository.findByIdStudentID(student.getUserID());
                    List<String> sectionStrings = new ArrayList<>();
                    for(StudentSection a : assignedSections) {
                        sectionStrings.add(a.getSectionIdentifier());
                    }
                    List<TeachingAssistantStudent> assignedTeachingAssistants = teachingAssistantStudentRepository.findByIdStudentID(student.getUserID());
                    List<String> teachingAssistants = new ArrayList<>();
                    for(TeachingAssistantStudent a : assignedTeachingAssistants) {
                        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserID(a.getTeachingAssistantID());
                        teachingAssistants.add(teachingAssistant.getUserName());
                    }
                    JSONObject studentJSON = new JSONObject();
                    studentJSON.put("first_name", student.getFirstName());
                    studentJSON.put("last_name", student.getLastName());
                    studentJSON.put("id", student.getUserName());
                    studentJSON.put("sections", sectionStrings);
                    studentJSON.put("teaching_assistants", teachingAssistants);
                    studentJSON.put("grades", grades);
                    studentJSON.put("hiddenGrades", grades);
                    studentJSON.put("commitCounts", commitCounts);
                    studentJSON.put("timeSpent", timeSpent);
                    if (OBFUSCATE) {
                        // RandomStringGenerator generator = new RandomStringGenerator.Builder()
                        //        .withinRange('a', 'z').build();
                        studentJSON.put("first_name", student.getFirstName());
                        studentJSON.put("last_name", student.getLastName());
                        studentJSON.put("id", student.getUserName());
                        studentJSON.put("grades", grades);
                        studentJSON.put("hiddenGrades", grades);
                        studentJSON.put("commitCounts", commitCounts);
                        studentJSON.put("timeSpent", timeSpent);
                    }
                    studentsJSON.add(studentJSON);
                }
            }
        }
        return studentsJSON;
    }

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
}
