package edu.purdue.cs.encourse.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.purdue.cs.encourse.model.TestSuiteModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Killian Le Clainche on 1/18/2019.
 */
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PROJECT_SUITES")
@Getter
@Setter
@ToString
public class TestSuite {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "TEST_SUITE_ID")
	private Long id;
	
	@ManyToOne
	@NonNull
	@JoinColumn(name = "PROJECT_ID")
	private Project project;
	
	@NonNull
	private String name;
	
	@NonNull
	private Boolean hidden;
	
	@NonNull
	@ManyToMany(fetch = FetchType.EAGER,
			cascade = {
					CascadeType.PERSIST,
					CascadeType.MERGE
			})
	@JoinTable(name = "PROJECT_TEST_SUITES",
			joinColumns = @JoinColumn(name = "TEST_SUITE_ID"),
			inverseJoinColumns = @JoinColumn(name = "TEST_SCRIPT_ID"))
	private List<TestScript> testScripts;
	
	public TestSuite(@NonNull Project project, @NonNull TestSuiteModel model) {
		this.project = project;
		
		this.name = model.getName();
		this.hidden = model.getHidden();
		
		this.testScripts = new ArrayList<>();
	}
	
	@Override
	public int hashCode() {
		return Math.toIntExact(id);
	}
	
	@Override
	public boolean equals(Object testSuite) {
		return testSuite instanceof TestSuite && ((TestSuite) testSuite).id.equals(this.id);
	}
	
}
