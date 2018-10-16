package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.Report;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;

public interface ReportRepository extends CrudRepository<Report, String> {
    Report findByReportID(@NonNull String reportID);
}
