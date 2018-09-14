package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.service.ProfService;
import edu.purdue.cs.encourse.domain.*;

import java.io.File;
import java.util.List;

public class ProfServiceImpl implements ProfService {
    public int createHub(Section section, List<Student> students){
        if(!(new File("/sourcecontrol/" + section.getCourseID() + "/" + section.getSemester()).mkdirs())) {
            return -1;
        }
        section.setCourseHub("/sourcecontrol/" + section.getCourseID() + "/" + section.getSemester());
        for(Student s : students){
            if(!(new File(section.getCourseHub() + "/" + s.getUserName()).mkdir())) {
                return -1;
            }
        }
        return 0;
    }

    public int cloneProjects(Section section, Project project, List<Student> students){
        ProcessBuilder builder = new ProcessBuilder();
        for(Student s : students){
            String destPath = (section.getCourseHub() + "/" + s.getUserName());
            String repoPath = (section.getRemotePath() + "/" + s.getUserName() + "/" + project.getRepoName() + ".git");
            builder.command("cloneProject.sh", destPath, repoPath);
        }
        builder.command("setPermissions.sh", section.getCourseHub(), section.getCourseID());
        return 0;
    }

    public int pullProjects(Section section, Project project, List<Student> students){
        ProcessBuilder builder = new ProcessBuilder();
        for(Student s : students){
            String destPath = (section.getCourseHub() + "/" + s.getUserName() + "/" + project.getRepoName());
            builder.command("pullProject.sh", destPath);
        }
        builder.command("setPermissions.sh", section.getCourseHub(), section.getCourseID());
        return 0;
    }
}
