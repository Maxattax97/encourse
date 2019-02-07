package edu.purdue.cs.encourse.service.helper;

import edu.purdue.cs.encourse.model.BasicStatistics;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Killian Le Clainche on 1/18/2019.
 */
public class General {
	
	private static final HashSet<String> extensions = new HashSet<>(Arrays.asList(
			"c", "y", "l", "cpp", "java", "h", "hpp", "cxx", "hxx", "js", "html", "css", "scss", "ll", "s", "yxx", "yy"
	));
	
	public static <T> T findFirst(List<T> list) {
		if(list.size() == 0)
			return null;
		
		return list.get(0);
	}
	
	public static boolean isSourceCodeExtension(String file) {
		int last = file.lastIndexOf('.');
		
		if(last == -1)
			return false;
		
		return extensions.contains(file.substring(last + 1));
	}
	
	public static String[] readFirst(InputStream inputStream, int count) throws IOException {
		String[] lines = new String[count];
		
		boolean flag = false;
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		
		for(int i = 0; i < lines.length; i++) {
			if(flag)
				lines[i] = null;
			else {
				String line = reader.readLine();
				
				if(line == null)
					flag = true;
				
				lines[i] = line;
			}
		}
		
		reader.close();
		
		return lines;
	}
	
}
