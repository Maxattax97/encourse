package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.domain.Section;
import lombok.NonNull;
import org.json.simple.JSONObject;

import java.util.List;

public interface CourseService {
    List<Section> getSectionsBySemesterAndCourseID(@NonNull String semester, @NonNull String courseID);

    int setDirectory(@NonNull String semester, @NonNull String courseID);
    int createDirectory(@NonNull String semester, @NonNull String courseID);

    int setSectionRemotePaths(@NonNull String semester, @NonNull String courseID, @NonNull String remotePath);
    int cloneProjects(@NonNull String semester, @NonNull String courseID, @NonNull String projectID);
    int pullProjects(@NonNull String semester, @NonNull String courseID, @NonNull String projectID);

    JSONObject getCourseInfoByCourseID(@NonNull String semester, @NonNull String courseID);
}