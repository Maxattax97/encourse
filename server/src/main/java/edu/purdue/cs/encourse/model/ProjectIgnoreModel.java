package edu.purdue.cs.encourse.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * Created by Killian Le Clainche on 2/19/2019.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProjectIgnoreModel {
	
	@NonNull
	private Long projectID;
	
	@NonNull
	private String user;
	
}
