package edu.purdue.cs.encourse.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Map;
import java.util.Set;

/**
 * Created by Killian Le Clainche on 1/20/2019.
 */
@Data
@Entity
@Table(name = "ADDITION_HASH")
@NoArgsConstructor
@AllArgsConstructor
public class AdditionHash {
	
	@Id
	private String id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "projectID")
	private Project project;
	
	@ElementCollection
	@CollectionTable(name = "ADDITION_HASH_STUDENTS", joinColumns = @JoinColumn(name = "id"))
	private Map<Long, Integer> studentCounts;
	
}
