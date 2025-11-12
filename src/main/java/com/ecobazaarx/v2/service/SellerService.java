package com.ecobazaarx.v2.service;

import com.ecobazaarx.v2.dto.PayoutDetailsDto;
import com.ecobazaarx.v2.dto.ProfileResponse;
import com.ecobazaarx.v2.dto.SellerApplicationRequest;
import com.ecobazaarx.v2.dto.StoreProfileRequest;
import com.ecobazaarx.v2.model.PayoutDetails;
import com.ecobazaarx.v2.model.SellerApplicationStatus;
import com.ecobazaarx.v2.model.User;
import com.ecobazaarx.v2.repository.PayoutDetailsRepository;
import com.ecobazaarx.v2.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final UserRepository userRepository;
    private final ProfileService profileService;
    private final PayoutDetailsRepository payoutDetailsRepository;

    @Transactional
    public ProfileResponse applyToBeSeller(UserDetails currentUser, SellerApplicationRequest request) {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (user.getSellerStatus() == SellerApplicationStatus.PENDING ||
                user.getSellerStatus() == SellerApplicationStatus.APPROVED) {
            throw new IllegalStateException("You have already applied or are already a seller.");
        }

        user.setSellerStatus(SellerApplicationStatus.PENDING);
        user.setStoreName(request.getStoreName());
        userRepository.save(user);
        return profileService.getProfile(user);
    }

    @Transactional(readOnly = true)
    public SellerApplicationStatus getApplicationStatus(UserDetails currentUser) {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return user.getSellerStatus();
    }

    @Transactional
    public ProfileResponse updateStoreProfile(UserDetails sellerDetails, StoreProfileRequest request) {
        User seller = findUserByEmail(sellerDetails.getUsername());

        seller.setStoreName(request.getStoreName());
        seller.setStoreDescription(request.getStoreDescription());

        userRepository.save(seller);

        return profileService.mapUserToProfileResponse(seller);
    }

    @Transactional(readOnly = true)
    public PayoutDetailsDto getPayoutDetails(UserDetails sellerDetails) {
        User seller = findUserByEmail(sellerDetails.getUsername());

        PayoutDetails details = payoutDetailsRepository.findById(seller.getId())
                .orElse(new PayoutDetails(seller));

        return mapToDto(details);
    }

    @Transactional
    public PayoutDetailsDto updatePayoutDetails(UserDetails sellerDetails, PayoutDetailsDto dto) {
        User seller = findUserByEmail(sellerDetails.getUsername());

        PayoutDetails details = payoutDetailsRepository.findById(seller.getId())
                .orElse(new PayoutDetails(seller));

        details.setBankName(dto.getBankName());
        details.setAccountHolderName(dto.getAccountHolderName());
        details.setAccountNumber(dto.getAccountNumber());
        details.setIfscCode(dto.getIfscCode());

        PayoutDetails savedDetails = payoutDetailsRepository.save(details);
        return mapToDto(savedDetails);
    }

    private PayoutDetailsDto mapToDto(PayoutDetails entity) {
        PayoutDetailsDto dto = new PayoutDetailsDto();
        dto.setBankName(entity.getBankName());
        dto.setAccountHolderName(entity.getAccountHolderName());
        dto.setAccountNumber(entity.getAccountNumber());
        dto.setIfscCode(entity.getIfscCode());
        return dto;
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
