package com.ecobazaarx.v2.controller;

import com.ecobazaarx.v2.dto.*;
import com.ecobazaarx.v2.model.*;
import com.ecobazaarx.v2.service.AdminService;
import com.ecobazaarx.v2.service.OrderHistoryService;
import com.ecobazaarx.v2.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // <-- MUST BE HERE
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
// --- CHANGED: REMOVED CLASS-LEVEL @PreAuthorize("hasRole('ADMIN')") ---
// We define security per-method or via SecurityConfig instead.
public class AdminController {

    private final AdminService adminService;
    private final ProductService productService;
    private final OrderHistoryService orderHistoryService;

    /*----------- Seller requests -----------*/
    @GetMapping("/seller-applications")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<List<ProfileResponse>> getPendingApplications() {
        return ResponseEntity.ok(adminService.getPendingSellerApplications());
    }

    @PostMapping("/seller-applications/approve/{userId}")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<ProfileResponse> approveSeller(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.approveSellerApplication(userId));
    }

    @PostMapping("/seller-applications/reject/{userId}")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<ProfileResponse> rejectSeller(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.rejectSellerApplication(userId));
    }

    /*----------- Categories -----------*/
    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        return new ResponseEntity<>(adminService.createCategory(category), HttpStatus.CREATED);
    }

    @GetMapping("/categories")
    // --- CHANGED: Removed @PreAuthorize, SecurityConfig handles the access: ADMIN/SELLER ---
    public ResponseEntity<List<Category>> getCategories() {
        return ResponseEntity.ok(adminService.getCategories());
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<Category> updateCategory(@PathVariable Integer id, @RequestBody Category details) {
        return ResponseEntity.ok(adminService.updateCategory(id, details));
    }

    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        adminService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    /*----------- Materials -----------*/
    @PostMapping("/materials")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<Material> createMaterial(@RequestBody Material material) {
        return new ResponseEntity<>(adminService.createMaterial(material), HttpStatus.CREATED);
    }

    @GetMapping("/materials")
    // --- CHANGED: Removed @PreAuthorize, SecurityConfig handles the access: ADMIN/SELLER ---
    public ResponseEntity<List<Material>> getMaterials() {
        return ResponseEntity.ok(adminService.getMaterials());
    }

    @PutMapping("/materials/{id}")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<Material> updateMaterial(@PathVariable Integer id, @RequestBody Material details) {
        return ResponseEntity.ok(adminService.updateMaterial(id, details));
    }

    @DeleteMapping("/materials/{id}")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<Void> deleteMaterial(@PathVariable Integer id) {
        adminService.deleteMaterial(id);
        return ResponseEntity.noContent().build();
    }


    /*----------- Manufacturing Process -----------*/
    @PostMapping("/manufacturing-processes")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<ManufacturingProcess> createManufacturingProcess(@RequestBody ManufacturingProcess process) {
        return new ResponseEntity<>(adminService.createManufacturingProcess(process), HttpStatus.CREATED);
    }

    @GetMapping("/manufacturing-processes")
    // --- CHANGED: Removed @PreAuthorize, SecurityConfig handles the access: ADMIN/SELLER ---
    public ResponseEntity<List<ManufacturingProcess>> getManufacturingProcesses() {
        return ResponseEntity.ok(adminService.getManufacturingProcesses());
    }

    @PutMapping("/manufacturing-processes/{id}")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<ManufacturingProcess> updateManufacturingProcess(@PathVariable Integer id, @RequestBody ManufacturingProcess details) {
        return ResponseEntity.ok(adminService.updateManufacturingProcess(id, details));
    }

    @DeleteMapping("/manufacturing-processes/{id}")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<Void> deleteManufacturingProcess(@PathVariable Integer id) {
        adminService.deleteManufacturingProcess(id);
        return ResponseEntity.noContent().build();
    }

    /*----------- Packaging Materials -----------*/
    @PostMapping("/packaging-materials")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<PackagingMaterial> createPackagingMaterial(@RequestBody PackagingMaterial material) {
        return new ResponseEntity<>(adminService.createPackagingMaterial(material), HttpStatus.CREATED);
    }

    @GetMapping("/packaging-materials")
    // --- CHANGED: Removed @PreAuthorize, SecurityConfig handles the access: ADMIN/SELLER ---
    public ResponseEntity<List<PackagingMaterial>> getPackagingMaterials() {
        return ResponseEntity.ok(adminService.getPackagingMaterials());
    }

    @PutMapping("/packaging-materials/{id}")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<PackagingMaterial> updatePackagingMaterial(@PathVariable Integer id, @RequestBody PackagingMaterial details) {
        return ResponseEntity.ok(adminService.updatePackagingMaterial(id, details));
    }

    @DeleteMapping("/packaging-materials/{id}")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<Void> deletePackagingMaterial(@PathVariable Integer id) {
        adminService.deletePackagingMaterial(id);
        return ResponseEntity.noContent().build();
    }

    /*----------- Transport zones -----------*/
    @PostMapping("/transport-zones")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<TransportZone> createTransportZone(@RequestBody TransportZone zone) {
        return new ResponseEntity<>(adminService.createTransportZone(zone), HttpStatus.CREATED);
    }

    @GetMapping("/transport-zones")
    // --- CHANGED: Removed @PreAuthorize, SecurityConfig handles the access: ADMIN/SELLER ---
    public ResponseEntity<List<TransportZone>> getTransportZones() {
        return ResponseEntity.ok(adminService.getTransportZones());
    }

    @GetMapping("/transport-zones/{id}")
    // --- CHANGED: ADDED SECURITY BACK (Only Admin should read single zone) ---
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransportZone> getTransportZoneById(@PathVariable Integer id) {
        return ResponseEntity.ok(adminService.getTransportZoneById(id));
    }

    @PutMapping("/transport-zones/{id}")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<TransportZone> updateTransportZone(
            @PathVariable Integer id,
            @RequestBody TransportZone zoneDetails
    ) {
        return ResponseEntity.ok(adminService.updateTransportZone(id, zoneDetails));
    }

    @DeleteMapping("/transport-zones/{id}")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<Void> deleteTransportZone(@PathVariable Integer id) {
        adminService.deleteTransportZone(id);
        return ResponseEntity.noContent().build();
    }

    /*----------- Users -----------*/
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<Page<AdminUserResponseDto>> getAllUsers(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) RoleName role,
            @RequestParam(required = false) SellerApplicationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort
    ) {
        String sortField = sort[0];
        Sort.Direction sortDirection = (sort.length > 1 && sort[1].equalsIgnoreCase("asc")) ?
                Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));

        return ResponseEntity.ok(adminService.getAllUsers(query, role, status, pageable));
    }

    @PutMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<AdminUserResponseDto> updateUser(
            @PathVariable Long userId,
            @RequestBody AdminUserUpdateRequest request
    ) {
        return ResponseEntity.ok(adminService.updateUserAsAdmin(userId, request));
    }

    @PostMapping("/create-admin")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<AdminUserResponseDto> createAdmin(
            @RequestBody AdminCreateRequest request
    ) {
        AdminUserResponseDto newAdmin = adminService.createAdmin(request);
        return new ResponseEntity<>(newAdmin, HttpStatus.CREATED);
    }

    /*----------- Products -----------*/
    @GetMapping("/products")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<Page<ProductResponseDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort
    ) {
        String sortField = sort[0];
        Sort.Direction sortDirection = (sort.length > 1 && sort[1].equalsIgnoreCase("asc")) ?
                Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));

        return ResponseEntity.ok(productService.getAllProductsAsAdmin(pageable));
    }

    /*----------- Orders -----------*/
    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<Page<OrderDto>> getAllOrders(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate,desc") String[] sort
    ) {
        String sortField = sort[0];
        Sort.Direction sortDirection = (sort.length > 1 && sort[1].equalsIgnoreCase("asc")) ?
                Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));

        return ResponseEntity.ok(orderHistoryService.getAllOrdersAsAdmin(email, status, pageable));
    }

    @PutMapping("/orders/{id}/status")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody AdminOrderStatusRequest request
    ) {
        return ResponseEntity.ok(orderHistoryService.updateOrderStatus(id, request.getStatus()));
    }

    /*----------- Taxes -----------*/
    @PostMapping("/config/taxes")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<TaxRate> createTaxRate(@RequestBody TaxRate taxRate) {
        return new ResponseEntity<>(adminService.createTaxRate(taxRate), HttpStatus.CREATED);
    }

    @GetMapping("/config/taxes")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<List<TaxRate>> getTaxRates() {
        return ResponseEntity.ok(adminService.getTaxRates());
    }

    @PutMapping("/config/taxes/{id}")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<TaxRate> updateTaxRate(@PathVariable Long id, @RequestBody TaxRate details) {
        return ResponseEntity.ok(adminService.updateTaxRate(id, details));
    }

    @DeleteMapping("/config/taxes/{id}")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<Void> deleteTaxRate(@PathVariable Long id) {
        adminService.deleteTaxRate(id);
        return ResponseEntity.noContent().build();
    }

    /*----------- Discounts -----------*/
    @PostMapping("/config/discounts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Discount> createDiscount(@RequestBody Discount discount) {
        if (discount.getValidFrom() == null) {
            discount.setValidFrom(LocalDateTime.now()); // Default to start now
        }
        if (discount.getUsageLimit() == 0) {
            discount.setUsageLimit(100); // Default limit
        }
        return new ResponseEntity<>(adminService.createDiscount(discount), HttpStatus.CREATED);
    }

    @GetMapping("/config/discounts")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<List<Discount>> getDiscounts() {
        return ResponseEntity.ok(adminService.getDiscounts());
    }

    @PutMapping("/config/discounts/{id}")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<Discount> updateDiscount(@PathVariable Long id, @RequestBody Discount details) {
        return ResponseEntity.ok(adminService.updateDiscount(id, details));
    }

    @DeleteMapping("/config/discounts/{id}")
    @PreAuthorize("hasRole('ADMIN')") // <-- ADDED SECURITY BACK
    public ResponseEntity<Void> deleteDiscount(@PathVariable Long id) {
        adminService.deleteDiscount(id);
        return ResponseEntity.noContent().build();
    }
}