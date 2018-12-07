package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.domain.User;
import edu.purdue.cs.encourse.domain.relations.ProjectTestScript;
import edu.purdue.cs.encourse.util.JSONReturnable;
import lombok.NonNull;
import org.json.simple.JSONArray;

import java.io.File;
import java.util.List;

public interface ProfessorService {
    int assignProject(@NonNull String projectID);
    int assignProjectToStudent(@NonNull String projectID, @NonNull String userName);
    int cloneProjects(@NonNull String projectID);
    int deleteProject(@NonNull String projectID);
    int modifyProject(@NonNull String projectID, @NonNull String field, String value);
    int pullProjects(@NonNull String projectID);
    Project addProject(@NonNull String courseID, @NonNull String semester, @NonNull String projectName, String repoName, String startDate, String dueDate, int testRate);

    JSONReturnable getClassCheating(@NonNull String projectID);
    JSONReturnable getClassCommitList(@NonNull String projectID);
    JSONReturnable getClassProgress(@NonNull String projectID);
    JSONReturnable getClassSimilar(@NonNull String projectID);
    JSONReturnable getClassStatistics(@NonNull String projectID);
    JSONReturnable getClassTestSummary(@NonNull String projectID);

    int assignTeachingAssistantToAllStudentsInSection(@NonNull String teachAssistUserName, @NonNull String sectionID);
    int assignTeachingAssistantToSection(@NonNull String teachAssistUserName, @NonNull String sectionID);
    int assignTeachingAssistantToStudentInSection(@NonNull String teachAssistUserName, @NonNull String studentUserName, @NonNull String sectionID);
    int modifyTestScript(@NonNull String projectID, @NonNull String testName, @NonNull String field, @NonNull String value);
    void pullAndTestAllProjects();
    int runTestall(@NonNull String projectID);
    int runHistoricTestall(@NonNull String projectID);

    ProjectTestScript addTestScript(@NonNull String projectID, @NonNull String testName, boolean isHidden, double points);
    ProjectTestScript uploadTestScript(@NonNull String projectID, @NonNull String testName, @NonNull String testContents, boolean isHidden, double points);
    int addTestScriptToSuite(@NonNull String projectID, @NonNull String testName, @NonNull String suite);
    int addSuiteToProject(@NonNull String projectID, @NonNull String suite);

    JSONArray getCourseData(@NonNull String userName);
    JSONArray getStudentData(@NonNull String semester, @NonNull String courseID);
    JSONArray getTeachingAssistantData(@NonNull String semester, @NonNull String courseID);
}
