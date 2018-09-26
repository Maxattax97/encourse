package edu.purdue.cs.encourse.config;

import edu.purdue.cs.encourse.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StartupFeed implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ProfService profService;
    
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        feedDatabase();
    }
    
    private void feedDatabase() {
        if (adminService.findAllUsers().isEmpty()) {
            adminService.addAccount("0", "grr", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", "Gustavo", "Rodriguez-Rivera", "Professor", "A", "grr@purdue.edu");
            adminService.addAccount("1", "buckmast", "$2a$04$9c76evM3G9DGPy0SoSvA7uH567Raz6Tuv5vTeV/BxL.3gNSel1POK", "Jordan", "Buckmaster", "Admin", "M", "buckmast@purdue.edu");
            adminService.addAccount("2", "kleclain", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", "Killian", "LeClainche", "Admin", "A", "kleclain@purdue.edu");
            adminService.addAccount("3", "lee2363", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", "Jarett", "Lee", "Admin", "B", "lee2363@purdue.edu");
            adminService.addAccount("4", "montgo38", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", "Shawn", "Montgomery", "Admin", "K", "montgo38@purdue.edu");
            adminService.addAccount("5", "reed226", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", "William", "Reed", "Admin", "J", "reed226@purdue.edu");
            adminService.addAccount("6", "sullil96", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", "Ryan", "Sullivan", "Admin", "P", "sulli196@purdue.edu");

            adminService.addUser("grr", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", false, false, false, true);
            adminService.addUser("buckmast", "$2a$04$9c76evM3G9DGPy0SoSvA7uH567Raz6Tuv5vTeV/BxL.3gNSel1POK", false, false, false, true);
            adminService.addUser("kleclain", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", false, false, false, true);
            adminService.addUser("lee2363", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", false, false, false, true);
            adminService.addUser("montgo38", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", false, false, false, true);
            adminService.addUser("reed226", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", false, false, false, true);
            adminService.addUser("sullil96", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", false, false, false, true);
        }
        adminService.addAccount("101", "apolcyn", "a", "Student", "One", "Student", null, "purdue.edu");
        adminService.addAccount("102", "brookea", "b", "Student", "Two", "Student", null, "purdue.edu");
        adminService.addAccount("103", "hayc", "c", "Student", "Three", "Student", null, "purdue.edu");
        adminService.addAccount("104", "jdsheple", "d", "Student", "Four", "Student", null, "purdue.edu");
        adminService.addAccount("105", "ko76", "e", "Student", "Five", "Student", null, "purdue.edu");
        adminService.addAccount("106", "mbounab", "f", "Student", "Six", "Student", null, "purdue.edu");
        adminService.addAccount("107", "oneil4", "g", "Student", "Seven", "Student", null, "purdue.edu");
        adminService.addAccount("108", "riggs4", "h", "Student", "Eight", "Student", null, "purdue.edu");
        adminService.addAccount("109", "son35", "i", "Student", "Nine", "Student", null, "purdue.edu");
        adminService.addAccount("110", "varleyj", "j", "Student", "Ten", "Student", null, "purdue.edu");

        adminService.addSection("1001", "Fall2018", "cs252", "Systems Programming", "Lab1");
        adminService.addSection("1002", "Fall2018", "cs252", "Systems Programming", "Lab2");
        adminService.addSection("1003", "Fall2018", "cs252", "Systems Programming", "Lab3");

        adminService.registerStudentToSection("apolcyn", "cs252", "Fall2018", "Lab1");
        adminService.registerStudentToSection("brookea", "cs252", "Fall2018", "Lab2");
        adminService.registerStudentToSection("hayc", "cs252", "Fall2018", "Lab3");
        adminService.registerStudentToSection("jdsheple", "cs252", "Fall2018", "Lab1");
        adminService.registerStudentToSection("ko76", "cs252", "Fall2018", "Lab2");
        adminService.registerStudentToSection("mbounab", "cs252", "Fall2018", "Lab3");
        adminService.registerStudentToSection("oneil4", "cs252", "Fall2018", "Lab1");
        adminService.registerStudentToSection("riggs4", "cs252", "Fall2018", "Lab2");
        adminService.registerStudentToSection("son35", "cs252", "Fall2018", "Lab3");
        adminService.registerStudentToSection("varleyj", "cs252", "Fall2018", "Lab1");

        adminService.assignProfessorToCourse("grr", "cs252", "Fall2018");

        profService.setSectionRemotePaths("cs252", "/homes/cs252/sourcecontrol/work");
        profService.setHub("cs252");
        profService.addProject("cs252", "Fall2018", "MyMalloc", "lab1-src", "8/25/2018", "9/06/2018");
    }
}
