package edu.purdue.cs.encourse.domain;

import edu.purdue.cs.encourse.domain.relations.StudentProjectDate;
import edu.purdue.cs.encourse.model.BasicStatistics;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Killian Le Clainche on 1/16/2019.
 */
@Getter
@Entity
@Table(name = "PROJECT_DATE")
@NoArgsConstructor
public class ProjectDate {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "projectID")
	private Project project;
	
	@Setter
	private LocalDate date;
	
	@Setter
	@Embedded
	private BasicStatistics totalPointStats;
	
	@Setter
	@Embedded
	private BasicStatistics visiblePointStats;
	
	@Setter
	@Embedded
	private BasicStatistics hiddenPointStats;
	
	@Setter
	@Embedded
	private BasicStatistics commitStats;
	
	@Setter
	@Embedded
	private BasicStatistics minuteStats;
	
	@Setter
	@Embedded
	private BasicStatistics additionStats;
	
	@Setter
	@Embedded
	private BasicStatistics deletionStats;
	
	@Setter
	@Embedded
	private BasicStatistics changesStats;
	
	@Setter
	@Embedded
	private BasicStatistics similarityStats;
	
	@Setter
	@Embedded
	private BasicStatistics timeVelocityStats;
	
	@Setter
	@Embedded
	private BasicStatistics commitVelocityStats;
	
	@ElementCollection
	@CollectionTable(name = "PROJECT_DATE_TESTS", joinColumns = @JoinColumn(name = "projectID"))
	private Map<String, Integer> testsTotal;
	
	public ProjectDate(Project project, LocalDate date) {
		this.project = project;
		this.date = date;
		
		this.totalPointStats = new BasicStatistics();
		this.visiblePointStats = new BasicStatistics();
		this.hiddenPointStats = new BasicStatistics();
		this.commitStats = new BasicStatistics();
		this.minuteStats = new BasicStatistics();
		this.additionStats = new BasicStatistics();
		this.deletionStats = new BasicStatistics();
		this.testsTotal = new HashMap<>();
	}
	
}
