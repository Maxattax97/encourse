package edu.purdue.cs.encourse.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Created by Killian Le Clainche on 2/3/2019.
 */
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SectionModel {

	private Long sectionID;
	
	@NonNull
	private String type;
	
	@NonNull
	private String time;

}
