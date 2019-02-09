package edu.purdue.cs.encourse.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Killian Le Clainche on 1/16/2019.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DoubleRange {
	
	private Double begin;
	private Double end;
	
	public void populate() {
		if(this.begin == null)
			this.begin = -1.0;
		
		if(this.end == null)
			this.end = 10000000.0;
	}
	
}
