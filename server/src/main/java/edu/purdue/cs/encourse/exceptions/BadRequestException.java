package edu.purdue.cs.encourse.exceptions;

/**
 * Created by Killian Le Clainche on 9/27/2018.
 */
public class BadRequestException extends EncourseException {
	
	public BadRequestException(String msg) {
		super("Bad request made. " + msg);
	}
	
}
