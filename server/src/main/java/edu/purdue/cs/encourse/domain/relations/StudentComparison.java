package edu.purdue.cs.encourse.domain.relations;

import edu.purdue.cs.encourse.domain.Project;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@RequiredArgsConstructor
@AllArgsConstructor
public class StudentComparison {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "STUDENT_COMPARISON_ID")
	private Long id;
	
	@NonNull
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PROJECT_ID")
	private Project project;
	
	@NonNull
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "FIRST_STUDENT_PROJECT_ID")
	private StudentProject studentProject1;
	
	@NonNull
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SECOND_STUDENT_PROJECT_ID")
	private StudentProject studentProject2;
	
	@NonNull
	@Column(name = "SIMILARITY_COUNT")
	private Integer count;
	
}
