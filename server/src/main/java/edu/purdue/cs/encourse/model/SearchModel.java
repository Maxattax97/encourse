package edu.purdue.cs.encourse.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;

/**
 * Created by Killian Le Clainche on 2/3/2019.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SearchModel {
	
	private Map<String, Boolean> options;
	
	private LocalDate date;
	
	public boolean hasOptions() { return options != null; }
	
	public boolean hasOption(String option) { return hasOptions() && options.containsKey(option); }
	
	public Boolean getOption(String option) {
		if(hasOptions()) {
			Boolean optionValue = options.get(option);
			return optionValue == null ? false : optionValue;
		}
		
		return false;
	}
	
	public boolean hasDate() { return date != null; }
	
}
