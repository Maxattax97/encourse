package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.domain.relations.StudentProject;
import edu.purdue.cs.encourse.domain.relations.StudentProjectDate;
import edu.purdue.cs.encourse.model.DoubleRange;
import edu.purdue.cs.encourse.model.course.CourseStudentSearch;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentProjectDateRepository extends JpaRepository<StudentProjectDate, Long>, JpaSpecificationExecutor<StudentProjectDate> {
    List<StudentProjectDate> findByProjectAndDateGreaterThanEqual(@NonNull Project project, @NonNull LocalDate date);
    List<StudentProjectDate> findByProjectAndDate(@NonNull Project project, @NonNull LocalDate date);
    
    Optional<StudentProjectDate> findByStudentProjectAndDateEquals(@NonNull StudentProject studentProject, @NonNull LocalDate date);
    
    @Query("select s from StudentProjectDate s where s.date = (:#{#date}) and s.project = (:#{#project}) and " +
            "(:#{#commits.begin} <= s.currentCommits and :#{#commits.end} >= s.currentCommits) and " +
            "(:#{#time.begin} <= s.currentSeconds and :#{#time.end} >= s.currentSeconds) and " +
            "(:#{#progress.begin} <= s.visiblePoints + s.hiddenPoints and :#{#progress.end} >= s.visiblePoints + s.hiddenPoints) and " +
            "((:#{#selectedAll} = false and s.studentProject.student.id in (:#{#students})) or (:#{#selectedAll} = true and not (s.studentProject.student.id in (:#{#students}))))")
    List<StudentProjectDate> findByProjectAndDateAndFilters(@Param("project") @NonNull Project project, @Param("date") LocalDate date,
                                                            @Param("commits") DoubleRange commits, @Param("time") DoubleRange time, @Param("progress") DoubleRange progress,
                                                            @Param("students") List<Long> students, @Param("selectedAll") Boolean selectedAll);
    
    default List<StudentProjectDate> findAllByCourseStudentSearch(@NonNull Project project, @NonNull CourseStudentSearch courseStudentSearch) {
        if(courseStudentSearch.hasFilters())
            return findByProjectAndDateAndFilters(project, courseStudentSearch.getDate(),
                    courseStudentSearch.getFilters().getCommits(), courseStudentSearch.getFilters().getTime(), courseStudentSearch.getFilters().getProgress(),
                    courseStudentSearch.getFilters().getStudents(), courseStudentSearch.getFilters().getSelectedAll());
        
        return findByProjectAndDate(project, courseStudentSearch.getDate());
    }
}