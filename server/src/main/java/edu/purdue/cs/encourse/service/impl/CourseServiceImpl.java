package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.domain.Section;
import edu.purdue.cs.encourse.domain.Student;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;

@Service(value = CourseServiceImpl.NAME)
public class CourseServiceImpl implements CourseService {
    public final static String NAME = "CourseService";

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeachingAssistantRepository teachingAssistantRepository;

    @Autowired
    private ProjectRepository projectRepository;

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

    private Map<String, Double> parseProgressForProjects(List<StudentProject> projects) {
        Map<String, Double> grades = new TreeMap<>();
        int code = 0;
        for(StudentProject p : projects) {
            if(p.getBestGrade() == null) {
                code--;
                grades.put(p.getProjectIdentifier(), 0.0);
                continue;
            }
            String[] testResults = p.getBestGrade().split(" ");
            double passedCount = 0.0;
            for(String r : testResults) {
                if(r.endsWith("P")) {
                    passedCount++;
                }
            }
            grades.put(p.getProjectIdentifier(), passedCount / testResults.length);
        }
        return grades;
    }

    public List<Section> getSectionsBySemesterAndCourseID(@NonNull String semester, @NonNull String courseID) {
        return sectionRepository.findBySemesterAndCourseID(semester, courseID);

    }

    /** Mainly needed to populate the database when course hub already exists **/
    public int setDirectory(@NonNull String semester, @NonNull String courseID){
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
                    Map<String, Double> grades = parseProgressForProjects(studentProjects);
                    Map<String, Integer> commitCounts = new TreeMap<>();
                    Map<String, Double> timeSpent = new TreeMap<>();
                    for(StudentProject p : studentProjects) {
                        commitCounts.put(p.getProjectIdentifier(), p.getCommitCount());
                        timeSpent.put(p.getProjectIdentifier(), p.getTotalTimeSpent());
                    }
                    JSONObject studentJSON = new JSONObject();
                    studentJSON.put("first_name", student.getFirstName());
                    studentJSON.put("last_name", student.getLastName());
                    studentJSON.put("id", student.getUserName());
                    studentJSON.put("grades", grades);
                    studentJSON.put("commitCounts", commitCounts);
                    studentJSON.put("timeSpend", timeSpent);
                    studentsJSON.add(studentJSON);
                }
            }
        }
        return studentsJSON;
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
            projectJSON.put("project_name", p.getProjectName());
            projectJSON.put("source_name", p.getRepoName());
            projectJSON.put("start_date", p.getStartDate());
            projectJSON.put("due_date", p.getDueDate());
            projectJSON.put("id", p.getProjectIdentifier());
            projectsJSON.add(projectJSON);
        }
        return projectsJSON;
    }
}