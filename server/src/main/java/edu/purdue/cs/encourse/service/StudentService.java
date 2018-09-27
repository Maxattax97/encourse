package edu.purdue.cs.encourse.service;

import org.json.simple.JSONObject;

public interface StudentService {
	
	
	JSONObject getCommitCountByDay(String courseID, String projectID, String studentID);
	
	JSONObject getChangeCountByDay(String courseID, String projectID, String studentID);
	
	JSONObject getProgressChangeByDay(String courseID, String projectID, String studentID);
}
