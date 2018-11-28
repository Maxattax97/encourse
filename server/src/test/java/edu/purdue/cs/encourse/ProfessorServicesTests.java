package edu.purdue.cs.encourse;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.domain.Section;
import edu.purdue.cs.encourse.domain.relations.StudentProject;
import edu.purdue.cs.encourse.domain.relations.TeachingAssistantStudent;
import edu.purdue.cs.encourse.service.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:application-dev.properties")
public class ProfessorServicesTests {

    @Autowired
    public AccountRepository accountRepository;

    @Autowired
    public StudentRepository studentRepository;

    @Autowired
    public TeachingAssistantRepository teachingAssistantRepository;

    @Autowired
    public ProfessorRepository professorRepository;

    @Autowired
    public AdminRepository adminRepository;

    @Autowired
    public SectionRepository sectionRepository;

    @Autowired
    public ProjectRepository projectRepository;

    @Autowired
    public AdminService adminService;

    @Autowired
    public ProfessorService professorService;

    @Autowired
    public CourseService courseService;

    @Autowired
    public StudentSectionRepository studentSectionRepository;

    @Autowired
    public StudentProjectRepository studentProjectRepository;

    @Autowired
    public TeachingAssistantStudentRepository teachingAssistantStudentRepository;

    public Section sect1;
    public Project proj1;

    @Before
    public void populateDatabase() {
        adminRepository.deleteAll();
        professorRepository.deleteAll();
        studentRepository.deleteAll();
        teachingAssistantRepository.deleteAll();
        projectRepository.deleteAll();
        sectionRepository.deleteAll();
        studentSectionRepository.deleteAll();
        studentProjectRepository.deleteAll();
        teachingAssistantStudentRepository.deleteAll();
        assertEquals(0, adminService.addAccount("1", "rravind","Student", "One",
                "STUDENT", null, "rravind@purdue.edu"));
        assertEquals(0, adminService.addAccount("2", "grr", "Gustavo", "Rodriguez-Rivera",
                "PROFESSOR", null, "grr@purdue.edu"));
        assertEquals(0, adminService.addAccount("3", "dwyork", "Student", "Two",
                "STUDENT", null, "dwyork@purdue.edu"));
        assertEquals(0, adminService.addAccount("4", "dkrolopp", "Daniel", "Krolopp",
                "TA", "J", "dkrolopp@purdue.edu"));
        sect1 = adminService.addSection("12345", "Fall2018", "cs250", "Hardware", "Lab1", "MWF 12:30 - 1:20");
        proj1 = professorService.addProject("cs250", "Fall2018", "MyMalloc", "lab1-src",
                "9/10/2018", "9/24/2018", 0);
        assertEquals(0, adminService.registerStudentToSection("dwyork", sect1.getSectionID()));
        assertEquals(0, adminService.registerStudentToSection("rravind", sect1.getSectionID()));
        assertEquals(0, adminService.assignTeachingAssistantToCourse("dkrolopp", "cs250", "Fall2018"));
        assertEquals(0, professorService.assignProject(proj1.getProjectID()));
        assertEquals(0, professorService.assignTeachingAssistantToSection("dkrolopp", sect1.getSectionID()));
    }

    @After
    public void clearDatabase() {
        adminRepository.deleteAll();
        professorRepository.deleteAll();
        studentRepository.deleteAll();
        teachingAssistantRepository.deleteAll();
        projectRepository.deleteAll();
        sectionRepository.deleteAll();
        studentSectionRepository.deleteAll();
        studentProjectRepository.deleteAll();
        teachingAssistantStudentRepository.deleteAll();
    }

    /** Empty test prevents error from being thrown over no @Test annotations **/
    @Test
    public void emptyTest() {

    }

    /** This test is meant to be specifically run on reed226@vm2.cs.purdue.edu. Please do not run
     this test since it attempts to ssh into reed226@data.cs.purdue.edu **/
    //@Test
    public void testShellScripts() {
        assertEquals(0, courseService.setSectionRemotePaths("Fall2018", "cs250", "/homes/cs252/sourcecontrol/work"));
        assertEquals(0, courseService.createDirectory("Fall2018", "cs250"));
        assertEquals(0, professorService.cloneProjects(proj1.getProjectID()));
        assertEquals(0, professorService.pullProjects(proj1.getProjectID()));
        assertEquals(0, professorService.uploadTestScript(proj1.getProjectID(), "ls_test.sh", "ls ..", false, 5));
        assertEquals(0, professorService.uploadTestScript(proj1.getProjectID(), "echo_test.sh", "echo \"\"", false, 10));
        assertEquals(0, professorService.runTestall(proj1.getProjectID()));
        List<StudentProject> projects = studentProjectRepository.findByIdStudentID("1");
        assertEquals("echo_test.sh:P ls_test.sh:F ", projects.get(0).getBestVisibleGrade());
    }

    @Test
    public void testProjectModification() {
        assertEquals(0, professorService.modifyProject(proj1.getProjectID(), "dueDate", "9/26/2018"));
        Project project = projectRepository.findByProjectID(proj1.getProjectID());
        assertEquals("9/26/2018", project.getDueDate());
    }

    @Test
    public void testAssigningTeachingAssistant() {
        assertEquals(0, professorService.assignTeachingAssistantToStudentInSection("dkrolopp", "rravind", sect1.getSectionID()));
        List<TeachingAssistantStudent> assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantID("4");
        assertEquals(1, assignments.size());
        assertEquals("1", assignments.get(0).getStudentID());
        assertEquals(0, professorService.assignTeachingAssistantToStudentInSection("dkrolopp", "dwyork", sect1.getSectionID()));
        assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantID("4");
        assertEquals(2, assignments.size());
        assertEquals(0, professorService.assignTeachingAssistantToStudentInSection("dkrolopp", "dwyork", sect1.getSectionID()));
        assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantID("4");
        assertEquals(2, assignments.size());
    }

    @Test
    public void testProjectAssignment() {
        List<StudentProject> projects = studentProjectRepository.findByIdStudentID("1");
        assertEquals(1, projects.size());
        assertEquals(proj1.getProjectID(), projects.get(0).getProjectID());
        projects = studentProjectRepository.findByIdStudentID("3");
        assertEquals(1, projects.size());
        assertEquals(proj1.getProjectID(), projects.get(0).getProjectID());
        assertEquals(0, adminService.addAccount("10", "hayc", "Student", "Three",
                "STUDENT", null, "dwyork@purdue.edu"));
        assertEquals(0, adminService.registerStudentToSection("hayc", sect1.getSectionID()));
        projects = studentProjectRepository.findByIdStudentID("10");
        assertEquals(0, projects.size());
        assertEquals(0, professorService.assignProjectToStudent(proj1.getProjectID(), "hayc"));
        projects = studentProjectRepository.findByIdStudentID("10");
        assertEquals(1, projects.size());
        assertEquals(proj1.getProjectID(), projects.get(0).getProjectID());
    }


}