package edu.purdue.cs.encourse.auth;

/**
 * An interface for sending temporary authentication tokens to users.
 *
 * @author Arik Cohen
 * @since Jan 30, 2018
 */
public interface Sender {

    void send (String userId, String email, String token);

}