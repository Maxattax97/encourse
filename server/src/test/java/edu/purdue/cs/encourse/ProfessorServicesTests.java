package edu.purdue.cs.encourse;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.service.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

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

    @Before
    public void populateDatabase() {
        adminRepository.deleteAll();
        professorRepository.deleteAll();
        studentRepository.deleteAll();
        teachingAssistantRepository.deleteAll();
        projectRepository.deleteAll();
        sectionRepository.deleteAll();
        studentSectionRepository.deleteAll();
        assertEquals(0, adminService.addAccount("1", "rravind","William", "Reed",
                "Student", "J", "reed226@purdue.edu"));
        assertEquals(0, adminService.addAccount("2", "grr", "Gustavo", "Rodriguez-Rivera",
                "Professor", null, "grr@purdue.edu"));
        assertEquals(0, adminService.addAccount("3", "dwyork", "Killian", "LeClainche",
                "Student", "A", "kleclain@purdue.edu"));
        assertEquals(0, adminService.addAccount("4", "dkrolopp", "Daniel", "Krolopp",
                "TA", "J", "dkrolopp@purdue.edu"));
        assertEquals(0, adminService.addSection("12345", "Fall2018", "cs250", "Hardware", "Lab1"), 0);
        assertEquals(0, professorService.addProject("cs250", "Fall2018", "MyMalloc", "lab1-src",
                "9/10/2018", "9/24/2018"));
        assertEquals(0, adminService.registerStudentToSection("dwyork", "cs250", "Fall2018", "Lab1"));
        assertEquals(0, adminService.registerStudentToSection("rravind", "cs250", "Fall2018", "Lab1"));
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
        assertEquals(0, courseService.cloneProjects("Fall2018", "cs250", Project.createProjectID("cs250", "Fall2018", "MyMalloc")));
        assertEquals(0, courseService.pullProjects("Fall2018", "cs250", Project.createProjectID("cs250", "Fall2018", "MyMalloc")));
        assertNotNull(professorService.countAllCommits("Fall2018", "cs250", Project.createProjectID("cs250", "Fall2018", "MyMalloc")));
        assertNotNull(professorService.countAllCommitsByDay("Fall2018", "cs250", Project.createProjectID("cs250", "Fall2018", "MyMalloc")));
        assertNotNull(professorService.countStudentCommitsByDay("Fall2018", "cs250", Project.createProjectID("cs250", "Fall2018", "MyMalloc"), "dwyork"));
        assertNotNull(professorService.listStudentCommitsByTime("Fall2018", "cs250", Project.createProjectID("cs250", "Fall2018", "MyMalloc"), "rravind"));
    }

    @Test
    public void testProjectModification() {
        assertEquals(0, professorService.modifyProject(Project.createProjectID("cs250", "Fall2018", "MyMalloc"), "dueDate", "9/26/2018"));
        Project project = projectRepository.findByProjectIdentifier(Project.createProjectID("cs250", "Fall2018", "MyMalloc"));
        assertEquals("9/26/2018", project.getDueDate());
    }


}