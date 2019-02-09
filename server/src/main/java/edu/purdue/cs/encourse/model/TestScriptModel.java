package edu.purdue.cs.encourse.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.PositiveOrZero;

/**
 * Created by Killian Le Clainche on 2/3/2019.
 */
@Data
public class TestScriptModel {
	
	private Long testScriptID;
	
	@NonNull
	private String name;
	
	@NonNull
	private Boolean hidden;
	
	@NonNull
	@PositiveOrZero
	private Double value;
	
}
