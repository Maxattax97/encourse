package edu.purdue.cs.encourse.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Created by Killian Le Clainche on 2/3/2019.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectStudentSearchModel extends SearchModel {
	
	@NonNull
	private Long projectID;
	
	@NonNull
	private Long studentID;
	
	private ProjectStudentFilters filters;
	
	public boolean hasFilters() { return filters != null; }
	
	public Boolean isBundledByDay() {
		if(hasFilters())
			return filters.getBundleDays() != null ? filters.getBundleDays() : false;
		return false;
	}
	
}
