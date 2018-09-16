package edu.purdue.cs.encourse.domain;

import lombok.*;
import java.util.List;

@Getter
public class TeachingAssistant extends Student {
    /** Course sections that TA oversees **/
    private List<String> teachAssistSectionCRNs;

    /** Students that the TA monitors the progress of **/
    private List<String> assignedStudentUIDs;

    public TeachingAssistant(@NonNull String UID, @NonNull String userName,
                        @NonNull String firstName, @NonNull String lastName,
                        String middleInit, String eduEmail) {
        super(UID, userName, firstName, lastName, middleInit, eduEmail);
    }

    public TeachingAssistant(@NonNull Student student) {
        super(student.getUID(), student.getUserName(), student.getFirstName(), student.getLastName(),
                student.getMiddleInit(), student.getEduEmail());
    }
}
