package edu.purdue.cs.encourse.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Created by Killian Le Clainche on 2/3/2019.
 */
@AllArgsConstructor
@Data
public class SectionModel {

	@NonNull
	private Long courseID;
	
	@NonNull
	private String type;
	
	@NonNull
	private String time;

}
