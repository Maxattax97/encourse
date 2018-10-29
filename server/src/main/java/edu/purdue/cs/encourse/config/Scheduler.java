package edu.purdue.cs.encourse.config;

import edu.purdue.cs.encourse.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class Scheduler {
    @Autowired
    ProfessorService professorService;

    /** Pulls and tests all projects ready to be synchronized every hour **/
    @Scheduled(fixedRate = 3600000)
    public void pullAndTestAllProjects() {
        System.out.println("Scheduled Task Running");
        professorService.pullAndTestAllProjects();
    }
}