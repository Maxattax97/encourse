package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.Professor;
import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.domain.Section;
import edu.purdue.cs.encourse.domain.Student;
import edu.purdue.cs.encourse.domain.relations.ProfessorCourse;
import edu.purdue.cs.encourse.domain.relations.ProjectTestScript;
import edu.purdue.cs.encourse.domain.relations.StudentProject;
import edu.purdue.cs.encourse.domain.relations.StudentSection;
import edu.purdue.cs.encourse.service.CourseService;
import lombok.NonNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.concurrent.Executors;

@Service(value = CourseServiceImpl.NAME)
public class CourseServiceImpl implements CourseService {
    public final static String NAME = "CourseService";
    public final static Boolean OBFUSCATE = false;

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
                    JSONObject studentJSON = new JSONObject();
                    studentJSON.put("first_name", student.getFirstName());
                    studentJSON.put("last_name", student.getLastName());
                    studentJSON.put("id", student.getUserName());
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
            projectsJSON.add(projectJSON);
        }
        return projectsJSON;
    }
}
