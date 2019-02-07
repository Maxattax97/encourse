package edu.purdue.cs.encourse.model.student;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Created by Killian Le Clainche on 1/30/2019.
 */
@NoArgsConstructor
@AllArgsConstructor
public class StudentProjectDiff {
	
	public LocalDateTime date;
	
	public Double visiblePoints;
	public Double hiddenPoints;
	
	public Double additions;
	public Double deletions;
}
