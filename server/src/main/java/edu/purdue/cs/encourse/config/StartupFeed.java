package edu.purdue.cs.encourse.config;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.domain.Student;
import edu.purdue.cs.encourse.domain.relations.StudentProject;
import edu.purdue.cs.encourse.service.*;
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
    
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        feedDatabase();
    }
    
    private void feedDatabase() {
        System.out.println("CONDITIONAL RAN");
        if (adminService.findAllUsers().isEmpty()) {
            adminService.addAccount("0", "grr", "Gustavo", "Rodriguez-Rivera", "Professor", "A", "grr@purdue.edu");
            adminService.addAccount("1", "buckmast", "Jordan", "Buckmaster", "Admin", "M", "buckmast@purdue.edu");
            adminService.addAccount("2", "kleclain", "Killian", "LeClainche", "Admin", "A", "kleclain@purdue.edu");
            adminService.addAccount("3", "lee2363", "Jarett", "Lee", "Admin", "B", "lee2363@purdue.edu");
            adminService.addAccount("4", "montgo38", "Shawn", "Montgomery", "Admin", "K", "montgo38@purdue.edu");
            adminService.addAccount("5", "reed226", "William", "Reed", "Admin", "J", "reed226@purdue.edu");
            adminService.addAccount("6", "sullil96", "Ryan", "Sullivan", "Admin", "P", "sulli196@purdue.edu");

            adminService.addUser("grr", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", "PROFESSOR", false, false, false, true);
            adminService.addUser("buckmast", "$2a$04$9c76evM3G9DGPy0SoSvA7uH567Raz6Tuv5vTeV/BxL.3gNSel1POK", "ADMIN", false, false, false, true);
            adminService.addUser("kleclain", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", "ADMIN", false, false, false, true);
            adminService.addUser("lee2363", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", "ADMIN", false, false, false, true);
            adminService.addUser("montgo38", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", "ADMIN", false, false, false, true);
            adminService.addUser("reed226", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", "ADMIN", false, false, false, true);
            adminService.addUser("sullil96", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", "ADMIN", false, false, false, true);

            adminService.addSection("1001", "Fall2018", "cs252", "Systems Programming", "LE1");
            adminService.assignProfessorToCourse("grr", "cs252", "Fall2018");

            // DELETE LATER
            adminService.addAccount("500", "test1", "Ryan", "Sullivan", "Student", "P", "sulli196@purdue.edu");
            adminService.addAccount("600", "test2", "Ryan", "Sullivan", "Student", "P", "sulli196@purdue.edu");
            adminService.addUser("test1", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", "STUDENT", false, false, false, true);
            adminService.addUser("test2", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", "STUDENT", false, false, false, true);
            adminService.registerStudentToSection("test1", "cs252", "Fall2018", "LE1");
            adminService.registerStudentToSection("test2", "cs252", "Fall2018", "LE1");

            try {
                BufferedReader fileReader = new BufferedReader(new FileReader("/sourcecontrol/cs252/Fall2018/students.txt"));
                String student = null;
                int count = 1;
                while((student = fileReader.readLine()) != null) {
                    if(student.equals("grr")) {
                        continue;
                    }
                    //adminService.addAccount(Integer.toString(100 + count), student, "Student", Integer.toString(count),
                    adminService.addAccount(Integer.toString(100 + count), student, "Student", student,
                            "Student", null, student + "@purdue.edu");
                    adminService.registerStudentToSection(student, "cs252", "Fall2018", "LE1");
                    count++;
                }
            }
            catch(IOException e) {
                e.printStackTrace();
            }

            courseService.setSectionRemotePaths("Fall2018", "cs252", "/homes/cs252/sourcecontrol/work");
            courseService.setDirectory("Fall2018", "cs252");
            professorService.addProject("cs252", "Fall2018", "MyMalloc", "lab1-src", "8/27/2018", "9/10/2018", 0);
            professorService.addProject("cs252", "Fall2018", "Shell", "lab3-src", "9/24/2018", "10/8/2018", 0);
            professorService.assignProject(Project.createProjectID("cs252", "Fall2018", "MyMalloc"));
            professorService.assignProject(Project.createProjectID("cs252", "Fall2018", "Shell"));
            professorService.uploadTestScript(Project.createProjectID("cs252", "Fall2018", "MyMalloc"), "Test1.sh",
                    "#!/bin/bash\nif[[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", false, 5);
            professorService.uploadTestScript(Project.createProjectID("cs252", "Fall2018", "MyMalloc"), "Test2.sh",
                    "#!/bin/bash\nif [[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", false, 10);
            professorService.uploadTestScript(Project.createProjectID("cs252", "Fall2018", "MyMalloc"), "Test3.sh",
                    "#!/bin/bash\nif [[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", false, 5);
            professorService.uploadTestScript(Project.createProjectID("cs252", "Fall2018", "MyMalloc"), "Test4.sh",
                    "#!/bin/bash\nif [[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", false, 10);
            professorService.uploadTestScript(Project.createProjectID("cs252", "Fall2018", "MyMalloc"), "Test5.sh",
                    "#!/bin/bash\nif [[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", false, 20);
            professorService.uploadTestScript(Project.createProjectID("cs252", "Fall2018", "MyMalloc"), "Test6.sh",
                    "#!/bin/bash\nif [[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", true, 5);
            professorService.uploadTestScript(Project.createProjectID("cs252", "Fall2018", "MyMalloc"), "Test7.sh",
                    "#!/bin/bash\nif [[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", true, 10);
            professorService.uploadTestScript(Project.createProjectID("cs252", "Fall2018", "MyMalloc"), "Test8.sh",
                    "#!/bin/bash\nif [[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", true, 5);
            professorService.uploadTestScript(Project.createProjectID("cs252", "Fall2018", "MyMalloc"), "Test9.sh",
                    "#!/bin/bash\nif [[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", true, 10);
            professorService.uploadTestScript(Project.createProjectID("cs252", "Fall2018", "MyMalloc"), "Test10.sh",
                    "#!/bin/bash\nif [[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", true, 20);
            professorService.runTestall(Project.createProjectID("cs252", "Fall2018", "MyMalloc"));

            professorService.uploadTestScript(Project.createProjectID("cs252", "Fall2018", "Shell"), "Test1.sh",
                    "#!/bin/bash\nif[[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", false, 5);
            professorService.uploadTestScript(Project.createProjectID("cs252", "Fall2018", "Shell"), "Test2.sh",
                    "#!/bin/bash\nif [[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", false, 10);
            professorService.uploadTestScript(Project.createProjectID("cs252", "Fall2018", "Shell"), "Test3.sh",
                    "#!/bin/bash\nif [[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", false, 5);
            professorService.uploadTestScript(Project.createProjectID("cs252", "Fall2018", "Shell"), "Test4.sh",
                    "#!/bin/bash\nif [[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", false, 10);
            professorService.uploadTestScript(Project.createProjectID("cs252", "Fall2018", "Shell"), "Test5.sh",
                    "#!/bin/bash\nif [[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", false, 20);
            professorService.uploadTestScript(Project.createProjectID("cs252", "Fall2018", "Shell"), "Test6.sh",
                    "#!/bin/bash\nif [[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", true, 5);
            professorService.uploadTestScript(Project.createProjectID("cs252", "Fall2018", "Shell"), "Test7.sh",
                    "#!/bin/bash\nif [[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", true, 10);
            professorService.uploadTestScript(Project.createProjectID("cs252", "Fall2018", "Shell"), "Test8.sh",
                    "#!/bin/bash\nif [[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", true, 5);
            professorService.uploadTestScript(Project.createProjectID("cs252", "Fall2018", "Shell"), "Test9.sh",
                    "#!/bin/bash\nif [[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", true, 10);
            professorService.uploadTestScript(Project.createProjectID("cs252", "Fall2018", "Shell"), "Test10.sh",
                    "#!/bin/bash\nif [[ $(($RANDOM % 2)) == 0 ]]\nthen echo \"\"\nelse echo \"Failure\"\nfi\n", true, 20);
            professorService.runTestall(Project.createProjectID("cs252", "Fall2018", "Shell"));

            List<StudentProject> projects = studentProjectRepository.findByIdProjectIdentifier(Project.createProjectID("cs252", "Fall2018", "MyMalloc"));
            for(StudentProject p : projects) {
                Student student = studentRepository.findByUserID(p.getStudentID());
                professorService.getStatistics(p.getProjectIdentifier(), student.getUserName());
            }
            projects = studentProjectRepository.findByIdProjectIdentifier(Project.createProjectID("cs252", "Fall2018", "Shell"));
            for(StudentProject p : projects) {
                Student student = studentRepository.findByUserID(p.getStudentID());
                professorService.getStatistics(p.getProjectIdentifier(), student.getUserName());
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
