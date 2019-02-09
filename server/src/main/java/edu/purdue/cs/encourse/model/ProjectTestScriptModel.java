package edu.purdue.cs.encourse.model;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * Created by Killian Le Clainche on 2/3/2019.
 */
@Getter
@Setter
public class ProjectTestScriptModel extends TestScriptModel {
	
	@NonNull
	private Long projectID;
	
	public ProjectTestScriptModel(@NonNull Long projectID, @NonNull String name, @NonNull Boolean hidden, @PositiveOrZero Double value) {
		super(name, hidden, value);
		
		this.projectID = projectID;
	}
}
