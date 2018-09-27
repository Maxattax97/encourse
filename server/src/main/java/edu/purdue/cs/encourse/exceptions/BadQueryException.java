package edu.purdue.cs.encourse.exceptions;

/**
 * Created by Killian Le Clainche on 9/26/2018.
 */
public class BadQueryException extends EncourseException {
	
	BadQueryException(String msg) {
		super(msg);
	}

	public static class NoCourseWithIDException extends BadQueryException {
		public NoCourseWithIDException(String courseID) {
			super("Invalid Course ID, can't find course with ID: " + courseID);
		}
	}
	
	public static class ProjectExistsException extends BadQueryException {
		public ProjectExistsException(String projectID) {
			super("Project identifier: (" + projectID + ") already exists in the database.");
		}
	}
	
	public static class NoProjectWithIDException extends BadQueryException {
		public NoProjectWithIDException(String projectID) {
			super("Invalid Project ID, can't find project with ID: " + projectID);
		}
	}
	
}
