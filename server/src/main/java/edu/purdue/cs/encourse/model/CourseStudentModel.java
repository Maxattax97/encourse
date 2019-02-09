package edu.purdue.cs.encourse.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Created by Killian Le Clainche on 2/3/2019.
 */
@AllArgsConstructor
@Getter
@Setter
public class CourseStudentModel {

	@NonNull
	private Long courseID;
	
	@NonNull
	private Long studentID;
	
}
