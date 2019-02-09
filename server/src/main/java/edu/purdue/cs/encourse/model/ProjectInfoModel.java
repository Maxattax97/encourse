package edu.purdue.cs.encourse.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Killian Le Clainche on 1/15/2019.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProjectInfoModel {
	
	//Histogram of student testall points
	private CourseBarChartModel progress;
	
	//Histogram of student commit count
	private CourseBarChartModel commits;
	
	//Histogram of student time spent count
	private CourseBarChartModel time;
	
	//Histogram of student total additions divided by total deletions
	private CourseBarChartModel changes;
	
	//Histogram of student similiarity count to any other student
	private CourseBarChartModel similarity;
	
	//Histogram of student total progress to time spent
	private CourseBarChartModel timeVelocity;
	
	//Histogram of student total progress to commit count
	private CourseBarChartModel commitVelocity;
	
}
