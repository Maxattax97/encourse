package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.domain.Student;
import edu.purdue.cs.encourse.domain.relations.StudentProject;
import edu.purdue.cs.encourse.domain.relations.StudentProjectDate;
import edu.purdue.cs.encourse.model.course.CourseStudentFilters;
import edu.purdue.cs.encourse.model.course.CourseStudentSearch;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentProjectDateRepository extends JpaRepository<StudentProjectDate, Long>, JpaSpecificationExecutor<StudentProjectDate> {
    List<StudentProjectDate> findByProjectAndDateGreaterThanEqual(@NonNull Project project, @NonNull LocalDate date);
    List<StudentProjectDate> findByProjectAndDate(@NonNull Project project, @NonNull LocalDate date);
    
    Optional<StudentProjectDate> findByStudentProjectAndDateEquals(@NonNull StudentProject studentProject, @NonNull LocalDate date);
    
    
    @Query("select s from StudentProjectDate s where s.date = :#{#date} and " +
            "(:#{#filters.commits} is null or ((:#{#filters.commits.begin} is null or :#{#filters.commits.begin} <= s.currentCommits) and (:#{#filters.commits.end} is null or :#{#filters.commits.end} >= s.currentCommits))) and " +
            "(:#{#filters.time} is null or ((:#{#filters.time.begin} is null or :#{#filters.time.begin} <= s.currentMinutes) and (:#{#filters.time.end} is null or :#{#filters.time.end} >= s.currentMinutes))) and " +
            "(:#{#filters.progress} is null or ((:#{#filters.progress.begin} is null or :#{#filters.progress.begin} <= s.visiblePoints + s.hiddenPoints) and (:#{#filters.progress.end} is null or :#{#filters.progress.end} >= s.visiblePoints + s.hiddenPoints))) and " +
            "(:#{#filters.students} is null or ((:#{#filters.selectedAll} is null and s.studentProject in :#{#filters.students})) or (:#{#filters.selectedAll} = false and s.studentProject in :#{#filters.students}) or (:#{#filters.selectedAll} = true and not (s.studentProject in :#{#filters.students})))")
    List<StudentProjectDate> findByProjectAndDateAndFilters(@NonNull Project project, @Param("date") LocalDate date, @Param("filters") @NonNull CourseStudentFilters filters);
    
    default List<StudentProjectDate> findAllByCourseStudentSearch(@NonNull Project project, @NonNull CourseStudentSearch courseStudentSearch) {
        if(courseStudentSearch.hasFilters())
            return findByProjectAndDateAndFilters(project, courseStudentSearch.getDate(), courseStudentSearch.getFilters());
        
        return findByProjectAndDate(project, courseStudentSearch.getDate());
    }
}