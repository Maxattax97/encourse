package edu.purdue.cs.encourse.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * Created by Killian Le Clainche on 2/10/2019.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChangePasswordModel {
	
	@NonNull
	private String password;
	
	@NonNull
	private String newPassword;
	
}
