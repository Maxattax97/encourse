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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PROJECT_TESTS")
@ToString
public class TestScript {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "projectID")
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "projectID")
	@JsonIdentityReference(alwaysAsId=true)
	private Project project;
	
	@Setter
	@ManyToMany(fetch = FetchType.LAZY,
			cascade = {
					CascadeType.PERSIST,
					CascadeType.MERGE
			},
			mappedBy = "testScripts")
	@JsonIgnore
	private List<TestSuite> testSuites;
	
	@Setter
	@NonNull
	private String name;
	
	@Setter
	@NonNull
	private Boolean hidden;
	
	@Setter
	@NonNull
	private Double value;
	
	public TestScript(Project project, TestScriptModel model) {
		this.project = project;
		
		this.testSuites = new ArrayList<>();
		
		this.name = model.getName();
		this.hidden = model.getHidden();
		this.value = model.getValue();
	}
	
}
