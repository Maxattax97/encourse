package edu.purdue.cs.encourse.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by Killian Le Clainche on 1/31/2019.
 */
@Data
@NoArgsConstructor
public class StudentInfoModel {
	
	private Long studentID;
	
	private String firstName;
	
	private String lastName;
	
	private List<Long> sections;
	
	private List<Long> teachingAssistants;
	
	private Long additions;
	private Long deletions;
	
	private Long commits;
	
	private Long seconds;
	
	private Long visiblePoints;
	private Long hiddenPoints;
	
	private List<StudentTestSuiteInfoModel> testSuites;
	
	private Double changes;
	private Double timeVelocity;
	private Double commitVelocity;
	private Integer comparison;
	private Double comparisonPercent;
	
}
