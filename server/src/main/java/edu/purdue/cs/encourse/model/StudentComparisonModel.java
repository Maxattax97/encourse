package edu.purdue.cs.encourse.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Created by Killian Le Clainche on 2/18/2019.
 */
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StudentComparisonModel {
	
	@NonNull
	private Long studentID;
	
	@NonNull
	private String firstName;
	
	@NonNull
	private String lastName;
	
	@NonNull
	private Integer count;
	
	@NonNull
	private Double percent;
	
}
