package edu.purdue.cs.encourse.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Created by Killian Le Clainche on 2/3/2019.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProjectStudentFilters {

	IntegerRange additions;
	IntegerRange deletions;
	LocalDate start;
	LocalDate end;
	Boolean bundleDays;
	
}
