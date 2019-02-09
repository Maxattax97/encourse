package edu.purdue.cs.encourse.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import javax.persistence.Basic;
import javax.persistence.Embeddable;

/**
 * Created by Killian Le Clainche on 1/16/2019.
 */
@Embeddable
@AllArgsConstructor
@Getter
@Setter
public class BasicStatistics {
	
	@NonNull
	private Double min;
	
	@NonNull
	private Double max;
	
	@NonNull
	private Double mean;
	
	@NonNull
	private Double median;
	
	@NonNull
	private Double variance;
	
	public BasicStatistics(DescriptiveStatistics descriptiveStatistics) {
		min = descriptiveStatistics.getMin();
		max = descriptiveStatistics.getMax();
		mean = descriptiveStatistics.getMean();
		median = descriptiveStatistics.getPercentile(50);
		variance = descriptiveStatistics.getVariance();
	}
	
	public BasicStatistics() {
		min = Double.MIN_NORMAL;
		max = Double.MIN_NORMAL;
		mean = Double.MIN_NORMAL;
		median = Double.MIN_NORMAL;
		variance = Double.MIN_NORMAL;
	}
}
