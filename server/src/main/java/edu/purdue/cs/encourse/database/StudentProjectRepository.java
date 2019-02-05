package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.domain.relations.StudentProject;
import edu.purdue.cs.encourse.domain.relations.StudentProjectDate;
import edu.purdue.cs.encourse.model.course.CourseStudentFilters;
import edu.purdue.cs.encourse.model.course.CourseStudentSearch;
import lombok.NonNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentProjectRepository extends PagingAndSortingRepository<StudentProject, Long> {
    List<StudentProject> findByProject(@NonNull Project project);
    
    Optional<StudentProject> findByProject_ProjectIDAndStudent_Id(@NonNull Long projectID, @NonNull Long studentID);
    
    @Query("select s from StudentProjectDate s where s.project = :#{#project} and " +
            "(:#{#filters.commits} is null or ((:#{#filters.commits.begin} is null or :#{#filters.commits.begin} <= s.currentCommits) and (:#{#filters.commits.end} is null or :#{#filters.commits.end} >= s.currentCommits))) and " +
            "(:#{#filters.time} is null or ((:#{#filters.time.begin} is null or :#{#filters.time.begin} <= s.currentMinutes) and (:#{#filters.time.end} is null or :#{#filters.time.end} >= s.currentMinutes))) and " +
            "(:#{#filters.progress} is null or ((:#{#filters.progress.begin} is null or :#{#filters.progress.begin} <= s.visiblePoints + s.hiddenPoints) and (:#{#filters.progress.end} is null or :#{#filters.progress.end} >= s.visiblePoints + s.hiddenPoints))) and " +
            "(:#{#filters.students} is null or ((:#{#filters.selectedAll} is null and s.studentProject in :#{#filters.students})) or (:#{#filters.selectedAll} = false and s.studentProject in :#{#filters.students}) or (:#{#filters.selectedAll} = true and not (s.studentProject in :#{#filters.students})))")
    List<StudentProject> findByProjectAndFilters(@Param("project") @NonNull Project project, @Param("filters") @NonNull CourseStudentFilters filters);
    
    default List<StudentProject> findAllByCourseStudentSearch(Project project, CourseStudentSearch courseStudentSearch) {
        if(courseStudentSearch.hasFilters())
            return findByProjectAndFilters(project, courseStudentSearch.getFilters());
    
        return findByProject(project);
    }
}