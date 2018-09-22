package edu.purdue.cs.encourse;

import edu.purdue.cs.encourse.database.AccountRepository;
import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.service.*;
import edu.purdue.cs.encourse.service.impl.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

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
    public AdminService adminService;

    @Autowired
    public AccountService accountService;

    @Before
    public void populateDatabase() {
        assertEquals(adminService.addAccount("1", "reed226", "a","William", "Reed",
                "Admin", "J", "reed226@purdue.edu"), 0);
        assertEquals(adminService.addAccount("2", "grr", "b", "Gustavo", "Rodriguez-Rivera",
                "Professor", null, "grr@purdue.edu"), 0);
        assertEquals(adminService.addAccount("3", "kleclain", "c", "Killian", "LeClainche",
                "Student", "A", "kleclain@purdue.edu"), 0);
        assertEquals(adminService.addAccount("4", "dkrolopp", "d", "Daniel", "Krolopp",
                "TA", "J", "dkrolopp@purdue.edu"), 0);
    }

    @After
    public void clearDatabase() {
        adminRepository.deleteAll();
        professorRepository.deleteAll();
        studentRepository.deleteAll();
        teachingAssistantRepository.deleteAll();
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

}