package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.domain.relations.*;
import edu.purdue.cs.encourse.service.CourseService;
import edu.purdue.cs.encourse.service.helper.ServiceHelper;
import edu.purdue.cs.encourse.service.helper.StreamGobbler;
import edu.purdue.cs.encourse.util.JSONReturnable;
import lombok.NonNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;

@Service(value = CourseServiceImpl.NAME)
public class CourseServiceImpl implements CourseService {
    public final static String NAME = "CourseService";

    private final static ServiceHelper helper = ServiceHelper.getInstance();

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

    private int executeBashScript(@NonNull String command) {
        try {
            Process process = Runtime.getRuntime().exec("./src/main/bash/" + command);
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

    public List<Section> getSectionsBySemesterAndCourseID(@NonNull String semester, @NonNull String courseID) {
        return sectionRepository.findBySemesterAndCourseID(semester, courseID);

    }

    /** Mainly needed to populate the database when course hub already exists **/
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

    /** Creates a directory containing a directory for every student in the course. Each of those student directories
     will contain all of the cloned repositories that is being used to track git information **/
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

    /** Sets the location of the git repositories for every section under a particular courseID **/
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

    public JSONReturnable getProgress(@NonNull String projectID, List<String> userNames) {
        List<StudentProject> projects = helper.getStudentProjects(projectID, userNames);
        String visibleTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_visibleTests.txt";
        String hiddenTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_hiddenTests.txt";
        try {
            helper.createTestFiles(visibleTestFile, hiddenTestFile, projects);
        } catch (IOException e) {
            return new JSONReturnable(-1, null);
        }

        if (helper.DEBUG) {
            visibleTestFile = helper.pythonPath + "/test_datasets/sampleVisibleTestCases.txt";
            hiddenTestFile = helper.pythonPath + "/test_datasets/sampleHiddenTestCases.txt";
        }

        // TODO: Check that test results work as expected
        String pyPath = helper.pythonPath + "get_class_progress.py";
        String command = helper.pythonCommand + " " + pyPath + " " + visibleTestFile + " " + hiddenTestFile;
        JSONReturnable json = helper.runPython(command);
        return json;
    }

    public JSONReturnable getSimilar(@NonNull String projectID, List<String> userNames) {
        List<StudentProject> projects = helper.getStudentProjects(projectID, userNames);
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
        String pyPath = helper.pythonPath + "get_identical_count.py";
        String command = helper.pythonCommand + " " + pyPath + " " + diffsFile;
        JSONReturnable json = helper.runPython(command);
        return json;
    }

    public JSONReturnable getStatistics(@NonNull String projectID, List<String> userNames) {
        List<StudentProject> projects = helper.getStudentProjects(projectID, userNames);
        String dailyCountsFile = helper.countAllCommitsByDay(projectID, projects);
        String commitLogFile = helper.listAllCommitsByTime(projectID, projects);
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
        String pyPath = helper.pythonPath + "get_class_statistics.py";
        String command = helper.pythonCommand + " " + pyPath + " " + visibleTestFile + " " + hiddenTestFile + " " + commitLogFile + " -t 1.0 -l 200";
        JSONReturnable json = helper.runPython(command);
        return json;
    }

    public JSONReturnable getTestSummary(@NonNull String projectID, List<String> userNames) {
        List<StudentProject> projects = helper.getStudentProjects(projectID, userNames);
        String visibleTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_visibleTests.txt";
        String hiddenTestFile = "src/main/temp/" + Long.toString(Math.round(Math.random() * Long.MAX_VALUE)) + "_hiddenTests.txt";
        try {
            helper.createTestFiles(visibleTestFile, hiddenTestFile, projects);
        } catch (IOException e) {
            return new JSONReturnable(-1, null);
        }
        if (helper.DEBUG) {
            visibleTestFile = helper.pythonPath + "/test_datasets/sampleVisibleTestCases.txt";
            hiddenTestFile = helper.pythonPath + "/test_datasets/sampleHiddenTestCases.txt";
        }
        String pyPath = helper.pythonPath + "get_test_summary.py";
        String command = helper.pythonCommand + " " + pyPath + " " + visibleTestFile + " " + hiddenTestFile;
        JSONReturnable json = helper.runPython(command);
        return json;
    }

    public JSONReturnable getCommitList(@NonNull String projectID, List<String> userNames) {
        List<StudentProject> projects = helper.getStudentProjects(projectID, userNames);
        String commitLogFile = helper.listAllCommitsByTime(projectID, projects);
        if(commitLogFile == null) {
            return new JSONReturnable(-1, null);
        }
        String pyPath = helper.pythonPath + "get_git_commit_list.py";
        String command = helper.pythonCommand + " " + pyPath + " " + commitLogFile;
        JSONReturnable json = helper.runPython(command);
        return json;
    }

    public JSONReturnable getCheating(@NonNull String projectID, List<String> userNames) {
        List<StudentProject> projects = helper.getStudentProjects(projectID, userNames);
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return new JSONReturnable(-1, null);
        }
        List<Section> sections = sectionRepository.findBySemesterAndCourseID(project.getSemester(), project.getCourseID());
        if(sections.isEmpty()) {
            return new JSONReturnable(-2, null);
        }
        String commitLogFile = helper.listAllCommitsByTime(projectID, projects);
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

        if (helper.DEBUG){
            commitLogFile = helper.pythonPath + "/test_datasets/sampleCommitList.txt";
            visibleTestFile = helper.pythonPath + "/test_datasets/sampleTestsDay.txt";
            hiddenTestFile = helper.pythonPath + "/test_datasets/sampleTestsDay.txt";
        }
        String pyPath = helper.pythonPath + "get_class_cheating.py";
        String command = helper.pythonCommand + " " + pyPath + " " + visibleTestFile + " " + hiddenTestFile + " " + commitLogFile + " -l 1000";
        JSONReturnable json = helper.runPython(command);
        return json;
    }

    public JSONArray sortStudentData(@NonNull JSONArray studentsJSON, @NonNull List<String> parameters, @NonNull List<Boolean> isAscending) {
        JSONArray sorted = new JSONArray();
        List<JSONObject> jsonList = new ArrayList<>();
        for(Object obj : studentsJSON) {
            jsonList.add((JSONObject)obj);
        }
        for(int i = 0; i < parameters.size(); i++) {
            switch(parameters.get(i)) {
                case "first_name": {
                    if(isAscending.get(i)) {
                        jsonList.sort(new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject a, JSONObject b) {
                                String valA = ((String) a.get("first_name")).toLowerCase();
                                String valB = ((String) b.get("first_name")).toLowerCase();
                                return valA.compareTo(valB);
                            }
                        });
                    }
                    else {
                        jsonList.sort(new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject a, JSONObject b) {
                                String valA = ((String) a.get("first_name")).toLowerCase();
                                String valB = ((String) b.get("first_name")).toLowerCase();
                                return valB.compareTo(valA);
                            }
                        });
                    }
                    break;
                }
                case "last_name": {
                    if(isAscending.get(i)) {
                        jsonList.sort(new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject a, JSONObject b) {
                                String valA = ((String) a.get("last_name")).toLowerCase();
                                String valB = ((String) b.get("last_name")).toLowerCase();
                                return valA.compareTo(valB);
                            }
                        });
                    }
                    else {
                        jsonList.sort(new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject a, JSONObject b) {
                                String valA = ((String) a.get("last_name")).toLowerCase();
                                String valB = ((String) b.get("last_name")).toLowerCase();
                                return valB.compareTo(valA);
                            }
                        });
                    }
                    break;
                }
                case "id": {
                    if(isAscending.get(i)) {
                        jsonList.sort(new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject a, JSONObject b) {
                                String valA = ((String) a.get("id")).toLowerCase();
                                String valB = ((String) b.get("id")).toLowerCase();
                                return valA.compareTo(valB);
                            }
                        });
                    }
                    else {
                        jsonList.sort(new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject a, JSONObject b) {
                                String valA = ((String) a.get("id")).toLowerCase();
                                String valB = ((String) b.get("id")).toLowerCase();
                                return valB.compareTo(valA);
                            }
                        });
                    }
                    break;
                }
                case "grades": {
                    if(isAscending.get(i)) {
                        jsonList.sort(new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject a, JSONObject b) {
                                Double valA = (Double) a.get("grades");
                                Double valB = (Double) b.get("grades");
                                return valA.compareTo(valB);
                            }
                        });
                    }
                    else {
                        jsonList.sort(new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject a, JSONObject b) {
                                Double valA = (Double) a.get("grades");
                                Double valB = (Double) b.get("grades");
                                return valB.compareTo(valA);
                            }
                        });
                    }
                    break;
                }
                case "hiddenGrades": {
                    if(isAscending.get(i)) {
                        jsonList.sort(new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject a, JSONObject b) {
                                Double valA = (Double) a.get("hiddenGrades");
                                Double valB = (Double) b.get("hiddenGrades");
                                return valA.compareTo(valB);
                            }
                        });
                    }
                    else {
                        jsonList.sort(new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject a, JSONObject b) {
                                Double valA = (Double) a.get("hiddenGrades");
                                Double valB = (Double) b.get("hiddenGrades");
                                return valB.compareTo(valA);
                            }
                        });
                    }
                    break;
                }
                case "commitCounts": {
                    if(isAscending.get(i)) {
                        jsonList.sort(new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject a, JSONObject b) {
                                Integer valA = (Integer) a.get("commitCounts");
                                Integer valB = (Integer) b.get("commitCounts");
                                return valA.compareTo(valB);
                            }
                        });
                    }
                    else {
                        jsonList.sort(new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject a, JSONObject b) {
                                Integer valA = (Integer) a.get("commitCounts");
                                Integer valB = (Integer) b.get("commitCounts");
                                return valB.compareTo(valA);
                            }
                        });
                    }
                    break;
                }
                case "timeSpent": {
                    if(isAscending.get(i)) {
                        jsonList.sort(new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject a, JSONObject b) {
                                Double valA = (Double) a.get("timeSpent");
                                Double valB = (Double) b.get("timeSpent");
                                return valA.compareTo(valB);
                            }
                        });
                    }
                    else {
                        jsonList.sort(new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject a, JSONObject b) {
                                Double valA = (Double) a.get("timeSpent");
                                Double valB = (Double) b.get("timeSpent");
                                return valB.compareTo(valA);
                            }
                        });
                    }
                    break;
                }
                default: break;
            }
        }
        sorted.addAll(jsonList);
        return sorted;
    }

    public JSONArray filterStudentData(@NonNull JSONArray studentsJSON, @NonNull List<String> parameters, @NonNull List<List<String>> values) {
        JSONArray filtered = new JSONArray();
        List<JSONObject> jsonList = new ArrayList<>();
        for(Object obj : studentsJSON) {
            jsonList.add((JSONObject)obj);
        }
        for(int i = 0; i < parameters.size(); i++) {
            List<JSONObject> tempList = new ArrayList<>();
            for(int j = 0; j < values.get(i).size(); j++) {
                for(JSONObject obj : jsonList) {
                    if(obj.get(parameters.get(i)).equals(values.get(i).get(j))) {
                        tempList.add(obj);
                    }
                }
            }
            jsonList = tempList;
        }
        filtered.addAll(jsonList);
        return filtered;
    }

    /** Retrieves basic data for all projects in the course **/
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

    public Project getProject(@NonNull String projectID) {
        return projectRepository.findByProjectIdentifier(projectID);
    }
}
