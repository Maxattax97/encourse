package edu.purdue.cs.encourse.domain;

import edu.purdue.cs.encourse.domain.relations.StudentProjectDate;
import edu.purdue.cs.encourse.model.BasicStatistics;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
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
	@Column(name = "PROJECT_DATE_ID")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PROJECT_ID")
	private Project project;
	
	@Setter
	@Column(name = "DATE")
	private LocalDate date;
	
	@Setter
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "max", column = @Column(name = "MAX_TOTAL_POINTS")),
			@AttributeOverride(name = "min", column = @Column(name = "MIN_TOTAL_POINTS")),
			@AttributeOverride(name = "mean", column = @Column(name = "MEAN_TOTAL_POINTS")),
			@AttributeOverride(name = "median", column = @Column(name = "MEDIAN_TOTAL_POINTS")),
			@AttributeOverride(name = "variance", column = @Column(name = "VARIANCE_TOTAL_POINTS"))
	})
	private BasicStatistics totalPointStats;
	
	@Setter
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "max", column = @Column(name = "MAX_VISIBLE_POINTS")),
			@AttributeOverride(name = "min", column = @Column(name = "MIN_VISIBLE_POINTS")),
			@AttributeOverride(name = "mean", column = @Column(name = "MEAN_VISIBLE_POINTS")),
			@AttributeOverride(name = "median", column = @Column(name = "MEDIAN_VISIBLE_POINTS")),
			@AttributeOverride(name = "variance", column = @Column(name = "VARIANCE_VISIBLE_POINTS"))
	})
	private BasicStatistics visiblePointStats;
	
	@Setter
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "max", column = @Column(name = "MAX_HIDDEN_POINTS")),
			@AttributeOverride(name = "min", column = @Column(name = "MIN_HIDDEN_POINTS")),
			@AttributeOverride(name = "mean", column = @Column(name = "MEAN_HIDDEN_POINTS")),
			@AttributeOverride(name = "median", column = @Column(name = "MEDIAN_HIDDEN_POINTS")),
			@AttributeOverride(name = "variance", column = @Column(name = "VARIANCE_HIDDEN_POINTS"))
	})
	private BasicStatistics hiddenPointStats;
	
	@Setter
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "max", column = @Column(name = "MAX_COMMITS")),
			@AttributeOverride(name = "min", column = @Column(name = "MIN_COMMITS")),
			@AttributeOverride(name = "mean", column = @Column(name = "MEAN_COMMITS")),
			@AttributeOverride(name = "median", column = @Column(name = "MEDIAN_COMMITS")),
			@AttributeOverride(name = "variance", column = @Column(name = "VARIANCE_COMMITS"))
	})
	private BasicStatistics commitStats;
	
	@Setter
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "max", column = @Column(name = "MAX_MINUTES")),
			@AttributeOverride(name = "min", column = @Column(name = "MIN_MINUTES")),
			@AttributeOverride(name = "mean", column = @Column(name = "MEAN_MINUTES")),
			@AttributeOverride(name = "median", column = @Column(name = "MEDIAN_MINUTES")),
			@AttributeOverride(name = "variance", column = @Column(name = "VARIANCE_MINUTES"))
	})
	private BasicStatistics minuteStats;
	
	@Setter
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "max", column = @Column(name = "MAX_ADDITIONS")),
			@AttributeOverride(name = "min", column = @Column(name = "MIN_ADDITIONS")),
			@AttributeOverride(name = "mean", column = @Column(name = "MEAN_ADDITIONS")),
			@AttributeOverride(name = "median", column = @Column(name = "MEDIAN_ADDITIONS")),
			@AttributeOverride(name = "variance", column = @Column(name = "VARIANCE_ADDITIONS"))
	})
	private BasicStatistics additionStats;
	
	@Setter
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "max", column = @Column(name = "MAX_DELETIONS")),
			@AttributeOverride(name = "min", column = @Column(name = "MIN_DELETIONS")),
			@AttributeOverride(name = "mean", column = @Column(name = "MEAN_DELETIONS")),
			@AttributeOverride(name = "median", column = @Column(name = "MEDIAN_DELETIONS")),
			@AttributeOverride(name = "variance", column = @Column(name = "VARIANCE_DELETIONS"))
	})
	private BasicStatistics deletionStats;
	
	@ElementCollection
	@CollectionTable(name = "PROJECT_DATE_TESTS", joinColumns = @JoinColumn(name = "PROJECT_DATE_ID"))
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
