package edu.purdue.cs.encourse.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Created by Killian Le Clainche on 2/3/2019.
 */
@Getter
@Setter
public class ProjectTestSuiteModel extends TestSuiteModel {
	
	@NonNull
	private Long projectID;
	
	public ProjectTestSuiteModel(@NonNull Long projectID, @NonNull String name, @NonNull Boolean hidden) {
		super(name, hidden);
		
		this.projectID = projectID;
	}
}
