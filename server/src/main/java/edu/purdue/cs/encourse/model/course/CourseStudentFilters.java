package edu.purdue.cs.encourse.model.course;

import edu.purdue.cs.encourse.model.DoubleRange;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by Killian Le Clainche on 1/15/2019.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CourseStudentFilters {
	
	private DoubleRange commits;
	private DoubleRange time;
	private DoubleRange progress;
	private String view;
	private List<Long> students;
	private Boolean selectedAll;
	
	public boolean valid() {
		return view == null || (view.equals("All") || view.equals("Visible") || view.equals("Hidden"));
	}
	
}
