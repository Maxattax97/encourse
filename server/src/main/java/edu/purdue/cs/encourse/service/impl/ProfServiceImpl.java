package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.domain.relations.*;
import edu.purdue.cs.encourse.service.ProfService;
import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

@Service(value = ProfServiceImpl.NAME)
public class ProfServiceImpl implements ProfService {

    public final static String NAME = "ProfService";

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

    /** Mainly needed to populate the database when course hub already exists **/
    public int setHub(String courseID){
        List<Section> sections = sectionRepository.findByCourseID(courseID);
        if(sections.isEmpty()) {
            return -1;
        }
        for(Section s : sections) {
            s.setCourseHub("/sourcecontrol/" + sections.get(0).getCourseID() + "/" + sections.get(0).getSemester());
            if(sectionRepository.save(s) == null) {
                return -2;
            }
        }
        return 0;
    }

    /** Creates a directory containing a directory for every student in the course. Each of those student directories
        will contain all of the cloned repositories that is being used to track git information **/
    // TODO: Add Semester as a parameter
    public int createHub(String courseID){
        List<Section> sections = sectionRepository.findByCourseID(courseID);
        if(sections.isEmpty()) {
            return -1;
        }
        if(!(new File("/sourcecontrol/" + sections.get(0).getCourseID() + "/" + sections.get(0).getSemester()).mkdirs())) {
            return -2;
        }
        for(Section s : sections) {
            s.setCourseHub("/sourcecontrol/" + sections.get(0).getCourseID() + "/" + sections.get(0).getSemester());
            if(sectionRepository.save(s) == null) {
                return -3;
            }
            List<StudentSection> assignments =
                    studentSectionRepository.findByIdSectionIdentifier(s.getSectionIdentifier());
            for(StudentSection a : assignments){
                Student student = studentRepository.findByUserID(a.getStudentID());
                new File(sections.get(0).getCourseHub() + "/" + student.getUserName()).mkdir();
            }
        }
        return 0;
    }

    /** Runs a bash script to initially clone every student's git repository. Each university should supply its own
        bash script, since repo locations will vary **/
    // TODO: Add Semester as a parameter
    public int cloneProjects(String courseID, String projectID){
        List<Section> sections = sectionRepository.findByCourseID(courseID);
        if(sections.isEmpty()) {
            return -1;
        }
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
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
        for(Section s : sections){
            List<StudentSection> assignments =
                    studentSectionRepository.findByIdSectionIdentifier(s.getSectionIdentifier());
            for(StudentSection a : assignments){
                Student student = studentRepository.findByUserID(a.getStudentID());
                if(!(new File(s.getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName()).exists())) {
                    String destPath = (s.getCourseHub() + "/" + student.getUserName());
                    String repoPath = (s.getRemotePath() + "/" + student.getUserName() + "/" + project.getRepoName() + ".git");
                    try {
                        Process p = Runtime.getRuntime().exec("./src/main/bash/cloneRepositories.sh " + destPath + " " + repoPath);
                        StreamGobbler streamGobbler = new StreamGobbler(p.getInputStream(), System.out::println);
                        Executors.newSingleThreadExecutor().submit(streamGobbler);
                        p.waitFor();

                    }
                    catch(Exception e) {
                        return -6;
                    }
                }
            }
        }
        try {
            Process p = Runtime.getRuntime().exec("./src/main/bash/setPermissions.sh " + sections.get(0).getCourseID());
            StreamGobbler streamGobbler = new StreamGobbler(p.getInputStream(), System.out::println);
            Executors.newSingleThreadExecutor().submit(streamGobbler);
            p.waitFor();
        }
        catch(Exception e) {
            return -7;
        }
        return 0;
    }

    /** Pulls the designated project within every students directory under the course hub **/
    // TODO: Add Semester as a parameter
    public int pullProjects(String courseID, String projectID){
        List<Section> sections = sectionRepository.findByCourseID(courseID);
        if(sections.isEmpty()) {
            return -1;
        }
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return -2;
        }
        if(sections.get(0).getCourseHub() == null) {
            return -3;
        }
        if(project.getRepoName() == null) {
            return -4;
        }
        List<String> completedStudents = new ArrayList<>();
        for(Section s : sections){
            List<StudentSection> assignments =
                    studentSectionRepository.findByIdSectionIdentifier(s.getSectionIdentifier());
            for(StudentSection a : assignments) {
                Student student = studentRepository.findByUserID(a.getStudentID());
                if(!(completedStudents.contains(student.getUserName()))) {
                    String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
                    try {
                        Process p = Runtime.getRuntime().exec("./src/main/bash/pullRepositories.sh " + destPath);
                        StreamGobbler streamGobbler = new StreamGobbler(p.getInputStream(), System.out::println);
                        Executors.newSingleThreadExecutor().submit(streamGobbler);
                        p.waitFor();
                    }
                    catch(Exception e) {
                        return -5;
                    }
                    completedStudents.add(student.getUserName());
                }
            }
        }
        try {
            Process p = Runtime.getRuntime().exec("./src/main/bash/setPermissions.sh " + sections.get(0).getCourseID());
            StreamGobbler streamGobbler = new StreamGobbler(p.getInputStream(), System.out::println);
            Executors.newSingleThreadExecutor().submit(streamGobbler);
            p.waitFor();
        }
        catch(Exception e) {
            return -6;
        }
        return 0;
    }

    /** Adds a new project to the database, which needs to be done before cloning the project in the course hub **/
    public int addProject(String courseID, String semester, String projectName, String repoName, String startDate, String dueDate) {
        Project project = new Project(courseID, semester, projectName, repoName, startDate, dueDate);
        if(projectRepository.existsByProjectIdentifier(project.getProjectIdentifier())) {
            return -1;
        }
        if(projectRepository.save(project) == null) {
            return -2;
        }
        return 0;
    }

    /** Modifies project information like start and end dates **/
    public int modifyProject(String projectID, String field, String value) {
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

    /** Sets the location of the git repositories for every section under a particular courseID **/
    public int setSectionRemotePaths(String courseID, String remotePath) {
        List<Section> sections = sectionRepository.findByCourseID(courseID);
        if(sections.isEmpty()) {
            return -1;
        }
        for(Section s : sections) {
            s.setRemotePath(remotePath);
            if(sectionRepository.save(s) == null) {
                return -2;
            }
        }
        return 0;
    }

    /** Counts the number of commits that every student in the class has made for a project **/
    public int countAllCommits(String courseID, String projectID) {
        List<Section> sections = sectionRepository.findByCourseID(courseID);
        if(sections.isEmpty()) {
            return -1;
        }
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return -2;
        }
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifier(projectID);
        String fileName = Long.toString(Math.round(Math.random() * 1000000));
        for(StudentProject s : projects) {
            Student student = studentRepository.findByUserID(s.getStudentID());
            String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
            try {
                Process p = Runtime.getRuntime().exec("./src/main/bash/countCommits.sh " + destPath + " " + fileName + " " + student.getUserName());
                StreamGobbler streamGobbler = new StreamGobbler(p.getInputStream(), System.out::println);
                Executors.newSingleThreadExecutor().submit(streamGobbler);
                p.waitFor();

            }
            catch(Exception e) {
                return -3;
            }
        }

        // TODO: Call and receive input from python script

        return 0;
    }

    /** Counts the total number of commits made each day that the project was active **/
    public int countAllCommitsByDay(String courseID, String projectID) {
        List<Section> sections = sectionRepository.findByCourseID(courseID);
        if(sections.isEmpty()) {
            return -1;
        }
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return -2;
        }
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifier(projectID);
        String fileName = Long.toString(Math.round(Math.random() * 1000000));
        for(StudentProject s : projects) {
            Student student = studentRepository.findByUserID(s.getStudentID());
            String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
            try {
                Process p = Runtime.getRuntime().exec("./src/main/bash/countCommitsByDay.sh " + destPath + " " + fileName + " " + student.getUserName());
                StreamGobbler streamGobbler = new StreamGobbler(p.getInputStream(), System.out::println);
                Executors.newSingleThreadExecutor().submit(streamGobbler);
                p.waitFor();

            }
            catch(Exception e) {
                return -3;
            }
        }

        // TODO: Call and receive input from python script

        return 0;
    }

    /** Counts the number of commits that a single student has made for each day that the project is active **/
    public int countStudentCommitsByDay(String courseID, String projectID, String userName) {
        List<Section> sections = sectionRepository.findByCourseID(courseID);
        if(sections.isEmpty()) {
            return -1;
        }
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return -2;
        }
        Student student = studentRepository.findByUserID(userName);
        if(student == null) {
            return -3;
        }
        String fileName = Long.toString(Math.round(Math.random() * 1000000));
        String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
        try {
            Process p = Runtime.getRuntime().exec("./src/main/bash/countCommitsByDay.sh " + destPath + " " + fileName + " " + student.getUserName());
            StreamGobbler streamGobbler = new StreamGobbler(p.getInputStream(), System.out::println);
            Executors.newSingleThreadExecutor().submit(streamGobbler);
            p.waitFor();

        }
        catch(Exception e) {
            return -4;
        }

        // TODO: Call and receive input from python script

        return 0;
    }

    /** Lists various information about git history, including commit time and dates, and files modified in each commit **/
    public int listStudentCommitsByTime(String courseID, String projectID, String userName) {
        List<Section> sections = sectionRepository.findByCourseID(courseID);
        if(sections.isEmpty()) {
            return -1;
        }
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return -2;
        }
        Student student = studentRepository.findByUserID(userName);
        if(student == null) {
            return -3;
        }
        String fileName = Long.toString(Math.round(Math.random() * 1000000));
        String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
        try {
            Process p = Runtime.getRuntime().exec("./src/main/bash/listCommitsByTime.sh " + destPath + " " + fileName + " " + student.getUserName());
            StreamGobbler streamGobbler = new StreamGobbler(p.getInputStream(), System.out::println);
            Executors.newSingleThreadExecutor().submit(streamGobbler);
            p.waitFor();

        }
        catch(Exception e) {
            return -4;
        }

        // TODO: Call and receive input from python script

        return 0;
    }

    /** Makes a new assignment between teaching assistant and student. Can have multiple students assigned to same TA
        or multiple TAs assigned to the same student **/
    public int assignTeachingAssistantToStudent(String teachAssistUserName, String studentUserName) {
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

