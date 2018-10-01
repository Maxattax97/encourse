package edu.purdue.cs.encourse.domain;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Entity
@Table(name = "TEACHING_ASSISTANT")
public class TeachingAssistant extends Student {

    public TeachingAssistant(String userID, String userName, String firstName, String lastName,
                        String middleInit, String eduEmail) {
        super(userID, userName, firstName, lastName, middleInit, eduEmail, true);
    }

    public TeachingAssistant() {
        super();
    }

}
