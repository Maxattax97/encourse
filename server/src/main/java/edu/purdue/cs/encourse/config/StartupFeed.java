package edu.purdue.cs.encourse.config;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.domain.Student;
import edu.purdue.cs.encourse.domain.relations.StudentProject;
import edu.purdue.cs.encourse.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Component
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

            adminService.assignProfessorToCourse("grr", "cs252", "Fall2018");
            adminService.addSection("1001", "Fall2018", "cs252", "Systems Programming", "LE1");

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
            professorService.addProject("cs252", "Fall2018", "MyMalloc", "lab1-src", "8/27/2018", "9/10/2018");
            professorService.addProject("cs252", "Fall2018", "Shell", "lab3-src", "9/24/2018", "10/8/2018");
            professorService.assignProject(Project.createProjectID("cs252", "Fall2018", "MyMalloc"));
            professorService.assignProject(Project.createProjectID("cs252", "Fall2018", "Shell"));

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
