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

    }

    @After
    public void clearDatabase() {

    }

    /** Empty test prevents error from being thrown over no @Test annotations **/
    @Test
    public void emptyTest() {

    }

    //@Test
    public void testHubCreation(){

    }


}