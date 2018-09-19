package edu.purdue.cs.encourse.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;

import java.io.IOException;

public class AccountDeserializer extends StdDeserializer<Account> {

    public AccountDeserializer() {
        this(null);
    }

    public AccountDeserializer(Class<?> c) {
        super(c);
    }

    @Override
    public Account deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode accountNode = jp.getCodec().readTree(jp);

        String userID = accountNode.get("userID").asText();
        String userName = accountNode.get("userName").asText();
        String firstName = accountNode.get("firstName").asText();
        String lastName = accountNode.get("lastName").asText();
        int role = (Integer) ((IntNode) accountNode.get("role")).numberValue();
        String middleInit = accountNode.get("middleInit").asText();
        String eduEmail = accountNode.get("eduEmail").asText();

        Account a = new Account(userID, userName, firstName, lastName, role, middleInit, eduEmail);
        return a;
    }
}
