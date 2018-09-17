package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.service.ProfService;
import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.List;

public class ProfServiceImpl implements ProfService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SectionRepository sectionRepository;

    public int createHub(String sectionID, List<Student> students){
        Section section = sectionRepository.findBySectionIdentifier(sectionID);
        if(section == null) {
            return -1;
        }
        if(!(new File("/sourcecontrol/" + section.getCourseID() + "/" + section.getSemester()).mkdirs())) {
            return -3;
        }
        section.setCourseHub("/sourcecontrol/" + section.getCourseID() + "/" + section.getSemester());
        for(Student s : students){
            if(!(new File(section.getCourseHub() + "/" + s.getUserName()).mkdir())) {
                return -4;
            }
        }
        return 0;
    }

    public int cloneProjects(String sectionID, String projectID, List<Student> students){
        Section section = sectionRepository.findBySectionIdentifier(sectionID);
        if(section == null) {
            return -1;
        }
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return -2;
        }
        if(section.getCourseHub() == null) {
            return -3;
        }
        if(project.getRepoName() == null) {
            return -4;
        }
        if(section.getRemotePath() == null) {
            return -5;
        }
        ProcessBuilder builder = new ProcessBuilder();
        for(Student s : students){
            String destPath = (section.getCourseHub() + "/" + s.getUserName());
            String repoPath = (section.getRemotePath() + "/" + s.getUserName() + "/" + project.getRepoName() + ".git");
            builder.command("cloneProject.sh", destPath, repoPath);
        }
        builder.command("setPermissions.sh", section.getCourseID());
        return 0;
    }

    public int pullProjects(String sectionID, String projectID, List<Student> students){
        Section section = sectionRepository.findBySectionIdentifier(sectionID);
        if(section == null) {
            return -1;
        }
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return -2;
        }
        if(section.getCourseHub() == null) {
            return -3;
        }
        if(project.getRepoName() == null) {
            return -4;
        }
        ProcessBuilder builder = new ProcessBuilder();
        for(Student s : students){
            String destPath = (section.getCourseHub() + "/" + s.getUserName() + "/" + project.getRepoName());
            builder.command("pullProject.sh", destPath);
        }
        builder.command("setPermissions.sh", section.getCourseID());
        return 0;
    }

    public int addProject(String courseID, String semester, String projectName, String repoName, String dueDate, String dueTime) {
        Project project = new Project(courseID, semester, projectName, repoName, dueDate, dueTime);
        if(projectRepository.existsByProjectIdentifier(project.getProjectIdentifier())) {
            return -1;
        }
        if(projectRepository.save(project) == null) {
            return -2;
        }
        return 0;
    }

    public int modifyProject(String projectID, String field, String value) {
        Project project = projectRepository.findByProjectIdentifier(projectID);
        if(project == null) {
            return -1;
        }
        switch(field) {
            case "dueDate": project.setDueDate(value); break;
            case "dueTime": project.setDueTime(value); break;
            case "repoName": project.setRepoName(value); break;
            default: return -2;
        }
        return 0;
    }
}
