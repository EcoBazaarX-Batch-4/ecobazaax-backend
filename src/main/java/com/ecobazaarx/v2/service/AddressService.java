package com.ecobazaarx.v2.service;

import com.ecobazaarx.v2.dto.AddressDto;
import com.ecobazaarx.v2.model.Address;
import com.ecobazaarx.v2.model.User;
import com.ecobazaarx.v2.repository.AddressRepository;
import com.ecobazaarx.v2.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<AddressDto> getMyAddresses(UserDetails currentUser) {
        User user = findUserByEmail(currentUser.getUsername());
        return addressRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AddressDto getAddressById(UserDetails currentUser, Long addressId) {
        User user = findUserByEmail(currentUser.getUsername());
        Address address = findAddressById(addressId);
        checkOwnership(user, address);
        return mapToDto(address);
    }

    @Transactional
    public AddressDto createAddress(UserDetails currentUser, AddressDto dto) {
        User user = findUserByEmail(currentUser.getUsername());

        if (dto.isDefault()) {
            unsetOtherDefaults(user);
        }

        Address address = new Address();
        address.setUser(user);
        mapToEntity(dto, address);

        Address savedAddress = addressRepository.save(address);
        return mapToDto(savedAddress);
    }

    @Transactional
    public AddressDto updateAddress(UserDetails currentUser, Long addressId, AddressDto dto) {
        User user = findUserByEmail(currentUser.getUsername());
        Address address = findAddressById(addressId);
        checkOwnership(user, address);

        if (dto.isDefault()) {
            unsetOtherDefaults(user);
        }

        mapToEntity(dto, address);
        Address updatedAddress = addressRepository.save(address);
        return mapToDto(updatedAddress);
    }

    @Transactional
    public void deleteAddress(UserDetails currentUser, Long addressId) {
        User user = findUserByEmail(currentUser.getUsername());
        Address address = findAddressById(addressId);
        checkOwnership(user, address);
        addressRepository.delete(address);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private Address findAddressById(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));
    }

    private void checkOwnership(User user, Address address) {
        if (!address.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to access this address");
        }
    }

    private void unsetOtherDefaults(User user) {
        addressRepository.findByUserId(user.getId()).forEach(addr -> {
            if (addr.isDefault()) {
                addr.setDefault(false);
                addressRepository.save(addr);
            }
        });
    }

    private AddressDto mapToDto(Address entity) {
        AddressDto dto = new AddressDto();
        dto.setId(entity.getId());
        dto.setLabel(entity.getLabel());
        dto.setStreet(entity.getStreet());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setPostalCode(entity.getPostalCode());
        dto.setCountry(entity.getCountry());
        dto.setDefault(entity.isDefault());
        return dto;
    }

    private void mapToEntity(AddressDto dto, Address entity) {
        entity.setLabel(dto.getLabel());
        entity.setStreet(dto.getStreet());
        entity.setCity(dto.getCity());
        entity.setState(dto.getState());
        entity.setPostalCode(dto.getPostalCode());
        entity.setCountry(dto.getCountry());
        entity.setDefault(dto.isDefault());
    }
}
