package com.ecobazaarx.v2.service;

import com.ecobazaarx.v2.dto.AdminCreateRequest;
import com.ecobazaarx.v2.dto.AdminUserResponseDto;
import com.ecobazaarx.v2.dto.AdminUserUpdateRequest;
import com.ecobazaarx.v2.dto.ProfileResponse;
import com.ecobazaarx.v2.model.*;
import com.ecobazaarx.v2.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;
    private final MaterialRepository materialRepository;
    private final ManufacturingProcessRepository manufacturingProcessRepository;
    private final PackagingMaterialRepository packagingMaterialRepository;
    private final TransportZoneRepository transportZoneRepository;
    private final PasswordEncoder passwordEncoder;
    private final TaxRateRepository taxRateRepository;
    private final DiscountRepository discountRepository;


    @Transactional(readOnly = true)
    public List<ProfileResponse> getPendingSellerApplications() {
        return userRepository.findBySellerStatus(SellerApplicationStatus.PENDING)
                .stream()
                .map(profileService::mapUserToProfileResponse) // Re-use our mapper
                .collect(Collectors.toList());
    }

    @Transactional
    public ProfileResponse approveSellerApplication(Long userId) {
        User user = findUserById(userId);

        if (user.getSellerStatus() != SellerApplicationStatus.PENDING) {
            throw new IllegalStateException("User is not pending approval.");
        }

        user.setSellerStatus(SellerApplicationStatus.APPROVED);

        Role sellerRole = roleRepository.findByName(RoleName.ROLE_SELLER)
                .orElseThrow(() -> new RuntimeException("ROLE_SELLER not found"));
        user.getRoles().add(sellerRole);

        userRepository.save(user);
        return profileService.mapUserToProfileResponse(user);
    }

    @Transactional
    public ProfileResponse rejectSellerApplication(Long userId) {
        User user = findUserById(userId);

        if (user.getSellerStatus() != SellerApplicationStatus.PENDING) {
            throw new IllegalStateException("User is not pending approval.");
        }

        user.setSellerStatus(SellerApplicationStatus.REJECTED);
        user.setStoreName(null);
        userRepository.save(user);
        return profileService.mapUserToProfileResponse(user);
    }

    @Transactional(readOnly = true)
    public Page<AdminUserResponseDto> getAllUsers(
            String query,
            RoleName role,
            SellerApplicationStatus status,
            Pageable pageable
    ) {
        Specification<User> spec = (root, criteriaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (query != null && !query.isBlank()) {
                String likePattern = "%" + query.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), likePattern),
                        cb.like(cb.lower(root.get("email")), likePattern)
                ));
            }

            if (role != null) {
                predicates.add(cb.isMember(roleRepository.findByName(role)
                        .orElseThrow(() -> new EntityNotFoundException("Role not found")), root.get("roles")));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("sellerStatus"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<User> userPage = userRepository.findAll(spec, pageable);
        return userPage.map(this::mapUserToAdminResponseDto);
    }

    @Transactional
    public AdminUserResponseDto updateUserAsAdmin(Long userId, AdminUserUpdateRequest request) {
        User user = findUserById(userId);

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setAccountNonLocked(request.isAccountNonLocked());
        user.setEnabled(request.isEnabled());

        if (request.getRoles() != null) {
            Set<Role> newRoles = request.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new EntityNotFoundException("Role " + roleName + " not found")))
                    .collect(Collectors.toSet());
            user.setRoles(newRoles);
        }

        User updatedUser = userRepository.save(user);
        return mapUserToAdminResponseDto(updatedUser);
    }

    @Transactional
    public AdminUserResponseDto createAdmin(AdminCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new EntityNotFoundException("ROLE_ADMIN not found"));

        var user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );

        user.setRoles(Set.of(adminRole));

        user.setSellerStatus(SellerApplicationStatus.NOT_APPLICABLE);

        User savedUser = userRepository.save(user);

        return mapUserToAdminResponseDto(savedUser);
    }

    @Transactional(readOnly = true)
    public List<TransportZone> getTransportZones() {
        return transportZoneRepository.findAll();
    }

    /*----------- Tax Rate -----------*/
    public TaxRate createTaxRate(TaxRate taxRate) {
        return taxRateRepository.save(taxRate);
    }

    @Transactional(readOnly = true)
    public List<TaxRate> getTaxRates() {
        return taxRateRepository.findAll();
    }

    public TaxRate updateTaxRate(Long id, TaxRate details) {
        TaxRate entity = taxRateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TaxRate not found"));
        entity.setName(details.getName());
        entity.setRate(details.getRate());
        entity.setCountry(details.getCountry());
        entity.setState(details.getState());
        return taxRateRepository.save(entity);
    }

    public void deleteTaxRate(Long id) {
        taxRateRepository.deleteById(id);
    }

    /*----------- Discount -----------*/
    public Discount createDiscount(Discount discount) {
        return discountRepository.save(discount);
    }

    @Transactional(readOnly = true)
    public List<Discount> getDiscounts() {
        return discountRepository.findAll();
    }

    public Discount updateDiscount(Long id, Discount details) {
        Discount entity = discountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Discount not found"));

        entity.setCode(details.getCode());
        entity.setDiscountType(details.getDiscountType());
        entity.setValue(details.getValue());
        entity.setMinPurchaseAmount(details.getMinPurchaseAmount());
        entity.setValidFrom(details.getValidFrom());
        entity.setValidUntil(details.getValidUntil());
        entity.setUsageLimit(details.getUsageLimit());

        return discountRepository.save(entity);
    }

    public void deleteDiscount(Long id) {
        discountRepository.deleteById(id);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    }

    /*----------- Category -----------*/
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    public Category updateCategory(Integer id, Category details) {
        Category entity = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        entity.setName(details.getName());
        entity.setDescription(details.getDescription());
        return categoryRepository.save(entity);
    }

    public void deleteCategory(Integer id) {
        categoryRepository.deleteById(id);
    }

    /*----------- Material -----------*/
    public Material createMaterial(Material material) {
        return materialRepository.save(material);
    }

    @Transactional(readOnly = true)
    public List<Material> getMaterials() {
        return materialRepository.findAll();
    }

    public Material updateMaterial(Integer id, Material details) {
        Material entity = materialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Material not found"));
        entity.setName(details.getName());
        entity.setEmissionPerKg(details.getEmissionPerKg());
        entity.setSource(details.getSource());
        return materialRepository.save(entity);
    }

    public void deleteMaterial(Integer id) {
        materialRepository.deleteById(id);
    }

    /*----------- Manufacturing Process -----------*/
    public ManufacturingProcess createManufacturingProcess(ManufacturingProcess process) {
        return manufacturingProcessRepository.save(process);
    }

    @Transactional(readOnly = true)
    public List<ManufacturingProcess> getManufacturingProcesses() {
        return manufacturingProcessRepository.findAll();
    }

    public ManufacturingProcess updateManufacturingProcess(Integer id, ManufacturingProcess details) {
        ManufacturingProcess entity = manufacturingProcessRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Process not found"));
        entity.setName(details.getName());
        entity.setEmissionPerKg(details.getEmissionPerKg());
        return manufacturingProcessRepository.save(entity);
    }

    public void deleteManufacturingProcess(Integer id) {
        manufacturingProcessRepository.deleteById(id);
    }

    /*----------- Packaging Material -----------*/
    public PackagingMaterial createPackagingMaterial(PackagingMaterial material) {
        return packagingMaterialRepository.save(material);
    }

    @Transactional(readOnly = true)
    public List<PackagingMaterial> getPackagingMaterials() {
        return packagingMaterialRepository.findAll();
    }

    public PackagingMaterial updatePackagingMaterial(Integer id, PackagingMaterial details) {
        PackagingMaterial entity = packagingMaterialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Packaging material not found"));
        entity.setName(details.getName());
        entity.setEmissionPerKg(details.getEmissionPerKg());
        return packagingMaterialRepository.save(entity);
    }

    public void deletePackagingMaterial(Integer id) {
        packagingMaterialRepository.deleteById(id);
    }

    /*----------- Transport Zone -----------*/
    public TransportZone createTransportZone(TransportZone zone) {
        return transportZoneRepository.save(zone);
    }

    @Transactional(readOnly = true)
    public TransportZone getTransportZoneById(Integer id) {
        return transportZoneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TransportZone not found with id: " + id));
    }

    @Transactional
    public TransportZone updateTransportZone(Integer id, TransportZone zoneDetails) {
        TransportZone existingZone = getTransportZoneById(id); // Re-use our finder

        existingZone.setName(zoneDetails.getName());
        existingZone.setFlatCarbonFootprint(zoneDetails.getFlatCarbonFootprint());
        existingZone.setCost(zoneDetails.getCost());

        return transportZoneRepository.save(existingZone);
    }

    @Transactional
    public void deleteTransportZone(Integer id) {
        if (!transportZoneRepository.existsById(id)) {
            throw new EntityNotFoundException("TransportZone not found with id: " + id);
        }
        transportZoneRepository.deleteById(id);
    }


    private AdminUserResponseDto mapUserToAdminResponseDto(User user) {
        return AdminUserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .sellerStatus(user.getSellerStatus())
                .storeName(user.getStoreName())
                .isAccountNonLocked(user.isAccountNonLocked())
                .isEnabled(user.isEnabled())
                .ecoPoints(user.getEcoPoints())
                .rankLevel(user.getRankLevel())
                .lifetimeAverageCarbon(user.getLifetimeAverageCarbon())
                .build();
    }
}
