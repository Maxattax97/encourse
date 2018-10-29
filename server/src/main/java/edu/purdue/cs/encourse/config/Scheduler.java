package edu.purdue.cs.encourse.config;

import edu.purdue.cs.encourse.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class Scheduler {
    @Autowired
    ProfessorService professorService;

    @Scheduled(fixedRate = 1000)
    public void pullAndTestAllProjects() {
        System.out.println("\n\n\nScheduled Task Running\n\n\n");
        professorService.pullAndTestAllProjects();
    }
}
