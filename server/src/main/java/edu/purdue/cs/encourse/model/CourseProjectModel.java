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
	
	public CourseProjectModel(@NonNull Long courseID, @NonNull Long projectID, @NonNull String name, @NonNull LocalDate startDate, @NonNull LocalDate dueDate, @NonNull String repository) {
		super(name, startDate, dueDate, repository);
		
		this.setProjectID(projectID);
		
		this.courseID = courseID;
	}
}
