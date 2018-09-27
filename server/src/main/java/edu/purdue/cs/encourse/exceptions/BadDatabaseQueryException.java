package edu.purdue.cs.encourse.exceptions;

/**
 * Created by Killian Le Clainche on 9/26/2018.
 */
public class BadDatabaseQueryException extends EncourseException {
	
	public BadDatabaseQueryException() {
		super("Unable to execute database query");
	}
}
