package edu.purdue.cs.encourse.config;

import edu.purdue.cs.encourse.service.ProjectAnalysisService;
import edu.purdue.cs.encourse.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Class that contains tasks which are scheduled to run automatically
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
public class Scheduler {
    
    @Autowired
    private ProjectAnalysisService projectService;

    /** Pulls and tests all projects ready to be synchronized with 1 minute delays **/
    @Scheduled(fixedDelay = 60000 * 30)
    public void pullAndTestAllProjects() {
        System.out.println("Scheduled Task Running");
        //professorService.pullAndTestAllProjects();
        projectService.analyzeProjects();
    }
}
