package edu.purdue.cs.encourse.service;

import org.json.simple.JSONObject;

import javax.validation.constraints.NotNull;

public interface CourseService {
    void setDirectory(String courseID) throws Exception;
    void createDirectory(String courseID) throws Exception;

    void createProject(String courseID, String semester, String projectName, String repoName, String startDate, String dueDate) throws Exception;
    void updateProject(String projectID, String field, String value) throws Exception;
    
    void setRemotePath(String courseID, String remotePath) throws Exception;
    void cloneProjectsFromRemote(String courseID, String projectID) throws Exception;
    void pullProjectsFromRemote(String courseID, String projectID) throws Exception;

    /*
    Retrieves all students names (first + last)
    Retrieves all students commit counts
    Retrieves all students time spent (measured as a parsed string)
    Retrieves all students progress (measured as a percentile integer)
    Retrieves all students unique ids
     */
    @NotNull JSONObject getStudentsProjectInfoByCourse(String courseID, String projectID);
}
