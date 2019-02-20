package edu.purdue.cs.encourse.service;

import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Killian Le Clainche on 2/16/2019.
 */
public interface ProjectAnalysisService {
	
	@Transactional
	void analyzeProjects();
}
