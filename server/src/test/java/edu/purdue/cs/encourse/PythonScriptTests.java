package edu.purdue.cs.encourse;

import edu.purdue.cs.encourse.database.AccountRepository;
import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.service.*;
import edu.purdue.cs.encourse.util.ConfigurationManager;
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

        @Autowired
        public CourseService courseService;

        public Project proj1;
        public static final Boolean DEBUG = ConfigurationManager.getInstance().debug;

        @Before
        public void populateDatabase() {
            if (DEBUG) {
                return;
            }
            assertEquals(0, adminService.addAccount("1", "reed226","William", "Reed",
                    "ADMIN", "J", "reed226@purdue.edu"));
            assertEquals(0, adminService.addAccount("2", "grr", "Gustavo", "Rodriguez-Rivera",
                    "PROFESSOR", null, "grr@purdue.edu"));
            proj1 = professorService.addProject("cs252", "Fall2018", "MyMalloc", "lab1-src",
                    "9/10/2018", "9/24/2018", 0);

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
            String projectID = DEBUG ? "cs252" : proj1.getProjectIdentifier();
            String studentID = "cutz";
            JSONReturnable jsonReturn = null;
            System.out.println("=============================   Individual Progress Test    ============================\n");
            jsonReturn = courseService.getStudentProgress(projectID, studentID);
            assertEquals("Failed to generate individual progress data", 1, jsonReturn.errorCode);
        }

        @Test
        public void testClassProgressData() {
            String projectID = DEBUG ? "cs252" : proj1.getProjectIdentifier();
            String studentID = "cutz";
            JSONReturnable jsonReturn = null;
            System.out.println("=============================  Class Progress Test    ============================\n");
            jsonReturn = professorService.getClassProgress(projectID);
            assertEquals("Failed to generate class progress histogram data", 1, jsonReturn.errorCode);
        }

        @Test
        public void testTestSummaryData() {
            String projectID = DEBUG ? "cs252" : proj1.getProjectIdentifier();
            String studentID = "cutz";
            JSONReturnable jsonReturn = null;
            System.out.println("=============================  Test Summary Test    ============================\n");
            jsonReturn = professorService.getClassTestSummary(projectID);
            assertEquals("Failed to generate class progress histogram data", 1, jsonReturn.errorCode);
        }

        @Test
        public void testStatisticsData() {
            String projectID = DEBUG ? "cs252" : proj1.getProjectIdentifier();
            String studentID = "cutz";
            JSONReturnable jsonReturn = null;
            System.out.println("=============================  Statistics Test    ============================\n");
            jsonReturn = courseService.getStudentStatistics(projectID, studentID);
            assertEquals("Failed to generate individual statistics", 1, jsonReturn.errorCode);
        }

        @Test
        public void testAdditionDeletionData() {
            String projectID = DEBUG ? "cs252" : proj1.getProjectIdentifier();
            String studentID = "cutz";
            JSONReturnable jsonReturn = null;
            System.out.println("=============================   Addition Deletion Test    ============================\n");
            jsonReturn = courseService.getStudentAdditionsAndDeletions(projectID, studentID);
            assertEquals("Failed to generate addition/deletion data", 1, jsonReturn.errorCode);
        }

        @Test
        public void testCommitListData() {
            String projectID = DEBUG ? "cs252" : proj1.getProjectIdentifier();
            String studentID = "cutz";
            JSONReturnable jsonReturn = null;
            System.out.println("=============================  Git Commit List Test    ============================\n");
            jsonReturn = courseService.getStudentCommitList(projectID, studentID);
            assertEquals("Failed to generate git commit list data", 1, jsonReturn.errorCode);
        }


        @Test
        public void testCommitCountData() {
            String projectID = DEBUG ? "cs252" : proj1.getProjectIdentifier();
            String studentID = "cutz";
            JSONReturnable jsonReturn = null;
            System.out.println("=============================  Git Commit Count Test    ============================\n");
            jsonReturn = courseService.getStudentCommitCounts(projectID, studentID);
            assertEquals("Failed to generate git commit count data", 1, jsonReturn.errorCode);
        }

        @Test
        public void testCommitVelocity() {
            String projectID = DEBUG ? "cs252" : proj1.getProjectIdentifier();
            String studentID = "cutz";
            JSONReturnable jsonReturn = null;
            System.out.println("=============================  Commit Velocity Test    ============================\n");
            jsonReturn = courseService.getStudentCommitVelocity(projectID, studentID);
            assertEquals("Failed to generate commit velocity data", 1, jsonReturn.errorCode);
        }

        @Test
        public void testClassCheating() {
            String projectID = DEBUG ? "cs252" : proj1.getProjectIdentifier();
            JSONReturnable jsonReturn = null;
            System.out.println("=============================  Class Cheating Test    ============================\n");
            jsonReturn = professorService.getClassCheating(projectID);
            assertEquals("Failed to generate class cheating report", 1, jsonReturn.errorCode);
        }
}
