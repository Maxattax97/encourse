package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.domain.Student;
import edu.purdue.cs.encourse.domain.relations.StudentProject;
import edu.purdue.cs.encourse.domain.relations.TeachingAssistantStudent;
import edu.purdue.cs.encourse.util.JSONReturnable;
import lombok.NonNull;

import java.io.IOException;
import java.util.List;

public interface HelperService {
    boolean getDebug();
    boolean getObfuscate();

    String getPythonCommand();
    String getPythonPath();
    String getTailFilePath();
    String getTestDir();

    int executeBashScript(@NonNull String command);
    JSONReturnable runPython(@NonNull String command);

    boolean isTakingCourse(@NonNull Student student, @NonNull Project project);

    double parseProgressForProject(@NonNull String projectID, @NonNull String testOutput);
    int updateStudentInformation(@NonNull String projectID, @NonNull String userName);
    void updateTestResults(String result, String studentID, String projectID, boolean isHidden);
    void initTestResults(String studentID, String projectID);
    void createTestFiles(String visibleTestFile, String hiddenTestFile, List<StudentProject> projects) throws IOException;
    void createTestFilesTA(String visibleTestFile, String hiddenTestFile, List<TeachingAssistantStudent> assignments, String projectID) throws IOException;

    List<StudentProject> getStudentProjects(String projectID, List<String> userNames);
    List<String> getStudentUserNames(List<StudentProject> studentProjects);
    List<String> getStudentUserNamesForTA(List<TeachingAssistantStudent> assignments);

    String countAllCommits(@NonNull String projectID, List<StudentProject> projects);
    String countAllCommitsByDay(@NonNull String projectID, List<StudentProject> projects);
    String countStudentCommitsByDay(@NonNull String projectID, @NonNull String userName);
    String listAllCommitsByTime(@NonNull String projectID, List<StudentProject> projects);
    String listStudentCommitsByTime(@NonNull String projectID, @NonNull String userName);
}