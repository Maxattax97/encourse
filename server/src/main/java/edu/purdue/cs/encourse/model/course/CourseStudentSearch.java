package edu.purdue.cs.encourse.model.course;

import edu.purdue.cs.encourse.model.SearchModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * Created by Killian Le Clainche on 1/15/2019.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CourseStudentSearch extends SearchModel {
	
	@NonNull
	private Long projectID;
	
	private CourseStudentFilters filters;
	
	public boolean hasFilters() { return filters != null; }
	
	public String getView() {
		if(hasFilters())
			return filters.getView() != null ? filters.getView() : "All";
		return "All";
	}
	
}

