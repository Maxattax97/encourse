package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.domain.relations.*;
import edu.purdue.cs.encourse.service.ProfessorService;
import edu.purdue.cs.encourse.service.TeachingAssistantService;
import edu.purdue.cs.encourse.util.JSONReturnable;
import org.aspectj.weaver.patterns.IfPointcut;
import org.codehaus.jackson.map.util.JSONPObject;
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
import java.util.*;
import java.util.concurrent.Executors;
import lombok.NonNull;

import javax.persistence.criteria.CriteriaBuilder;

@Service(value = TeachingAssistantServiceImpl.NAME)
public class TeachingAssistantServiceImpl implements TeachingAssistantService {

    public final static String NAME = "TeachingAssistantService";
    private final static String pythonPath = "src/main/python/";
    private final static String tailFilePath = "src/main/temp/";
    private final static int RATE = 3600000;
    private final static Boolean DEBUG = false;
    private final static Boolean OBFUSCATE = false;

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
    private ProjectTestScriptRepository projectTestScriptRepository;

    @Autowired
    private StudentProjectTestRepository studentProjectTestRepository;

    private int executeBashScript(@NonNull String command) {
        try {
            Process process = Runtime.getRuntime().exec("./src/main/bash/" + command + " 2> /dev/null");
            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
            Executors.newSingleThreadExecutor().submit(streamGobbler);
            process.waitFor();
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }

    private boolean isTakingCourse(@NonNull Student student, @NonNull Project project) {
        List<StudentSection> studentSections = studentSectionRepository.findByIdStudentID(student.getUserID());
        boolean isTaking = false;
        for (StudentSection s : studentSections) {
            Section section = sectionRepository.findBySectionIdentifier(s.getSectionIdentifier());
            if (section.getCourseID().equals(project.getCourseID()) && section.getSemester().equals(project.getSemester())) {
                isTaking = true;
                break;
            }
        }
        return isTaking;
    }

    private double parseProgressForProject(@NonNull String projectID, @NonNull String testOutput) {
        String[] testResults = testOutput.split(";");
        double earnedPoints = 0.0;
        double maxPoints = 0.0;
        for (String r : testResults) {
            String testName = r.split(":")[0];
            ProjectTestScript testScript = projectTestScriptRepository.findByIdProjectIdentifierAndIdTestScriptName(projectID, testName);
            if (testScript == null) {
                continue;
            }
            maxPoints += testScript.getPointsWorth();
            if (r.endsWith("P")) {
                earnedPoints += testScript.getPointsWorth();
            }
        }
        if (maxPoints == 0.0) {
            return 0.0;
        }
        return (earnedPoints / maxPoints) * 100;
    }

    private void updateTestResults(String result, String studentID, String projectID, boolean isHidden) {
        String[] testResults = result.split(";");
        for (String s : testResults) {
            String testName = s.split(":")[0];
            String testScore = s.split(":")[1];
            boolean isPassing = testScore.equals("P");
            StudentProjectTest studentProjectTest =
                    studentProjectTestRepository.findByIdProjectIdentifierAndIdTestScriptNameAndIdStudentID(projectID, testName, studentID);
            if (studentProjectTest == null) {
                ProjectTestScript testScript = projectTestScriptRepository.findByIdProjectIdentifierAndIdTestScriptName(projectID, testName);
                studentProjectTest = new StudentProjectTest(studentID, projectID, testName, isPassing, isHidden, testScript.getPointsWorth());
                studentProjectTestRepository.save(studentProjectTest);
            } else {
                studentProjectTest.setPassing(isPassing);
                studentProjectTestRepository.save(studentProjectTest);
            }
        }
    }

    public JSONReturnable getStudentProgress(@NonNull String projectID, @NonNull String userName) {
        return null;
    }

    public JSONReturnable getAdditionsAndDeletions(@NonNull String projectID, @NonNull String userName) {
        return null;
    }

    public JSONReturnable getStatistics(@NonNull String projectID, @NonNull String userName) {
        return null;
    }

    public JSONReturnable getCommitList(@NonNull String projectID, @NonNull String userName) {
        return null;
    }

    public JSONReturnable getClassProgress(@NonNull String projectID) {
        return null;
    }

    public JSONReturnable getTestSummary(@NonNull String projectID) {
        return null;
    }

    public JSONReturnable getCommitCounts(@NonNull String projectID, @NonNull String userName) {
        return null;
    }

    public int runTestallForStudent(@NonNull String projectID, @NonNull String userName) {
        return 0;
    }

    public JSONArray getStudentData(@NonNull String semester, @NonNull String courseID, @NonNull String userName) {
        return null;
    }

    public JSONArray getProjectData(@NonNull String semester, @NonNull String courseID, @NonNull String userName) {
        return null;
    }

    public Project getProject(String projectID) {
        return null;
    }
}
