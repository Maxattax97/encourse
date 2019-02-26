package edu.purdue.cs.encourse.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Created by Killian Le Clainche on 2/25/2019.
 */
@Getter
@Setter
public class StringWrapperModel {
	
	@NonNull
	private String response;
	
	public StringWrapperModel(@NonNull String response) {
		this.response = response;
	}
}
