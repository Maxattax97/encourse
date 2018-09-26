package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.domain.relations.*;
import edu.purdue.cs.encourse.service.ProfService;
import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service(value = ProfServiceImpl.NAME)
public class ProfServiceImpl implements ProfService {

    public final static String NAME = "ProfService";
    private final static String pythonPath = "./src/main/java/edu/purdue/cs/encourse/service/impl/python/";

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
        ProcessBuilder builder = new ProcessBuilder();
        for(Section s : sections){
            List<StudentSection> assignments =
                    studentSectionRepository.findByIdSectionIdentifier(s.getSectionIdentifier());
            for(StudentSection a : assignments){
                Student student = studentRepository.findByUserID(a.getStudentID());
                if(!(new File(s.getCourseHub() + "/" + student.getUserName()).exists())) {
                    String destPath = (s.getCourseHub() + "/" + student.getUserName());
                    String repoPath = (s.getRemotePath() + "/" + student.getUserName() + "/" + project.getRepoName() + ".git");
                    builder.command("bash/cloneProject.sh", destPath, repoPath);
                }
            }
        }
        builder.command("bash/setPermissions.sh", sections.get(0).getCourseID());
        return 0;
    }

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
        ProcessBuilder builder = new ProcessBuilder();
        List<String> completedStudents = new ArrayList<>();
        for(Section s : sections){
            List<StudentSection> assignments =
                    studentSectionRepository.findByIdSectionIdentifier(s.getSectionIdentifier());
            for(StudentSection a : assignments) {
                Student student = studentRepository.findByUserID(a.getStudentID());
                if(!(completedStudents.contains(student.getUserName()))) {
                    String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
                    builder.command("bash/pullProject.sh", destPath);
                    completedStudents.add(student.getUserName());
                }
            }
        }
        builder.command("bash/setPermissions.sh", sections.get(0).getCourseID());
        return 0;
    }

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
        return 0;
    }

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

    public int countAllCommits(String courseID, String projectID) {
        List<Section> sections = sectionRepository.findByCourseID(courseID);
        if(sections.isEmpty()) {
            return -1;
        }
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return -2;
        }
        ProcessBuilder builder = new ProcessBuilder();
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifier(projectID);
        String fileName = Long.toString(Math.round(Math.random() * 1000000));
        for(StudentProject p : projects) {
            Student student = studentRepository.findByUserID(p.getStudentID());
            String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
            builder.command("bash/countCommits.sh", destPath, fileName, student.getUserName());
        }

        // TODO: Call and receive input from python script


        return 0;
    }

    public int countAllCommitsByDay(String courseID, String projectID) {
        List<Section> sections = sectionRepository.findByCourseID(courseID);
        if(sections.isEmpty()) {
            return -1;
        }
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return -2;
        }
        ProcessBuilder builder = new ProcessBuilder();
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifier(projectID);
        String fileName = Long.toString(Math.round(Math.random() * 1000000));
        for(StudentProject p : projects) {
            Student student = studentRepository.findByUserID(p.getStudentID());
            String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
            builder.command("bash/countCommitsByDay.sh", destPath, fileName, student.getUserName());
        }

        // TODO: Call and receive input from python script

        return 0;
    }

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
        ProcessBuilder builder = new ProcessBuilder();
        String fileName = Long.toString(Math.round(Math.random() * 1000000));
        String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
        builder.command("bash/countCommitsByDay.sh", destPath, fileName, student.getUserName());

        // TODO: Call and receive input from python script

        return 0;
    }

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
        ProcessBuilder builder = new ProcessBuilder();
        String fileName = Long.toString(Math.round(Math.random() * 1000000));
        String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
        builder.command("bash/listCommitsByTime.sh", destPath, fileName, student.getUserName());

        // TODO: Call and receive input from python script

        return 0;
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

    public int getCommitData() {
        String filePath = pythonPath + "getStatistics.py";
        String timeFilePath = pythonPath + "sampleCountsDay.txt";
        String countFilePath = pythonPath + "sampleCounts.txt";
        try {
            Process process = Runtime.getRuntime().exec("python " + filePath + " " + timeFilePath + " " + countFilePath);
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
                    return -3;
                }
                if (obj != null) {
                    System.out.println(obj);
                    JSONObject jsonObject = (JSONObject)obj;
                    //System.out.println(jsonObject.toString());
                    return 1;
                }
            }
            return -1;
        } catch (IOException e) {
                e.printStackTrace();
                return -2;
        }
    }

    public int getProgressHistorgram(String studentID) {



    }


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
