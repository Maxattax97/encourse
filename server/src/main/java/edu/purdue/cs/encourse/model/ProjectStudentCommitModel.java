package edu.purdue.cs.encourse.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * Created by Killian Le Clainche on 2/12/2019.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectStudentCommitModel {
	
	@NonNull
	private Long projectID;
	
	@NonNull
	private Long studentID;
	
	@NonNull
	private String commit;
	
	private String previousCommit;
	
}
