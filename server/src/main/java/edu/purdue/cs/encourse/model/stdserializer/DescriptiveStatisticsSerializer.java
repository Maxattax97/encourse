package edu.purdue.cs.encourse.model.stdserializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.IOException;

/**
 * Created by Killian Le Clainche on 1/16/2019.
 */
public class DescriptiveStatisticsSerializer extends JsonSerializer<DescriptiveStatistics> {
	
	@Override
	public void serialize(DescriptiveStatistics descriptiveStatistics, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
		jsonGenerator.writeStartObject();
		jsonGenerator.writeNumberField("mean", descriptiveStatistics.getMean());
		jsonGenerator.writeNumberField("variance", descriptiveStatistics.getVariance());
		jsonGenerator.writeNumberField("population_variance", descriptiveStatistics.getPopulationVariance());
		jsonGenerator.writeNumberField("min", descriptiveStatistics.getMin());
		jsonGenerator.writeNumberField("max", descriptiveStatistics.getMax());
		jsonGenerator.writeEndObject();
		
	}
}
