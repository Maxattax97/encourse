package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.domain.User;
import edu.purdue.cs.encourse.domain.relations.ProjectTestScript;
import edu.purdue.cs.encourse.util.JSONReturnable;
import lombok.NonNull;
import org.json.simple.JSONArray;

import java.io.File;

public interface TeachingAssistantService {
    JSONReturnable getStudentProgress(@NonNull String projectID, @NonNull String userName);
    JSONReturnable getAdditionsAndDeletions(@NonNull String projectID, @NonNull String userName);
    JSONReturnable getStatistics(@NonNull String projectID, @NonNull String userName);
    JSONReturnable getCommitList(@NonNull String projectID, @NonNull String userName);
    JSONReturnable getClassProgress(@NonNull String projectID);
    JSONReturnable getTestSummary(@NonNull String projectID);
    JSONReturnable getCommitCounts(@NonNull String projectID, @NonNull String userName);

    int runTestallForStudent(@NonNull String projectID, @NonNull String userName);

    JSONArray getStudentData(@NonNull String semester, @NonNull String courseID, @NonNull String userName);
    JSONArray getProjectData(@NonNull String semester, @NonNull String courseID, @NonNull String userName);

    Project getProject(String projectID);
}