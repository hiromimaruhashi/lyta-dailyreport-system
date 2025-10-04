package com.techacademy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Transactional
    public Report save(Report report) {
        LocalDateTime now = LocalDateTime.now();

        if (report.getId() == null) {
            // 新規作成
            report.setCreatedAt(now);
        } else {
            // 更新時は作成日時は保持
            Report dbReport = reportRepository.findById(report.getId()).orElseThrow();
            report.setCreatedAt(dbReport.getCreatedAt());
        }

        report.setUpdatedAt(now);
        report.setDeleteFlg(false);

        return reportRepository.save(report);
    }

    @Transactional
    public void delete(Integer id) {
        Report report = reportRepository.findById(id).orElseThrow();
        report.setDeleteFlg(true);
        report.setUpdatedAt(LocalDateTime.now());
        reportRepository.save(report);
    }

    public List<Report> findAll() {
        return reportRepository.findAll();
    }



    public List<Report> findByEmployee(Employee employee) {
        return reportRepository.findByEmployee(employee);
    }


    public Report findById(Integer id) {
        return reportRepository.findById(id).orElse(null);

    }
    public boolean existsByEmployeeAndDateExceptId(Employee employee, LocalDate date, Integer id) {
        return reportRepository.existsByEmployeeAndReportDateAndIdNot(employee, date, id);
    }


    public boolean existsByEmployeeAndDate(Employee employee, LocalDate date) {
        return reportRepository.existsByEmployeeAndReportDate(employee, date);
    }
}
