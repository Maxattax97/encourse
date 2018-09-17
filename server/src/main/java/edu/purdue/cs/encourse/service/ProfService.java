package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.domain.*;
import lombok.*;

import java.util.*;

public interface ProfService {
    int createHub(String sectionID, List<Student> students);
    int cloneProjects(String sectionID, String projectID, List<Student> students);
    int pullProjects(String sectionID, String projectID, List<Student> students);

    int addProject(String courseID, String semester, String projectName, String repoName, String dueDate, String dueTime);
    int modifyProject(String projectID, String field, String value);
}
