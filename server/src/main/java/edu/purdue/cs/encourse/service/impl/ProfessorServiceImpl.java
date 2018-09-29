package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.domain.Section;
import edu.purdue.cs.encourse.domain.Student;
import edu.purdue.cs.encourse.domain.TeachingAssistant;
import edu.purdue.cs.encourse.domain.relations.StudentProject;
import edu.purdue.cs.encourse.domain.relations.StudentSection;
import edu.purdue.cs.encourse.domain.relations.TeachingAssistantStudent;
import edu.purdue.cs.encourse.service.ProfessorService;
import edu.purdue.cs.encourse.util.JSONReturnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import lombok.NonNull;

@Service(value = ProfessorServiceImpl.NAME)
public class ProfessorServiceImpl implements ProfessorService {

    public final static String NAME = "ProfessorService";
    final static String pythonPath = "src/main/java/edu/purdue/cs/encourse/service/impl/python/";

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

    private List<Section> getSectionsBySemesterAndCourseID(@NonNull String semester, @NonNull String courseID) {
        List<Section> sections = sectionRepository.findByCourseID(courseID);
        List<Section> filteredSections = new ArrayList<>();
        if(sections.isEmpty()) {
            return null;
        }
        for(Section s : sections) {
            if(s.getSemester().equals(semester)) {
                filteredSections.add(s);
            }
        }
        return filteredSections;

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

    /** Counts the number of commits that every student in the class has made for a project **/
    public JSONReturnable countAllCommits(@NonNull String semester, @NonNull String courseID, @NonNull String projectID) {
        List<Section> sections = getSectionsBySemesterAndCourseID(semester, courseID);
        if(sections.isEmpty()) {
            return null;
        }
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return null;
        }
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifier(projectID);
        String fileName = "./src/main/temp/" + Long.toString(Math.round(Math.random() * 1000000));
        for(StudentProject s : projects) {
            Student student = studentRepository.findByUserID(s.getStudentID());
            String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
            executeBashScript("countCommits.sh " + destPath + " " + fileName + " " + student.getUserName());
        }

        // TODO: Call and receive input from python script

        return null;
    }

    /** Counts the total number of commits made each day that the project was active **/
    public JSONReturnable countAllCommitsByDay(@NonNull String semester, @NonNull String courseID, @NonNull String projectID) {
        List<Section> sections = getSectionsBySemesterAndCourseID(semester, courseID);
        if(sections.isEmpty()) {
            return null;
        }
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return null;
        }
        List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifier(projectID);
        String fileName = "./src/main/temp/" + Long.toString(Math.round(Math.random() * 1000000));
        for(StudentProject s : projects) {
            Student student = studentRepository.findByUserID(s.getStudentID());
            String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
            executeBashScript("countCommitsByDay.sh " + destPath + " " + fileName + " " + student.getUserName());
        }

        // TODO: Call and receive input from python script

        return null;
    }

    /** Counts the number of commits that a single student has made for each day that the project is active **/
    public JSONReturnable countStudentCommitsByDay(@NonNull String semester, @NonNull String courseID, @NonNull String projectID, @NonNull String userName) {
        List<Section> sections = getSectionsBySemesterAndCourseID(semester, courseID);
        if(sections.isEmpty()) {
            return null;
        }
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return null;
        }
        Student student = studentRepository.findByUserID(userName);
        if(student == null) {
            return null;
        }
        String fileName = "./src/main/temp/" + Long.toString(Math.round(Math.random() * 1000000));
        String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
        if(executeBashScript("countCommitsByDay.sh " + destPath + " " + fileName + " " + student.getUserName()) == -1) {
            return null;
        }

        // TODO: Call and receive input from python script

        return null;
    }

    /** Lists various information about git history, including commit time and dates, and files modified in each commit **/
    public JSONReturnable listStudentCommitsByTime(@NonNull String semester, @NonNull String courseID, @NonNull String projectID, @NonNull String userName) {
        List<Section> sections = getSectionsBySemesterAndCourseID(semester, courseID);
        if(sections.isEmpty()) {
            return null;
        }
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return null;
        }
        Student student = studentRepository.findByUserID(userName);
        if(student == null) {
            return null;
        }
        String fileName = "./src/main/temp/" + Long.toString(Math.round(Math.random() * 1000000));
        String destPath = (sections.get(0).getCourseHub() + "/" + student.getUserName() + "/" + project.getRepoName());
        if(executeBashScript("listCommitsByTime.sh " + destPath + " " + fileName + " " + student.getUserName()) == -1) {
            return null;
        }

        // TODO: Call and receive input from python script

        return null;
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

    public JSONReturnable getCommitData() {
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

    public JSONReturnable getProgressHistogram(@NonNull String studentID) {
        String filePath = pythonPath + "getProgressHistogram.py";
        String changeListFile = pythonPath + "sampleCommitList.txt";
        try {
            Process process = Runtime.getRuntime().exec("python " +  filePath + " " + studentID + " " + changeListFile);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String input = null;
            String error = null;
            /*while ((error = stdError.readLine()) != null) {
                System.out.println(error);
            }*/
            while ((input = stdInput.readLine()) != null) {
                System.out.println(input);
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
                        jsonObject.put(studentID, jsonArray);
                    } else {
                        return new JSONReturnable(-4, null);
                    }
                    JSONReturnable jsonReturn = new JSONReturnable(1, jsonObject);
                    //System.out.println(jsonObject.toString());
                    System.out.println(jsonReturn.jsonObject.toJSONString());
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


