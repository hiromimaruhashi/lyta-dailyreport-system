package com.techacademy.entity;



import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;

import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;


@Data
@Entity
@Table(name = "reports")
@SQLRestriction("delete_flg = false")
public class Report {



    // ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 日付
    @NotNull
    @Column(name = "report_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate reportDate;

    // タイトル
    @Column(nullable = false, length = 100)
    @NotEmpty
    @Size(max = 100, message = "100文字以下で入力してください")
    private String title;

    // 内容
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    @NotEmpty
    @Size(max = 600, message = "600文字以下で入力してください")
    private String content;

    // 社員番号（外部キー）

    @ManyToOne
    @JoinColumn(name = "employee_code", referencedColumnName = "code", nullable = false)
    private Employee employee;

    // 削除フラグ
    @Column(name = "delete_flg", columnDefinition="TINYINT", nullable=false)
    private boolean deleteFlg;

 // 登録日時
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 更新日時
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}


