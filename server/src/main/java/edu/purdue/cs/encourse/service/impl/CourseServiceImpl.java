package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.domain.relations.*;
import edu.purdue.cs.encourse.service.CourseService;
import edu.purdue.cs.encourse.service.HelperService;
import edu.purdue.cs.encourse.service.helper.*;
import edu.purdue.cs.encourse.util.ConfigurationManager;
import edu.purdue.cs.encourse.util.JSONReturnable;
import lombok.NonNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

/**
 * Contains implementations for all services which may be used by course personnel (Professors and TAs)
 * Primarily used internally from other services, however some services are called directly for certain functionality
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
@Service(value = CourseServiceImpl.NAME)
public class CourseServiceImpl implements CourseService {
    public final static String NAME = "CourseService";
    private final static String pythonPath = "src/main/python/encourse/";
    private final static String tailFilePath = "src/main/temp/";
    private final static Boolean DEBUG = ConfigurationManager.getInstance().debug;
    private final static String pythonCommand = DEBUG ? "/anaconda3/bin/python" : "python3";

    @Autowired
    private HelperService helperService;

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
    private ProjectTestScriptRepository projectTestScriptRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private StudentSectionRepository studentSectionRepository;

    @Autowired
    private StudentProjectRepository studentProjectRepository;

    @Autowired
    private TeachingAssistantStudentRepository teachingAssistantStudentRepository;

    @Autowired
    private TeachingAssistantSectionRepository teachingAssistantSectionRepository;

    @Autowired
    private StudentProjectDateRepository studentProjectDateRepository;

    @Autowired
    private StudentProjectTestRepository studentProjectTestRepository;

    public Project getProject(@NonNull String projectID) {
        return projectRepository.findByProjectID(projectID);
    }

    /**
     * Sets only the directory used for each section in the database, without creating it on the server
     * Mainly used internally for courses which already have repositories cloned
     *
     * @param semester  Semester of course having a directory set
     * @param courseID  Identifier for the course being having a directory set
     * @return          Error Code
     */
    public int setDirectory(@NonNull String semester, @NonNull String courseID) {
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(semester, courseID);
        if(sections.isEmpty()) {
            return -1;
        }
        int code = 0;
        for(Section s : sections) {
            s.setCourseHub("/sourcecontrol/" + sections.get(0).getCourseID() + "/" + sections.get(0).getSemester());
            if(sectionRepository.save(s) == null) {
                code = -2;
            }
        }
        return code;
    }

    /**
     * Creates a directory for storing all student repositories, test cases, and Makefiles used by the course
     *
     * @param semester  Semester of course having a directory created
     * @param courseID  Identifier for the course having a directory created
     * @return          Error Code
     */
    public int createDirectory(@NonNull String semester, @NonNull String courseID){
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(semester, courseID);
        if(sections.isEmpty()) {
            return -1;
        }
        if(!(new File("/sourcecontrol/" + sections.get(0).getCourseID() + "/" + sections.get(0).getSemester() + "/testcases").mkdirs())) {
            return -2;
        }
        int code = 0;
        for(Section s : sections) {
            s.setCourseHub("/sourcecontrol/" + sections.get(0).getCourseID() + "/" + sections.get(0).getSemester());
            if(sectionRepository.save(s) == null) {
                code = -3;
            }
            List<StudentSection> assignments =
                    studentSectionRepository.findByIdSectionID(s.getSectionID());
            for(StudentSection a : assignments){
                Student student = studentRepository.findByUserID(a.getStudentID());
                new File(sections.get(0).getCourseHub() + "/" + student.getUserName()).mkdir();
            }
        }
        return code;
    }

    /**
     * Stores the remote path to use for cloning student git repositories
     *
     * @param semester      Semester of course having a remote path set
     * @param courseID      Identifier for the course having a remote path set
     * @param remotePath    Path to the directory containing the subdirectories for each student on university server
     * @return              Error code
     */
    public int setSectionRemotePaths(@NonNull String semester, @NonNull String courseID, @NonNull String remotePath) {
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(semester, courseID);
        if(sections.isEmpty()) {
            return -1;
        }
        int code = 0;
        for(Section s : sections) {
            s.setRemotePath(remotePath);
            if(sectionRepository.save(s) == null) {
                code = -2;
            }
        }
        return code;
    }

    /**
     * Obtains data for all courses that an account is associated with
     * Primarily used internally to generate the JSON for front-end to parse
     *
     * @param userName  Front-end identifier for account data is needed for
     * @param courseIDs List of identifiers for the courses that information needs to be retrieved for
     * @return          JSON for front-end to parse
     */
    public JSONArray getCourseData(@NonNull String userName, List<String> courseIDs) {
        JSONArray coursesJSON = new JSONArray();
        for(String courseID : courseIDs) {
            JSONObject courseJSON = new JSONObject();
            List<Section> sections = sectionRepository.findByCourseID(courseID);
            List<String> sectionIDs = new ArrayList<>();
            for(Section s : sections) {
                sectionIDs.add(s.getSectionID());
            }
            courseJSON.put("course_number", sections.get(0).getCourseID());
            courseJSON.put("course_name", sections.get(0).getCourseTitle());
            courseJSON.put("semester", sections.get(0).getSemester());
            courseJSON.put("id", userName);
            courseJSON.put("sections", sectionIDs);
            coursesJSON.add(courseJSON);
        }
        return coursesJSON;
    }

    /**
     * Obtains data for all projects associated with a courseID and semester
     * Primarily used to find projects for a course that a user selects
     *
     * @param semester  Semester of the course that project data is requested for
     * @param courseID  Identifier for the course that project data is requested for
     * @return          JSON for front-end to parse
     */
    public JSONArray getProjectData(@NonNull String semester, @NonNull String courseID) {
        List<Project> projects = projectRepository.findBySemesterAndCourseID(semester, courseID);
        if(projects.isEmpty()) {
            return null;
        }
        JSONArray projectsJSON = new JSONArray();
        for(Project p : projects) {
            JSONObject projectJSON = new JSONObject();
            List<ProjectTestScript> testScripts = projectTestScriptRepository.findByIdProjectID(p.getProjectID());
            List<String> testNames = new ArrayList<>();
            for(ProjectTestScript t : testScripts) {
                testNames.add(t.getTestScriptName());
            }
            projectJSON.put("project_name", p.getProjectName());
            projectJSON.put("source_name", p.getRepoName());
            projectJSON.put("start_date", p.getStartDate());
            projectJSON.put("due_date", p.getDueDate());
            projectJSON.put("test_scripts", testNames);
            projectJSON.put("id", p.getProjectID());
            projectJSON.put("last_sync", p.getSyncDate());
            projectJSON.put("last_test", p.getTestDate());
            projectsJSON.add(projectJSON);
        }
        return projectsJSON;
    }

    /**
     * Obtains data for all sections associated with a semester and courseID
     * Primarily used to display the sections for a professor's course
     *
     * @param semester  Semester of the course that project data is requested for
     * @param courseID  Identifier for the course that project data is requested for
     * @return          JSON for front-end to parse
     */
    public JSONArray getSectionData(@NonNull String semester, @NonNull String courseID) {
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(semester, courseID);
        JSONArray sectionsJSON = new JSONArray();
        for(Section s : sections) {
            JSONObject sectionJSON = new JSONObject();
            List<StudentSection> assignedStudents = studentSectionRepository.findByIdSectionID(s.getSectionID());
            List<String> students = new ArrayList<>();
            for(StudentSection a : assignedStudents) {
                Student student = studentRepository.findByUserID(a.getStudentID());
                students.add(student.getUserName());
            }
            List<TeachingAssistantSection> assignedTeachingAssistants = teachingAssistantSectionRepository.findByIdSectionID(s.getSectionID());
            List<String> teachingAssistants = new ArrayList<>();
            for(TeachingAssistantSection a : assignedTeachingAssistants) {
                TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserID(a.getTeachingAssistantID());
                teachingAssistants.add(teachingAssistant.getUserName());
            }
            sectionJSON.put("name", s.getSectionType());
            sectionJSON.put("id", s.getSectionID());
            sectionJSON.put("time", s.getTimeSlot());
            sectionJSON.put("students", students);
            sectionJSON.put("teaching_assistants", teachingAssistants);
            sectionsJSON.add(sectionJSON);
        }
        return sectionsJSON;
    }

    /**
     * Obtains data for all of the students with the specified usernames
     * Primarily used as a generalized way to obtain student data for professors or TAs
     *
     * @param userNames Front-end identifiers for all students data should pertain to
     * @return          JSON for front-end to parse
     */
    public JSONArray getStudentData(List<String> userNames) {
        List<String> completedStudents = new ArrayList<>();
        JSONArray studentsJSON = new JSONArray();
        for(String userName : userNames) {
            Student student = studentRepository.findByUserName(userName);
            if(!(completedStudents.contains(student.getUserID()))) {
                completedStudents.add(student.getUserID());
                // TODO: Security issue in multiple course system. Must make sure professor teaches the course each project belongs to
                List<StudentProject> studentProjects = studentProjectRepository.findByIdStudentIDAndIdSuite(student.getUserID(), "testall");
                Map<String, Double> grades = new TreeMap<>();
                Map<String, Double> points = new TreeMap<>();
                Map<String, Double> totals = new TreeMap<>();
                Map<String, Double> hiddenGrades = new TreeMap<>();
                Map<String, Double> hiddenPoints = new TreeMap<>();
                Map<String, Double> hiddenTotals = new TreeMap<>();
                Map<String, Integer> commitCounts = new TreeMap<>();
                Map<String, Double> timeSpent = new TreeMap<>();
                for (StudentProject p : studentProjects) {
                    grades.put(p.getProjectID(), p.getBestVisibleGrade());
                    points.put(p.getProjectID(), p.getBestVisiblePoints());
                    totals.put(p.getProjectID(), p.getVisiblePointTotal());
                    hiddenGrades.put(p.getProjectID(), p.getBestHiddenGrade());
                    hiddenPoints.put(p.getProjectID(), p.getBestHiddenPoints());
                    hiddenTotals.put(p.getProjectID(), p.getHiddenPointTotal());
                    commitCounts.put(p.getProjectID(), p.getCommitCount());
                    timeSpent.put(p.getProjectID(), p.getTotalTimeSpent());
                }
                List<StudentSection> assignedSections = studentSectionRepository.findByIdStudentID(student.getUserID());
                List<String> sectionStrings = new ArrayList<>();
                for (StudentSection a : assignedSections) {
                    sectionStrings.add(a.getSectionID());
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
                studentJSON.put("points", points);
                studentJSON.put("totals", totals);
                studentJSON.put("hiddenGrades", hiddenGrades);
                studentJSON.put("hiddenPoints", hiddenPoints);
                studentJSON.put("hiddenTotals", hiddenTotals);
                studentJSON.put("commitCounts", commitCounts);
                studentJSON.put("timeSpent", timeSpent);
                studentsJSON.add(studentJSON);
            }
        }
        return studentsJSON;
    }

    public JSONArray getSuitesData(String userName, String projectID) {
        Student student = studentRepository.findByUserName(userName);
        if(student == null) {
            return null;
        }
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIDAndIdStudentID(projectID, student.getUserID());
        JSONArray suitesJSON = new JSONArray();
        for(StudentProject p : projects) {
            JSONObject suiteJSON = new JSONObject();
            suiteJSON.put("suite_name", p.getSuite().toUpperCase());
            suiteJSON.put("grade", p.getBestVisibleGrade());
            suiteJSON.put("points", p.getBestVisiblePoints());
            suiteJSON.put("total", p.getVisiblePointTotal());
            suiteJSON.put("hiddenGrade", p.getBestHiddenGrade());
            suiteJSON.put("hiddenPoints", p.getBestHiddenPoints());
            suiteJSON.put("hiddenTotal", p.getHiddenPointTotal());
            suitesJSON.add(suiteJSON);
        }
        return suitesJSON;
    }

    /**
     * Obtains data for all test scripts associated with a project
     * Primarily used to show existing test scripts that a project has
     *
     * @param projectID Identifier for project to obtain test scripts for
     * @return          JSON for front-end to parse
     */
    public JSONArray getTestScriptData(@NonNull String projectID) {
        List<ProjectTestScript> testScripts = projectTestScriptRepository.findByIdProjectID(projectID);
        if(testScripts.isEmpty()) {
            return null;
        }
        JSONArray testsJSON = new JSONArray();
        for(ProjectTestScript t : testScripts) {
            JSONObject testJSON = new JSONObject();
            testJSON.put("id", t.getTestScriptName());
            testJSON.put("hidden", t.isHidden());
            testJSON.put("points", t.getPointsWorth());
            testJSON.put("suites", t.getSuites().split(","));
            testsJSON.add(testJSON);
        }
        return testsJSON;

    }

    /**
     * Obtains information on any operation currently being done for a project
     * Primarily used to keep the user updated on progress for a time consuming operation
     *
     * @param projectID Identifier for project operation information is obtained for
     * @return          JSON for front-end to parse
     */
    public JSONObject getOperationData(@NonNull String projectID) {
        Project project = projectRepository.findByProjectID(projectID);
        if(project == null) {
            return null;
        }
        JSONObject operationJSON = new JSONObject();
        operationJSON.put("progress", project.getOperationProgress());
        operationJSON.put("time_elapsed", project.getOperationTime());
        operationJSON.put("estimated_time_remaining", project.getEstimatedTimeRemaining());
        if(project.isSyncing()) {
            operationJSON.put("operation", "sync");
        }
        else if(project.isTesting()) {
            operationJSON.put("operation", "test");
        }
        else if(project.isAnalyzing()) {
            operationJSON.put("operation", "analyze");
        }
        else {
            operationJSON.put("operation", "none");
            operationJSON.put("progress", 0);
            operationJSON.put("time_elapsed", 0);
            operationJSON.put("estimated_time_remaining", 0);
        }
        return operationJSON;
    }

    /**
     * Obtains information on amount of progress each student specified has made on a project
     * Primarily used for graphs which show progress over time on front-end
     *
     * @param projectID Identifier for project that progress is being obtained from
     * @param userNames Front-end identifier for each student to include in the analysis
     * @return          JSON for front-end to parse
     */
    public JSONReturnable getProgress(@NonNull String projectID, List<String> userNames) {
        JSONReturnable json = null;
        List<StudentProject> projects = helperService.getStudentProjects(projectID, userNames);
        String visibleTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_visibleTests.txt";
        String hiddenTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_hiddenTests.txt";
        try {
            helperService.createTestFiles(visibleTestFile, hiddenTestFile, projects);
        } catch (IOException e) {
            json = new JSONReturnable(-1, (JSONObject) null);
        }

        if (helperService.getDebug()) {
            visibleTestFile = helperService.getPythonPath() + "data/sampleVisibleTestCases.txt";
            hiddenTestFile = helperService.getPythonPath() + "data/sampleHiddenTestCases.txt";
        } else if (json != null) {
            return json;
        }

        // TODO: Check that test results work as expected
        String command = helperService.getPythonCommand() + " class-progress " + visibleTestFile + " " + hiddenTestFile;
        json = helperService.runPython(command);
        return json;
    }

    /**
     * Obtains information on similarities between students' projects
     * Primarily used for showing signs of academic dishonesty on front-end
     *
     * @param projectID Identifier for project that similarities are being analyzed from
     * @param userNames Front-end identifier for each student to include in the analysis
     * @return          JSON for front-end to parse
     */
    public JSONReturnable getSimilar(@NonNull String projectID, List<String> userNames) {
        JSONReturnable json = null;
        if (helperService.getDebug()) {
            String command = helperService.getPythonCommand() + " identical " + helperService.getPythonPath() + "data/sampleDiffs.txt";
            return helperService.runPython(command);
        }

        List<StudentProject> projects = helperService.getStudentProjects(projectID, userNames);
        Project project = projectRepository.findByProjectID(projectID);
        if(project == null) {
            json = new JSONReturnable(-1, (JSONObject) null);
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            json = new JSONReturnable(-2, (JSONObject) null);
        }
        String diffsFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_codeDiffs.txt";
        List<StudentProject> temp = new ArrayList<>(projects);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(diffsFile));
            for (StudentProject projectOne : projects) {
                temp.remove(projectOne);
                if(temp.isEmpty()) {
                    break;
                }
                Student studentOne = studentRepository.findByUserID(projectOne.getStudentID());
                String studentOnePath = (sections.get(0).getCourseHub() + "/" + studentOne.getUserName() + "/" + project.getRepoName());
                Process process = Runtime.getRuntime().exec("./src/main/bash/listCommitHistoryByAuthor.sh " + studentOnePath + " CS252");
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String hash = stdInput.readLine().split(" ")[1];
                process.destroy();
                StringBuilder builder = new StringBuilder();
                builder.append(studentOne.getUserName()).append(":");
                for (StudentProject projectTwo : temp) {
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
            writer.close();
        } catch (Exception e) {
            json = new JSONReturnable(-3, (JSONObject) null);
        }
        if (!helperService.getDebug() && json != null) {
            return json;
        }
        String command = helperService.getPythonCommand() + " identical " + helperService.getPythonPath() + diffsFile;
        json = helperService.runPython(command);
        return json;
    }

    /**
     * Obtains various statistics for a group of students for a project
     * Primarily used for showing class and group statistics summaries
     *
     * @param projectID Identifier for project that statistics are obtained from
     * @param userNames Front-end identifier for each student to include in the analysis
     * @return          JSON for front-end to parse
     */
    public JSONReturnable getStatistics(@NonNull String projectID, List<String> userNames) {
        JSONReturnable json = null;
        List<StudentProject> projects = helperService.getStudentProjects(projectID, userNames);
        String dailyCountsFile = helperService.countAllCommitsByDay(projectID, projects);
        String commitLogFile = helperService.listAllCommitsByTime(projectID, projects);
        if(dailyCountsFile == null) {
            json = new JSONReturnable(-1, (JSONObject) null);
        }
        if(commitLogFile == null) {
            json = new JSONReturnable(-2, (JSONObject) null);
        }
        String visibleTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_visibleTestsDates.txt";
        String hiddenTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_hiddenTestsDates.txt";
        try {
            helperService.createTestFiles(visibleTestFile, hiddenTestFile, projects);
        }
        catch (IOException e) {
            json = new JSONReturnable(-3, (JSONObject) null);
        }
        if (helperService.getDebug()) {
            visibleTestFile = "src/main/python/encourse/data/sampleVisibleTestCases.txt";
            hiddenTestFile = "src/main/python/encourse/data/sampleHiddenTestCases.txt";
        }
        String command = helperService.getPythonCommand() + " stats " + commitLogFile + " " + visibleTestFile + " " + hiddenTestFile + " -t 1.0 -l 200";
        json = helperService.runPython(command);
        return json;
    }

    /**
     * Obtains information on current test results for several students
     * Primarily used for showing which test cases are being passed on front-end
     *
     * @param projectID Identifier for project that test results are obtained from
     * @param userNames Front-end identifier for each student to include in the analysis
     * @return          JSON for front-end to parse
     */
    public JSONReturnable getTestSummary(@NonNull String projectID, List<String> userNames) {
        JSONReturnable json = null;
        List<StudentProject> projects = helperService.getStudentProjects(projectID, userNames);
        String visibleTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_visibleTests.txt";
        String hiddenTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_hiddenTests.txt";
        try {
            helperService.createTestFiles(visibleTestFile, hiddenTestFile, projects);
        } catch (IOException e) {
            json = new JSONReturnable(-1, (JSONObject) null);
        }
        if (helperService.getDebug()) {
            visibleTestFile = helperService.getPythonPath() + "data/sampleVisibleTestCases.txt";
            hiddenTestFile = helperService.getPythonPath() + "data/sampleHiddenTestCases.txt";
        } else if (json != null) {
            return json;
        }
        String command = helperService.getPythonCommand() + " test-summary " + visibleTestFile + " " + hiddenTestFile;
        json = helperService.runPython(command);
        return json;
    }

    /**
     * Obtains information on commit history by day for several students
     * Primarily used for showing the list of most recent commits in a group of students
     *
     * @param projectID Identifier for project that commit history is obtained from
     * @param userNames Front-end identifier for each student to include in the analysis
     * @return          JSON for front-end to parse
     */
    public JSONReturnable getCommitList(@NonNull String projectID, List<String> userNames) {
        JSONReturnable json = null;
        List<StudentProject> projects = helperService.getStudentProjects(projectID, userNames);
        String commitLogFile = helperService.listAllCommitsByTime(projectID, projects);
        if(commitLogFile == null) {
            json = new JSONReturnable(-1, (JSONObject) null);
        }
        if (!helperService.getDebug() && json != null) {
            return json;
        }
        String command = helperService.getPythonCommand() + " gitlist " + commitLogFile;
        json = helperService.runPython(command);
        return json;
    }

    /**
     * Obtains analysis for which students are likely cheating on a project
     * Primarily used for showing signs of academic dishonesty
     *
     * @param projectID Identifier for project that cheating analysis is performed from
     * @param userNames Front-end identifier for each student to include in the analysis
     * @return          JSON for front-end to parse
     */
    public JSONReturnable getCheating(@NonNull String projectID, List<String> userNames) {
        JSONReturnable json = null;
        String visibleTestFile = null;
        String hiddenTestFile = null;
        String commitLogFile = null;
        if (!helperService.getDebug()) {
            List<StudentProject> projects = helperService.getStudentProjects(projectID, userNames);
            Project project = projectRepository.findByProjectID(projectID);
            if (project == null) {
                json = new JSONReturnable(-1, (JSONObject) null);
            }
            List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
            if (sections.isEmpty()) {
                json = new JSONReturnable(-2, (JSONObject) null);
            }
            commitLogFile = helperService.listAllCommitsByTime(projectID, projects);
            if (commitLogFile == null) {
                json = new JSONReturnable(-3, (JSONObject) null);
            }
            visibleTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_visibleTestsDates.txt";
            hiddenTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_hiddenTestsDates.txt";
            try {
                BufferedWriter visibleWriter = new BufferedWriter(new FileWriter(visibleTestFile));
                BufferedWriter hiddenWriter = new BufferedWriter(new FileWriter(hiddenTestFile));
                for (StudentProject p : projects) {
                    Student student = studentRepository.findByUserID(p.getStudentID());
                    List<StudentProjectDate> projectDates = studentProjectDateRepository.findByIdProjectIDAndIdStudentID(projectID, student.getUserID());
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
                json = new JSONReturnable(-3, (JSONObject) null);
            }
        }

        if (helperService.getDebug()){
            commitLogFile = helperService.getPythonPath() + "data/sampleCommitList.txt";
            visibleTestFile = helperService.getPythonPath() + "data/sampleTestsDay.txt";
            hiddenTestFile = helperService.getPythonPath() + "data/sampleTestsDay.txt";
        } else if (json != null) {
            return json;
        }
        String command = helperService.getPythonCommand() + " cheating " + visibleTestFile + " " + hiddenTestFile + " " + commitLogFile + " -l 1000";
        json = helperService.runPython(command);
        return json;
    }

    /**
     * Obtains information on additions and deletions that a student has made for a project
     * Primarily used for displaying the information in an additions and deletions graph
     *
     * @param projectID Identifier for project additions and deletions are obtained from
     * @param userName  Front-end identifier for student whose project is being analyzed
     * @return          JSON for front-end to parse
     */
    public JSONReturnable getStudentAdditionsAndDeletions(@NonNull String projectID, @NonNull String userName) {
        JSONReturnable json = null;
        String dailyCountsFile = helperService.countStudentCommitsByDay(projectID, userName);
        String commitLogFile = helperService.listStudentCommitsByTime(projectID, userName);
        if(dailyCountsFile == null) {
            json = new JSONReturnable(-1, (JSONObject) null);
        }
        if(commitLogFile == null) {
            json = new JSONReturnable(-2, (JSONObject) null);
        }
        if (!helperService.getDebug() && json != null) {
            return json;
        }
        String command = helperService.getPythonCommand() + " changes " + commitLogFile + " " + dailyCountsFile + " " + userName + " -l 200";
        return helperService.runPython(command);
    }

    /**
     * Obtains each day's commit count for a student's project
     * Primarily used for displaying the information in a commits over time graph
     *
     * @param projectID Identifier for project daily commit counts are obtained from
     * @param userName  Front-end identifier for student whose project is being analyzed
     * @return          JSON for front-end to parse
     */
    public JSONReturnable getStudentCommitCounts(@NonNull String projectID, @NonNull String userName) {
        JSONReturnable json = null;;
        String commitLogFile = helperService.listStudentCommitsByTime(projectID, userName);
        if(commitLogFile == null) {
            json = new JSONReturnable(-2, (JSONObject) null);
        }
        if (!helperService.getDebug() && json != null) {
            return json;
        }
        String command = helperService.getPythonCommand() + " commitcount " + commitLogFile + " " + userName;
        return helperService.runPython(command);
    }

    /**
     * Obtains files modified and major files changed for each day that student has worked on project
     * Primarily used for showing a brief commit history on the student page
     *
     * @param projectID Identifier for project that commit history is obtained from
     * @param userName  Front-end identifier for student whose project is being analyzed
     * @return          JSON for front-end to parse
     */
    public JSONReturnable getStudentCommitList(@NonNull String projectID, @NonNull String userName) {
        JSONReturnable json = null;
        String commitLogFile = helperService.listStudentCommitsByTime(projectID, userName);
        if(commitLogFile == null) {
            json = new JSONReturnable(-1, (JSONObject) null);
        }
        if (!helperService.getDebug() && json != null) {
            return json;
        }
        String command = helperService.getPythonCommand() + " gitlist " + commitLogFile + " " + userName;
        return helperService.runPython(command);
    }

    /**
     * Obtains analysis on progress per commit for a student
     * Primarily used for showing signs of academic dishonesty
     *
     * @param projectID Identifier for project that progress per commit is obtained from
     * @param userName  Front-end identifier for student whose project is being analyzed
     * @return          JSON for front-end to parse
     */
    public JSONReturnable getStudentCommitVelocity(@NonNull String projectID, @NonNull String userName) {
        JSONReturnable json = null;
        String dailyCountsFile = helperService.countStudentCommitsByDay(projectID, userName);
        String commitLogFile = helperService.listStudentCommitsByTime(projectID, userName);
        if(dailyCountsFile == null) {
            json = new JSONReturnable(-1, (JSONObject) null);
        }
        if(commitLogFile == null) {
            json = new JSONReturnable(-2, (JSONObject) null);
        }
        if(!projectRepository.existsByProjectID(projectID)) {
            json = new JSONReturnable(-1, (JSONObject) null);
        }
        Student student = studentRepository.findByUserName(userName);
        if(student == null) {
            json = new JSONReturnable(-2, (JSONObject) null);
        }
        String visibleTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_visibleTestDates.txt";
        String hiddenTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_hiddenTestDates.txt";
        if (helperService.getDebug()) {
            visibleTestFile = "src/main/python/encourse/data/sampleTestsDay.txt";
            hiddenTestFile = "src/main/python/encourse/data/sampleTestsDay.txt";
        }
        else {
            try {
                List<StudentProjectDate> projectDates = studentProjectDateRepository.findByIdProjectIDAndIdStudentID(projectID, student.getUserID());
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
            } catch (IOException e) {
                json = new JSONReturnable(-3, (JSONObject) null);
            }
        }
        if (!helperService.getDebug() && json != null) {
            return json;
        }

        String command = helperService.getPythonCommand() + " velocity " + visibleTestFile + " " + hiddenTestFile + " " + commitLogFile + " " + userName;
        return helperService.runPython(command);
    }

    /**
     * Obtains information on amount of progress that a student has made on a project
     * Primarily used for graphs which show progress over time on front-end
     *
     * @param projectID Identifier for project that progress is being obtained from
     * @param userName  Front-end identifier for student whose project is being analyzed
     * @return          JSON for front-end to parse
     */
    public JSONReturnable getStudentProgress(@NonNull String projectID, @NonNull String userName) {
        JSONReturnable json = null;
        String visibleTestFile;
        String hiddenTestFile;
        String dailyCountsFile;
        if (helperService.getDebug()) {
            visibleTestFile = "src/main/python/encourse/data/sampleTestsDay.txt";
            hiddenTestFile = "src/main/python/encourse/data/sampleTestsDay.txt";
            dailyCountsFile = "src/main/python/encourse/data/sampleCountsDay.txt";
        } else {
            dailyCountsFile = helperService.countStudentCommitsByDay(projectID, userName);
            if (!projectRepository.existsByProjectID(projectID)) {
                return new JSONReturnable(-1, (JSONObject) null);
            }
            Student student = studentRepository.findByUserName(userName);
            if (student == null) {
                return new JSONReturnable(-2, (JSONObject) null);
            }
            List<StudentProjectDate> projectDates = studentProjectDateRepository.findByIdProjectIDAndIdStudentID(projectID, student.getUserID());
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
                return new JSONReturnable(-3, (JSONObject) null);
            }
        }

        String command = helperService.getPythonCommand() + " student-progress " + visibleTestFile + " " + hiddenTestFile + " " + dailyCountsFile + " " + userName;
        return helperService.runPython(command);
    }

    /**
     * Obtains various statistics for a student's work on a project
     * Primarily used for showing student statistics summaries
     *
     * @param projectID Identifier for project that statistics are being obtained from
     * @param userName  Front-end identifier for student whose project is being analyzed
     * @return          JSON for front-end to parse
     */
    public JSONReturnable getStudentStatistics(@NonNull String projectID, @NonNull String userName) {
        JSONReturnable json = null;
        String dailyCountsFile = helperService.countStudentCommitsByDay(projectID, userName);
        String commitLogFile = helperService.listStudentCommitsByTime(projectID, userName);
        if(dailyCountsFile == null) {
            json = new JSONReturnable(-1, (JSONObject) null);
        }
        if(commitLogFile == null) {
            json = new JSONReturnable(-2, (JSONObject) null);
        }
        String visibleTestFile;
        String hiddenTestFile;
        if (helperService.getDebug()) {
            visibleTestFile = "src/main/python/encourse/data/sampleVisibleTestCases.txt";
            hiddenTestFile = "src/main/python/encourse/data/sampleHiddenTestCases.txt";
        } else {
            visibleTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_visibleTests.txt";
            hiddenTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_hiddenTests.txt";
            Student student = studentRepository.findByUserName(userName);
            StudentProject project = studentProjectRepository.findByIdProjectIDAndIdStudentIDAndIdSuite(projectID, student.getUserID(), "testall");
            List<StudentProject> projects = new ArrayList<>();
            projects.add(project);
            try {
                helperService.createTestFiles(visibleTestFile, hiddenTestFile, projects);
            }
            catch (IOException e) {
                json = new JSONReturnable(-1, (JSONObject) null);
            }
            if (json != null) {
                return json;
            }
        }
        String command = helperService.getPythonCommand() + " stats " + commitLogFile + " " + visibleTestFile + " " + hiddenTestFile + " -t 1.0 -l 200";
        return helperService.runPython(command);
    }

    /**
     * Obtains a specified source file with changes between specified commit hashes
     * Hashes will typically be the last commit on the previous day for start and last commit on the current day for end
     * Primarily used to show a file with all changes between the hashes
     *
     * @param projectID         Identifier for project that source is being taken from
     * @param userName          Front-end identifier for student whose source is being shown
     * @param startCommitHash   Hash corresponding to the commit with the initial state of the file
     * @param endCommitHash     Hash corresponding to the commit with the final state of the file
     * @param sourceName        Name of the source file being analyzed
     * @return                  Path to a new file with the entire source file and additions and deletion shown inline
     */
    public String getSourceWithChanges(@NonNull String projectID, @NonNull String userName, @NonNull String startCommitHash, @NonNull String endCommitHash, @NonNull String sourceName) {
        Project project = projectRepository.findByProjectID(projectID);
        if(project == null) {
            return null;
        }
        Student student = studentRepository.findByUserName(userName);
        if(student == null) {
            return null;
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return null;
        }
        String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
        String fileName = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_sourceChanges.txt";
        if(sourceName == null) {
            if (helperService.executeBashScript("getSourceChanges.sh " + destPath + " " + fileName + " " + startCommitHash + " " + endCommitHash) < 0) {
                return null;
            }
        }
        else {
            if (helperService.executeBashScript("getSourceChanges.sh " + destPath + " " + fileName + " " + startCommitHash + " " + endCommitHash + " " + sourceName) < 0) {
                return null;
            }
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            int count = 0;
            while(reader.readLine() != null && count < 3) {
                count++;
            }
            reader.close();
            if(count < 3) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
                if(sourceName == null) {
                    writer.write("No changes found from commit hash " + startCommitHash + " to hash " + endCommitHash);
                }
                else {
                    writer.write("No changes found from commit hash " + startCommitHash + " to hash " + endCommitHash + " for file " + sourceName);
                }
                writer.close();
            }
        }
        catch(IOException e) {
            return null;
        }
        return fileName;
    }

    /**
     * Runs a testall immediately for a particular student
     * Primarily used for quickly running a testall on a particular student without having to test the entire class
     *
     * @param projectID Identifier for the project being tested
     * @param userName  Front-end identifier for the student being tested
     * @return          Error code
     */
    public int runTestallForStudent(@NonNull String projectID, @NonNull String userName) {
        Project project = projectRepository.findByProjectID(projectID);
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
        StudentProject studentProject = studentProjectRepository.findByIdProjectIDAndIdStudentIDAndIdSuite(projectID, student.getUserID(), "testall");
        if(studentProject == null) {
            return -6;
        }
        String testingDirectory = sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName();
        String fileName = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_gitHashes.txt";
        helperService.executeBashScript("listTestUpdateHistory.sh " + testingDirectory + " " + fileName);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            helperService.executeBashScript("checkoutPreviousCommit.sh " + testingDirectory + " origin");
            String[] commitInfo = line.split(" ");
            String date = commitInfo[2];
            helperService.executeBashScript("checkoutPreviousCommit.sh " + testingDirectory + " " + commitInfo[1]);
            if(helperService.executeBashScript("runMakefile.sh " + testingDirectory + " " + makefilePath) == -1) {
                return -7;
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
            double visibleGrade = helperService.parseProgressForProject(projectID, visibleResult);
            double hiddenGrade = helperService.parseProgressForProject(projectID, hiddenResult);
            StudentProjectDate projectDate = studentProjectDateRepository.findByIdDateAndIdProjectIDAndIdStudentID(date, projectID, student.getUserID());
            if(projectDate == null) {
                StudentProjectDate d = new StudentProjectDate(studentProject.getStudentID(), studentProject.getProjectID(), date, visibleGrade, hiddenGrade);
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
                helperService.updateTestResults(visibleResult, studentProject.getStudentID(), studentProject.getProjectID(), false);
            }
            if(hiddenGrade > studentProject.getBestHiddenGrade()) {
                helperService.updateTestResults(hiddenResult, studentProject.getStudentID(), studentProject.getProjectID(), true);
            }
            studentProject = studentProjectRepository.findByIdProjectIDAndIdStudentIDAndIdSuite(studentProject.getProjectID(), studentProject.getStudentID(), "testall");
            line = reader.readLine();
            commitInfo = line.split(" ");
            date = commitInfo[2];
            helperService.executeBashScript("checkoutPreviousCommit.sh " + testingDirectory + " " + commitInfo[1]);
            if(helperService.executeBashScript("runMakefile.sh " + testingDirectory + " " + makefilePath) == -1) {
                return -8;
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
            projectDate = studentProjectDateRepository.findByIdDateAndIdProjectIDAndIdStudentID(date, projectID, student.getUserID());
            if(projectDate == null) {
                StudentProjectDate d = new StudentProjectDate(studentProject.getStudentID(), studentProject.getProjectID(), date, visibleGrade, hiddenGrade);
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
                helperService.updateTestResults(visibleResult, studentProject.getStudentID(), studentProject.getProjectID(), false);
            }
            if(hiddenGrade > studentProject.getBestHiddenGrade()) {
                helperService.updateTestResults(hiddenResult, studentProject.getStudentID(), studentProject.getProjectID(), true);
            }
            helperService.executeBashScript("checkoutPreviousCommit.sh " + testingDirectory + " origin");
            reader.close();
        }
        catch(Exception e) {
            helperService.executeBashScript("checkoutPreviousCommit.sh " + testingDirectory + " origin");
            return -9;
        }
        return 0;
    }

}
