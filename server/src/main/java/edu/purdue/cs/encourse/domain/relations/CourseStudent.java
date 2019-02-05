package edu.purdue.cs.encourse.domain.relations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.purdue.cs.encourse.domain.Course;
import edu.purdue.cs.encourse.domain.Section;
import edu.purdue.cs.encourse.domain.Student;
import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Killian Le Clainche on 1/15/2019.
 */
@Entity
@Table(name = "COURSE_STUDENT")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class CourseStudent {
	/** Primary key for relation in database. Never used directly */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "courseID")
	@NonNull
	private Course course;
	
	@ManyToOne
	@JoinColumn(name = "studentID")
	@NonNull
	private Student student;
	
	private Boolean isStudent;
	
	@ManyToMany(cascade = {
			CascadeType.MERGE,
			CascadeType.PERSIST
	}, fetch = FetchType.LAZY, mappedBy = "students")
	@NonNull
	private List<Section> sections;
	
	@OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonIgnore
	private List<StudentProject> projects;
	
	@ManyToMany(cascade = {
			CascadeType.MERGE,
			CascadeType.PERSIST
	}, fetch = FetchType.LAZY, mappedBy = "teachingAssistants")
	@NonNull
	private List<CourseStudent> students;
	
	@ManyToMany(cascade = {
			CascadeType.MERGE,
			CascadeType.PERSIST
	}, fetch = FetchType.LAZY)
	@JoinTable(name="COURSE_STUDENT_STUDENT",
			joinColumns=@JoinColumn(name="studentID"),
			inverseJoinColumns=@JoinColumn(name="taID")
	)
	@NonNull
	private List<CourseStudent> teachingAssistants;
	
	public CourseStudent(@NonNull Course course, @NonNull Student student, @NonNull Boolean isStudent) {
		this.course = course;
		this.student = student;
		
		this.isStudent = isStudent;
		
		this.sections = new ArrayList<>();
		
		this.projects = new ArrayList<>();
		
		this.students = new ArrayList<>();
		
		this.teachingAssistants = new ArrayList<>();
	}
	
	@Override
	public boolean equals(Object courseStudent) {
		return courseStudent instanceof CourseStudent && ((CourseStudent) courseStudent).id.equals(this.id);
	}
	
	@Override
	public int hashCode() {
		return Math.toIntExact(this.id);
	}
	
}


