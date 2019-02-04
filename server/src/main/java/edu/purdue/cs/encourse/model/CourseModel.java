package edu.purdue.cs.encourse.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.Size;

/**
 * Created by Killian Le Clainche on 2/3/2019.
 */
@AllArgsConstructor
@Data
public class CourseModel {
	
	@NonNull
	private Long professorID;
	
	/** May be useful to track for registration purposes */
	@NonNull
	@Size(min = 1, max = 32, message = "CRN can only be 1 - 32 characters long")
	private String CRN;
	
	/** Use courseID or courseTitle to group together sections of the same course */
	@NonNull
	@Size(min = 1, max = 64, message = "Course title can only be 1 - 64 characters long")
	private String title;
	
	@NonNull
	@Size(min = 1, max = 32, message = "Course name can only be 1 - 32 characters long")
	private String name;
	
	@NonNull
	@Size(min = 1, max = 32, message = "Semester can only be 1 - 32 characters long")
	private String semester;
	
	@NonNull
	@Size(min = 1, max = 256, message = "Remote path can only be 1 - 256 characters long")
	private String remotePath;
	
}
