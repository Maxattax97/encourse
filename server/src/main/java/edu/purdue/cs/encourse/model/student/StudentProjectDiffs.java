package edu.purdue.cs.encourse.model.student;

import edu.purdue.cs.encourse.domain.Commit;
import edu.purdue.cs.encourse.domain.relations.StudentProject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Created by Killian Le Clainche on 1/30/2019.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StudentProjectDiffs {
	
	private List<Commit> commits;
	
	private Map<LocalDate, Integer> frequencies;
	
}
