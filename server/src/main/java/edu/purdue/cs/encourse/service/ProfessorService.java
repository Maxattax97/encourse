package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.util.JSONReturnable;
import lombok.NonNull;

import java.io.File;

public interface ProfessorService {
    int addProject(@NonNull String courseID, @NonNull String semester, @NonNull String projectName, String repoName, String startDate, String dueDate);
    int assignProject(@NonNull String projectID);
    int assignProjectToStudent(@NonNull String projectID, @NonNull String userName);
    int modifyProject(@NonNull String projectID, @NonNull String field, String value);
    int cloneProjects(@NonNull String projectID);
    int pullProjects(@NonNull String projectID);

    JSONReturnable countAllCommits(@NonNull String projectID);
    JSONReturnable countAllCommitsByDay(@NonNull String projectID);
    JSONReturnable countStudentCommitsByDay(@NonNull String projectID, @NonNull String userName);
    JSONReturnable listStudentCommitsByTime(@NonNull String projectID, @NonNull String userName);

    int uploadTestScript(@NonNull String projectID, @NonNull String testName, @NonNull String testContents);
    int runTestall(@NonNull String projectID);
    int runTestallForStudent(@NonNull String projectID, @NonNull String userName);

    int pullAndTestAllProjects();

    int assignTeachingAssistantToStudent(@NonNull String teachAssistUserName, @NonNull String studentUserName);

    int testPythonDirectory();
    JSONReturnable getCommitData(@NonNull String filePath);
    JSONReturnable getProgressHistogram(@NonNull String filePath, @NonNull String studentID);
}