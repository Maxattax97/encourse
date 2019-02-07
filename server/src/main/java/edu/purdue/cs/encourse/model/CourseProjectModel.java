package edu.purdue.cs.encourse.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Created by Killian Le Clainche on 2/3/2019.
 */
@Getter
@Setter
public class CourseProjectModel extends ProjectModel {
	
	@NonNull
	private Long courseID;
	
	public CourseProjectModel(@NonNull Long courseID, @NonNull String name, @NonNull LocalDate startDate, @NonNull LocalDate dueDate, @NonNull String repository, @NonNull Boolean runTestall) {
		super(name, startDate, dueDate, repository, runTestall);
		
		this.courseID = courseID;
	}
	
	public CourseProjectModel(@NonNull Long courseID, @NonNull Long projectID, @NonNull String name, @NonNull LocalDate startDate, @NonNull LocalDate dueDate, @NonNull String repository, @NonNull Boolean runTestall) {
		super(name, startDate, dueDate, repository, runTestall);
		
		this.setProjectID(projectID);
		
		this.courseID = courseID;
	}
}
