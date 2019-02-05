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
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "projectID")
	private Project project;
	
	@Setter
	private LocalDate date;
	
	@Setter
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "max", column = @Column(name = "max1")),
			@AttributeOverride(name = "min", column = @Column(name = "min1")),
			@AttributeOverride(name = "mean", column = @Column(name = "mean1")),
			@AttributeOverride(name = "median", column = @Column(name = "median1")),
			@AttributeOverride(name = "variance", column = @Column(name = "variance1"))
	})
	private BasicStatistics totalPointStats;
	
	@Setter
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "max", column = @Column(name = "max2")),
			@AttributeOverride(name = "min", column = @Column(name = "min2")),
			@AttributeOverride(name = "mean", column = @Column(name = "mean2")),
			@AttributeOverride(name = "median", column = @Column(name = "median2")),
			@AttributeOverride(name = "variance", column = @Column(name = "variance2"))
	})
	private BasicStatistics visiblePointStats;
	
	@Setter
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "max", column = @Column(name = "max3")),
			@AttributeOverride(name = "min", column = @Column(name = "min3")),
			@AttributeOverride(name = "mean", column = @Column(name = "mean3")),
			@AttributeOverride(name = "median", column = @Column(name = "median3")),
			@AttributeOverride(name = "variance", column = @Column(name = "variance3"))
	})
	private BasicStatistics hiddenPointStats;
	
	@Setter
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "max", column = @Column(name = "max4")),
			@AttributeOverride(name = "min", column = @Column(name = "min4")),
			@AttributeOverride(name = "mean", column = @Column(name = "mean4")),
			@AttributeOverride(name = "median", column = @Column(name = "median4")),
			@AttributeOverride(name = "variance", column = @Column(name = "variance4"))
	})
	private BasicStatistics commitStats;
	
	@Setter
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "max", column = @Column(name = "max5")),
			@AttributeOverride(name = "min", column = @Column(name = "min5")),
			@AttributeOverride(name = "mean", column = @Column(name = "mean5")),
			@AttributeOverride(name = "median", column = @Column(name = "median5")),
			@AttributeOverride(name = "variance", column = @Column(name = "variance5"))
	})
	private BasicStatistics minuteStats;
	
	@Setter
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "max", column = @Column(name = "max6")),
			@AttributeOverride(name = "min", column = @Column(name = "min6")),
			@AttributeOverride(name = "mean", column = @Column(name = "mean6")),
			@AttributeOverride(name = "median", column = @Column(name = "median6")),
			@AttributeOverride(name = "variance", column = @Column(name = "variance6"))
	})
	private BasicStatistics additionStats;
	
	@Setter
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "max", column = @Column(name = "max7")),
			@AttributeOverride(name = "min", column = @Column(name = "min7")),
			@AttributeOverride(name = "mean", column = @Column(name = "mean7")),
			@AttributeOverride(name = "median", column = @Column(name = "median7")),
			@AttributeOverride(name = "variance", column = @Column(name = "variance7"))
	})
	private BasicStatistics deletionStats;
	
	@Setter
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "max", column = @Column(name = "max8")),
			@AttributeOverride(name = "min", column = @Column(name = "min8")),
			@AttributeOverride(name = "mean", column = @Column(name = "mean8")),
			@AttributeOverride(name = "median", column = @Column(name = "median8")),
			@AttributeOverride(name = "variance", column = @Column(name = "variance8"))
	})
	private BasicStatistics changesStats;
	
	@Setter
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "max", column = @Column(name = "max9")),
			@AttributeOverride(name = "min", column = @Column(name = "min9")),
			@AttributeOverride(name = "mean", column = @Column(name = "mean9")),
			@AttributeOverride(name = "median", column = @Column(name = "median9")),
			@AttributeOverride(name = "variance", column = @Column(name = "variance9"))
	})
	private BasicStatistics similarityStats;
	
	@Setter
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "max", column = @Column(name = "max10")),
			@AttributeOverride(name = "min", column = @Column(name = "min10")),
			@AttributeOverride(name = "mean", column = @Column(name = "mean10")),
			@AttributeOverride(name = "median", column = @Column(name = "median10")),
			@AttributeOverride(name = "variance", column = @Column(name = "variance10"))
	})
	private BasicStatistics timeVelocityStats;
	
	@Setter
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "max", column = @Column(name = "max11")),
			@AttributeOverride(name = "min", column = @Column(name = "min11")),
			@AttributeOverride(name = "mean", column = @Column(name = "mean11")),
			@AttributeOverride(name = "median", column = @Column(name = "median11")),
			@AttributeOverride(name = "variance", column = @Column(name = "variance11"))
	})
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
