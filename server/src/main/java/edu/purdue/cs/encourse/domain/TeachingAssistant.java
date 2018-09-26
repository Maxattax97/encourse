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
        super(userID, userName, saltPass, firstName, lastName, middleInit, eduEmail, true);
    }

    public TeachingAssistant() {
        super();
    }
}
