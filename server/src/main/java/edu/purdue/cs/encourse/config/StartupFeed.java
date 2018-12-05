package edu.purdue.cs.encourse.config;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.Account;
import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.domain.Section;
import edu.purdue.cs.encourse.domain.Student;
import edu.purdue.cs.encourse.domain.relations.StudentProject;
import edu.purdue.cs.encourse.service.*;
import edu.purdue.cs.encourse.util.ConfigurationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Conditional(value = {ProdProfileCondition.class})
public class StartupFeed implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentProjectRepository studentProjectRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private HelperService helperService;
    
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        feedDatabase();
    }
    
    private void feedDatabase() {
        System.out.println("CONDITIONAL RAN");
        if (adminService.findAllUsers().isEmpty()) {
            adminService.addAccount("0", "grr", "Gustavo", "Rodriguez-Rivera", Account.Role_Names.PROFESSOR, "A", "grr@purdue.edu");
            adminService.addAccount("1", "buckmast", "Jordan", "Buckmaster", Account.Role_Names.ADMIN, "M", "buckmast@purdue.edu");
            adminService.addAccount("2", "kleclain", "Killian", "LeClainche", Account.Role_Names.ADMIN, "A", "kleclain@purdue.edu");
            adminService.addAccount("3", "lee2363", "Jarett", "Lee", Account.Role_Names.ADMIN, "B", "lee2363@purdue.edu");
            adminService.addAccount("4", "montgo38", "Shawn", "Montgomery", Account.Role_Names.ADMIN, "K", "montgo38@purdue.edu");
            adminService.addAccount("5", "reed226", "William", "Reed", Account.Role_Names.TA, "J", "reed226@purdue.edu");
            adminService.addAccount("6", "sullil96", "Ryan", "Sullivan", Account.Role_Names.ADMIN, "P", "sulli196@purdue.edu");

            adminService.addUser("grr", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", "PROFESSOR", false, false, false, true);
            adminService.addUser("buckmast-a", "$2a$04$9c76evM3G9DGPy0SoSvA7uH567Raz6Tuv5vTeV/BxL.3gNSel1POK", "ADMIN", false, false, false, true);
            adminService.addUser("kleclain-a", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", "ADMIN", false, false, false, true);
            adminService.addUser("lee2363-a", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", "ADMIN", false, false, false, true);
            adminService.addUser("montgo38-a", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", "ADMIN", false, false, false, true);
            adminService.addUser("reed226-t", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", "TA", false, false, false, true);
            adminService.addUser("sullil96-a", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", "ADMIN", false, false, false, true);

            Section section;
            Project malloc;
            Project shell;
            if(ConfigurationManager.getInstance().testing) {
                section = adminService.addSection("1001", "Fall2018", "testing", "Systems Programming", "LE1", "MWF 12:30 - 1:20");
                adminService.assignProfessorToCourse("grr", "testing", "Fall2018");
                adminService.assignTeachingAssistantToCourse("reed226-t", "testing", "Fall2018");
                professorService.assignTeachingAssistantToSection("reed226-t", section.getSectionID());

                adminService.addAccount("101", "reed226", "William", "Reed", Account.Role_Names.STUDENT, "J", "reed226@purdue.edu");
                adminService.registerStudentToSection("reed226", section.getSectionID());

                adminService.addAccount("102", "kleclain", "Killian", "LeClainche", Account.Role_Names.STUDENT, "A", "kleclain@purdue.edu");
                adminService.registerStudentToSection("kleclain", section.getSectionID());

                adminService.addAccount("103", "lee2363", "Jarett", "Lee", Account.Role_Names.STUDENT, "B", "lee2363@purdue.edu");
                adminService.registerStudentToSection("lee2363", section.getSectionID());

                adminService.addAccount("104", "montgo38", "Shawn", "Montgomery", Account.Role_Names.STUDENT, "K", "montgo38@purdue.edu");
                adminService.registerStudentToSection("montgo38", section.getSectionID());

                adminService.addAccount("105", "buckmast", "Jordan", "Buckmaster", Account.Role_Names.STUDENT, "M", "buckmast@purdue.edu");
                adminService.registerStudentToSection("buckmast", section.getSectionID());

                adminService.addAccount("106", "sulli196", "Ryan", "Sullivan", Account.Role_Names.STUDENT, "P", "sulli196@purdue.edu");
                adminService.registerStudentToSection("sulli196", section.getSectionID());

                professorService.assignTeachingAssistantToStudentInSection("reed226-t", "kleclain", section.getSectionID());
                professorService.assignTeachingAssistantToStudentInSection("reed226-t", "buckmast", section.getSectionID());
                professorService.assignTeachingAssistantToStudentInSection("reed226-t", "montgo38", section.getSectionID());

                courseService.setSectionRemotePaths("Fall2018", "testing", "/homes/cs252/sourcecontrol/work_2017Fall");
                courseService.setDirectory("Fall2018", "testing");
                shell = professorService.addProject("testing", "Fall2018", "Shell", "lab3-src", "9/24/2018", "10/8/2018", 0);

            }
            else {
                section = adminService.addSection("1001", "Fall2018", "cs252", "Systems Programming", "LE1", "MWF 12:30 - 1:20");
                adminService.assignProfessorToCourse("grr", "cs252", "Fall2018");
                adminService.assignTeachingAssistantToCourse("reed226", "cs252", "Fall2018");
                professorService.assignTeachingAssistantToSection("reed226", section.getSectionID());

                try {
                    BufferedReader fileReader = new BufferedReader(new FileReader("/sourcecontrol/cs252/Fall2018/students.txt"));
                    String student = null;
                    int count = 1;
                    while ((student = fileReader.readLine()) != null && count <= ConfigurationManager.getInstance().limit) {
                        if (student.equals("grr")) {
                            continue;
                        }
                        //adminService.addAccount(Integer.toString(100 + count), student, "Student", Integer.toString(count),
                        adminService.addAccount(Integer.toString(100 + count), student, "Student", student,
                                Account.Role_Names.STUDENT, null, student + "@purdue.edu");
                        adminService.registerStudentToSection(student, section.getSectionID());
                        if (count == 5) {
                            professorService.assignTeachingAssistantToAllStudentsInSection("reed226", section.getSectionID());
                        }
                        count++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                courseService.setSectionRemotePaths("Fall2018", "cs252", "/homes/cs252/sourcecontrol/work");
                courseService.setDirectory("Fall2018", "cs252");
                //Project malloc = professorService.addProject("cs252", "Fall2018", "MyMalloc", "lab1-src", "8/27/2018", "9/10/2018", 0);
                shell = professorService.addProject("cs252", "Fall2018", "Shell", "lab3-src", "9/24/2018", "10/8/2018", 0);
            }
            //professorService.assignProject(malloc.getProjectID());
            professorService.assignProject(shell.getProjectID());
            /*professorService.uploadTestScript(malloc.getProjectID(), "Test1.sh",
                    "#!/bin/bash\nif[[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", false, 5);
            professorService.uploadTestScript(malloc.getProjectID(), "Test2.sh",
                    "#!/bin/bash\nif [[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", false, 10);
            professorService.uploadTestScript(malloc.getProjectID(), "Test3.sh",
                    "#!/bin/bash\nif [[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", false, 10);
            professorService.uploadTestScript(malloc.getProjectID(), "Test4.sh",
                    "#!/bin/bash\nif [[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", false, 10);
            professorService.uploadTestScript(malloc.getProjectID(), "Test5.sh",
                    "#!/bin/bash\nif [[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", false, 20);
            professorService.uploadTestScript(malloc.getProjectID(), "Test6.sh",
                    "#!/bin/bash\nif [[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", true, 10);
            professorService.uploadTestScript(malloc.getProjectID(), "Test7.sh",
                    "#!/bin/bash\nif [[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", true, 20);
            professorService.uploadTestScript(malloc.getProjectID(), "Test8.sh",
                    "#!/bin/bash\nif [[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", true, 20);
            professorService.uploadTestScript(malloc.getProjectID(), "Test9.sh",
                    "#!/bin/bash\nif [[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", true, 25);
            professorService.uploadTestScript(malloc.getProjectID(), "Test10.sh",
                    "#!/bin/bash\nif [[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", true, 30);
            professorService.runTestall(malloc.getProjectID());*/

            //professorService.addTestScript(shell.getProjectID(), "test_redirect_input", false, 2);
            professorService.addTestScript(shell.getProjectID(), "test_redirect_output", false, 2);
            professorService.addTestScript(shell.getProjectID(), "test_redirect_error", false, 1);
            professorService.addTestScript(shell.getProjectID(), "test_redirect_error2", false, 1);
            professorService.addTestScript(shell.getProjectID(), "test_redirect_out_err", false, 2);
            professorService.addTestScript(shell.getProjectID(), "test_redirect_multiple", false, 1);
            professorService.addTestScript(shell.getProjectID(), "test_append_output", false, 1);
            professorService.addTestScript(shell.getProjectID(), "test_pipes1", false, 2);
            professorService.addTestScript(shell.getProjectID(), "test_pipes2", false, 2);
            professorService.addTestScript(shell.getProjectID(), "test_pipes_redirect_out", false, 2);
            professorService.addTestScript(shell.getProjectID(), "test_pipes_redirect_err", false, 2);
            professorService.addTestScript(shell.getProjectID(), "test_background", false, 2);
            //professorService.addTestScript(shell.getProjectID(), "test_zombie", false, 1);
            professorService.addTestScript(shell.getProjectID(), "test_printenv", false, 1);
            //professorService.addTestScript(shell.getProjectID(), "test_setenv", false, 0.5);
            professorService.addTestScript(shell.getProjectID(), "test_unsetenv", false, 0.5);
            //professorService.addTestScript(shell.getProjectID(), "test_source", false, 2);
            professorService.addTestScript(shell.getProjectID(), "test_cd1", false, 0.5);
            professorService.addTestScript(shell.getProjectID(), "test_cd2", false, 0.5);
            professorService.addTestScript(shell.getProjectID(), "test_cd3", false, 0.5);
            //professorService.addTestScript(shell.getProjectID(), "test_cd4", false, 0.5);
            professorService.addTestScript(shell.getProjectID(), "test_parsing1", false, 0.5);
            professorService.addTestScript(shell.getProjectID(), "test_parsing2", false, 0.5);
            //professorService.addTestScript(shell.getProjectID(), "test_quotes1", false, 1);
            //professorService.addTestScript(shell.getProjectID(), "test_quotes2", false, 1);
            professorService.addTestScript(shell.getProjectID(), "test_escape", false, 2);
            //professorService.addTestScript(shell.getProjectID(), "test_subshell", false, 10);
            professorService.addTestScript(shell.getProjectID(), "test_env_expand1", false, 1);
            professorService.addTestScript(shell.getProjectID(), "test_env_expand2", false, 1);
            professorService.addTestScript(shell.getProjectID(), "test_env_var_shell", false, 1);
            professorService.addTestScript(shell.getProjectID(), "test_env_var_dollar", false, 1);
            professorService.addTestScript(shell.getProjectID(), "test_env_var_question", false, 1);
            professorService.addTestScript(shell.getProjectID(), "test_env_var_bang", false, 1);
            //professorService.addTestScript(shell.getProjectID(), "test_env_var_uscore", false, 1);
            professorService.addTestScript(shell.getProjectID(), "test_wildcards1", false, 3);
            professorService.addTestScript(shell.getProjectID(), "test_wildcards2", false, 1);
            professorService.addTestScript(shell.getProjectID(), "test_wildcards3", false, 1);
            professorService.addTestScript(shell.getProjectID(), "test_wildcards4", false, 1);
            professorService.addTestScript(shell.getProjectID(), "test_wildcards5", false, 1);
            professorService.addTestScript(shell.getProjectID(), "test_wildcards6", false, 1);
            professorService.addTestScript(shell.getProjectID(), "test_wildcards7", false, 1);
            //professorService.addTestScript(shell.getProjectID(), "test_tilde", false, 2);
            //professorService.addTestScript(shell.getProjectID(), "test_robustness", false, 10);
            professorService.runHistoricTestall(shell.getProjectID());

            /*List<StudentProject> projects = studentProjectRepository.findByIdProjectID(malloc.getProjectID());
            for(StudentProject p : projects) {
                Student student = studentRepository.findByUserID(p.getStudentID());
                professorService.updateStudentInformation(p.getProjectID(), student.getUserName());
            }*/
            List<StudentProject> projects = studentProjectRepository.findByIdProjectID(shell.getProjectID());
            for (StudentProject p : projects) {
                Student student = studentRepository.findByUserID(p.getStudentID());
                helperService.updateStudentInformation(p.getProjectID(), student.getUserName());
            }
        }
        else {
            List<Project> projects = projectRepository.findBySemesterAndCourseID("Fall2018", "cs252");
            for(Project p : projects) {
                p.setTestRate(4);
                p.setTestCount(0);
                projectRepository.save(p);
                List<StudentProject> studentProjects = studentProjectRepository.findByIdProjectID(p.getProjectID());
                for(StudentProject s : studentProjects) {
                    Student student = studentRepository.findByUserID(s.getStudentID());
                    helperService.updateStudentInformation(s.getProjectID(), student.getUserName());
                }
            }
        }
    }
}

abstract class ProfileCondition extends SpringBootCondition {
    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        if (matchProfiles(conditionContext.getEnvironment())) {
            return ConditionOutcome.match("A local profile has been found.");
        }
        return ConditionOutcome.noMatch("No local profiles found.");
    }

    protected abstract boolean matchProfiles(final Environment environment);
}

class ProdProfileCondition extends ProfileCondition {
    public boolean matchProfiles(final Environment environment) {
        return !Arrays.stream(environment.getActiveProfiles()).anyMatch(prof -> {
            return prof.equals("dev");
        });
    }
}
