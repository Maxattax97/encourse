package edu.purdue.cs.encourse.model;

import edu.purdue.cs.encourse.domain.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Created by Killian Le Clainche on 2/5/2019.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserModel {
	
	@NonNull
	private Long userID;
	
	@NonNull
	private String username;
	
	@NonNull
	private String password;
	
	@NonNull
	private Boolean accountExpired;
	
	@NonNull
	private Boolean accountLocked;
	
	@NonNull
	private Boolean credentialsExpired;
	
	@NonNull
	private Boolean enabled;
	
	public UserModel(@NonNull Account account, @NonNull String password) {
		this.userID = account.getUserID();
		this.username = account.getUsername();
		
		this.password = password;
		
		this.accountExpired = false;
		this.accountLocked = false;
		this.credentialsExpired = false;
		this.enabled = true;
	}
}
