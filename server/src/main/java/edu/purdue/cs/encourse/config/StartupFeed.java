package edu.purdue.cs.encourse.config;

import edu.purdue.cs.encourse.service.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StartupFeed implements ApplicationListener<ApplicationReadyEvent> {
    
    @Autowired
    private AdminServiceImpl adminService;
    
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        feedDatabase();
    }
    
    private void feedDatabase() {
        if (adminService.findAllUsers().isEmpty()) {
            adminService.addAccount("0", "grr", "Gustavo", "Rodriguez-Rivera", 2, "A", "grr@purdue.edu");
            adminService.addAccount("1", "buckmast", "Jordan", "Buckmaster", 3, "M", "buckmast@purdue.edu");
            adminService.addAccount("2", "kleclain", "Killian", "LeClainche", 3, "A", "kleclain@purdue.edu");
            adminService.addAccount("3", "lee2363", "Jarett", "Lee", 3, "B", "lee2363@purdue.edu");
            adminService.addAccount("4", "montgo38", "Shawn", "Montgomery", 3, "K", "montgo38@purdue.edu");
            adminService.addAccount("5", "reed226", "William", "Reed", 3, "J", "reed226@purdue.edu");
            adminService.addAccount("6", "sullil96", "Ryan", "Sullivan", 3, "P", "sulli196@purdue.edu");

            //password: encourse
            adminService.addUser("grr", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", false, false, false, true);
            adminService.addUser("buckmast", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", false, false, false, true);
            adminService.addUser("kleclain", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", false, false, false, true);
            adminService.addUser("lee2363", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", false, false, false, true);
            adminService.addUser("montgo38", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", false, false, false, true);
            adminService.addUser("reed226", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", false, false, false, true);
            adminService.addUser("sullil96", "$2a$04$KDYkLNaDhiKvMqJhRQ58iumiMAd8Rxf4az3COnKsPKNlHcK7PMjs6", false, false, false, true);
        }
    }
}
