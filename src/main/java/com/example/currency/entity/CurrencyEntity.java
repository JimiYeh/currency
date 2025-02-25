package com.example.currency.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "CURRENCY")
public class CurrencyEntity {

    @Id
    @Column(length = 3)
    private String code; // 幣別代碼

    @Column(nullable = false)
    private String chineseName; // 中文名稱
}