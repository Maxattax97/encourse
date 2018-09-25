package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.domain.*;
import lombok.*;

import java.util.*;

public interface ProfService {
    int createHub(String courseID);
    int cloneProjects(String courseID, String projectID);
    int pullProjects(String courseID, String projectID);

    int addProject(String courseID, String semester, String projectName, String repoName, String startDate, String dueDate);
    int modifyProject(String projectID, String field, String value);
    int setSectionRemotePaths(String courseID, String remotePath);

    int countAllCommits(String courseID, String projectID);
    int countAllCommitsByDay(String courseID, String projectID);
    int countStudentCommitsByDay(String courseID, String projectID, String userName);
    int listStudentCommitsByTime(String courseID, String projectID, String userName);

    int assignTeachingAssistantToStudent(String teachAssistUserName, String studentUserName);

    int testPythonDirectory();
    int getCommitData();
}
