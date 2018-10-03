package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.domain.relations.*;
import edu.purdue.cs.encourse.service.ProfessorService;
import edu.purdue.cs.encourse.util.JSONReturnable;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import lombok.NonNull;

@Service(value = ProfessorServiceImpl.NAME)
public class ProfessorServiceImpl implements ProfessorService {

    public final static String NAME = "ProfessorService";
    private final static String pythonPath = "src/main/python/";
    private final static int RATE = 3600000;

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

    private int executeBashScript(@NonNull String command) {
        try {
            Process process = Runtime.getRuntime().exec("./src/main/bash/" + command);
            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
            Executors.newSingleThreadExecutor().submit(streamGobbler);
            process.waitFor();
        }
        catch(Exception e) {
            return -1;
        }
        return 0;
    }

    private boolean isTakingCourse(Student student, Project project) {
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

    private double parseProgressForProject(String testOutput) {
        String[] testResults = testOutput.split(" ");
        double passedCount = 0.0;
        for(String r : testResults) {
            if(r.endsWith("P")) {
                passedCount++;
            }
        }
        return passedCount / testResults.length;
    }

    /** Adds a new project to the database, which needs to be done before cloning the project in the course hub **/
    public int addProject(@NonNull String courseID, @NonNull String semester, @NonNull String projectName, String repoName, String startDate, String dueDate) {
        Project project = new Project(courseID, semester, projectName, repoName, startDate, dueDate);
        if(projectRepository.existsByProjectIdentifier(project.getProjectIdentifier())) {
            return -1;
        }
        if(projectRepository.save(project) == null) {
            return -2;
        }
        return 0;
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

    /** Modifies project information like start and end dates **/
    public int modifyProject(@NonNull String projectID, @NonNull String field, String value) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return -1;
        }
        switch(field) {
            case "startDate": project.setStartDate(value); break;
            case "dueDate": project.setDueDate(value); break;
            case "repoName": project.setRepoName(value); break;
            default: return -2;
        }
        if(projectRepository.save(project) == null) {
            return -3;
        }
        return 0;
    }

    private Project getProject(@NonNull String projectID) {
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
        String dailyCountsFile = countStudentCommitsByDay(projectID, userName);
        String commitLogFile = listStudentCommitsByTime(projectID, userName);
        if(dailyCountsFile == null) {
            return new JSONReturnable(-1, null);
        }
        if(commitLogFile == null) {
            return new JSONReturnable(-2, null);
        }

        /** python executable.py dailyCountsFile commitLogFile userName **/
        JSONReturnable json = null;
        String pyPath = pythonPath + "get_individual_progress.py";
        try {
            Process process = Runtime.getRuntime().exec("python " + pyPath + " " + dailyCountsFile + " " + commitLogFile + " " + userName);
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
            json =  new JSONReturnable(-1, null);
        } catch (IOException e) {
            e.printStackTrace();
            json =  new JSONReturnable(-2, null);
        }

        executeBashScript("cleanDirectory.sh src/main/temp");
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

        /** python executable.py commitLogFile dailyCountsFile userName **/
        JSONReturnable json = null;
        String pyPath = pythonPath + "get_add_del.py";
        try {
            Process process = Runtime.getRuntime().exec("python " + pyPath + " " + commitLogFile + " " + dailyCountsFile + " " + userName);
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
            json =  new JSONReturnable(-1, null);
        } catch (IOException e) {
            e.printStackTrace();
            json =  new JSONReturnable(-2, null);
        }

        executeBashScript("cleanDirectory.sh src/main/temp");
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
        Student student = studentRepository.findByUserName(userName);
        StudentProject project = studentProjectRepository.findByIdProjectIdentifierAndIdStudentID(projectID, student.getUserID());
        String testResult = project.getBestGrade();

        /** python executable.py commitLogFile dailyCountsFile project.getBestGrade() userName **/
        JSONReturnable json = null;
        String pyPath = pythonPath + "get_statistics.py";
        try {
            Process process = Runtime.getRuntime().exec("python " + pyPath + " " + dailyCountsFile + " " + commitLogFile + " " + userName);
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
                        jsonObject.get("");
                    } else {
                        json = new JSONReturnable(-4, null);
                    }
                    json = new JSONReturnable(1, jsonObject);
                }
            }
            json =  new JSONReturnable(-1, null);
        } catch (IOException e) {
            e.printStackTrace();
            json =  new JSONReturnable(-2, null);
        }
        // TODO: JARDON
        executeBashScript("cleanDirectory.sh src/main/temp");
        return json;
    }

    public JSONReturnable getCommitList(@NonNull String projectID, @NonNull String userName) {
        String commitLogFile = listStudentCommitsByTime(projectID, userName);
        if(commitLogFile == null) {
            return new JSONReturnable(-1, null);
        }

        /** python executable.py commitLogFile userName **/
        JSONReturnable json = null;
        String pyPath = pythonPath + "get_get_commit_list.py";
        try {
            Process process = Runtime.getRuntime().exec("python " + pyPath + " " + commitLogFile + " " + userName);
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
            json =  new JSONReturnable(-1, null);
        } catch (IOException e) {
            e.printStackTrace();
            json =  new JSONReturnable(-2, null);
        }

        executeBashScript("cleanDirectory.sh src/main/temp");
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
        String fileName = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + ".txt";
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
        String fileName = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + ".txt";
        for(StudentProject s : projects) {
            Student student = studentRepository.findByUserID(s.getStudentID());
            String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
            executeBashScript("countCommitsByDay.sh " + destPath + " " + fileName + " " + student.getUserName());
        }
        return fileName;
    }

    /** Counts the number of commits that a single student has made for each day that the project is active **/
    public String countStudentCommitsByDay(@NonNull String projectID, @NonNull String userName) {
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
        String fileName = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + ".txt";
        String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
        if(executeBashScript("countCommitsByDay.sh " + destPath + " " + fileName + " " + student.getUserName()) == -1) {
            return null;
        }
        return fileName;
    }

    /** Lists various information about git history, including commit time and dates, and files modified in each commit for all students **/
    public String listAllCommitsByTime(@NonNull String projectID) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return null;
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return null;
        }
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifier(projectID);
        String fileName = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + ".txt";
        for(StudentProject s : projects) {
            Student student = studentRepository.findByUserID(s.getStudentID());
            String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
            executeBashScript("listCommitsByTime.sh " + destPath + " " + fileName + " " + student.getUserName());
        }
        return fileName;
    }

    /** Lists various information about git history, including commit time and dates, and files modified in each commit for one student **/
    public String listStudentCommitsByTime(@NonNull String projectID, @NonNull String userName) {
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
        String fileName = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + ".txt";
        String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
        if(executeBashScript("listCommitsByTime.sh " + destPath + " " + fileName + " " + student.getUserName()) == -1) {
            return null;
        }
        return fileName;
    }

    /** Uploads a testing script to testcases directory in the course hub **/
    public int uploadTestScript(@NonNull String projectID, @NonNull String testName, @NonNull String testContents) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return -1;
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return -2;
        }
        new File(sections.get(0).getCourseHub() + "/testcases/" + project.getRepoName()).mkdirs();
        String filePath = sections.get(0).getCourseHub() + "/testcases/" + project.getRepoName() + "/" + testName;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(testContents);
            writer.close();
        }
        catch(IOException e) {
            return -3;
        }
        if(executeBashScript("setPermissions.sh " + sections.get(0).getCourseID()) == -1) {
            return -4;
        }
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
        File directory = new File(testCaseDirectory);
        if(!directory.isDirectory() || directory.listFiles().length == 0) {
            return -3;
        }
        String date = LocalDate.now().toString();
        int code = 0;
        for(StudentProject p : projects) {
            Student student = studentRepository.findByUserID(p.getStudentID());
            List<StudentProjectDate> projectDates = studentProjectDateRepository.findByIdDateAndIdStudentID(date, p.getStudentID());
            String testingDirectory = sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName() + "/" + testDir;
            try {
                Process process = Runtime.getRuntime().exec("./src/main/bash/testall.sh " + testingDirectory + " " + testCaseDirectory);
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String result = stdInput.readLine();
                double grade = parseProgressForProject(result);
                boolean exists = false;
                for(StudentProjectDate d : projectDates) {
                    if(d.getProjectIdentifier().equals(p.getProjectIdentifier())) {
                        if(grade > parseProgressForProject(d.getDateGrade())) {
                            d.setDateGrade(result);
                            studentProjectDateRepository.save(d);
                        }
                        exists = true;
                        break;
                    }
                }
                if(!exists) {
                    StudentProjectDate projectDate = new StudentProjectDate(p.getStudentID(), p.getProjectIdentifier(), date, result);
                    studentProjectDateRepository.save(projectDate);
                }
                if(p.getBestGrade() == null || grade > parseProgressForProject(p.getBestGrade())) {
                    p.setBestGrade(result);
                    studentProjectRepository.save(p);
                }
            }
            catch(Exception e) {
                code = -4;
            }
        }
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
        String date = LocalDate.now().toString();
        List<StudentProject> projects = studentProjectRepository.findByIdStudentID(student.getUserID());
        boolean hasProject = false;
        for(StudentProject p : projects) {
            if(p.getProjectIdentifier().equals(projectID)) {
                List<StudentProjectDate> projectDates = studentProjectDateRepository.findByIdDateAndIdStudentID(date, p.getStudentID());
                String testingDirectory = sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName() + "/" + testDir;
                String testCaseDirectory = sections.get(0).getCourseHub() + "/testcases/" + project.getRepoName();
                try {
                    Process process = Runtime.getRuntime().exec("./src/main/bash/testall.sh " + testingDirectory + " " + testCaseDirectory);
                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String result = stdInput.readLine();
                    double grade = parseProgressForProject(result);
                    boolean exists = false;
                    for(StudentProjectDate d : projectDates) {
                        if(d.getProjectIdentifier().equals(p.getProjectIdentifier())) {
                            if(grade > parseProgressForProject(d.getDateGrade())) {
                                d.setDateGrade(result);
                                studentProjectDateRepository.save(d);
                            }
                            exists = true;
                            break;
                        }
                    }
                    if(!exists) {
                        StudentProjectDate projectDate = new StudentProjectDate(p.getStudentID(), p.getProjectIdentifier(), date, result);
                        studentProjectDateRepository.save(projectDate);
                    }
                    if(grade > parseProgressForProject(p.getBestGrade())) {
                        p.setBestGrade(result);
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
    }

    /** Pulls and tests every project in the database on one hour intervals **/
    @Scheduled(fixedRate = RATE)
    public int pullAndTestAllProjects() {
        int code = 0;
        for(Project project : projectRepository.findAll()) {
            if(pullProjects(project.getProjectIdentifier()) < 0) {
                code -= 1;
            }
            if(runTestall(project.getProjectIdentifier()) < 0) {
                code -= 1;
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
            Process process = Runtime.getRuntime().exec("python " + filePath + " " + dataFilePath);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String input = null;
            String error = null;
            while ((error = stdError.readLine()) != null) {
                System.out.println(error);
            }
            while ((input = stdInput.readLine()) != null) {
                System.out.println(input);
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

    public JSONReturnable getCommitData(@NonNull String filePath) {
        String pyPath = pythonPath + "getStatistics.py";
        try {
            Process process = Runtime.getRuntime().exec("python " + pyPath + " " + filePath + " " + filePath);
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
                    return new JSONReturnable(-3, null);
                }
                if (obj != null) {
                    System.out.println(obj);
                    JSONObject jsonObject = (JSONObject)obj;
                    JSONReturnable jsonReturn = new JSONReturnable(1, jsonObject);
                    //System.out.println(jsonObject.toString());
                    return jsonReturn;
                }
            }
            return new JSONReturnable(-1, null);
        } catch (IOException e) {
            e.printStackTrace();
            return new JSONReturnable(-2, null);
        }
    }

    public JSONReturnable getProgressHistogram(@NonNull String filePath, @NonNull String userName) {
        String pyPath = pythonPath + "getProgressHistogram.py";
        try {
            Process process = Runtime.getRuntime().exec("python " +  pyPath + " " + userName + " " + filePath);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String input = null;
            String error = null;
            /*while ((error = stdError.readLine()) != null) {
                System.out.println(error);
            }*/
            while ((input = stdInput.readLine()) != null) {
                System.out.println("INPUT: " + input);
                JSONParser jsonParser = new JSONParser();
                Object obj = null;
                try {
                    obj = jsonParser.parse(input);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return new JSONReturnable(-3, null);
                }
                if (obj != null) {
                    JSONObject jsonObject = null;
                    if (obj.getClass() == JSONObject.class) {
                        jsonObject = (JSONObject)obj;
                    } else if (obj.getClass() == JSONArray.class) {
                        jsonObject = new JSONObject();
                        JSONArray jsonArray = (JSONArray)obj;
                        jsonObject.put(userName, jsonArray);
                    } else {
                        return new JSONReturnable(-4, null);
                    }
                    JSONReturnable jsonReturn = new JSONReturnable(1, jsonObject);
                    //System.out.println(jsonObject.toString());
                    System.out.println("JSONRETURN OBJECT: " + jsonReturn.jsonObject.toJSONString());
                    return jsonReturn;
                }
            }
            return new JSONReturnable(-1, null);
        } catch (IOException e){
            e.printStackTrace();
            return new JSONReturnable(-2, null);
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
}


