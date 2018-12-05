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

/**
 * Contains implementations for all services which are used by teaching assistants for managing their assigned students
 * Primarily used by endpoints called for teaching assistants
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
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

    /**
     * Obtains analysis on all assigned students for a TA for potential cheating on a project
     * Primarily used in a teaching assistant's summary for academic dishonesty
     *
     * @param projectID     Identifier for the project being analyzed
     * @param userNameTA    Front-end identifier for teaching assistant retrieving the data
     * @return              JSON for front-end to parse
     */
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

    /**
     * Obtains commit information for all assigned students for a TA
     * Primarily used in a teaching assistant's summary for a project
     *
     * @param projectID     Identifier for the project being analyzed
     * @param userNameTA    Front-end identifier for teaching assistant retrieving the data
     * @return              JSON for front-end to parse
     */
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

    /**
     * Obtains progress over time for all assigned students for a TA
     * Primarily used in a teaching assistant's summary for a project
     *
     * @param projectID     Identifier for the project being analyzed
     * @param userNameTA    Front-end identifier for teaching assistant retrieving the data
     * @return              JSON for front-end to parse
     */
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

    /**
     * Obtains analysis on all assigned students for a TA for similarities between projects
     * Primarily used in a teaching assistant's summary for academic dishonesty
     *
     * @param projectID     Identifier for the project being analyzed
     * @param userNameTA    Front-end identifier for teaching assistant retrieving the data
     * @return              JSON for front-end to parse
     */
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

    /**
     * Obtains statistics, such as additions and deletions, for all assigned students for a TA
     * Primarily used in a teaching assistant's summary for a project
     *
     * @param projectID     Identifier for the project being analyzed
     * @param userNameTA    Front-end identifier for teaching assistant retrieving the data
     * @return              JSON for front-end to parse
     */
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

    /**
     * Obtains information on which test cases were passed for all assigned students for a TA
     * Primarily used in a teaching assistant's summary for a project
     *
     * @param projectID     Identifier for the project being analyzed
     * @param userNameTA    Front-end identifier for teaching assistant retrieving the data
     * @return              JSON for front-end to parse
     */
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

    /**
     * Obtains data needed for displaying a teaching assistant's courses when they log in
     *
     * @param userName  Front-end identifier for the teaching assistant
     * @return          JSON for front-end to parse
     */
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

    /**
     * Obtains data for displaying every student that is assigned to a TA for a course
     *
     * @param semester      Semester that course is being taught
     * @param courseID      Identifier for the course
     * @param userNameTA    Front-end identifier for the teaching assistant
     * @return              JSON for front-end to parse
     */
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
