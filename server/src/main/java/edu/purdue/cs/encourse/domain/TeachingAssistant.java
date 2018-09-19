package edu.purdue.cs.encourse.domain;

import lombok.*;
import java.util.List;
import javax.persistence.*;

@Getter
@Entity
@Table(name = "TEACHING_ASSISTANT")
public class TeachingAssistant extends Student {

    public TeachingAssistant(@NonNull String UID, @NonNull String userName,
                        @NonNull String firstName, @NonNull String lastName,
                        String middleInit, String eduEmail) {
        super(UID, userName, firstName, lastName, middleInit, eduEmail);
    }

    public TeachingAssistant(@NonNull Account account) {
        super(account.getUserID(), account.getUserName(), account.getFirstName(),
                account.getLastName(), account.getMiddleInit(), account.getEduEmail());
    }

    public TeachingAssistant() {
    }
}
