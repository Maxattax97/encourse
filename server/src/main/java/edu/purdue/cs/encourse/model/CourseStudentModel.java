package edu.purdue.cs.encourse.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

/**
 * Created by Killian Le Clainche on 2/3/2019.
 */
@AllArgsConstructor
@Data
public class CourseStudentModel {

	@NonNull
	private Long courseID;
	
	@NonNull
	private Long studentID;
	
}
