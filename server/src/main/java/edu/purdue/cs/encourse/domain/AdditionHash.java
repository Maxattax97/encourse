package edu.purdue.cs.encourse.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.math3.analysis.function.Add;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
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
	@Column(name = "ADDITION_HASH_ID")
	@NonNull
	private String id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PROJECT_ID")
	private Project project;
	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "ADDITION_HASH_STUDENTS", joinColumns = @JoinColumn(name = "ADDITION_HASH_ID"))
	private Map<Long, Integer> studentCounts;

	@Override
	public boolean equals(Object object) {
		return object instanceof AdditionHash && ((AdditionHash) object).id.equals(this.id);
	}
	
	@Override
	public int hashCode() {
		return this.id.hashCode();
	}
	
}
