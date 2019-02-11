package edu.purdue.cs.encourse.service.helper;

import edu.purdue.cs.encourse.model.BarValue;
import edu.purdue.cs.encourse.model.BasicStatistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Killian Le Clainche on 1/16/2019.
 */
public class ChartHelper {
	
	public static List<BarValue> toBarChart(int count, BasicStatistics bounds, double[] values) {
		BarValue[] buckets = new BarValue[(int) Math.floor(Math.sqrt(count))];
		
		for(int i = 0; i < buckets.length; i++)
			buckets[i] = new BarValue(i, (short) 0);
		
		double interval = (bounds.getMax() - bounds.getMin()) / buckets.length;
		
		for(double value : values)
			buckets[(int)Math.min(Math.max(Math.floor(((value - bounds.getMin()) / interval)), 0), buckets.length - 1)].size++;
		
		List<BarValue> bucketList = new ArrayList<>(buckets.length);
		
		bucketList.addAll(Arrays.asList(buckets));
		
		return bucketList;
	}
	
}
