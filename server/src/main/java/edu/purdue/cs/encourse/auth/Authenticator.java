package edu.purdue.cs.encourse.auth;

import java.security.Principal;

/**
 * Authenticates a User ID / Token combination.
 *
 * @author Arik Cohen
 * @since Jan 30, 2018
 */
public interface Authenticator {

    Principal authenticate (String userId, String token);

}