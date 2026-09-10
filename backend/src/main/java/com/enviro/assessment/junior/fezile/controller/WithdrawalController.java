package com.enviro.assessment.junior.fezile.controller;

import com.enviro.assessment.junior.fezile.dto.WithdrawalRequest;
import com.enviro.assessment.junior.fezile.model.WithdrawalNotice;
import com.enviro.assessment.junior.fezile.service.WithdrawalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Satisfies the mandatory backend requirements:
 * "Create withdrawal notices (with balance calculations)" and provides the
 * data the "Withdrawal history table" frontend requirement needs.
 */
@RestController
@RequestMapping("/api/investors/{investorId}/withdrawals")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    public WithdrawalController(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WithdrawalNotice createWithdrawal(@PathVariable Long investorId,
                                              @Valid @RequestBody WithdrawalRequest request) {
        return withdrawalService.createWithdrawal(investorId, request);
    }

    @GetMapping
    public List<WithdrawalNotice> getHistory(@PathVariable Long investorId) {
        return withdrawalService.getHistory(investorId);
    }
}
