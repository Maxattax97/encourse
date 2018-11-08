package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.domain.relations.*;
import edu.purdue.cs.encourse.service.TeachingAssistantService;
import edu.purdue.cs.encourse.service.helper.ServiceHelper;
import edu.purdue.cs.encourse.service.helper.StreamGobbler;
import edu.purdue.cs.encourse.util.JSONReturnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import lombok.NonNull;

@Service(value = TeachingAssistantServiceImpl.NAME)
public class TeachingAssistantServiceImpl implements TeachingAssistantService {

    public final static String NAME = "TeachingAssistantService";
    
    private final static ServiceHelper helper = ServiceHelper.getInstance();

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
    private TeachingAssistantCourseRepository teachingAssistantCourseRepository;

    @Autowired
    private ProjectTestScriptRepository projectTestScriptRepository;

    @Autowired
    private StudentProjectTestRepository studentProjectTestRepository;

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
        String pyPath = helper.pythonPath + "get_individual_progress.py";
        String command = helper.pythonCommand + " " + pyPath + " " + visibleTestFile + " " + hiddenTestFile + " " + userNameStudent;
        JSONReturnable json = helper.runPython(command);
        //helper.executeBashScript("cleanDirectory.sh src/main/temp");
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
        String pyPath = helper.pythonPath + "get_add_del.py";
        String command = helper.pythonCommand + " " + pyPath + " " + commitLogFile + " " + dailyCountsFile + " " + userNameStudent + " -l 1000";
        JSONReturnable json = helper.runPython(command);
        //helper.executeBashScript("cleanDirectory.sh src/main/temp");
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
        if (helper.DEBUG) {
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
        String pyPath = helper.pythonPath + "get_statistics.py";
        String command = helper.pythonCommand + " " + pyPath + " " + commitLogFile + " " + dailyCountsFile + " " + userNameStudent + " " + testResult + " -t 1.0 -l 1000";
        JSONReturnable json = helper.runPython(command);
        if(json == null || json.getJsonObject() == null) {
            return json;
        }
        if (helper.DEBUG) {
            helper.executeBashScript("cleanDirectory.sh src/main/temp");
            return json;
        }
        //helper.executeBashScript("cleanDirectory.sh src/main/temp");
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
            helper.createTestFilesTA(visibleTestFile, hiddenTestFile, assignments, projectID);
        } catch (IOException e) {
            return new JSONReturnable(-3, null);
        }

        if (helper.DEBUG) {
            visibleTestFile = helper.pythonPath + "/test_datasets/sampleTestCases.txt";
            hiddenTestFile = helper.pythonPath + "/test_datasets/sampleTestCases.txt";
        }

        String pyPath = helper.pythonPath + "get_class_progress.py";
        String command = helper.pythonCommand + " " + pyPath + " " + visibleTestFile + " " + hiddenTestFile;
        JSONReturnable json = helper.runPython(command);
        //helper.executeBashScript("cleanDirectory.sh src/main/temp");
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
            helper.createTestFilesTA(visibleTestFile, hiddenTestFile, assignments, projectID);
        }
        catch(IOException e) {
            return new JSONReturnable(-3, null);
        }

        if (helper.DEBUG) {
            visibleTestFile = helper.pythonPath + "/test_datasets/sampleTestCases.txt";
            hiddenTestFile = helper.pythonPath + "/test_datasets/sampleTestCases.txt";
        }

        String pyPath = helper.pythonPath + "get_test_summary.py";
        String command = helper.pythonCommand + " " + pyPath + " " + visibleTestFile + " " + hiddenTestFile;
        JSONReturnable json = helper.runPython(command);
        //helper.executeBashScript("cleanDirectory.sh src/main/temp");
        return json;
    }

    public JSONReturnable getAssignmentsSimilar(@NonNull String projectID, @NonNull String userNameTA) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return new JSONReturnable(-1, null);
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return new JSONReturnable(-2, null);
        }
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userNameTA);
        if(teachingAssistant == null) {
            return new JSONReturnable(-3, null);
        }
        List<TeachingAssistantStudent> assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdCourseID(teachingAssistant.getUserID(), project.getCourseID());
        String diffsFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_codeDiffs.txt";
        List<TeachingAssistantStudent> temp = new ArrayList<>(assignments);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(diffsFile));
            for (TeachingAssistantStudent projectOne : assignments) {
                temp.remove(projectOne);
                Student studentOne = studentRepository.findByUserID(projectOne.getStudentID());
                String studentOnePath = (sections.get(0).getCourseHub() + "/" + studentOne.getUserName() + "/" + project.getRepoName());
                Process process = Runtime.getRuntime().exec("./src/main/bash/listCommitHistoryByAuthor.sh " + studentOnePath + " CS252");
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String hash = stdInput.readLine().split(" ")[1];
                process.destroy();
                StringBuilder builder = new StringBuilder();
                builder.append(studentOne.getUserName()).append(":");
                for (TeachingAssistantStudent projectTwo : temp) {
                    Student studentTwo = studentRepository.findByUserID(projectTwo.getStudentID());
                    builder.append(studentTwo.getUserName()).append(";");
                    String studentTwoPath = (sections.get(0).getCourseHub() + "/" + studentTwo.getUserName() + "/" + project.getRepoName());
                    process = Runtime.getRuntime().exec("./src/main/bash/calculateDiffScore.sh " + studentOnePath + " " + studentTwoPath + " " + hash);
                    stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String result = stdInput.readLine();
                    process.destroy();
                    builder.append(result).append("_");
                }
                writer.write(builder.toString() + "\n");
            }
        } catch (Exception e) {
            return new JSONReturnable(-4, null);
        }

        String pyPath = helper.pythonPath + "get_similarity.py";
        String command = helper.pythonCommand + " " + pyPath + " " + diffsFile;
        JSONReturnable json = helper.runPython(command);
        //helper.executeBashScript("cleanDirectory.sh src/main/temp");
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
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return new JSONReturnable(-2, null);
        }
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userNameTA);
        if(teachingAssistant == null) {
            return new JSONReturnable(-3, null);
        }
        List<TeachingAssistantStudent> assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdCourseID(teachingAssistant.getUserID(), project.getCourseID());
        String visibleTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_visibleTestsDates.txt";
        String hiddenTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_hiddenTestsDates.txt";
        try {
            BufferedWriter visibleWriter = new BufferedWriter(new FileWriter(visibleTestFile));
            BufferedWriter hiddenWriter = new BufferedWriter(new FileWriter(hiddenTestFile));
            for(TeachingAssistantStudent assignment : assignments) {
                Student student = studentRepository.findByUserID(assignment.getStudentID());
                List<StudentProjectDate> projectDates = studentProjectDateRepository.findByIdProjectIdentifierAndIdStudentID(projectID, student.getUserID());
                visibleWriter.write("Start " + student.getUserName() + "\n");
                hiddenWriter.write("Start " + student.getUserName() + "\n");
                for (StudentProjectDate d : projectDates) {
                    visibleWriter.write(d.getDate() + " " + d.getDateVisibleGrade() + "\n");
                    hiddenWriter.write(d.getDate() + " " + d.getDateHiddenGrade() + "\n");
                }
                visibleWriter.write("End " + student.getUserName() + "\n");
                hiddenWriter.write("End " + student.getUserName() + "\n");
            }
            visibleWriter.close();
            hiddenWriter.close();
        } catch (IOException e) {
            return new JSONReturnable(-3, null);
        }

        if (helper.DEBUG){
            commitLogFile = helper.pythonPath + "/test_datasets/sampleCommitList.txt";
            visibleTestFile = helper.pythonPath + "/test_datasets/sampleTestsDay.txt";
            hiddenTestFile = helper.pythonPath + "/test_datasets/sampleTestsDay.txt";
        }

        String pyPath = helper.pythonPath + "get_class_cheating.py";
        //String command = helper.pythonCommand + " " + pyPath + " " + visibleTestFile + " " + hiddenTestFile + " " + commitLogFile + " " + diffsFile + " -l 1000";
        String command = helper.pythonCommand + " " + pyPath + " " + visibleTestFile + " " + hiddenTestFile + " " + commitLogFile + " -l 1000";
        JSONReturnable json = helper.runPython(command);
        //helper.executeBashScript("cleanDirectory.sh src/main/temp");
        return json;
    }

    // TODO: JARETT DO PYTHON
    public JSONReturnable getAssignmentsStatistics(@NonNull String projectID, @NonNull String userNameTA) {
        String dailyCountsFile = countAllCommitsByDay(projectID, userNameTA);
        String commitLogFile = listAllCommitsByTime(projectID, userNameTA);
        if(dailyCountsFile == null) {
            return new JSONReturnable(-1, null);
        }
        if(commitLogFile == null) {
            return new JSONReturnable(-2, null);
        }
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userNameTA);
        if(teachingAssistant == null) {
            return new JSONReturnable(-3, null);
        }
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return new JSONReturnable(-3, null);
        }
        List<TeachingAssistantStudent> assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdCourseID(teachingAssistant.getUserID(), project.getCourseID());
        String visibleTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_visibleTestsDates.txt";
        String hiddenTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_hiddenTestsDates.txt";
        try {
            BufferedWriter visibleWriter = new BufferedWriter(new FileWriter(visibleTestFile));
            BufferedWriter hiddenWriter = new BufferedWriter(new FileWriter(hiddenTestFile));
            for(TeachingAssistantStudent assignment : assignments) {
                Student student = studentRepository.findByUserID(assignment.getStudentID());
                List<StudentProjectDate> projectDates = studentProjectDateRepository.findByIdProjectIdentifierAndIdStudentID(projectID, student.getUserID());
                visibleWriter.write("Start " + student.getUserName() + "\n");
                hiddenWriter.write("Start " + student.getUserName() + "\n");
                for (StudentProjectDate d : projectDates) {
                    visibleWriter.write(d.getDate() + " " + d.getDateVisibleGrade() + "\n");
                    hiddenWriter.write(d.getDate() + " " + d.getDateHiddenGrade() + "\n");
                }
                visibleWriter.write("End " + student.getUserName() + "\n");
                hiddenWriter.write("End " + student.getUserName() + "\n");
            }
            visibleWriter.close();
            hiddenWriter.close();
        } catch (IOException e) {
            return new JSONReturnable(-3, null);
        }
        String pyPath = helper.pythonPath + "get_statistics.py";
        String command = helper.pythonCommand + " " + pyPath + " " + commitLogFile + " " + dailyCountsFile + " " + visibleTestFile + " " + hiddenTestFile + " -t 1.0 -l 200";
        JSONReturnable json = helper.runPython(command);
        //helper.executeBashScript("cleanDirectory.sh src/main/temp");
        return json;
    }

    public JSONReturnable getGroupProgress(@NonNull String projectID, @NonNull List<String> userNames, @NonNull String userNameTA) {
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userNameTA);
        if(teachingAssistant == null) {
            return new JSONReturnable(-1, null);
        }
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return new JSONReturnable(-2, null);
        }
        List<StudentProject> projects = new ArrayList<>();
        for(String userName: userNames) {
            Student student = studentRepository.findByUserName(userName);
            StudentProject studentProject = studentProjectRepository.findByIdProjectIdentifierAndIdStudentID(projectID, student.getUserID());
            TeachingAssistantStudent check = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdStudentIDAndIdCourseID(teachingAssistant.getUserID(), student.getUserID(), project.getCourseID());
            if(check != null && studentProject != null) {
                projects.add(studentProject);
            }
        }
        String visibleTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_visibleTests.txt";
        String hiddenTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_hiddenTests.txt";
        try {
            helper.createTestFiles(visibleTestFile, hiddenTestFile, projects);
        } catch (IOException e) {
            return new JSONReturnable(-3, null);
        }

        if (helper.DEBUG) {
            visibleTestFile = helper.pythonPath + "/test_datasets/sampleVisibleTestCases.txt";
            hiddenTestFile = helper.pythonPath + "/test_datasets/sampleHiddenTestCases.txt";
        }

        // TODO: Check that test results work as expected
        String pyPath = helper.pythonPath + "get_class_progress.py";
        String command = helper.pythonCommand + " " + pyPath + " " + visibleTestFile + " " + hiddenTestFile;
        JSONReturnable json = helper.runPython(command);
        //helper.executeBashScript("cleanDirectory.sh src/main/temp");
        return json;
    }

    public JSONReturnable getGroupTestSummary(@NonNull String projectID, @NonNull List<String> userNames, @NonNull String userNameTA) {
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userNameTA);
        if(teachingAssistant == null) {
            return new JSONReturnable(-1, null);
        }
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return new JSONReturnable(-2, null);
        }
        List<StudentProject> projects = new ArrayList<>();
        for(String userName: userNames) {
            Student student = studentRepository.findByUserName(userName);
            StudentProject studentProject = studentProjectRepository.findByIdProjectIdentifierAndIdStudentID(projectID, student.getUserID());
            TeachingAssistantStudent check = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdStudentIDAndIdCourseID(teachingAssistant.getUserID(), student.getUserID(), project.getCourseID());
            if(check != null && studentProject != null) {
                projects.add(studentProject);
            }
        }
        String visibleTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_visibleTests.txt";
        String hiddenTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_hiddenTests.txt";
        try {
            helper.createTestFiles(visibleTestFile, hiddenTestFile, projects);
        } catch (IOException e) {
            return new JSONReturnable(-3, null);
        }
        if (helper.DEBUG) {
            visibleTestFile = helper.pythonPath + "/test_datasets/sampleVisibleTestCases.txt";
            hiddenTestFile = helper.pythonPath + "/test_datasets/sampleHiddenTestCases.txt";
        }
        String pyPath = helper.pythonPath + "get_test_summary.py";
        String command = helper.pythonCommand + " " + pyPath + " " + visibleTestFile + " " + hiddenTestFile;
        JSONReturnable json = helper.runPython(command);
        //helper.executeBashScript("cleanDirectory.sh src/main/temp");
        return json;
    }

    public JSONReturnable getCommitCounts(@NonNull String projectID, @NonNull String userNameStudent, @NonNull String userNameTA) {
        String commitLogFile = listStudentCommitsByTime(projectID, userNameStudent, userNameTA);
        if(commitLogFile == null) {
            return new JSONReturnable(-1, null);
        }
        String pyPath = helper.pythonPath + "get_git_commits.py";
        String command = helper.pythonCommand + " " + pyPath + " " + commitLogFile + " " + userNameStudent;
        JSONReturnable json = helper.runPython(command);

        //helper.executeBashScript("cleanDirectory.sh src/main/temp");
        return json;
    }

    public JSONReturnable getCommitList(@NonNull String projectID, @NonNull String userNameStudent, @NonNull String userNameTA) {
        String commitLogFile = listStudentCommitsByTime(projectID, userNameStudent, userNameTA);
        if(commitLogFile == null) {
            return new JSONReturnable(-1, null);
        }
        String pyPath = helper.pythonPath + "get_git_commit_list.py";
        String command = helper.pythonCommand + " " + pyPath + " " + commitLogFile + " " + userNameStudent;
        JSONReturnable json = helper.runPython(command);
        //helper.executeBashScript("cleanDirectory.sh src/main/temp");
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
            helper.executeBashScript("countCommits.sh " + destPath + " " + fileName + " " + student.getUserName());
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
            helper.executeBashScript("countCommitsByDay.sh " + destPath + " " + fileName + " " + student.getUserName());
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
        if(!helper.isTakingCourse(student, project)) {
            return null;
        }
        String fileName = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_studentCountsByDay_TA.txt";
        String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
        if(helper.executeBashScript("countCommitsByDay.sh " + destPath + " " + fileName + " " + student.getUserName()) == -1) {
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
            helper.executeBashScript("listCommitsByTime.sh " + destPath + " " + fileName + " " + student.getUserName());
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
        if(!helper.isTakingCourse(student, project)) {
            return null;
        }
        String fileName = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_studentCommitInfo_TA.txt";
        String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
        if(helper.executeBashScript("listCommitsByTime.sh " + destPath + " " + fileName + " " + student.getUserName()) == -1) {
            return null;
        }
        return fileName;

    }

    public int runTestallForStudent(@NonNull String projectID, @NonNull String userNameStudent, @NonNull String userNameTA) {
        return 0;
    }

    public JSONArray getStudentData(@NonNull String semester, @NonNull String courseID, @NonNull String userNameTA) {
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(semester, courseID);
        if(sections.isEmpty()) {
            return null;
        }
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userNameTA);
        if(teachingAssistant == null) {
            return null;
        }
        List<TeachingAssistantStudent> assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdCourseID(teachingAssistant.getUserID(), courseID);
        List<String> completedStudents = new ArrayList<>();
        JSONArray studentsJSON = new JSONArray();
        for(TeachingAssistantStudent assignment : assignments) {
            Student student = studentRepository.findByUserID(assignment.getStudentID());
            if(!(completedStudents.contains(student.getUserID()))) {
                completedStudents.add(student.getUserID());
                List<StudentProject> studentProjects = studentProjectRepository.findByIdStudentID(student.getUserID());
                Map<String, Double> grades = new TreeMap<>();
                Map<String, Double> hiddenGrades = new TreeMap<>();
                Map<String, Integer> commitCounts = new TreeMap<>();
                Map<String, Double> timeSpent = new TreeMap<>();
                for (StudentProject p : studentProjects) {
                    grades.put(p.getProjectIdentifier(), p.getBestVisibleGrade());
                    hiddenGrades.put(p.getProjectIdentifier(), p.getBestHiddenGrade());
                    commitCounts.put(p.getProjectIdentifier(), p.getCommitCount());
                    timeSpent.put(p.getProjectIdentifier(), p.getTotalTimeSpent());
                }
                List<StudentSection> assignedSections = studentSectionRepository.findByIdStudentID(student.getUserID());
                List<String> sectionStrings = new ArrayList<>();
                for (StudentSection a : assignedSections) {
                    sectionStrings.add(a.getSectionIdentifier());
                }
                List<TeachingAssistantStudent> assignedTeachingAssistants = teachingAssistantStudentRepository.findByIdStudentID(student.getUserID());
                List<String> teachingAssistants = new ArrayList<>();
                for (TeachingAssistantStudent a : assignedTeachingAssistants) {
                    TeachingAssistant teachingAssistantAssigned = teachingAssistantRepository.findByUserID(a.getTeachingAssistantID());
                    teachingAssistants.add(teachingAssistantAssigned.getUserName());
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
                if (helper.OBFUSCATE) {
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
        return studentsJSON;
    }

    /** Gets all courses that a teaching assistant works for **/
    public JSONArray getCourseData(@NonNull String userNameTA) {
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userNameTA);
        if(teachingAssistant == null) {
            return null;
        }
        List<TeachingAssistantCourse> courses = teachingAssistantCourseRepository.findByIdTeachingAssistantID(teachingAssistant.getUserID());
        if(courses.isEmpty()) {
            return null;
        }
        JSONArray coursesJSON = new JSONArray();
        for(TeachingAssistantCourse c : courses) {
            JSONObject courseJSON = new JSONObject();
            List<Section> sections = sectionRepository.findByCourseID(c.getCourseID());
            List<String> sectionIDs = new ArrayList<>();
            for(Section s : sections) {
                sectionIDs.add(s.getSectionIdentifier());
            }
            courseJSON.put("course_number", c.getCourseID());
            courseJSON.put("course_name", sections.get(0).getCourseID());
            courseJSON.put("semester", c.getSemester());
            courseJSON.put("id", teachingAssistant.getUserName());
            courseJSON.put("sections", sectionIDs);
            coursesJSON.add(courseJSON);
        }
        return coursesJSON;
    }
}
