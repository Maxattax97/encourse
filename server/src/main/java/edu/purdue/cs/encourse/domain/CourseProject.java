package edu.purdue.cs.encourse.domain;

import lombok.NonNull;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by Killian Le Clainche on 2/19/2019.
 */
public class CourseProject {
	
	/** courseID + semester + projectName, forms the primary key */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name  = "COURSE_PROJECT_ID")
	private Long projectID;
	
	@ManyToOne
	@JoinColumn(name = "COURSE_ID")
	@NonNull
	private Course course;
	
	@OneToOne(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "PROJECT_ID")
	private Project project;
	
	@NonNull
	@Column(name  = "PROJECT_NAME")
	private String name;
	
	@NonNull
	@Column(name  = "START_DATE")
	private LocalDate startDate;
	
	@NonNull
	@Column(name  = "DUE_DATE")
	private LocalDate dueDate;
	
	/** The name of the directory that will store the project. Remove .git if this is included */
	@NonNull
	@Column(name  = "REPOSITORY")
	private String repository;
	
	/** Rate in which projects will be pulled and tested, in hourse */
	@Column(name  = "TEST_RATE")
	private int testRate;
	
	@Column(name = "RUN_TESTALL")
	private Boolean runTestall;
	
	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<TestScript> testScripts;
	
	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<TestSuite> testSuites;
	
}
