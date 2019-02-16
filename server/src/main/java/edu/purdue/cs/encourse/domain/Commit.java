package edu.purdue.cs.encourse.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;

/**
 * Created by Killian Le Clainche on 1/22/2019.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Commit {
	private String hash;
	
	private LocalDateTime date;
	
	private Double additions;
	private Double deletions;
	
	private Double visiblePoints;
	private Double hiddenPoints;
	
	private Double minutes;
	
}
