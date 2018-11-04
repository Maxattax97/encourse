package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.ReportRepository;
import edu.purdue.cs.encourse.domain.Report;
import edu.purdue.cs.encourse.service.ReportService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;

@Service(value = ReportServiceImpl.NAME)
public class ReportServiceImpl implements ReportService {

    public final static String NAME = "ReportService";

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private PasswordEncoder userPasswordEncoder;

    public void addReport(String accountID, String comments) {
        Report report = new Report();
        report.setAccountID(accountID);
        report.setComments(comments);
        reportRepository.save(report);
    }

    public String shareReport(String reportID) {
        Report report = reportRepository.findByReportID(reportID);
        if (report == null) {
            return null;
        }
        String password = generatePassword(20);
        report.setLock(userPasswordEncoder.encode(password));
        reportRepository.save(report);
        return userPasswordEncoder.encode(password);
    }

    public Report getReport(String reportID, String password) {
        Report report = reportRepository.findByReportID(reportID);
        if (report == null) {
            return null;
        }
        if (userPasswordEncoder.matches(password, report.getLock())) {
            reportRepository.delete(report);
            return report;
        }
        return null;
    }

    private String generatePassword(int size) {
        char[] possibleCharacters = (new String("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?")).toCharArray();
        return RandomStringUtils.random( size, 0, possibleCharacters.length - 1, false, false, possibleCharacters, new SecureRandom());
    }

}
