package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.domain.Section;
import lombok.NonNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

public interface CourseService {
    List<Section> getSectionsBySemesterAndCourseID(@NonNull String semester, @NonNull String courseID);

    int setDirectory(@NonNull String semester, @NonNull String courseID);
    int createDirectory(@NonNull String semester, @NonNull String courseID);

    int setSectionRemotePaths(@NonNull String semester, @NonNull String courseID, @NonNull String remotePath);

    JSONArray getCourseData(@NonNull String userName);
    public JSONArray sortStudentData(@NonNull JSONArray studentsJSON, @NonNull List<String> parameters, @NonNull List<Integer> orders);
    JSONArray getStudentData(@NonNull String semester, @NonNull String courseID);
    JSONArray getProjectData(@NonNull String semester, @NonNull String courseID);
}