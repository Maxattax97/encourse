package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.domain.relations.*;
import edu.purdue.cs.encourse.service.*;
import edu.purdue.cs.encourse.util.JSONReturnable;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import lombok.NonNull;

@Service(value = TeachingAssistantServiceImpl.NAME)
public class TeachingAssistantServiceImpl implements TeachingAssistantService {

    public final static String NAME = "TeachingAssistantService";
    
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
        Project project = projectRepository.findByProjectID(projectID);
        if(project == null) {
            return new JSONReturnable(-1, null);
        }
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userNameTA);
        if(teachingAssistant == null) {
            return new JSONReturnable(-2, null);
        }
        List<TeachingAssistantStudent> assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdCourseID(teachingAssistant.getUserID(), project.getCourseID());
        List<String> userNames = helperService.getStudentUserNamesForTA(assignments);
        return courseService.getCheating(projectID, userNames);
    }

    public JSONReturnable getAssignmentsCommitList(@NonNull String projectID, @NonNull String userNameTA) {
        Project project = projectRepository.findByProjectID(projectID);
        if(project == null) {
            return new JSONReturnable(-1, null);
        }
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userNameTA);
        if(teachingAssistant == null) {
            return new JSONReturnable(-2, null);
        }
        List<TeachingAssistantStudent> assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdCourseID(teachingAssistant.getUserID(), project.getCourseID());
        List<String> userNames = helperService.getStudentUserNamesForTA(assignments);
        return courseService.getCommitList(projectID, userNames);
    }

    public JSONReturnable getAssignmentsProgress(@NonNull String projectID, @NonNull String userNameTA) {
        Project project = projectRepository.findByProjectID(projectID);
        if(project == null) {
            return new JSONReturnable(-1, null);
        }
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userNameTA);
        if(teachingAssistant == null) {
            return new JSONReturnable(-2, null);
        }
        List<TeachingAssistantStudent> assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdCourseID(teachingAssistant.getUserID(), project.getCourseID());
        List<String> userNames = helperService.getStudentUserNamesForTA(assignments);
        return courseService.getProgress(projectID, userNames);
    }

    public JSONReturnable getAssignmentsSimilar(@NonNull String projectID, @NonNull String userNameTA) {
        Project project = projectRepository.findByProjectID(projectID);
        if(project == null) {
            return new JSONReturnable(-1, null);
        }
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userNameTA);
        if(teachingAssistant == null) {
            return new JSONReturnable(-2, null);
        }
        List<TeachingAssistantStudent> assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdCourseID(teachingAssistant.getUserID(), project.getCourseID());
        List<String> userNames = helperService.getStudentUserNamesForTA(assignments);
        return courseService.getSimilar(projectID, userNames);
    }

    public JSONReturnable getAssignmentsStatistics(@NonNull String projectID, @NonNull String userNameTA) {
        Project project = projectRepository.findByProjectID(projectID);
        if(project == null) {
            return new JSONReturnable(-1, null);
        }
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userNameTA);
        if(teachingAssistant == null) {
            return new JSONReturnable(-2, null);
        }
        List<TeachingAssistantStudent> assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdCourseID(teachingAssistant.getUserID(), project.getCourseID());
        List<String> userNames = helperService.getStudentUserNamesForTA(assignments);
        return courseService.getStatistics(projectID, userNames);
    }

    public JSONReturnable getAssignmentsTestSummary(@NonNull String projectID, @NonNull String userNameTA) {
        Project project = projectRepository.findByProjectID(projectID);
        if(project == null) {
            return new JSONReturnable(-1, null);
        }
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userNameTA);
        if(teachingAssistant == null) {
            return new JSONReturnable(-2, null);
        }
        List<TeachingAssistantStudent> assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdCourseID(teachingAssistant.getUserID(), project.getCourseID());
        List<String> userNames = helperService.getStudentUserNamesForTA(assignments);
        return courseService.getTestSummary(projectID, userNames);
    }

    /** Gets all courses that a teaching assistant works for **/
    public JSONArray getCourseData(@NonNull String userName) {
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userName);
        if(teachingAssistant == null) {
            return null;
        }
        List<TeachingAssistantCourse> courses = teachingAssistantCourseRepository.findByIdTeachingAssistantID(teachingAssistant.getUserID());
        if(courses.isEmpty()) {
            return null;
        }
        List<String> courseIDs = new ArrayList<>();
        for(TeachingAssistantCourse course : courses) {
            courseIDs.add(course.getCourseID());
        }
        return courseService.getCourseData(userName, courseIDs);
    }

    public JSONArray getStudentData(@NonNull String semester, @NonNull String courseID, @NonNull String userNameTA) {
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName(userNameTA);
        if(teachingAssistant == null) {
            return null;
        }
        List<TeachingAssistantStudent> assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantIDAndIdCourseID(teachingAssistant.getUserID(), courseID);
        List<String> userNames = new ArrayList<>();
        for(TeachingAssistantStudent assignment : assignments) {
            List<StudentSection> studentSections = studentSectionRepository.findByIdStudentID(assignment.getStudentID());
            for(StudentSection studentSection : studentSections) {
                Section section = sectionRepository.findBySectionID(studentSection.getSectionID());
                if(section.getSemester().equals(semester)) {
                    Student student = studentRepository.findByUserID(assignment.getStudentID());
                    userNames.add(student.getUserName());
                    break;
                }
            }
        }
        return courseService.getStudentData(userNames);
    }
}
