package edu.purdue.cs.encourse.model;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

/**
 * Created by Killian Le Clainche on 2/3/2019.
 */
@Data
public class ProjectModel {
	
	private Long projectID;
	
	@NonNull
	private String name;
	
	@NonNull
	private LocalDate startDate;
	
	@NonNull
	private LocalDate dueDate;
	
	@NonNull
	private String repository;
	
	private Double totalVisiblePoints;
	
	private Double totalHiddenPoints;
	
}
