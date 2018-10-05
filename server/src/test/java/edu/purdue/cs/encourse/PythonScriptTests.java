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
            System.out.println("");
            adminRepository.deleteAll();
            professorRepository.deleteAll();
            studentRepository.deleteAll();
            teachingAssistantRepository.deleteAll();
        }

        /** Empty test prevents error from being thrown over no @Test annotations **/
        @Test
        public void emptyTest() {

        }

        @Test
        public void testIndividualProgressData() {
            String projectID = Project.createProjectID("cs252", "Fall2018", "MyMalloc");
            String studentID = "cutz";
            JSONReturnable jsonReturn = null;
            System.out.println("=============================   Individual Progress Test    ============================\n");
            jsonReturn = professorService.getStudentProgress(projectID, studentID);
            assertEquals("Failed to generate individual progress data", 1, jsonReturn.errorCode);
        }

        @Test
        public void testClassProgressData() {
            String projectID = Project.createProjectID("cs252", "Fall2018", "MyMalloc");
            String studentID = "cutz";
            JSONReturnable jsonReturn = null;
            System.out.println("=============================  Class Progress Test    ============================\n");
            jsonReturn = professorService.getClassProgress(projectID);
            assertEquals("Failed to generate class progress histogram data", 1, jsonReturn.errorCode);
        }

        @Test
        public void testTestSummaryData() {
            String projectID = Project.createProjectID("cs252", "Fall2018", "MyMalloc");
            String studentID = "cutz";
            JSONReturnable jsonReturn = null;
            System.out.println("=============================  Test Summary Test    ============================\n");
            jsonReturn = professorService.getTestSummary(projectID);
            assertEquals("Failed to generate class progress histogram data", 1, jsonReturn.errorCode);
        }

        @Test
        public void testStatisticsData() {
            String projectID = Project.createProjectID("cs252", "Fall2018", "MyMalloc");
            String studentID = "cutz";
            JSONReturnable jsonReturn = null;
            System.out.println("=============================  Statistics Test    ============================\n");
            jsonReturn = professorService.getStatistics(projectID, studentID);
            assertEquals("Failed to generate individual statistics", 1, jsonReturn.errorCode);
        }

        @Test
        public void testAdditionDeletionData() {
            String projectID = Project.createProjectID("cs252", "Fall2018", "MyMalloc");
            String studentID = "cutz";
            JSONReturnable jsonReturn = null;
            System.out.println("=============================   Addition Deletion Test    ============================\n");
            jsonReturn = professorService.getAdditionsAndDeletions(projectID, studentID);
            assertEquals("Failed to generate addition/deletion data", 1, jsonReturn.errorCode);
        }

        @Test
        public void testCommitListData() {
            String projectID = Project.createProjectID("cs252", "Fall2018", "MyMalloc");
            String studentID = "cutz";
            JSONReturnable jsonReturn = null;
            System.out.println("=============================  Git Commit List Test    ============================\n");
            jsonReturn = professorService.getCommitList(projectID, studentID);
            assertEquals("Failed to generate git commit list data", 1, jsonReturn.errorCode);
        }


        @Test
        public void testCommitCountData() {
            String projectID = Project.createProjectID("cs252", "Fall2018", "MyMalloc");
            String studentID = "cutz";
            JSONReturnable jsonReturn = null;
            System.out.println("=============================  Git Commit Count Test    ============================\n");
            jsonReturn = professorService.getCommitCounts(projectID, studentID);
            assertEquals("Failed to generate git commit count data", 1, jsonReturn.errorCode);
        }
}
