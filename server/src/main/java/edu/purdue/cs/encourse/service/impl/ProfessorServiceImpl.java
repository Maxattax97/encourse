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
                    studentProjectRepository.save(new StudentProject(a.getStudentID(), project.getProjectIdentifier(), "testall"));
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
        if(!helperService.isTakingCourse(student, project)) {
            return -4;
        }
        studentProjectRepository.save(new StudentProject(student.getUserID(), project.getProjectIdentifier(), "testall"));
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
                    if(helperService.executeBashScript("cloneRepositories.sh " + destPath + " " + repoPath) == -1) {
                        code = -6;
                    }
                }
            }
        }
        if(helperService.executeBashScript("setPermissions.sh " + sections.get(0).getCourseID()) == -1) {
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
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifierAndIdSuite(projectID, "testall");
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

    public int updateStudentInformation(@NonNull String projectID, @NonNull String userName) {
        String dailyCountsFile = helperService.countStudentCommitsByDay(projectID, userName);
        String commitLogFile = helperService.listStudentCommitsByTime(projectID, userName);
        if(dailyCountsFile == null) {
            return -1;
        }
        if(commitLogFile == null) {
            return -2;
        }
        Student student = studentRepository.findByUserName(userName);
        StudentProject project = studentProjectRepository.findByIdProjectIdentifierAndIdStudentIDAndIdSuite(projectID, student.getUserID(), "testall");
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
        String testResult = builder.toString();
        String pyPath = helperService.getPythonPath() + "get_statistics.py";
        String command = helperService.getPythonCommand() + " " + pyPath + " " + commitLogFile + " " + dailyCountsFile + " " + userName + " " + testResult + " -t 1.0 -l 200";
        JSONReturnable json = helperService.runPython(command);
        if(json == null || json.getJsonObject() == null) {
            return 0;
        }
        if (helperService.getDebug()) {
            helperService.executeBashScript("cleanDirectory.sh src/main/temp");
            return 0;
        }
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
        //helperService.executeBashScript("cleanDirectory.sh src/main/temp");
        return 0;
    }

    public JSONReturnable getClassCheating(@NonNull String projectID) {
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifierAndIdSuite(projectID, "testall");
        List<String> userNames = helperService.getStudentUserNames(projects);
        return courseService.getCheating(projectID, userNames);
    }

    public JSONReturnable getClassCommitList(@NonNull String projectID) {
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifierAndIdSuite(projectID, "testall");
        List<String> userNames = helperService.getStudentUserNames(projects);
        return courseService.getCommitList(projectID, userNames);
    }

    public JSONReturnable getClassProgress(@NonNull String projectID) {
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifierAndIdSuite(projectID, "testall");
        List<String> userNames = helperService.getStudentUserNames(projects);
        return courseService.getProgress(projectID, userNames);
    }

    public JSONReturnable getClassSimilar(@NonNull String projectID) {
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifierAndIdSuite(projectID, "testall");
        List<String> userNames = helperService.getStudentUserNames(projects);
        return courseService.getSimilar(projectID, userNames);
    }

    public JSONReturnable getClassStatistics(@NonNull String projectID) {
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifierAndIdSuite(projectID, "testall");
        List<String> userNames = helperService.getStudentUserNames(projects);
        return courseService.getStatistics(projectID, userNames);
    }

    public JSONReturnable getClassTestSummary(@NonNull String projectID) {
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifierAndIdSuite(projectID, "testall");
        List<String> userNames = helperService.getStudentUserNames(projects);
        return courseService.getTestSummary(projectID, userNames);
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
        helperService.executeBashScript("setPermissions.sh " + sections.get(0).getCourseID());
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

    public int addTestScriptToSuite(@NonNull String projectID, @NonNull String testName, @NonNull String suite) {
        ProjectTestScript testScript = projectTestScriptRepository.findByIdProjectIdentifierAndIdTestScriptName(projectID, testName);
        if(testScript == null) {
            return -1;
        }
        if(!testScript.hasSuite(suite)) {
            testScript.addSuite(suite);
        }
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return -2;
        }
        if(!project.hasSuite(suite)) {
            project.addSuite(suite);
            List<StudentProject> studentProjects = studentProjectRepository.findByIdProjectIdentifierAndIdSuite(projectID, "testall");
            for(StudentProject p : studentProjects) {
                StudentProject studentProject = new StudentProject(p.getStudentID(), p.getProjectIdentifier(), suite);
                studentProjectRepository.save(studentProject);
            }
        }
        projectTestScriptRepository.save(testScript);
        projectRepository.save(project);
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
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifierAndIdSuite(projectID, "testall");
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
        project = projectRepository.save(project);
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
                Thread.sleep(5000);
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
                double visibleGrade = helperService.parseProgressForProject(p.getProjectIdentifier(), visibleResult);
                double hiddenGrade = helperService.parseProgressForProject(p.getProjectIdentifier(), hiddenResult);
                helperService.updateTestResults(visibleResult, p.getStudentID(), p.getProjectIdentifier(), false);
                helperService.updateTestResults(hiddenResult, p.getStudentID(), p.getProjectIdentifier(), true);
                StudentProjectDate projectDate = studentProjectDateRepository.findByIdDateAndIdProjectIdentifierAndIdStudentID(date, projectID, student.getUserID());
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
                p = studentProjectRepository.findByIdProjectIdentifierAndIdStudentIDAndIdSuite(p.getProjectIdentifier(), p.getStudentID(), "testall");
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
                Thread.sleep(5000);
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
                helperService.updateTestResults(visibleResult, p.getStudentID(), p.getProjectIdentifier(), false);
                helperService.updateTestResults(hiddenResult, p.getStudentID(), p.getProjectIdentifier(), true);
                projectDate = studentProjectDateRepository.findByIdDateAndIdProjectIdentifierAndIdStudentID(date, projectID, student.getUserID());
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
                helperService.executeBashScript("checkoutPreviousCommit.sh " + testingDirectory + " origin");
                reader.close();
                project.setOperationProgress(1.0 * count / projects.size());
                project.setOperationTime((System.currentTimeMillis() - start) / 1000);
                project = projectRepository.save(project);
            }
            catch(Exception e) {
                code = -6;
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
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifierAndIdSuite(projectID, "testall");
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
                    Thread.sleep(5000);
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
                    helperService.updateTestResults(visibleResult, p.getStudentID(), p.getProjectIdentifier(), false);
                    helperService.updateTestResults(hiddenResult, p.getStudentID(), p.getProjectIdentifier(), true);
                    double visibleGrade = helperService.parseProgressForProject(projectID, visibleResult);
                    double hiddenGrade = helperService.parseProgressForProject(projectID, hiddenResult);
                    StudentProjectDate projectDate = studentProjectDateRepository.findByIdDateAndIdProjectIdentifierAndIdStudentID(date, projectID, student.getUserID());
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
                    p = studentProjectRepository.findByIdProjectIdentifierAndIdStudentIDAndIdSuite(p.getProjectIdentifier(), p.getStudentID(), "testall");
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
                    Thread.sleep(5000);
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
                    helperService.updateTestResults(visibleResult, p.getStudentID(), p.getProjectIdentifier(), false);
                    helperService.updateTestResults(hiddenResult, p.getStudentID(), p.getProjectIdentifier(), true);
                    visibleGrade = helperService.parseProgressForProject(projectID, visibleResult);
                    hiddenGrade = helperService.parseProgressForProject(projectID, hiddenResult);
                    projectDate = studentProjectDateRepository.findByIdDateAndIdProjectIdentifierAndIdStudentID(date, projectID, student.getUserID());
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
                }
                helperService.executeBashScript("checkoutPreviousCommit.sh " + testingDirectory + " origin");
                reader.close();
            }
            catch(Exception e) {
                code = -6;
                helperService.executeBashScript("checkoutPreviousCommit.sh " + testingDirectory + " origin");
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
                List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifierAndIdSuite(project.getProjectIdentifier(), "testall");
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
        helperService.executeBashScript("cleanDirectory.sh src/main/temp");
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
        List<String> courseIDs = new ArrayList<>();
        for(ProfessorCourse course : courses) {
            courseIDs.add(course.getCourseID());
        }
        return courseService.getCourseData(userName, courseIDs);
    }

    /** Retrieves basic data for all students in course, including name, userName, and simple project info **/
    public JSONArray getStudentData(@NonNull String semester, @NonNull String courseID) {
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(semester, courseID);
        List<String> userNames = new ArrayList<>();
        for(Section section : sections) {
            List<StudentSection> studentSections = studentSectionRepository.findByIdSectionIdentifier(section.getSectionIdentifier());
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
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifierAndIdSuite(projectID, "testall");
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
