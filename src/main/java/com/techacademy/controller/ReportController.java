package com.techacademy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;




@Controller
@RequestMapping("reports")
public class ReportController {

	private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }
    // 従業員一覧画面
    @GetMapping
    public String list(Model model, @AuthenticationPrincipal UserDetail loginUser) {

        Employee employee = loginUser.getEmployee();

        List<Report> reports;

        if (employee.getRole() == Employee.Role.ADMIN) {
            // 管理者は全件表示
            reports = reportService.findAll();
        } else {
            // 一般は自分の登録分のみ
            reports = reportService.findByEmployee(employee);
        }

        // model にセット
        model.addAttribute("reportsList", reports);
        model.addAttribute("listSize", reports.size());

        return "reports/list";
    }
 // 従業員新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Report report, @AuthenticationPrincipal Employee loginUser,Model model) {
    	model.addAttribute("employee", loginUser);
        return "reports/new";
    }

    //従業員日報新規登録画面
    @PostMapping("/add")
    public String add(@Validated Report report, BindingResult res, Model model, @AuthenticationPrincipal UserDetail loginUser) {

        // ログイン中の従業員をセット
        report.setEmployee(loginUser.getEmployee());

        // バリデーションエラーがあれば画面に戻す
        if (res.hasErrors()) {
            return "reports/new";
        }

        // 日付重複チェック（業務チェック、まだ作る場合）
        if (reportService.existsByEmployeeAndDate(report.getEmployee(), report.getReportDate())) {
        	res.rejectValue("reportDate", "error.reportDate", "既に登録されている日付です");

            return "reports/new";
        }


        // 保存して一覧画面にリダイレクト
        reportService.save(report);
        return "redirect:/reports";
    }

 // 従業員日報一覧画面
    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Integer id, Model model) {
        Report report = reportService.findById(id);
        model.addAttribute("report", report);
        return "reports/detail";  // detail.html へ遷移
    }
   //従業員日報削除
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id) {
        // サービスの delete() を呼ぶ
        reportService.delete(id);

        // 削除後は一覧画面にリダイレクト
        return "redirect:/reports";
    }

    //従業員日報更新
    @GetMapping("/update/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {
    	 model.addAttribute("report", reportService.findById(id));
         return "reports/update";
}
 // 従業員日報更新処理
    @PostMapping("/update/{id}")
    public String update(@PathVariable("id") Integer id,@Validated @ModelAttribute Report report,BindingResult res,@AuthenticationPrincipal UserDetail loginUser,Model model) {

        // ログインユーザーセット
        report.setEmployee(loginUser.getEmployee());

        // バリデーションチェック
        if (res.hasErrors()) {
            return "reports/update";
        }

        // 日付重複チェック（自分自身は除く）
        if (reportService.existsByEmployeeAndDateExceptId(report.getEmployee(), report.getReportDate(), id)) {
            res.rejectValue("reportDate", "error.reportDate", "既に登録されている日付です");
            return "reports/update";
        }

        // 更新処理
        report.setId(id);
        reportService.save(report);

        return "redirect:/reports";
    }
}