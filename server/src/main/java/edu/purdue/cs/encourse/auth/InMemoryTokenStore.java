package edu.purdue.cs.encourse.auth;

import java.security.SecureRandom;
import java.util.Map;

import org.springframework.security.crypto.codec.Hex;
import org.springframework.util.Assert;

/**
 * @author Arik Cohen, Jordan Buckmaster
 * @since Jan 28, 2018
 */
public class InMemoryTokenStore implements TokenStore {

    private static final long FIFTEEN_MINS = 15 * 60 * 1000;

    private final Map<String, String> store = new SelfExpiringHashMap<>(FIFTEEN_MINS);

    private final SecureRandom random = new SecureRandom();

    private final int TOKEN_BYTE_SIZE = 16;

    @Override
    public String create (String userId) {
        Assert.notNull(userId,"user id can't be null");
        byte bytes[] = new byte[TOKEN_BYTE_SIZE];
        random.nextBytes(bytes);
        String token = String.valueOf(Hex.encode(bytes));
        store.put(userId, token);
        return token;
    }

    @Override
    public String get(String userId) {
        Assert.notNull(userId,"user id can't be null");
        return store.remove(userId);
    }

    @Override
    public String remove(String userId) {

        return "";
    }

}