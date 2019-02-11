package edu.purdue.cs.encourse.domain;

import edu.purdue.cs.encourse.domain.relations.CourseStudent;
import edu.purdue.cs.encourse.model.CourseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Killian Le Clainche on 1/18/2019.
 */
@Entity
@Table(name = "COURSE")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Course {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "COURSE_ID")
	private Long courseID;
	
	@ManyToOne
	@JoinColumn(name = "USER_ID")
	@NonNull
	private Professor professor;
	
	/** The path to the remote repositories on data.cs **/
	@NonNull
	@Column(name = "REMOTE_PATH")
	private String remotePath;
	
	/** May be useful to track for registration purposes */
	@NonNull
	@Column(name = "CRN")
	private String CRN;
	
	/** Use courseID or courseTitle to group together sections of the same course */
	@NonNull
	@Column(name = "TITLE")
	private String title;
	
	@NonNull
	@Column(name = "NAME")
	private String name;
	
	@NonNull
	@Column(name = "SEMESTER")
	private String semester;
	
	@NonNull
	@Column(name = "STUDENT_COUNT")
	private Integer studentCount;
	
	@OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@NonNull
	private List<Project> projects;
	
	@OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@NonNull
	private List<Section> sections;
	
	@OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@NonNull
	private List<CourseStudent> students;
	
	public Course(@NonNull Professor professor, @NonNull CourseModel courseModel) {
		this.professor = professor;
		this.remotePath = courseModel.getRemotePath();
		this.CRN = courseModel.getCRN();
		this.title = courseModel.getTitle();
		this.name = courseModel.getName();
		this.semester = courseModel.getSemester();
		
		this.studentCount = 0;
		
		this.projects = new ArrayList<>();
		this.sections = new ArrayList<>();
		this.students = new ArrayList<>();
	}
	
	public String getCourseHub() {
		return "/sourcecontrol/" + name + "/" + semester;
	}
	
	public String toString() {
		return "Course (id=" + this.courseID + ", remote=" + this.remotePath + ", name=" + this.name + ", semester=" + this.semester + ")";
	}
}
