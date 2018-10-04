package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.util.JSONReturnable;
import lombok.NonNull;
import org.json.simple.JSONArray;

import java.io.File;

public interface ProfessorService {
    int addProject(@NonNull String courseID, @NonNull String semester, @NonNull String projectName, String repoName, String startDate, String dueDate);
    int assignProject(@NonNull String projectID);
    int assignProjectToStudent(@NonNull String projectID, @NonNull String userName);
    int modifyProject(@NonNull String projectID, @NonNull String field, String value);
    int cloneProjects(@NonNull String projectID);
    int pullProjects(@NonNull String projectID);

    JSONReturnable getStudentProgress(@NonNull String projectID, @NonNull String userName);
    JSONReturnable getAdditionsAndDeletions(@NonNull String projectID, @NonNull String userName);
    JSONReturnable getStatistics(@NonNull String projectID, @NonNull String userName);
    JSONReturnable getCommitList(@NonNull String projectID, @NonNull String userName);
    JSONReturnable getClassProgress(@NonNull String projectID);
    JSONReturnable getCommitCounts(@NonNull String projectID, @NonNull String userName);

    String countAllCommits(@NonNull String projectID);
    String countAllCommitsByDay(@NonNull String projectID);
    String countStudentCommitsByDay(@NonNull String projectID, @NonNull String userName);
    String listAllCommitsByTime(@NonNull String projectID);
    String listStudentCommitsByTime(@NonNull String projectID, @NonNull String userName);

    int uploadTestScript(@NonNull String projectID, @NonNull String testName, @NonNull String testContents, boolean isHidden, int points);
    int modifyTestScript(@NonNull String projectID, @NonNull String testName, @NonNull String field, @NonNull String value);
    int runTestall(@NonNull String projectID);
    int runTestallForStudent(@NonNull String projectID, @NonNull String userName);

    int pullAndTestAllProjects();

    int assignTeachingAssistantToStudent(@NonNull String teachAssistUserName, @NonNull String studentUserName);

    int testPythonDirectory();
}
