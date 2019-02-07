package edu.purdue.cs.encourse.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.purdue.cs.encourse.model.BarValue;
import edu.purdue.cs.encourse.model.BasicStatistics;
import edu.purdue.cs.encourse.model.stdserializer.DescriptiveStatisticsSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.List;

/**
 * Created by Killian Le Clainche on 1/30/2019.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CourseBarChartModel {
	
	private List<BarValue> bars;
	
	@JsonSerialize(using = DescriptiveStatisticsSerializer.class)
	private DescriptiveStatistics stats;
	
	private BasicStatistics courseStats;
	
}
