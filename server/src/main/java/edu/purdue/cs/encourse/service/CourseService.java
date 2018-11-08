package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.domain.Section;
import edu.purdue.cs.encourse.util.JSONReturnable;
import lombok.NonNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

public interface CourseService {
    List<Section> getSectionsBySemesterAndCourseID(@NonNull String semester, @NonNull String courseID);

    int setDirectory(@NonNull String semester, @NonNull String courseID);
    int createDirectory(@NonNull String semester, @NonNull String courseID);

    int setSectionRemotePaths(@NonNull String semester, @NonNull String courseID, @NonNull String remotePath);

    JSONArray getSectionData(@NonNull String semester, @NonNull String courseID);
    JSONArray getProjectData(@NonNull String semester, @NonNull String courseID);

    JSONReturnable getProgress(@NonNull String projectID, List<String> userNames);
    JSONReturnable getSimilar(@NonNull String projectID, List<String> userNames);
    JSONReturnable getStatistics(@NonNull String projectID, List<String> userNames);
    JSONReturnable getTestSummary(@NonNull String projectID, List<String> userNames);
    JSONReturnable getCommitList(@NonNull String projectID, List<String> userNames);
    JSONReturnable getCheating(@NonNull String projectID, List<String> userNames);

    JSONArray sortStudentData(@NonNull JSONArray studentsJSON, @NonNull List<String> parameters, @NonNull List<Boolean> isAscending);
    JSONArray filterStudentData(@NonNull JSONArray studentsJSON, @NonNull List<String> parameters, @NonNull List<List<String>> values);

    Project getProject(String projectID);
}