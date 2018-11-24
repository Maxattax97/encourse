package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.domain.relations.*;
import edu.purdue.cs.encourse.service.CourseService;
import edu.purdue.cs.encourse.service.HelperService;
import edu.purdue.cs.encourse.service.helper.*;
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
        return projectRepository.findByProjectIdentifier(projectID);
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
                    studentSectionRepository.findByIdSectionIdentifier(s.getSectionIdentifier());
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
                sectionIDs.add(s.getSectionIdentifier());
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
            List<ProjectTestScript> visibleTestScripts = projectTestScriptRepository.findByIdProjectIdentifierAndIsHidden(p.getProjectIdentifier(), false);
            List<ProjectTestScript> hiddenTestScripts = projectTestScriptRepository.findByIdProjectIdentifierAndIsHidden(p.getProjectIdentifier(), true);
            projectJSON.put("project_name", p.getProjectName());
            projectJSON.put("source_name", p.getRepoName());
            projectJSON.put("start_date", p.getStartDate());
            projectJSON.put("due_date", p.getDueDate());
            projectJSON.put("test_script", visibleTestScripts);
            projectJSON.put("hidden_test_script", hiddenTestScripts);
            projectJSON.put("id", p.getProjectIdentifier());
            projectJSON.put("last_sync", p.getSyncDate());
            projectJSON.put("last_test", p.getTestDate());
            projectsJSON.add(projectJSON);
        }
        return projectsJSON;
    }

    /** Gets all sections for a particular course **/
    public JSONArray getSectionData(@NonNull String semester, @NonNull String courseID) {
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(semester, courseID);
        JSONArray sectionsJSON = new JSONArray();
        for(Section s : sections) {
            JSONObject sectionJSON = new JSONObject();
            List<StudentSection> assignedStudents = studentSectionRepository.findByIdSectionIdentifier(s.getSectionIdentifier());
            List<String> students = new ArrayList<>();
            for(StudentSection a : assignedStudents) {
                Student student = studentRepository.findByUserID(a.getStudentID());
                students.add(student.getUserName());
            }
            List<TeachingAssistantSection> assignedTeachingAssistants = teachingAssistantSectionRepository.findByIdSectionID(s.getSectionIdentifier());
            List<String> teachingAssistants = new ArrayList<>();
            for(TeachingAssistantSection a : assignedTeachingAssistants) {
                TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserID(a.getTeachingAssistantID());
                teachingAssistants.add(teachingAssistant.getUserName());
            }
            sectionJSON.put("name", s.getSectionType());
            sectionJSON.put("id", s.getSectionIdentifier());
            sectionJSON.put("time", s.getTimeSlot());
            sectionJSON.put("students", students);
            sectionJSON.put("teaching_assistants", teachingAssistants);
            sectionsJSON.add(sectionJSON);
        }
        return sectionsJSON;
    }

    public JSONArray getStudentData(List<String> userNames) {
        List<String> completedStudents = new ArrayList<>();
        JSONArray studentsJSON = new JSONArray();
        for(String userName : userNames) {
            Student student = studentRepository.findByUserName(userName);
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
                if (helperService.getObfuscate()) {
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

    public JSONReturnable getProgress(@NonNull String projectID, List<String> userNames) {
        List<StudentProject> projects = helperService.getStudentProjects(projectID, userNames);
        String visibleTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_visibleTests.txt";
        String hiddenTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_hiddenTests.txt";
        try {
            helperService.createTestFiles(visibleTestFile, hiddenTestFile, projects);
        } catch (IOException e) {
            return new JSONReturnable(-1, null);
        }

        if (helperService.getDebug()) {
            visibleTestFile = helperService.getPythonPath() + "/test_datasets/sampleVisibleTestCases.txt";
            hiddenTestFile = helperService.getPythonPath() + "/test_datasets/sampleHiddenTestCases.txt";
        }

        // TODO: Check that test results work as expected
        String pyPath = helperService.getPythonPath() + "get_class_progress.py";
        String command = helperService.getPythonCommand() + " " + pyPath + " " + visibleTestFile + " " + hiddenTestFile;
        JSONReturnable json = helperService.runPython(command);
        return json;
    }

    public JSONReturnable getSimilar(@NonNull String projectID, List<String> userNames) {
        List<StudentProject> projects = helperService.getStudentProjects(projectID, userNames);
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return new JSONReturnable(-1, null);
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return new JSONReturnable(-2, null);
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
            return new JSONReturnable(-3, null);
        }
        String pyPath = helperService.getPythonPath() + "get_identical_count.py";
        String command = helperService.getPythonCommand() + " " + pyPath + " " + diffsFile;
        JSONReturnable json = helperService.runPython(command);
        return json;
    }

    public JSONReturnable getStatistics(@NonNull String projectID, List<String> userNames) {
        List<StudentProject> projects = helperService.getStudentProjects(projectID, userNames);
        String dailyCountsFile = helperService.countAllCommitsByDay(projectID, projects);
        String commitLogFile = helperService.listAllCommitsByTime(projectID, projects);
        if(dailyCountsFile == null) {
            return new JSONReturnable(-1, null);
        }
        if(commitLogFile == null) {
            return new JSONReturnable(-2, null);
        }
        String visibleTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_visibleTestsDates.txt";
        String hiddenTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_hiddenTestsDates.txt";
        try {
            BufferedWriter visibleWriter = new BufferedWriter(new FileWriter(visibleTestFile));
            BufferedWriter hiddenWriter = new BufferedWriter(new FileWriter(hiddenTestFile));
            for(StudentProject project : projects) {
                Student student = studentRepository.findByUserID(project.getStudentID());
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
        String pyPath = helperService.getPythonPath() + "get_class_statistics.py";
        String command = helperService.getPythonCommand() + " " + pyPath + " " + visibleTestFile + " " + hiddenTestFile + " " + commitLogFile + " -t 1.0 -l 200";
        JSONReturnable json = helperService.runPython(command);
        return json;
    }

    public JSONReturnable getTestSummary(@NonNull String projectID, List<String> userNames) {
        List<StudentProject> projects = helperService.getStudentProjects(projectID, userNames);
        String visibleTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_visibleTests.txt";
        String hiddenTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_hiddenTests.txt";
        try {
            helperService.createTestFiles(visibleTestFile, hiddenTestFile, projects);
        } catch (IOException e) {
            return new JSONReturnable(-1, null);
        }
        if (helperService.getDebug()) {
            visibleTestFile = helperService.getPythonPath() + "/test_datasets/sampleVisibleTestCases.txt";
            hiddenTestFile = helperService.getPythonPath() + "/test_datasets/sampleHiddenTestCases.txt";
        }
        String pyPath = helperService.getPythonPath() + "get_test_summary.py";
        String command = helperService.getPythonCommand() + " " + pyPath + " " + visibleTestFile + " " + hiddenTestFile;
        JSONReturnable json = helperService.runPython(command);
        return json;
    }

    public JSONReturnable getCommitList(@NonNull String projectID, List<String> userNames) {
        List<StudentProject> projects = helperService.getStudentProjects(projectID, userNames);
        String commitLogFile = helperService.listAllCommitsByTime(projectID, projects);
        if(commitLogFile == null) {
            return new JSONReturnable(-1, null);
        }
        String pyPath = helperService.getPythonPath() + "get_git_commit_list.py";
        String command = helperService.getPythonCommand() + " " + pyPath + " " + commitLogFile;
        JSONReturnable json = helperService.runPython(command);
        return json;
    }

    public JSONReturnable getCheating(@NonNull String projectID, List<String> userNames) {
        List<StudentProject> projects = helperService.getStudentProjects(projectID, userNames);
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return new JSONReturnable(-1, null);
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return new JSONReturnable(-2, null);
        }
        String commitLogFile = helperService.listAllCommitsByTime(projectID, projects);
        if(commitLogFile == null) {
            return new JSONReturnable(-3, null);
        }
        String visibleTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_visibleTestsDates.txt";
        String hiddenTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_hiddenTestsDates.txt";
        try {
            BufferedWriter visibleWriter = new BufferedWriter(new FileWriter(visibleTestFile));
            BufferedWriter hiddenWriter = new BufferedWriter(new FileWriter(hiddenTestFile));
            for(StudentProject p : projects) {
                Student student = studentRepository.findByUserID(p.getStudentID());
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

        if (helperService.getDebug()){
            commitLogFile = helperService.getPythonPath() + "/test_datasets/sampleCommitList.txt";
            visibleTestFile = helperService.getPythonPath() + "/test_datasets/sampleTestsDay.txt";
            hiddenTestFile = helperService.getPythonPath() + "/test_datasets/sampleTestsDay.txt";
        }
        String pyPath = helperService.getPythonPath() + "get_class_cheating.py";
        String command = helperService.getPythonCommand() + " " + pyPath + " " + visibleTestFile + " " + hiddenTestFile + " " + commitLogFile + " -l 1000";
        JSONReturnable json = helperService.runPython(command);
        return json;
    }

    public JSONReturnable getStudentAdditionsAndDeletions(@NonNull String projectID, @NonNull String userName) {
        String dailyCountsFile = helperService.countStudentCommitsByDay(projectID, userName);
        String commitLogFile = helperService.listStudentCommitsByTime(projectID, userName);
        if(dailyCountsFile == null) {
            return new JSONReturnable(-1, null);
        }
        if(commitLogFile == null) {
            return new JSONReturnable(-2, null);
        }
        String pyPath = helperService.getPythonPath() + "get_add_del.py";
        String command = helperService.getPythonCommand() + " " + pyPath + " " + commitLogFile + " " + dailyCountsFile + " " + userName + " -l 200";
        return helperService.runPython(command);
    }

    public JSONReturnable getStudentCommitCounts(@NonNull String projectID, @NonNull String userName) {
        String commitLogFile = helperService.listStudentCommitsByTime(projectID, userName);
        if(commitLogFile == null) {
            return new JSONReturnable(-2, null);
        }
        String pyPath = helperService.getPythonPath() + "get_git_commits.py";
        String command = helperService.getPythonCommand() + " " + pyPath + " " + commitLogFile + " " + userName;
        return helperService.runPython(command);
    }

    public JSONReturnable getStudentCommitList(@NonNull String projectID, @NonNull String userName) {
        String commitLogFile = helperService.listStudentCommitsByTime(projectID, userName);
        if(commitLogFile == null) {
            return new JSONReturnable(-1, null);
        }
        String pyPath = helperService.getPythonPath() + "get_git_commit_list.py";
        String command = helperService.getPythonCommand() + " " + pyPath + " " + commitLogFile + " " + userName;
        return helperService.runPython(command);
    }

    public JSONReturnable getStudentCommitVelocity(@NonNull String projectID, @NonNull String userName) {
        String dailyCountsFile = helperService.countStudentCommitsByDay(projectID, userName);
        String commitLogFile = helperService.listStudentCommitsByTime(projectID, userName);
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
        if (helperService.getDebug()) {
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

        String pyPath = helperService.getPythonPath() + "get_velocity.py";
        String command = helperService.getPythonCommand() + " " + pyPath + " " + visibleTestFile + " " + hiddenTestFile + " " + commitLogFile + " " + userName;
        return helperService.runPython(command);
    }

    public JSONReturnable getStudentProgress(@NonNull String projectID, @NonNull String userName) {
        String visibleTestFile;
        String hiddenTestFile;
        String dailyCountsFile;
        if (helperService.getDebug()) {
            visibleTestFile = "src/main//python/test_datasets/sampleTestsDay.txt";
            hiddenTestFile = "src/main/python/test_datasets/sampleTestsDay.txt";
            dailyCountsFile = "src/main/python/test_datasets/sampleCountsDay.txt";
        } else {
            dailyCountsFile = helperService.countStudentCommitsByDay(projectID, userName);
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

        String pyPath = helperService.getPythonPath() + "get_individual_progress.py";
        String command = helperService.getPythonCommand() + " " + pyPath + " " + visibleTestFile + " " + hiddenTestFile + " " + dailyCountsFile + " " + userName;
        return helperService.runPython(command);
    }

    public JSONReturnable getStudentStatistics(@NonNull String projectID, @NonNull String userName) {
        String dailyCountsFile = helperService.countStudentCommitsByDay(projectID, userName);
        String commitLogFile = helperService.listStudentCommitsByTime(projectID, userName);
        if(dailyCountsFile == null) {
            return new JSONReturnable(-1, null);
        }
        if(commitLogFile == null) {
            return new JSONReturnable(-2, null);
        }
        String testResult;
        if (helperService.getDebug()) {
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
        String pyPath = helperService.getPythonPath() + "get_statistics.py";
        String command = helperService.getPythonCommand() + " " + pyPath + " " + commitLogFile + " " + dailyCountsFile + " " + userName + " " + testResult + " -t 1.0 -l 200";
        return helperService.runPython(command);
    }

    public String getSourceWithChanges(@NonNull String projectID, @NonNull String userName, @NonNull String commitHash, @NonNull String sourceName) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
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
        if(helperService.executeBashScript("getSourceChanges.sh " + destPath + " " + fileName + " " + commitHash + " " + sourceName) < 0) {
            return null;
        }
        return fileName;
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
        StudentProject studentProject = studentProjectRepository.findByIdProjectIdentifierAndIdStudentID(projectID, student.getUserID());
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
            StudentProjectDate projectDate = studentProjectDateRepository.findByIdDateAndIdProjectIdentifierAndIdStudentID(date, projectID, student.getUserID());
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
                helperService.updateTestResults(visibleResult, studentProject.getStudentID(), studentProject.getProjectIdentifier(), false);
            }
            if(hiddenGrade > studentProject.getBestHiddenGrade()) {
                studentProject.setBestHiddenGrade(hiddenGrade);
                studentProject = studentProjectRepository.save(studentProject);
                helperService.updateTestResults(hiddenResult, studentProject.getStudentID(), studentProject.getProjectIdentifier(), true);
            }
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
            projectDate = studentProjectDateRepository.findByIdDateAndIdProjectIdentifierAndIdStudentID(date, projectID, student.getUserID());
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
                helperService.updateTestResults(visibleResult, studentProject.getStudentID(), studentProject.getProjectIdentifier(), false);
            }
            if(hiddenGrade > studentProject.getBestHiddenGrade()) {
                studentProject.setBestHiddenGrade(hiddenGrade);
                studentProject = studentProjectRepository.save(studentProject);
                helperService.updateTestResults(hiddenResult, studentProject.getStudentID(), studentProject.getProjectIdentifier(), true);
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
