package edu.purdue.cs.encourse;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.Account;
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
        adminService.addAccount("0", "grr", "Gustavo", "Rodriguez-Rivera", Account.Role_Names.PROFESSOR, "A", "grr@purdue.edu");
        adminService.addAccount("1", "reed226-t", "William", "Reed", Account.Role_Names.TA, "J", "reed226@purdue.edu");
        adminService.addAccount("2", "kleclain-a", "Killian", "LeClainche", Account.Role_Names.ADMIN, "A", "kleclain@purdue.edu");

        sect1 = adminService.addSection("1001", "Fall2018", "testing", "Systems Programming", "LE1", "MWF 12:30 - 1:20");
        adminService.assignProfessorToCourse("grr", "testing", "Fall2018");

        adminService.addAccount("101", "reed226", "William", "Reed", Account.Role_Names.STUDENT, "J", "reed226@purdue.edu");
        adminService.registerStudentToSection("reed226", sect1.getSectionID());

        adminService.addAccount("102", "kleclain", "Killian", "LeClainche", Account.Role_Names.STUDENT, "A", "kleclain@purdue.edu");
        adminService.registerStudentToSection("kleclain", sect1.getSectionID());

        adminService.addAccount("103", "lee2363", "Jarett", "Lee", Account.Role_Names.STUDENT, "B", "lee2363@purdue.edu");
        adminService.registerStudentToSection("lee2363", sect1.getSectionID());

        adminService.addAccount("104", "montgo38", "Shawn", "Montgomery", Account.Role_Names.STUDENT, "K", "montgo38@purdue.edu");
        adminService.registerStudentToSection("montgo38", sect1.getSectionID());

        adminService.addAccount("105", "buckmast", "Jordan", "Buckmaster", Account.Role_Names.STUDENT, "M", "buckmast@purdue.edu");
        adminService.registerStudentToSection("buckmast", sect1.getSectionID());

        adminService.addAccount("106", "sulli196", "Ryan", "Sullivan", Account.Role_Names.STUDENT, "P", "sulli196@purdue.edu");
        adminService.registerStudentToSection("sulli196", sect1.getSectionID());

        courseService.setSectionRemotePaths("Fall2018", "testing", "/homes/cs252/sourcecontrol/work_2017Fall");
        courseService.setDirectory("Fall2018", "testing");
        proj1 = professorService.addProject("testing", "Fall2018", "Shell", "lab3-src", "9/24/2018", "10/8/2018", 0);
        professorService.assignProject(proj1.getProjectID());
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
        assertEquals(0, adminService.assignTeachingAssistantToCourse("reed226-t", "testing", "Fall2018"));
        assertEquals(-3,professorService.assignTeachingAssistantToStudentInSection("reed226-t", "montgo38", sect1.getSectionID()));
        assertEquals(0, professorService.assignTeachingAssistantToSection("reed226-t", sect1.getSectionID()));
        assertEquals(0, professorService.assignTeachingAssistantToStudentInSection("reed226-t", "montgo38", sect1.getSectionID()));
        List<TeachingAssistantStudent> assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantID("1");
        assertEquals(1, assignments.size());
        assertEquals("104", assignments.get(0).getStudentID());
        assertEquals(0, professorService.assignTeachingAssistantToStudentInSection("reed226-t", "kleclain", sect1.getSectionID()));
        assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantID("1");
        assertEquals(2, assignments.size());
        assertEquals(0, professorService.assignTeachingAssistantToStudentInSection("reed226-t", "kleclain", sect1.getSectionID()));
        assignments = teachingAssistantStudentRepository.findByIdTeachingAssistantID("1");
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