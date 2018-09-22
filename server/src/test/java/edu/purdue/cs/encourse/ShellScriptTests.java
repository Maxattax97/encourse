package edu.purdue.cs.encourse;

import edu.purdue.cs.encourse.database.AccountRepository;
import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

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

    @Before
    public void populateDatabase() {
        professorRepository.save(new Professor("1", "grr", "Gustavo",
                "Rodriguez-Rivera", null, "grr@purdue.edu"));

        sectionRepository.save(new Section("11", "Fall2018", "cs240",
                "Programming in C", "LE1"));

        studentRepository.save(new Student("2", "reed226", "William",
                "Reed", "J", "reed226@purdue.edu"));

        studentRepository.save(new Student("3", "kleclain", "Killian",
                "LeClainche", "A", "reed226@purdue.edu"));

        projectRepository.save(new Project("cs240", "Fall2018", "Shell",
                "lab1-src.git", "9/10/2018", "9/20/2018"));

    }

    @After
    public void clearDatabase() {
        professorRepository.deleteAll();
        sectionRepository.deleteAll();
        studentRepository.deleteAll();
        projectRepository.deleteAll();

    }

    @Test
    public void testHubCreationScript() {

    }


}