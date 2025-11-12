package com.ecobazaarx.v2.service;

import com.ecobazaarx.v2.dto.*;
import com.ecobazaarx.v2.model.*;
import com.ecobazaarx.v2.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final MaterialRepository materialRepository;
    private final ManufacturingProcessRepository manufacturingProcessRepository;
    private final PackagingMaterialRepository packagingMaterialRepository;
    private final TransportZoneRepository transportZoneRepository;
    private final ProductMaterialRepository productMaterialRepository;
    private final ProductPackagingRepository productPackagingRepository;
    private final ProductManufacturingRepository productManufacturingRepository;


    @Transactional
    public ProductResponseDto createProduct(ProductCreateRequest request, UserDetails sellerDetails) {

        User seller = userRepository.findByEmail(sellerDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Seller not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        TransportZone transportZone = transportZoneRepository.findById(request.getTransportZoneId())
                .orElseThrow(() -> new EntityNotFoundException("Transport zone not found"));

        BigDecimal totalCarbon = BigDecimal.ZERO;

        BigDecimal materialsCarbon = calculateMaterialsCarbon(request.getMaterials());
        totalCarbon = totalCarbon.add(materialsCarbon);

        BigDecimal manufacturingCarbon = calculateManufacturingCarbon(request.getManufacturing());
        totalCarbon = totalCarbon.add(manufacturingCarbon);

        BigDecimal packagingCarbon = calculatePackagingCarbon(request.getPackaging());
        totalCarbon = totalCarbon.add(packagingCarbon);

        totalCarbon = totalCarbon.add(transportZone.getFlatCarbonFootprint());

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrl(request.getImageUrl());
        product.setSeller(seller);
        product.setCategory(category);
        product.setTransportZone(transportZone);
        product.setCradleToWarehouseFootprint(totalCarbon);
        product.setEcoPoints(0);

        Product savedProduct = productRepository.save(product);

        Set<ProductMaterial> productMaterials = new HashSet<>();
        for (ProductMaterialDto dto : request.getMaterials()) {
            Material material = materialRepository.findById(dto.getMaterialId())
                    .orElseThrow(() -> new EntityNotFoundException("Material not found: " + dto.getMaterialId()));
            ProductMaterial pm = new ProductMaterial();
            pm.setProduct(savedProduct);
            pm.setMaterial(material);
            pm.setWeightKg(dto.getWeightKg());
            productMaterials.add(productMaterialRepository.save(pm));
        }
        savedProduct.setMaterials(productMaterials);

        Set<ProductPackaging> productPackaging = new HashSet<>();
        for (ProductPackagingDto dto : request.getPackaging()) {
            PackagingMaterial material = packagingMaterialRepository.findById(dto.getPackagingMaterialId())
                    .orElseThrow(() -> new EntityNotFoundException("Packaging material not found: " + dto.getPackagingMaterialId()));
            ProductPackaging pp = new ProductPackaging();
            pp.setProduct(savedProduct);
            pp.setPackagingMaterial(material);
            pp.setWeightKg(dto.getWeightKg());
            productPackaging.add(productPackagingRepository.save(pp));
        }
        savedProduct.setPackaging(productPackaging);

        // Manufacturing
        Set<ProductManufacturing> productManufacturing = new HashSet<>();
        for (ProductManufacturingDto dto : request.getManufacturing()) {
            ManufacturingProcess process = manufacturingProcessRepository.findById(dto.getProcessId())
                    .orElseThrow(() -> new EntityNotFoundException("Process not found: " + dto.getProcessId()));
            ProductManufacturing pm = new ProductManufacturing();
            pm.setProduct(savedProduct);
            pm.setProcess(process);
            pm.setWeightKg(dto.getWeightKg());
            productManufacturing.add(productManufacturingRepository.save(pm));
        }
        savedProduct.setManufacturing(productManufacturing);

        productRepository.save(savedProduct);

        return mapToResponseDto(savedProduct);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(this::mapToResponseDto); // Reuse our DTO mapper
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + id));
        return mapToResponseDto(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> searchProducts(
            String query,
            Integer categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    ) {
        Specification<Product> spec = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (query != null && !query.isBlank()) {
                String likePattern = "%" + query.toLowerCase() + "%";
                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern)
                        )
                );
            }
            if (categoryId != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("category").get("id"), categoryId)
                );
            }
            if (minPrice != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice)
                );
            }
            if (maxPrice != null) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice)
                );
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        Page<Product> productPage = productRepository.findAll(spec, pageable);
        return productPage.map(this::mapToResponseDto);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProductsBySeller(UserDetails sellerDetails, Pageable pageable) {
        User seller = findUserByEmail(sellerDetails.getUsername());
        Page<Product> productPage = productRepository.findBySellerId(seller.getId(), pageable);
        return productPage.map(this::mapToResponseDto);
    }

    @Transactional
    public ProductResponseDto updateSellerProduct(Long productId, ProductCreateRequest request, UserDetails sellerDetails) {
        User seller = findUserByEmail(sellerDetails.getUsername());
        Product product = findProductById(productId);

        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new AccessDeniedException("You do not have permission to update this product.");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrl(request.getImageUrl());

        Product updatedProduct = productRepository.save(product);
        return mapToResponseDto(updatedProduct);
    }

    @Transactional
    public void deleteSellerProduct(Long productId, UserDetails sellerDetails) {
        User seller = findUserByEmail(sellerDetails.getUsername());
        Product product = findProductById(productId);
    if (!product.getSeller().getId().equals(seller.getId())) {
            throw new AccessDeniedException("You do not have permission to delete this product.");
        }

        productRepository.delete(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getAllProductsAsAdmin(Pageable pageable) {
        return getAllProducts(pageable);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getRelatedProducts(Long productId) {
        Product product = findProductById(productId);

        Pageable top5 = PageRequest.of(0, 5);

        List<Product> relatedProducts = productRepository.findByCategoryIdAndIdNot(
                product.getCategory().getId(),
                productId,
                top5
        );

        return relatedProducts.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    private BigDecimal calculateMaterialsCarbon(List<ProductMaterialDto> materials) {
        BigDecimal total = BigDecimal.ZERO;
        for (ProductMaterialDto dto : materials) {
            Material material = materialRepository.findById(dto.getMaterialId())
                    .orElseThrow(() -> new EntityNotFoundException("Material not found: " + dto.getMaterialId()));
            BigDecimal carbon = material.getEmissionPerKg().multiply(dto.getWeightKg());
            total = total.add(carbon);
        }
        return total;
    }

    private BigDecimal calculateManufacturingCarbon(List<ProductManufacturingDto> processes) {
        BigDecimal total = BigDecimal.ZERO;
        for (ProductManufacturingDto dto : processes) {
            ManufacturingProcess process = manufacturingProcessRepository.findById(dto.getProcessId())
                    .orElseThrow(() -> new EntityNotFoundException("Process not found: " + dto.getProcessId()));

            BigDecimal carbon = process.getEmissionPerKg().multiply(dto.getWeightKg());
            total = total.add(carbon);
        }
        return total;
    }

    private BigDecimal calculatePackagingCarbon(List<ProductPackagingDto> packaging) {
        BigDecimal total = BigDecimal.ZERO;
        for (ProductPackagingDto dto : packaging) {
            PackagingMaterial material = packagingMaterialRepository.findById(dto.getPackagingMaterialId())
                    .orElseThrow(() -> new EntityNotFoundException("Packaging material not found: " + dto.getPackagingMaterialId()));
            BigDecimal carbon = material.getEmissionPerKg().multiply(dto.getWeightKg());
            total = total.add(carbon);
        }
        return total;
    }

    public ProductResponseDto mapToResponseDto(Product product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .categoryName(product.getCategory().getName())
                .sellerStoreName(product.getSeller().getStoreName())
                .cradleToWarehouseFootprint(product.getCradleToWarehouseFootprint())
                .ecoPoints(product.getEcoPoints())
                .build();
    }
}