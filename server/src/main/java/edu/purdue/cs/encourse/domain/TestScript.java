package edu.purdue.cs.encourse.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.purdue.cs.encourse.model.ProjectTestScriptModel;
import edu.purdue.cs.encourse.model.TestScriptModel;
import lombok.AllArgsConstructor;
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
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Killian Le Clainche on 1/18/2019.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PROJECT_TESTS")
@ToString
public class TestScript {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "TEST_SCRIPT_ID")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "PROJECT_ID")
	private Project project;
	
	@ManyToMany(fetch = FetchType.LAZY,
			cascade = {
					CascadeType.PERSIST,
					CascadeType.MERGE
			},
			mappedBy = "testScripts")
	private List<TestSuite> testSuites;
	
	@NonNull
	@Column(name = "NAME")
	private String name;
	
	@NonNull
	@Column(name = "HIDDEN")
	private Boolean hidden;
	
	@NonNull
	@Column(name = "VALUE")
	private Double value;
	
	public TestScript(@NonNull Project project, @NonNull TestScriptModel model) {
		this.project = project;
		
		this.testSuites = new ArrayList<>();
		
		this.name = model.getName();
		this.hidden = model.getHidden();
		this.value = model.getValue();
	}
	
	public String toString() {
		return "Test Script (id=" + this.id + ", name=" + this.name + ", hidden=" + this.hidden + ", value=" + this.value + ")";
	}
	
}
