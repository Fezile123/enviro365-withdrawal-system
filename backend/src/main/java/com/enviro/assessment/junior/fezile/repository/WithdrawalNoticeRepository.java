package com.enviro.assessment.junior.fezile.repository;

import com.enviro.assessment.junior.fezile.model.WithdrawalNotice;
import com.enviro.assessment.junior.fezile.model.enums.WithdrawalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WithdrawalNoticeRepository extends JpaRepository<WithdrawalNotice, Long> {

    List<WithdrawalNotice> findByInvestorId(Long investorId);

    List<WithdrawalNotice> findByInvestorIdAndStatus(Long investorId, WithdrawalStatus status);

    List<WithdrawalNotice> findByInvestorIdAndCreatedAtBetween(
            Long investorId, LocalDateTime from, LocalDateTime to);
}
