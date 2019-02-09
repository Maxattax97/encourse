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
public class AccountModel {
	
	@NonNull
	private String username;
	
	@NonNull
	private String firstName;
	
	@NonNull
	private String lastName;
	
	@NonNull
	private String eduEmail;
	
	@NonNull
	private Integer role;
	
	public AccountModel(@NonNull Account account) {
		this.username = account.getUsername();
		this.firstName = account.getFirstName();
		this.lastName = account.getLastName();
		this.eduEmail = account.getEduEmail();
		this.role =  account.getRole() == Account.Role.STUDENT ? 0 : account.getRole() ==  Account.Role.PROFESSOR ? 1 : 2;
	}

}
