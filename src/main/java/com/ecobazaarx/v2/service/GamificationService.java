package com.ecobazaarx.v2.service;

import com.ecobazaarx.v2.dto.EcoPointLedgerDto;
import com.ecobazaarx.v2.model.EcoPointLedger;
import com.ecobazaarx.v2.model.User;
import com.ecobazaarx.v2.repository.EcoPointLedgerRepository;
import com.ecobazaarx.v2.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GamificationService {

    private final EcoPointLedgerRepository ledgerRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<EcoPointLedgerDto> getPointHistory(UserDetails currentUser, Pageable pageable) {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Page<EcoPointLedger> ledgerPage = ledgerRepository.findByUserIdOrderByTransactionDateDesc(user.getId(), pageable);
        return ledgerPage.map(this::mapToDto);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addPointsForAction(User user, int pointsToAdd, String reason) {
        if (pointsToAdd <= 0) {
            return;
        }
        user.setEcoPoints(user.getEcoPoints() + pointsToAdd);
        userRepository.save(user);
        EcoPointLedger ledgerEntry = new EcoPointLedger(user, pointsToAdd, reason);
        ledgerRepository.save(ledgerEntry);
    }

    private EcoPointLedgerDto mapToDto(EcoPointLedger ledgerEntry) {
        return EcoPointLedgerDto.builder()
                .id(ledgerEntry.getId())
                .pointsChanged(ledgerEntry.getPointsChanged())
                .reason(ledgerEntry.getReason())
                .transactionDate(ledgerEntry.getTransactionDate())
                .build();
    }
}
