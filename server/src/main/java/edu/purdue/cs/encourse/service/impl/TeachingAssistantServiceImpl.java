package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.domain.relations.*;
import edu.purdue.cs.encourse.service.*;
import edu.purdue.cs.encourse.service.helper.*;
import edu.purdue.cs.encourse.util.JSONReturnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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

    @Autowired
    private CourseService courseService;

    public JSONReturnable getAssignmentsCheating(@NonNull String projectID, @NonNull String userNameTA) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return new JSONReturnable(-1, null);
        }
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userNameTA);
        if(teachingAssistant == null) {
            return new JSONReturnable(-2, null);
        }
        List<TeachingAssistantStudent> assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdCourseID(teachingAssistant.getUserID(), project.getCourseID());
        List<String> userNames = helper.getStudentUserNamesForTA(assignments);
        return courseService.getCheating(projectID, userNames);
    }

    public JSONReturnable getAssignmentsCommitList(@NonNull String projectID, @NonNull String userNameTA) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return new JSONReturnable(-1, null);
        }
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userNameTA);
        if(teachingAssistant == null) {
            return new JSONReturnable(-2, null);
        }
        List<TeachingAssistantStudent> assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdCourseID(teachingAssistant.getUserID(), project.getCourseID());
        List<String> userNames = helper.getStudentUserNamesForTA(assignments);
        return courseService.getCommitList(projectID, userNames);
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
        List<String> userNames = helper.getStudentUserNamesForTA(assignments);
        return courseService.getProgress(projectID, userNames);
    }

    public JSONReturnable getAssignmentsSimilar(@NonNull String projectID, @NonNull String userNameTA) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return new JSONReturnable(-1, null);
        }
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userNameTA);
        if(teachingAssistant == null) {
            return new JSONReturnable(-2, null);
        }
        List<TeachingAssistantStudent> assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdCourseID(teachingAssistant.getUserID(), project.getCourseID());
        List<String> userNames = helper.getStudentUserNamesForTA(assignments);
        return courseService.getSimilar(projectID, userNames);
    }

    public JSONReturnable getAssignmentsStatistics(@NonNull String projectID, @NonNull String userNameTA) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return new JSONReturnable(-1, null);
        }
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userNameTA);
        if(teachingAssistant == null) {
            return new JSONReturnable(-2, null);
        }
        List<TeachingAssistantStudent> assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdCourseID(teachingAssistant.getUserID(), project.getCourseID());
        List<String> userNames = helper.getStudentUserNamesForTA(assignments);
        return courseService.getStatistics(projectID, userNames);
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
        List<String> userNames = helper.getStudentUserNamesForTA(assignments);
        return courseService.getTestSummary(projectID, userNames);
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
