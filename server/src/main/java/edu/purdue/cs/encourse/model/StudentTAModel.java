package edu.purdue.cs.encourse.model;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Created by Killian Le Clainche on 2/3/2019.
 */
@Data
public class StudentTAModel {
	
	@NonNull
	private Long studentID;
	
	@NonNull
	private Long teachingAssistantID;
}
