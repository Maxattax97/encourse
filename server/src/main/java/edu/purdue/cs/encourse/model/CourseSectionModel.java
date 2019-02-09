package edu.purdue.cs.encourse.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Created by Killian Le Clainche on 2/8/2019.
 */
@Getter
@Setter
public class CourseSectionModel extends SectionModel {
	
	@NonNull
	private Long courseID;
	
	public CourseSectionModel(@NonNull Long courseID, @NonNull String type, @NonNull String time) {
		super(type, time);
		
		this.courseID = courseID;
	}
	
	public CourseSectionModel(@NonNull Long courseID, @NonNull Long sectionID, @NonNull String type, @NonNull String time) {
		super(type, time);
		
		this.setSectionID(sectionID);
		
		this.courseID = courseID;
	}
}
