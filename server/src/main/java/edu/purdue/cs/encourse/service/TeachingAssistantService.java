package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.domain.User;
import edu.purdue.cs.encourse.domain.relations.ProjectTestScript;
import edu.purdue.cs.encourse.util.JSONReturnable;
import lombok.NonNull;
import org.json.simple.JSONArray;

import java.io.File;

public interface TeachingAssistantService {
    JSONReturnable getStudentProgress(@NonNull String projectID, @NonNull String userNameStudent, @NonNull String userNameTA);
    JSONReturnable getAdditionsAndDeletions(@NonNull String projectID, @NonNull String userNameStudent, @NonNull String userNameTA);
    JSONReturnable getStatistics(@NonNull String projectID, @NonNull String userNameStudent, @NonNull String userNameTA);

    JSONReturnable getCommitCounts(@NonNull String projectID, @NonNull String userNameStudent, @NonNull String userNameTA);
    JSONReturnable getCommitList(@NonNull String projectID, @NonNull String userNameStudent, @NonNull String userNameTA);

    JSONReturnable getAssignmentsProgress(@NonNull String projectID, @NonNull String userNameTA);
    JSONReturnable getAssignmentsTestSummary(@NonNull String projectID, @NonNull String userNameTA);

    String countAllCommits(@NonNull String projectID, @NonNull String userNameTA);
    String countAllCommitsByDay(@NonNull String projectID, @NonNull String userNameTA);
    String countStudentCommitsByDay(@NonNull String projectID, @NonNull String userNameStudent, @NonNull String userNameTA);
    String listAllCommitsByTime(@NonNull String projectID, @NonNull String userNameTA);
    String listStudentCommitsByTime(@NonNull String projectID, @NonNull String userNameStudent, @NonNull String userNameTA);

    int runTestallForStudent(@NonNull String projectID, @NonNull String userNameStudent);

    JSONArray getStudentData(@NonNull String semester, @NonNull String courseID, @NonNull String userNameTA);
    JSONArray getProjectData(@NonNull String semester, @NonNull String courseID, @NonNull String userNameTA);

    Project getProject(String projectID);
}