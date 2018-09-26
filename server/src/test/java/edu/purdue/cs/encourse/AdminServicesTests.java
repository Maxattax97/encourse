package edu.purdue.cs.encourse;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.service.AccountService;
import edu.purdue.cs.encourse.service.AdminService;
import org.junit.After;
import org.junit.Before;
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
public class AdminServicesTests {

    /** Mainly tests that accounts are properly handled in all relevant databases**/

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
    public StudentSectionRepository studentSectionRepository;

    @Autowired
    public AdminService adminService;

    @Autowired
    public AccountService accountService;



    @Before
    public void populateDatabase() {
        accountRepository.deleteAll();
        adminRepository.deleteAll();
        professorRepository.deleteAll();
        studentRepository.deleteAll();
        teachingAssistantRepository.deleteAll();
        sectionRepository.deleteAll();
        studentSectionRepository.deleteAll();
        assertEquals(0, adminService.addAccount("1", "reed226", "a","William", "Reed",
                "Admin", "J", "reed226@purdue.edu"));
        assertEquals(0, adminService.addAccount("2", "grr", "b", "Gustavo", "Rodriguez-Rivera",
                "Professor", null, "grr@purdue.edu"));
        assertEquals(0, adminService.addAccount("3", "kleclain", "c", "Killian", "LeClainche",
                "Student", "A", "kleclain@purdue.edu"));
        assertEquals(0, adminService.addAccount("4", "dkrolopp", "d", "Daniel", "Krolopp",
                "TA", "J", "dkrolopp@purdue.edu"));
        assertEquals(0, adminService.addAccount("5", "buckmast", "e", "Jordan", "Buckmaster",
                "Student", "M", "buckmast@purdue.edu"));
        assertEquals(0, adminService.addSection("1234", "Fall2018", "cs250", "Hardware", "Lab1"));
        assertEquals(0, adminService.addSection("1235", "Fall2018", "cs250", "Hardware", "Lab2"));
    }

    @After
    public void clearDatabase() {
        accountRepository.deleteAll();
        adminRepository.deleteAll();
        professorRepository.deleteAll();
        studentRepository.deleteAll();
        teachingAssistantRepository.deleteAll();
        sectionRepository.deleteAll();
        studentSectionRepository.deleteAll();
    }

    @Test
    public void testAccountRetrieval() {
        Account account = accountService.retrieveAccount("reed226", "b");
        assertNull(account);
        account = accountService.retrieveAccount("reed226", "a");
        assertNotNull(account);
        assertEquals("1", account.getUserID());
        assertEquals("reed226", account.getUserName());
        assertNull(account.getSaltPass());
        assertEquals("William", account.getFirstName());
        assertEquals("Reed", account.getLastName());
        assertEquals(Account.Roles.ADMIN, account.getRole());
        assertEquals("J", account.getMiddleInit());
        assertEquals("reed226@purdue.edu", account.getEduEmail());
        CollegeAdmin admin = accountService.retrieveAdmin("reed226", "a");
        assertEquals("William", admin.getFirstName());
        Professor professor = accountService.retrieveProfessor("grr", "b");
        assertEquals("Gustavo", professor.getFirstName());
        Student student = accountService.retrieveStudent("kleclain", "c");
        assertEquals("Killian", student.getFirstName());
        TeachingAssistant teachingAssistant = accountService.retrieveTA("dkrolopp", "d");
        assertEquals("Daniel", teachingAssistant.getFirstName());
        student = accountService.retrieveStudent("dkrolopp", "d");
        assertEquals("Daniel", student.getFirstName());
        assertFalse(studentRepository.existsByUserID("grr"));
    }

    @Test
    public void testAssignments() {
        assertEquals(0, adminService.registerStudentToSection("kleclain", "cs250", "Fall2018", "Lab1"));
        assertEquals(0, adminService.registerStudentToSection("buckmast", "cs250", "Fall2018", "Lab2"));
        assertEquals(-2, adminService.registerStudentToSection("buckmast", "cs250", "Fall2018", "Lab3"));
        assertEquals(-1, adminService.registerStudentToSection("grr", "cs250", "Fall2018", "Lab1"));
        assertEquals(2, studentSectionRepository.count());
        assertEquals(0, adminService.assignProfessorToCourse("grr", "cs250", "Fall2018"));
        assertEquals(-2, adminService.assignProfessorToCourse("grr", "cs251", "Fall2018"));
        assertEquals(-1, adminService.assignProfessorToCourse("reed226", "cs250", "Fall2018"));
        assertEquals(0, adminService.hireStudentAsTeachingAssistant("buckmast"));
        Account account = accountRepository.findByUserName("buckmast");
        Student student = studentRepository.findByUserName("buckmast");
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName("buckmast");
        assertEquals(Account.Roles.TA, account.getRole());
        assertEquals(Account.Roles.TA, student.getRole());
        assertEquals(Account.Roles.TA, teachingAssistant.getRole());
        assertEquals(-2, adminService.hireStudentAsTeachingAssistant("grr"));
        assertEquals(-3, adminService.hireStudentAsTeachingAssistant("dkrolopp"));
    }

}