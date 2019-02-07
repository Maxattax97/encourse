package edu.purdue.cs.encourse.model;

import lombok.Data;
import lombok.NonNull;

/**
 * Created by Killian Le Clainche on 2/3/2019.
 */
@Data
public class TestSuiteModel {
	
	private Long testSuiteID;
	
	@NonNull
	private String name;
	
	@NonNull
	private Boolean hidden;
}
