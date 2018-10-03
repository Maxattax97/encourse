package edu.purdue.cs.encourse;

import edu.purdue.cs.encourse.database.AccountRepository;
import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.service.*;
import edu.purdue.cs.encourse.util.JSONReturnable;
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

public class PythonScriptTests {
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

        @Autowired
        public ProfessorService professorService;


        @Before
        public void populateDatabase() {
            assertEquals(0, adminService.addAccount("1", "reed226","William", "Reed",
                    "Admin", "J", "reed226@purdue.edu"));
            assertEquals(0, adminService.addAccount("2", "grr", "Gustavo", "Rodriguez-Rivera",
                    "Professor", null, "grr@purdue.edu"));
        }

        @After
        public void clearDatabase() {
            adminRepository.deleteAll();
            professorRepository.deleteAll();
            studentRepository.deleteAll();
            teachingAssistantRepository.deleteAll();
        }

        /** Empty test prevents error from being thrown over no @Test annotations **/
        @Test
        public void emptyTest() {

        }

        /** No longer necessary since scripts incorporated into Java services **/
        @Test
        public void testPythonDirectory() {
         /*   Account account = accountService.retrieveAccount("reed226", "b");
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
            assertFalse(studentRepository.existsByUserID("grr"));*/

            System.out.println("=============================   Python Tests    ============================\n");

            /*System.out.println("\n==============================    Python Directory Test    ==============================");
            assertEquals("Hello.py failed to execute", 1, professorService.testPythonDirectory());

            System.out.println("\n==============================    Python Start/End Test    ==============================");
            JSONReturnable jsonReturn = professorService.getCommitData("temp");
            assertNotEquals("STDIN is empty", -1, jsonReturn.errorCode);
            assertNotEquals("getStartEnd.py failed to execute", -2, jsonReturn.errorCode);
            assertNotEquals("Failed to parse STDOUT into json", -3, jsonReturn.errorCode);
            assertEquals("Unknown Error", 1, jsonReturn.errorCode);

            System.out.println("=============================   Python Progress Histogram Test    ============================\n");
            String studentID = "cutz";
            jsonReturn = professorService.getProgressHistogram(studentID, "temp");
            assertEquals("Failed to ogenerate progress histogram data", 1, jsonReturn.errorCode);
            */
            String projectID = Project.createProjectID("cs252", "Fall2018", "MyMalloc");
            String studentID = "cutz";
            JSONReturnable jsonReturn = null;
            System.out.println("=============================   Addition Deletion Test    ============================\n");
            jsonReturn = professorService.getAdditionsAndDeletions(projectID, studentID);
            assertEquals("Failed to generate addition/deletion data", 1, jsonReturn.errorCode);

            System.out.println("=============================   Individual Progress Test    ============================\n");
            jsonReturn = professorService.getStudentProgress(projectID, studentID);
            assertEquals("Failed to generate individual data", 1, jsonReturn.errorCode);

            System.out.println("=============================   End Python Tests    ============================\n");


            //int returnValue = professorService.countAllCommits("CS200", "Encourse");
        }
}
