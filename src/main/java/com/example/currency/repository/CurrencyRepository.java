package com.example.currency.repository;

import com.example.currency.entity.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurrencyRepository extends JpaRepository<CurrencyEntity, String> {
    // 根據幣別代碼排序查詢所有幣別
    List<CurrencyEntity> findAllByOrderByCodeAsc();
}