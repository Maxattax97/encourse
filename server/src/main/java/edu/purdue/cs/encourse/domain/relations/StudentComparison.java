package edu.purdue.cs.encourse.domain.relations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * Created by Killian Le Clainche on 2/9/2019.
 */
@Getter
@Setter
@Entity
@Table(name = "STUDENT_COMPARISON")
@NoArgsConstructor
@AllArgsConstructor
public class StudentComparison {
	
	@NonNull
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "FIRST_STUDENT_PROJECT_ID")
	private StudentProject studentProject1;
	
	@NonNull
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SECOND_STUDENT_PROJECT_ID")
	private StudentProject studentProject2;
	
	private Integer count;
	
}
