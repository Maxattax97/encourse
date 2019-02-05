package edu.purdue.cs.encourse.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;

/**
 * Created by Killian Le Clainche on 1/15/2019.
 */
@NoArgsConstructor
@AllArgsConstructor
public class BarValue {
	
	public int index;
	
	public int size;
	
}
