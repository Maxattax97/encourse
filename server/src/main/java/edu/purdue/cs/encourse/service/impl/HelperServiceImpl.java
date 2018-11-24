package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.domain.relations.*;
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

@Service(value = HelperServiceImpl.NAME)
public class HelperServiceImpl {

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
            json = new JSONReturnable(-2, null);
        }
        return json;
    }

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
    }

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

    public List<StudentProject> getStudentProjects(String projectID, List<String> userNames) {
        List<StudentProject> projects = new ArrayList<>();
        for(String userName: userNames) {
            Student student = studentRepository.findByUserName(userName);
            StudentProject project = studentProjectRepository.findByIdProjectIdentifierAndIdStudentID(projectID, student.getUserID());
            if(project != null) {
                projects.add(project);
            }
        }
        return projects;
    }

    public List<String> getStudentUserNames(List<StudentProject> studentProjects) {
        List<String> userNames = new ArrayList<>();
        for(StudentProject project : studentProjects) {
            Student student = studentRepository.findByUserID(project.getStudentID());
            userNames.add(student.getUserName());
        }
        return userNames;
    }

    public List<String> getStudentUserNamesForTA(List<TeachingAssistantStudent> assignments) {
        List<String> userNames = new ArrayList<>();
        for(TeachingAssistantStudent assignment : assignments) {
            Student student = studentRepository.findByUserID(assignment.getStudentID());
            userNames.add(student.getUserName());
        }
        return userNames;
    }

    /** Counts the number of commits that every student in the class has made for a project **/
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

    /** Counts the total number of commits made each day that the project was active **/
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
}
