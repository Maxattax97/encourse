package edu.purdue.cs.encourse.domain;

import lombok.*;
import java.util.List;
import javax.persistence.*;

@Getter
@Entity
@Table(name = "TEACHING_ASSISTANT")
public class TeachingAssistant extends Student {

    public TeachingAssistant(String userID, String userName, String saltPass, String firstName, String lastName,
                        String middleInit, String eduEmail) {
        super(userID, userName, saltPass, firstName, lastName, middleInit, eduEmail);
    }

    public TeachingAssistant(Account account) {
        super(account.getUserID(), account.getUserName(), account.getSaltPass(), account.getFirstName(),
                account.getLastName(), account.getMiddleInit(), account.getEduEmail());
    }

    public TeachingAssistant() {

    }
}
