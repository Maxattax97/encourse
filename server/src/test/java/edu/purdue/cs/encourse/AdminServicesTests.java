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

    public Section sect1;
    public Section sect2;

    @Before
    public void populateDatabase() {
        accountRepository.deleteAll();
        adminRepository.deleteAll();
        professorRepository.deleteAll();
        studentRepository.deleteAll();
        teachingAssistantRepository.deleteAll();
        sectionRepository.deleteAll();
        studentSectionRepository.deleteAll();
        assertEquals(0, adminService.addAccount("1", "reed226","William", "Reed",
                "ADMIN", "J", "reed226@purdue.edu"));
        assertEquals(0, adminService.addAccount("2", "grr", "Gustavo", "Rodriguez-Rivera",
                "PROFESSOR", null, "grr@purdue.edu"));
        assertEquals(0, adminService.addAccount("3", "kleclain", "Killian", "LeClainche",
                "STUDENT", "A", "kleclain@purdue.edu"));
        assertEquals(0, adminService.addAccount("4", "dkrolopp", "Daniel", "Krolopp",
                "TA", "J", "dkrolopp@purdue.edu"));
        assertEquals(0, adminService.addAccount("5", "buckmast", "Jordan", "Buckmaster",
                "STUDENT", "M", "buckmast@purdue.edu"));
        sect1 = adminService.addSection("1234", "Fall2018", "cs250", "Hardware", "Lab1", "MWF 12:30 - 1:20");
        sect2 = adminService.addSection("1235", "Fall2018", "cs250", "Hardware", "Lab2", "MWF 12:30 - 1:20");
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
        Account account = accountService.retrieveAccount("reed226");
        assertEquals("1", account.getUserID());
        assertEquals("reed226", account.getUserName());
        assertEquals("William", account.getFirstName());
        assertEquals("Reed", account.getLastName());
        assertEquals(Account.Roles.ADMIN, account.getRole());
        assertEquals("J", account.getMiddleInit());
        assertEquals("reed226@purdue.edu", account.getEduEmail());
        CollegeAdmin admin = accountService.retrieveAdmin("reed226");
        assertEquals("William", admin.getFirstName());
        Professor professor = accountService.retrieveProfessor("grr");
        assertEquals("Gustavo", professor.getFirstName());
        Student student = accountService.retrieveStudent("kleclain");
        assertEquals("Killian", student.getFirstName());
        TeachingAssistant teachingAssistant = accountService.retrieveTA("dkrolopp");
        assertEquals("Daniel", teachingAssistant.getFirstName());
        student = accountService.retrieveStudent("dkrolopp");
        assertEquals("Daniel", student.getFirstName());
        assertFalse(studentRepository.existsByUserID("grr"));
    }

    @Test
    public void testAccountModification() {
        assertEquals(0, adminService.modifyAccount("reed226", "firstName", "Jordan"));
        Account account = accountRepository.findByUserName("reed226");
        CollegeAdmin admin = adminRepository.findByUserName("reed226");
        assertEquals("Jordan", account.getFirstName());
        assertEquals("Jordan", admin.getFirstName());
        assertEquals(0, adminService.modifyAccount("reed226", "saltPass", "f"));
        assertEquals(0, adminService.modifyAccount("grr", "middleInitial", "J"));
        account = accountRepository.findByUserName("grr");
        Professor professor = professorRepository.findByUserName("grr");
        assertEquals("J", account.getMiddleInit());
        assertEquals("J", professor.getMiddleInit());
        assertEquals(0, adminService.modifyAccount("kleclain", "lastName", "Le Clainche"));
        account = accountRepository.findByUserName("kleclain");
        Student student = studentRepository.findByUserName("kleclain");
        assertEquals("Le Clainche", account.getLastName());
        assertEquals("Le Clainche", student.getLastName());
        assertEquals(0, adminService.modifyAccount("dkrolopp", "middleInitial", "A"));
        account = accountRepository.findByUserName("dkrolopp");
        student = studentRepository.findByUserName("dkrolopp");
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName("dkrolopp");
        assertEquals("A", account.getMiddleInit());
        assertEquals("A", student.getMiddleInit());
        assertEquals("A", teachingAssistant.getMiddleInit());
    }

    @Test
    public void testAssignments() {
        assertEquals(0, adminService.registerStudentToSection("kleclain", sect1.getSectionIdentifier()));
        assertEquals(0, adminService.registerStudentToSection("buckmast", sect2.getSectionIdentifier()));
        assertEquals(-2, adminService.registerStudentToSection("buckmast", "InvalidSection"));
        assertEquals(-1, adminService.registerStudentToSection("grr", sect1.getSectionIdentifier()));
        assertEquals(2, studentSectionRepository.count());
        assertEquals(0, adminService.assignProfessorToCourse("grr", "cs250", "Fall2018"));
        assertEquals(-2, adminService.assignProfessorToCourse("grr", "cs251", "Fall2018"));
        assertEquals(-1, adminService.assignProfessorToCourse("reed226", "cs250", "Fall2018"));
        assertEquals(0, adminService.hireStudentAsTeachingAssistant("buckmast"));
        Account account = accountRepository.findByUserName("buckmast");
        Student student = studentRepository.findByUserName("buckmast");
        TeachingAssistant teachingAssistant = teachingAssistantRepository.findByUserName("buckmast");
        assertTrue(studentSectionRepository.existsByIdStudentID(account.getUserID()));
        assertEquals(Account.Roles.TA, account.getRole());
        assertEquals(Account.Roles.TA, student.getRole());
        assertEquals(Account.Roles.TA, teachingAssistant.getRole());
        assertEquals(-2, adminService.hireStudentAsTeachingAssistant("grr"));
        assertEquals(-3, adminService.hireStudentAsTeachingAssistant("dkrolopp"));
    }

}