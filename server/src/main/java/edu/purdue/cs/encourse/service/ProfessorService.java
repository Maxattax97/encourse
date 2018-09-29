package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.util.JSONReturnable;
import lombok.NonNull;

public interface ProfessorService {
    int addProject(@NonNull String courseID, @NonNull String semester, @NonNull String projectName, String repoName, String startDate, String dueDate);
    int modifyProject(@NonNull String projectID, @NonNull String field, String value);

    JSONReturnable countAllCommits(@NonNull String semester, @NonNull String courseID, @NonNull String projectID);
    JSONReturnable countAllCommitsByDay(@NonNull String semester, @NonNull String courseID, @NonNull String projectID);
    JSONReturnable countStudentCommitsByDay(@NonNull String semester, @NonNull String courseID, @NonNull String projectID, @NonNull String userName);
    JSONReturnable listStudentCommitsByTime(@NonNull String semester, @NonNull String courseID, @NonNull String projectID, @NonNull String userName);

    int assignTeachingAssistantToStudent(@NonNull String teachAssistUserName, @NonNull String studentUserName);

    int testPythonDirectory();
    JSONReturnable getCommitData();
    JSONReturnable getProgressHistogram(@NonNull String studentID);
}
