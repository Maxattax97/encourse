package edu.purdue.cs.encourse.model.student;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by Killian Le Clainche on 1/30/2019.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StudentProjectInfo {
	
	private Long studentProjectID;
	
	private Long additions;
	private Long deletions;
	
	private Long commits;
	
	private Long minutes;
	
	private Long visiblePoints;
	private Long hiddenPoints;
	
	private List<StudentTestSuiteInfo> studentTestSuites;
	
}
