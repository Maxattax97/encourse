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
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long courseID;
	
	@ManyToOne
	@JoinColumn(name = "professor_id")
	@NonNull
	private Professor professor;
	
	/** The path to the remote repositories on data.cs **/
	@NonNull
	private String remotePath;
	
	/** May be useful to track for registration purposes */
	@NonNull
	private String CRN;
	
	/** Use courseID or courseTitle to group together sections of the same course */
	@NonNull
	private String title;
	
	@NonNull
	private String name;
	
	@NonNull
	private String semester;
	
	@NonNull
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
	
	@OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@NonNull
	private List<CourseStudent> teachingAssistants;
	
	
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
		this.teachingAssistants = new ArrayList<>();
	}
	
	public String getCourseHub() {
		return "/sourcecontrol/" + name + "/" + semester;
	}
	
	@Override
	public boolean equals(Object course) {
		return course instanceof Course && ((Course) course).getCourseID().equals(this.getCourseID());
	}
}
