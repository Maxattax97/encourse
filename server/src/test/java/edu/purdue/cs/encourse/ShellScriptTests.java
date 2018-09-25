package edu.purdue.cs.encourse;

import edu.purdue.cs.encourse.database.AccountRepository;
import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:application-dev.properties")
public class ShellScriptTests {

    /** These tests are meant to be specifically run on reed226@vm2.cs.purdue.edu. Please do not run
        these tests since they attempt to ssh into reed226@data.cs.purdue.edu **/

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
    public ProfService profService;

    @Autowired
    public StudentSectionRepository studentSectionRepository;

    //@Before
    public void populateDatabase() {
        assertEquals(0, adminService.addAccount("1", "rravind", "a","William", "Reed",
                "Student", "J", "reed226@purdue.edu"));
        assertEquals(0, adminService.addAccount("2", "grr", "b", "Gustavo", "Rodriguez-Rivera",
                "Professor", null, "grr@purdue.edu"));
        assertEquals(0, adminService.addAccount("3", "dwyork", "c", "Killian", "LeClainche",
                "Student", "A", "kleclain@purdue.edu"));
        assertEquals(0, adminService.addAccount("4", "dkrolopp", "d", "Daniel", "Krolopp",
                "TA", "J", "dkrolopp@purdue.edu"));
        assertEquals(0, adminService.addSection("12345", "Fall2018", "cs250", "Hardware", "Lab1"), 0);
        assertEquals(0, profService.addProject("cs250", "Fall2018", "MyMalloc", "lab1-src",
                "9/10/2018", "9/24/2018"));
        assertEquals(0, adminService.registerStudentToSection("dwyork", "cs250", "Fall2018", "Lab1"));
        assertEquals(0, adminService.registerStudentToSection("rravind", "cs250", "Fall2018", "Lab1"));
    }

    //@After
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

    //@Test
    public void testShellScripts(){
        assertEquals(0, profService.setSectionRemotePaths("cs250", "/homes/cs252/sourcecontrol/work"));
        assertEquals(0, profService.cloneProjects("cs250", Project.createProjectID("cs250", "Fall2018", "MyMalloc")));
        assertEquals(0, profService.pullProjects("cs250", Project.createProjectID("cs250", "Fall2018", "MyMalloc")));
    }


}