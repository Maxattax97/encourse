package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.domain.*;
import lombok.*;

import java.util.*;

public interface ProfService {
    int createHub(String courseID);
    int cloneProjects(String courseID, String projectID);
    int pullProjects(String courseID, String projectID);

    int addProject(String courseID, String semester, String projectName, String repoName, String dueDate, String dueTime);
    int modifyProject(String projectID, String field, String value);

    int countAllCommits(String courseID, String projectID);
    int countAllCommitsByDay(String courseID, String projectID);
    int countStudentCommitsByDay(String courseID, String projectID, String userName);
    int listStudentCommitsByTime(String courseID, String projectID, String userName);
}
