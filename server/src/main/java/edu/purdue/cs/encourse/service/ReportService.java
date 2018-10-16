package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.domain.Account;
import edu.purdue.cs.encourse.domain.Report;


public interface ReportService {
    void addReport(String accountID, String comments);
    String shareReport(String reportID);
    Report getReport(String reportID, String password);
}
