package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.domain.User;
import edu.purdue.cs.encourse.domain.relations.ProjectTestScript;
import edu.purdue.cs.encourse.util.JSONReturnable;
import lombok.NonNull;
import org.json.simple.JSONArray;

import java.io.File;
import java.util.List;

public interface TeachingAssistantService {
    JSONReturnable getAssignmentsCheating(@NonNull String projectID, @NonNull String userNameTA);
    JSONReturnable getAssignmentsCommitList(@NonNull String projectID, @NonNull String userNameTA);
    JSONReturnable getAssignmentsProgress(@NonNull String projectID, @NonNull String userNameTA);
    JSONReturnable getAssignmentsSimilar(@NonNull String projectID, @NonNull String userNameTA);
    JSONReturnable getAssignmentsStatistics(@NonNull String projectID, @NonNull String userNameTA);
    JSONReturnable getAssignmentsTestSummary(@NonNull String projectID, @NonNull String userNameTA);

    JSONArray getStudentData(@NonNull String semester, @NonNull String courseID, @NonNull String userNameTA);
    JSONArray getCourseData(@NonNull String userName);
}