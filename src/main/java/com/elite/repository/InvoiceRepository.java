package com.elite.repository;

import com.elite.models.Invoice;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, String> {
    @NotNull Page<Invoice> findAll(@NotNull Pageable pageable);
    List<Invoice> findByUserId(@NotNull String userId);
}
