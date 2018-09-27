package edu.purdue.cs.encourse.exceptions;

/**
 * Created by Killian Le Clainche on 9/26/2018.
 */
public class ParameterEmptyException extends EncourseException {
	
	public ParameterEmptyException(String parameter) {
		super("Parameter: (" + parameter + ") requires a non-null and non-empty parameter.");
	}
}
