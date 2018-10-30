package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.domain.relations.*;
import edu.purdue.cs.encourse.service.ProfessorService;
import edu.purdue.cs.encourse.service.TeachingAssistantService;
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

@Service(value = TeachingAssistantServiceImpl.NAME)
public class TeachingAssistantServiceImpl implements TeachingAssistantService {

    public final static String NAME = "TeachingAssistantService";
    private final static String pythonPath = "src/main/python/";
    private final static String tailFilePath = "src/main/temp/";
    private final static int RATE = 3600000;
    private final static Boolean DEBUG = false;
    private final static Boolean OBFUSCATE = false;

    /**
     * Hardcoded for shell project, since shell project test cases use relative paths instead of absolute
     **/
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
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }

    private JSONReturnable runPython(@NonNull String command) {
        if (OBFUSCATE) {
            command += " -O";
        }
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

    private boolean isTakingCourse(@NonNull Student student, @NonNull Project project) {
        List<StudentSection> studentSections = studentSectionRepository.findByIdStudentID(student.getUserID());
        boolean isTaking = false;
        for (StudentSection s : studentSections) {
            Section section = sectionRepository.findBySectionIdentifier(s.getSectionIdentifier());
            if (section.getCourseID().equals(project.getCourseID()) && section.getSemester().equals(project.getSemester())) {
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
        for (String r : testResults) {
            String testName = r.split(":")[0];
            ProjectTestScript testScript = projectTestScriptRepository.findByIdProjectIdentifierAndIdTestScriptName(projectID, testName);
            if (testScript == null) {
                continue;
            }
            maxPoints += testScript.getPointsWorth();
            if (r.endsWith("P")) {
                earnedPoints += testScript.getPointsWorth();
            }
        }
        if (maxPoints == 0.0) {
            return 0.0;
        }
        return (earnedPoints / maxPoints) * 100;
    }

    private void updateTestResults(String result, String studentID, String projectID, boolean isHidden) {
        String[] testResults = result.split(";");
        for (String s : testResults) {
            String testName = s.split(":")[0];
            String testScore = s.split(":")[1];
            boolean isPassing = testScore.equals("P");
            StudentProjectTest studentProjectTest =
                    studentProjectTestRepository.findByIdProjectIdentifierAndIdTestScriptNameAndIdStudentID(projectID, testName, studentID);
            if (studentProjectTest == null) {
                ProjectTestScript testScript = projectTestScriptRepository.findByIdProjectIdentifierAndIdTestScriptName(projectID, testName);
                studentProjectTest = new StudentProjectTest(studentID, projectID, testName, isPassing, isHidden, testScript.getPointsWorth());
                studentProjectTestRepository.save(studentProjectTest);
            } else {
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

    private void createTestFilesTA(String visibleTestFile, String hiddenTestFile, List<TeachingAssistantStudent> assignments, String projectID) throws IOException {
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

    public JSONReturnable getStudentProgress(@NonNull String projectID, @NonNull String userNameStudent, @NonNull String userNameTA) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return new JSONReturnable(-1, null);
        }
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userNameTA);
        if(teachingAssistant == null) {
            return new JSONReturnable(-2, null);
        }
        Student student = studentRepository.findByUserName(userNameStudent);
        if(student == null) {
            return new JSONReturnable(-3, null);
        }
        TeachingAssistantStudent assignment = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdStudentIDAndIdCourseID(teachingAssistant.getUserID(), student.getUserID(), project.getCourseID());
        if(assignment == null) {
            return new JSONReturnable(-4, null);
        }
        List<StudentProjectDate> projectDates = studentProjectDateRepository.findByIdProjectIdentifierAndIdStudentID(projectID, student.getUserID());
        String visibleTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_visibleTestDates.txt";
        String hiddenTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_hiddenTestDates.txt";
        try {
            BufferedWriter visibleWriter = new BufferedWriter(new FileWriter(visibleTestFile));
            BufferedWriter hiddenWriter = new BufferedWriter(new FileWriter(hiddenTestFile));
            visibleWriter.write("Start " + userNameStudent + "\n");
            hiddenWriter.write("Start " + userNameStudent + "\n");
            for (StudentProjectDate d : projectDates) {
                visibleWriter.write(d.getDate() + " " + d.getDateVisibleGrade() + "\n");
                hiddenWriter.write(d.getDate() + " " + d.getDateHiddenGrade() + "\n");
            }
            visibleWriter.write("End " + userNameStudent + "\n");
            hiddenWriter.write("End " + userNameStudent + "\n");
            visibleWriter.close();
            hiddenWriter.close();
        }
        catch(IOException e) {
            return new JSONReturnable(-5, null);
        }
        // TODO: @Ryan adjust python command if needed
        String pyPath = pythonPath + "get_individual_progress.py";
        String command = "python3 " + pyPath + " " + visibleTestFile + " " + hiddenTestFile + " " + userNameStudent;
        JSONReturnable json = runPython(command);
        //executeBashScript("cleanDirectory.sh src/main/temp");
        return json;
    }

    public JSONReturnable getAdditionsAndDeletions(@NonNull String projectID, @NonNull String userNameStudent, @NonNull String userNameTA) {
        String dailyCountsFile = countStudentCommitsByDay(projectID, userNameStudent, userNameTA);
        String commitLogFile = listStudentCommitsByTime(projectID, userNameStudent, userNameTA);
        if(dailyCountsFile == null) {
            return new JSONReturnable(-1, null);
        }
        if(commitLogFile == null) {
            return new JSONReturnable(-2, null);
        }
        String pyPath = pythonPath + "get_add_del.py";
        String command = "python3 " + pyPath + " " + commitLogFile + " " + dailyCountsFile + " " + userNameStudent + " -l 1000";
        JSONReturnable json = runPython(command);
        //executeBashScript("cleanDirectory.sh src/main/temp");
        return json;
    }

    public JSONReturnable getStatistics(@NonNull String projectID, @NonNull String userNameStudent, @NonNull String userNameTA) {
        String dailyCountsFile = countStudentCommitsByDay(projectID, userNameStudent, userNameTA);
        String commitLogFile = listStudentCommitsByTime(projectID, userNameStudent, userNameTA);
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
            Student student = studentRepository.findByUserName(userNameStudent);
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
        String command = "python3 " + pyPath + " " + commitLogFile + " " + dailyCountsFile + " " + userNameStudent + " " + testResult + " -t 1.0 -l 1000";
        JSONReturnable json = runPython(command);
        if(json == null || json.getJsonObject() == null) {
            return json;
        }
        if (DEBUG) {
            executeBashScript("cleanDirectory.sh src/main/temp");
            return json;
        }
        //executeBashScript("cleanDirectory.sh src/main/temp");
        return json;
    }

    public JSONReturnable getAssignmentsProgress(@NonNull String projectID, @NonNull String userNameTA) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return new JSONReturnable(-1, null);
        }
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userNameTA);
        if(teachingAssistant == null) {
            return new JSONReturnable(-2, null);
        }
        List<TeachingAssistantStudent> assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdCourseID(teachingAssistant.getUserID(), project.getCourseID());
        String visibleTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_visibleTests_TA.txt";
        String hiddenTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_hiddenTests_TA.txt";
        try {
            createTestFilesTA(visibleTestFile, hiddenTestFile, assignments, projectID);
        } catch (IOException e) {
            return new JSONReturnable(-3, null);
        }

        if (DEBUG) {
            visibleTestFile = pythonPath + "/test_datasets/sampleTestCases.txt";
            hiddenTestFile = pythonPath + "/test_datasets/sampleTestCases.txt";
        }

        String pyPath = pythonPath + "get_class_progress.py";
        String command = "python3 " + pyPath + " " + visibleTestFile + " " + hiddenTestFile;
        JSONReturnable json = runPython(command);
        //executeBashScript("cleanDirectory.sh src/main/temp");
        return json;
    }

    public JSONReturnable getAssignmentsTestSummary(@NonNull String projectID, @NonNull String userNameTA) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return new JSONReturnable(-1, null);
        }
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userNameTA);
        if(teachingAssistant == null) {
            return new JSONReturnable(-2, null);
        }
        List<TeachingAssistantStudent> assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdCourseID(teachingAssistant.getUserID(), project.getCourseID());
        String visibleTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_visibleTests_TA.txt";
        String hiddenTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_hiddenTests_TA.txt";
        try {
            createTestFilesTA(visibleTestFile, hiddenTestFile, assignments, projectID);
        }
        catch(IOException e) {
            return new JSONReturnable(-3, null);
        }

        if (DEBUG) {
            visibleTestFile = pythonPath + "/test_datasets/sampleTestCases.txt";
            hiddenTestFile = pythonPath + "/test_datasets/sampleTestCases.txt";
        }

        String pyPath = pythonPath + "get_test_summary.py";
        String command = "python3 " + pyPath + " " + visibleTestFile + " " + hiddenTestFile;
        JSONReturnable json = runPython(command);
        //executeBashScript("cleanDirectory.sh src/main/temp");
        return json;
    }

    public JSONReturnable getAssignmentsCheating(@NonNull String projectID, @NonNull String userNameTA) {
        String commitLogFile = listAllCommitsByTime(projectID, userNameTA);
        if(commitLogFile == null) {
            return new JSONReturnable(-1, null);
        }
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return new JSONReturnable(-2, null);
        }
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userNameTA);
        if(teachingAssistant == null) {
            return new JSONReturnable(-3, null);
        }
        List<TeachingAssistantStudent> assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdCourseID(teachingAssistant.getUserID(), project.getCourseID());
        String visibleTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_visibleTests.txt";
        String hiddenTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_hiddenTests.txt";
        try {
            createTestFilesTA(visibleTestFile, hiddenTestFile, assignments, projectID);
        } catch (IOException e) {
            return new JSONReturnable(-4, null);
        }

        if (DEBUG){
            commitLogFile = pythonPath + "/test_datasets/sampleCommitList.txt";
            visibleTestFile = pythonPath + "/test_datasets/sampleTestsDay.txt";
            hiddenTestFile = pythonPath + "/test_datasets/sampleTestsDay.txt";
        }

        String pyPath = pythonPath + "get_class_cheating.py";
        String command = "python3 " + pyPath + " " + visibleTestFile + " " + hiddenTestFile + " " + commitLogFile + " -l 1000";
        JSONReturnable json = runPython(command);
        //executeBashScript("cleanDirectory.sh src/main/temp");
        return json;
    }

    public JSONReturnable getAnonymousClassProgress(@NonNull String projectID) {
        JSONReturnable json = null;
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifier(projectID);
        String visibleTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_visibleTests.txt";
        String hiddenTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_hiddenTests.txt";
        try {
            createTestFiles(visibleTestFile, hiddenTestFile, projects);
        } catch (IOException e) {
            return new JSONReturnable(-1, null);
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

    public JSONReturnable getAnonymousTestSummary(@NonNull String projectID) {
        JSONReturnable json = null;
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifier(projectID);
        String visibleTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_visibleTests.txt";
        String hiddenTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_hiddenTests.txt";
        try {
            createTestFiles(visibleTestFile, hiddenTestFile, projects);
        }
        catch(IOException e) {
            return new JSONReturnable(-1, null);
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

    public JSONReturnable getCommitCounts(@NonNull String projectID, @NonNull String userNameStudent, @NonNull String userNameTA) {
        String commitLogFile = listStudentCommitsByTime(projectID, userNameStudent, userNameTA);
        if(commitLogFile == null) {
            return new JSONReturnable(-1, null);
        }
        String pyPath = pythonPath + "get_git_commits.py";
        String command = "python3 " + pyPath + " " + commitLogFile + " " + userNameStudent;
        JSONReturnable json = runPython(command);

        //executeBashScript("cleanDirectory.sh src/main/temp");
        return json;
    }

    public JSONReturnable getCommitList(@NonNull String projectID, @NonNull String userNameStudent, @NonNull String userNameTA) {
        String commitLogFile = listStudentCommitsByTime(projectID, userNameStudent, userNameTA);
        if(commitLogFile == null) {
            return new JSONReturnable(-1, null);
        }
        String pyPath = pythonPath + "get_git_commit_list.py";
        String command = "python3 " + pyPath + " " + commitLogFile + " " + userNameStudent;
        JSONReturnable json = runPython(command);
        //executeBashScript("cleanDirectory.sh src/main/temp");
        return json;
    }

    public String countAllCommits(@NonNull String projectID, @NonNull String userNameTA) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return null;
        }
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userNameTA);
        if(teachingAssistant == null) {
            return null;
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return null;
        }
        List<TeachingAssistantStudent> assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdCourseID(teachingAssistant.getUserID(), project.getCourseID());
        String fileName = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_counts_TA.txt";
        for(TeachingAssistantStudent s : assignments) {
            Student student = studentRepository.findByUserID(s.getStudentID());
            String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
            executeBashScript("countCommits.sh " + destPath + " " + fileName + " " + student.getUserName());
        }
        return fileName;
    }

    public String countAllCommitsByDay(@NonNull String projectID, @NonNull String userNameTA) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return null;
        }
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userNameTA);
        if(teachingAssistant == null) {
            return null;
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return null;
        }
        List<TeachingAssistantStudent> assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdCourseID(teachingAssistant.getUserID(), project.getCourseID());
        String fileName = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_countsByDay_TA.txt";
        for(TeachingAssistantStudent s : assignments) {
            Student student = studentRepository.findByUserID(s.getStudentID());
            String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
            executeBashScript("countCommitsByDay.sh " + destPath + " " + fileName + " " + student.getUserName());
        }
        return fileName;
    }

    public String countStudentCommitsByDay(@NonNull String projectID, @NonNull String userNameStudent, @NonNull String userNameTA) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return null;
        }
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userNameTA);
        if(teachingAssistant == null) {
            return null;
        }
        Student student = studentRepository.findByUserName(userNameStudent);
        if(student == null) {
            return null;
        }
        TeachingAssistantStudent assignment = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdStudentIDAndIdCourseID(teachingAssistant.getUserID(), student.getUserID(), project.getCourseID());
        if(assignment == null) {
            return null;
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return null;
        }
        if(!isTakingCourse(student, project)) {
            return null;
        }
        String fileName = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_studentCountsByDay_TA.txt";
        String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
        if(executeBashScript("countCommitsByDay.sh " + destPath + " " + fileName + " " + student.getUserName()) == -1) {
            return null;
        }
        return fileName;
    }

    public String listAllCommitsByTime(@NonNull String projectID, @NonNull String userNameTA) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return null;
        }
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userNameTA);
        if(teachingAssistant == null) {
            return null;
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return null;
        }
        List<TeachingAssistantStudent> assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdCourseID(teachingAssistant.getUserID(), project.getCourseID());
        String fileName = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_commitInfo_TA.txt";
        for(TeachingAssistantStudent s : assignments) {
            Student student = studentRepository.findByUserID(s.getStudentID());
            String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
            executeBashScript("listCommitsByTime.sh " + destPath + " " + fileName + " " + student.getUserName());
        }
        return fileName;
    }

    public String listStudentCommitsByTime(@NonNull String projectID, @NonNull String userNameStudent, @NonNull String userNameTA) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return null;
        }
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userNameTA);
        if(teachingAssistant == null) {
            return null;
        }
        Student student = studentRepository.findByUserName(userNameStudent);
        if(student == null) {
            return null;
        }
        TeachingAssistantStudent assignment = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdStudentIDAndIdCourseID(teachingAssistant.getUserID(), student.getUserID(), project.getCourseID());
        if(assignment == null) {
            return null;
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return null;
        }
        if(!isTakingCourse(student, project)) {
            return null;
        }
        String fileName = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_studentCommitInfo_TA.txt";
        String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
        if(executeBashScript("listCommitsByTime.sh " + destPath + " " + fileName + " " + student.getUserName()) == -1) {
            return null;
        }
        return fileName;

    }

    public int runTestallForStudent(@NonNull String projectID, @NonNull String userNameStudent) {
        return 0;
    }

    public JSONArray getStudentData(@NonNull String semester, @NonNull String courseID, @NonNull String userName) {
        return null;
    }

    public JSONArray getProjectData(@NonNull String semester, @NonNull String courseID, @NonNull String userName) {
        return null;
    }

    public Project getProject(String projectID) {
        return null;
    }
}
