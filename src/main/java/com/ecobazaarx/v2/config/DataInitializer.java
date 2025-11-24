package com.ecobazaarx.v2.config;

// 1. IMPORT ALL MODELS
import com.ecobazaarx.v2.model.*;
import com.ecobazaarx.v2.model.DiscountType;
import com.ecobazaarx.v2.model.RoleName;

// 2. IMPORT ALL REPOSITORIES
import com.ecobazaarx.v2.repository.*;

// 3. IMPORT DTOs & SERVICES WE NEED
import com.ecobazaarx.v2.dto.ProductCreateRequest;
import com.ecobazaarx.v2.dto.ProductManufacturingDto;
import com.ecobazaarx.v2.dto.ProductMaterialDto;
import com.ecobazaarx.v2.dto.ProductPackagingDto;
import com.ecobazaarx.v2.service.ProductService;

// 4. STANDARD IMPORTS
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    // --- 5. INJECT ALL REPOSITORIES & SERVICES ---
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final MaterialRepository materialRepository;
    private final ManufacturingProcessRepository manufacturingProcessRepository;
    private final PackagingMaterialRepository packagingMaterialRepository;
    private final TransportZoneRepository transportZoneRepository;
    private final TaxRateRepository taxRateRepository;
    private final DiscountRepository discountRepository;
    private final ProductRepository productRepository;
    private final ProductService productService; // We need this to create products

    // --- 6. CLASS FIELDS TO STORE SEEDED ENTITIES ---
    // This lets us pass entities from one seeder method to another

    // Roles
    private Role role_customer;
    private Role role_seller;
    private Role role_admin;

    // Transport Zones
    private TransportZone zone_intra_city;
    private TransportZone zone_intra_state;
    private TransportZone zone_inter_state;

    // Categories
    private Category cat_apparel;
    private Category cat_electronics;
    private Category cat_kitchenware;
    private Category cat_personal_care;
    private Category cat_home_appliances;
    private Category cat_accessories;
    private Category cat_groceries;
    private Category cat_decorative;
    private Category cat_stationary;
    private Category cat_eco_friendly;

    // Materials
    private Material mat_cotton;
    private Material mat_polyester_thread;
    private Material mat_polymers;
    private Material mat_battery;
    private Material mat_recycled_aluminum;
    private Material mat_glass;
    private Material mat_silicone;
    private Material mat_bamboo;
    private Material mat_steel;


    // Manufacturing
    private ManufacturingProcess mfg_spinning;
    private ManufacturingProcess mfg_dyeing;
    private ManufacturingProcess mfg_injection_molding;
    private ManufacturingProcess mfg_electronics_assembly;
    private ManufacturingProcess mfg_glass_forming;
    private ManufacturingProcess mfg_cut_sew;

    // Packaging
    private PackagingMaterial pkg_cardboard_box;
    private PackagingMaterial pkg_polybag;
    private PackagingMaterial pkg_paper_wrap;


    // Users
    private List<User> sellers = new ArrayList<>();
    private List<User> customers = new ArrayList<>();

    @Override
    public void run(String... args) throws Exception {
        // Check if the database is empty (no users)
        if (userRepository.count() == 0) {
            System.out.println("--- [ecobazaarx] Seeding Database ---");

            // Run seeders in order
            seedRoles();
            seedAdminConfig();
            seedCarbonFactors();
            seedUsers();
            seedProducts();

            System.out.println("--- [ecobazaarx] Database Seeding Complete ---");
        } else {
            System.out.println("[ecobazaarx] Database already populated. Skipping seeder.");
        }
    }

    // --- 7. Implement seedRoles ---
    private void seedRoles() {
        System.out.println("Seeding Roles...");

        role_customer = roleRepository.save(new Role(RoleName.ROLE_CUSTOMER));
        role_seller = roleRepository.save(new Role(RoleName.ROLE_SELLER));
        role_admin = roleRepository.save(new Role(RoleName.ROLE_ADMIN));
    }

    // --- 8. Implement seedAdminConfig ---
    private void seedAdminConfig() {
        System.out.println("Seeding Admin Config (Tax, Discount, Shipping)...");

        // a. Seed Tax Rates
        TaxRate gst = new TaxRate();
        gst.setName("GST");
        gst.setRate(new BigDecimal("18.00")); // 18.00%
        gst.setCountry("IN");
        taxRateRepository.save(gst);

        // b. Seed Discount Codes
        Discount welcome = new Discount();
        welcome.setCode("ECO10");
        welcome.setDiscountType(DiscountType.PERCENTAGE);
        welcome.setValue(new BigDecimal("10.00")); // 10%
        welcome.setMinPurchaseAmount(new BigDecimal("499.00")); // Min spend ₹499
        welcome.setValidFrom(LocalDateTime.now());
        welcome.setValidUntil(LocalDateTime.now().plusMonths(6));
        welcome.setUsageLimit(1000);
        discountRepository.save(welcome);

        // c. Seed Transport Zones (Realistic Indian E-commerce Rates)

        zone_intra_city = new TransportZone();
        zone_intra_city.setName("Intra-city");
        zone_intra_city.setFlatCarbonFootprint(new BigDecimal("0.20")); // 0.2 kg CO2e
        zone_intra_city.setCost(new BigDecimal("40.00")); // ₹40
        transportZoneRepository.save(zone_intra_city);

        zone_intra_state = new TransportZone();
        zone_intra_state.setName("Intra-state");
        zone_intra_state.setFlatCarbonFootprint(new BigDecimal("0.60")); // 0.6 kg CO2e
        zone_intra_state.setCost(new BigDecimal("70.00")); // ₹70
        transportZoneRepository.save(zone_intra_state);

        zone_inter_state = new TransportZone();
        zone_inter_state.setName("Inter-state");
        zone_inter_state.setFlatCarbonFootprint(new BigDecimal("1.20")); // 1.2 kg CO2e
        zone_inter_state.setCost(new BigDecimal("110.00")); // ₹110
        transportZoneRepository.save(zone_inter_state);
    }

    private void seedCarbonFactors() {
        System.out.println("Seeding Carbon Factors (Categories, Materials, etc.)...");

        // 1. Seed Categories (9)
        cat_apparel = categoryRepository.save(new Category("Apparel", "Clothing and textiles"));
        cat_electronics = categoryRepository.save(new Category("Electronics", "Gadgets and accessories"));
        cat_kitchenware = categoryRepository.save(new Category("Kitchenware", "Reusable cups, containers, etc."));
        cat_personal_care = categoryRepository.save(new Category("Personal Care", "Soap, toothbrushes, etc."));
        cat_home_appliances = categoryRepository.save(new Category("Home Appliances", "Small eco-friendly appliances"));
        cat_accessories = categoryRepository.save(new Category("Accessories", "Bags, wallets, etc."));
        cat_groceries = categoryRepository.save(new Category("Groceries", "Eco-friendly food items"));
        cat_decorative = categoryRepository.save(new Category("Decorative Items", "Sustainable home decor"));
        cat_stationary = categoryRepository.save(new Category("Stationary", "Recycled paper and office supplies"));
        cat_eco_friendly = categoryRepository.save(new Category("Eco-Friendly", "Unique, smart, and nature-first items"));

        // 2. Seed Materials (with UPDATED factors)
        mat_cotton = materialRepository.save(new Material("Raw Cotton", new BigDecimal("5.92"), "LCA Study"));
        mat_polyester_thread = materialRepository.save(new Material("Polyester Thread", new BigDecimal("5.5"), "Industry Avg."));
        mat_polymers = materialRepository.save(new Material("Polymers (ABS/PC)", new BigDecimal("10.0"), "LCA Study"));
        mat_battery = materialRepository.save(new Material("Li-ion Battery", new BigDecimal("400.0"), "Hotspot V2")); // <-- UPDATED (was 300)
        mat_recycled_aluminum = materialRepository.save(new Material("Recycled Aluminum", new BigDecimal("20.0"), "Industry Avg. V2")); // <-- UPDATED (was 8.0)
        mat_glass = materialRepository.save(new Material("Glass", new BigDecimal("1.5"), "Industry Avg."));
        mat_silicone = materialRepository.save(new Material("Silicone", new BigDecimal("6.0"), "Industry Avg."));
        mat_bamboo = materialRepository.save(new Material("Bamboo", new BigDecimal("1.2"), "Industry Avg."));
        mat_steel = materialRepository.save(new Material("Stainless Steel", new BigDecimal("7.0"), "Industry Avg."));
        materialRepository.save(new Material("Metals (Al/Steel)", new BigDecimal("25.0"), "Industry Avg."));
        materialRepository.save(new Material("Foam (PU)", new BigDecimal("20.0"), "Industry Avg."));
        materialRepository.save(new Material("Cables (Copper/PVC)", new BigDecimal("40.0"), "Industry Avg."));

        // 3. Seed Manufacturing Processes (UPDATED factors)
        mfg_spinning = manufacturingProcessRepository.save(new ManufacturingProcess("Yarn Spinning", new BigDecimal("3.0")));
        mfg_dyeing = manufacturingProcessRepository.save(new ManufacturingProcess("Dyeing/Finishing", new BigDecimal("4.0")));
        mfg_injection_molding = manufacturingProcessRepository.save(new ManufacturingProcess("Injection Molding", new BigDecimal("2.5")));
        mfg_electronics_assembly = manufacturingProcessRepository.save(new ManufacturingProcess("Electronics Assembly", new BigDecimal("20.0"))); // <-- UPDATED (was 10.0)
        mfg_glass_forming = manufacturingProcessRepository.save(new ManufacturingProcess("Glass Forming", new BigDecimal("2.0")));
        mfg_cut_sew = manufacturingProcessRepository.save(new ManufacturingProcess("Cut & Sew", new BigDecimal("1.0")));
        manufacturingProcessRepository.save(new ManufacturingProcess("Silicone Molding", new BigDecimal("2.0")));
        manufacturingProcessRepository.save(new ManufacturingProcess("Fabric Knitting/Weaving", new BigDecimal("2.5")));

        // 4. Seed Packaging Materials (Unchanged)
        pkg_cardboard_box = packagingMaterialRepository.save(new PackagingMaterial("Cardboard Box", new BigDecimal("0.8")));
        pkg_polybag = packagingMaterialRepository.save(new PackagingMaterial("Polybag (LDPE)", new BigDecimal("2.0")));
        pkg_paper_wrap = packagingMaterialRepository.save(new PackagingMaterial("Paper Wrap", new BigDecimal("0.7")));
        packagingMaterialRepository.save(new PackagingMaterial("Molded Paper Pulp", new BigDecimal("0.8")));
    }

    private void seedUsers() {
        System.out.println("Seeding 31 Users (1 Admin, 10 Sellers, 20 Customers)...");
        // 1. Seed Admin (1)
        User admin1 = new User(
                "Admin 1",
                "admin1@amail.com",
                passwordEncoder.encode("password123!")
        );
        admin1.setRoles(Set.of(role_admin, role_customer)); // Admin is also a customer
        admin1.setSellerStatus(SellerApplicationStatus.NOT_APPLICABLE);
        admin1.setCart(new Cart(admin1)); // Admins can shop
        userRepository.save(admin1);

        User admin2 = new User(
                "Admin 2",
                "admin2@amail.com",
                passwordEncoder.encode("password123!")
        );
        admin2.setRoles(Set.of(role_admin)); // Admin is also a customer
        admin2.setSellerStatus(SellerApplicationStatus.NOT_APPLICABLE);
//        admin2.setCart(new Cart(admin1)); // Admins can shop
        userRepository.save(admin2);


        // 2. Seed Sellers (10)
        for (int i = 1; i <= 10; i++) {
            User seller = new User(
                    "Seller " + i,
                    "seller" + i + "@amail.com",
                    passwordEncoder.encode("password123!")
            );
            seller.setRoles(Set.of(role_seller, role_customer)); // Sellers are also customers
            seller.setSellerStatus(SellerApplicationStatus.APPROVED); // Auto-approved
            seller.setStoreName("Eco Store " + i);
            seller.setStoreDescription("The best eco-friendly store #" + i);
            seller.setCart(new Cart(seller)); // Sellers can also shop

            // Add the saved seller to our class-level list
            sellers.add(userRepository.save(seller));
        }

        // 3. Seed Customers (20)
        for (int i = 1; i <= 15; i++) {
            User customer = new User(
                    "Customer " + i,
                    "customer" + i + "@amail.com",
                    passwordEncoder.encode("password123!")
            );
            customer.setRoles(Set.of(role_customer));
            customer.setCart(new Cart(customer)); // Create an empty cart for each

            userRepository.save(customer);
        }
    }
    private void seedProducts() {
        System.out.println("Seeding Products (Chunk 1: Apparel)...");

        // --- 1. Lymio Bomber Jacket ---
        ProductCreateRequest jacket = new ProductCreateRequest();
        jacket.setName("Bomber Jacket)");
        jacket.setDescription("Men's Lightweight Outwear Sportswear Bomber Jacket. Made from durable polyester. Note: Inner t-shirt not included.");
        jacket.setPrice(new BigDecimal("749.00"));
        jacket.setStockQuantity(120);
        jacket.setImageUrl("https://m.media-amazon.com/images/I/619xMvtqClL._SY741_.jpg");
        jacket.setCategoryId(cat_apparel.getId());
        jacket.setTransportZoneId(zone_inter_state.getId());
        jacket.setMaterials(List.of(
                new ProductMaterialDto(mat_polymers.getId(), new BigDecimal("0.5")) // 500g Polyester
        ));
        jacket.setManufacturing(List.of(
                new ProductManufacturingDto(mfg_cut_sew.getId(), new BigDecimal("0.5")),
                new ProductManufacturingDto(mfg_dyeing.getId(), new BigDecimal("0.5"))
        ));
        jacket.setPackaging(List.of(
                new ProductPackagingDto(pkg_polybag.getId(), new BigDecimal("0.01")),
                new ProductPackagingDto(pkg_cardboard_box.getId(), new BigDecimal("0.05"))
        ));
        productService.createProductInternal(jacket, sellers.get(0)); // Assign to Seller 1

        // --- 2. Lymio Cargo Pants ---
        ProductCreateRequest cargo = new ProductCreateRequest();
        cargo.setName("Men's Cotton Cargo Pants");
        cargo.setDescription("Men's Regular Fit Low Rise 100% Cotton Cargo Pants with drawstring waist.");
        cargo.setPrice(new BigDecimal("699.00"));
        cargo.setStockQuantity(150);
        cargo.setImageUrl("https://m.media-amazon.com/images/I/61j9jJhrStL._SY879_.jpg");
        cargo.setCategoryId(cat_apparel.getId());
        cargo.setTransportZoneId(zone_inter_state.getId());
        cargo.setMaterials(List.of(
                new ProductMaterialDto(mat_cotton.getId(), new BigDecimal("0.499")),
                new ProductMaterialDto(mat_polyester_thread.getId(), new BigDecimal("0.001"))
        ));
        cargo.setManufacturing(List.of(
                new ProductManufacturingDto(mfg_spinning.getId(), new BigDecimal("0.5")),
                new ProductManufacturingDto(mfg_dyeing.getId(), new BigDecimal("0.5")),
                new ProductManufacturingDto(mfg_cut_sew.getId(), new BigDecimal("0.5"))
        ));
        cargo.setPackaging(List.of(
                new ProductPackagingDto(pkg_polybag.getId(), new BigDecimal("0.01"))
        ));
        productService.createProductInternal(cargo, sellers.get(0)); // Assign to Seller 1

        // --- 3. DEELMO Short Kurta ---
        ProductCreateRequest kurta = new ProductCreateRequest();
        kurta.setName("Men's Cotton Blend Short Kurta");
        kurta.setDescription("Breathable and Comfortable Cotton Blend. Stylish one-side Solid/printed pattern. Versatile full sleeves.");
        kurta.setPrice(new BigDecimal("499.00"));
        kurta.setStockQuantity(200);
        kurta.setImageUrl("https://m.media-amazon.com/images/I/61P0jlvu1OL._SX679_.jpg");
        kurta.setCategoryId(cat_apparel.getId());
        kurta.setTransportZoneId(zone_inter_state.getId());
        kurta.setMaterials(List.of(
                new ProductMaterialDto(mat_cotton.getId(), new BigDecimal("0.192")), // 80%
                new ProductMaterialDto(mat_polyester_thread.getId(), new BigDecimal("0.048")) // 20%
        ));
        kurta.setManufacturing(List.of(
                new ProductManufacturingDto(mfg_spinning.getId(), new BigDecimal("0.24")),
                new ProductManufacturingDto(mfg_dyeing.getId(), new BigDecimal("0.24")),
                new ProductManufacturingDto(mfg_cut_sew.getId(), new BigDecimal("0.24"))
        ));
        kurta.setPackaging(List.of(
                new ProductPackagingDto(pkg_polybag.getId(), new BigDecimal("0.01"))
        ));
        productService.createProductInternal(kurta, sellers.get(1)); // Assign to Seller 2
        // --- 4. Bsisme Drone (E88) ---
        ProductCreateRequest drone = new ProductCreateRequest();
        drone.setName("Drone 4k Camera Foldable (E88)");
        drone.setDescription("Foldable 1080P HD Drone with FPV Live Video, Gestures Selfie, Altitude Hold, One Key Take Off/Landing, 3D Flips, Headless Mode.");
        drone.setPrice(new BigDecimal("2999.00"));
        drone.setStockQuantity(80);
        drone.setImageUrl("https://m.media-amazon.com/images/I/61chgC7yvlL._SX522_.jpg");
        drone.setCategoryId(cat_electronics.getId());
        drone.setTransportZoneId(zone_intra_state.getId());
        drone.setMaterials(List.of(
                new ProductMaterialDto(mat_polymers.getId(), new BigDecimal("0.149")),
                new ProductMaterialDto(mat_battery.getId(), new BigDecimal("0.025")),
                new ProductMaterialDto(mat_steel.getId(), new BigDecimal("0.038"))
        ));
        drone.setManufacturing(List.of(
                new ProductManufacturingDto(mfg_injection_molding.getId(), new BigDecimal("0.149")),
                new ProductManufacturingDto(mfg_electronics_assembly.getId(), new BigDecimal("0.100"))
        ));
        drone.setPackaging(List.of(
                new ProductPackagingDto(pkg_polybag.getId(), new BigDecimal("0.005")),
                new ProductPackagingDto(pkg_cardboard_box.getId(), new BigDecimal("0.05"))
        ));
        productService.createProductInternal(drone, sellers.get(2)); // Assign to Seller 3

        // --- 5. Apple MacBook Air (M4, 2025) ---
        ProductCreateRequest macbook = new ProductCreateRequest();
        macbook.setName("Apple 2025 MacBook Air (13-inch, M4 chip)");
        macbook.setDescription("MacBook Air with the M4 chip. 16GB Unified Memory, 256GB SSD. Sky Blue. Built for Apple Intelligence.");
        macbook.setPrice(new BigDecimal("119900.00"));
        macbook.setStockQuantity(30);
        macbook.setImageUrl("http://m.media-amazon.com/images/I/71pKJ+Mjd8L._SX679_.jpg");
        macbook.setCategoryId(cat_electronics.getId());
        macbook.setTransportZoneId(zone_inter_state.getId());
        macbook.setMaterials(List.of(
                new ProductMaterialDto(mat_recycled_aluminum.getId(), new BigDecimal("0.496")),
                new ProductMaterialDto(mat_glass.getId(), new BigDecimal("0.186")),
                new ProductMaterialDto(mat_battery.getId(), new BigDecimal("0.186")),
                new ProductMaterialDto(mat_polymers.getId(), new BigDecimal("0.372"))
        ));
        macbook.setManufacturing(List.of(
                new ProductManufacturingDto(mfg_electronics_assembly.getId(), new BigDecimal("1.054"))
        ));
        macbook.setPackaging(List.of(
                new ProductPackagingDto(pkg_cardboard_box.getId(), new BigDecimal("0.3")),
                new ProductPackagingDto(pkg_paper_wrap.getId(), new BigDecimal("0.1"))
        ));
        productService.createProductInternal(macbook, sellers.get(1)); // Assign to Seller 2

        // --- 6. Samsung Galaxy S24 Ultra ---
        ProductCreateRequest s24 = new ProductCreateRequest();
        s24.setName("Samsung Galaxy S24 Ultra 5G AI Smartphone");
        s24.setDescription("Titanium Gray, 12GB, 256GB Storage, Snapdragon 8 Gen 3, 200 MP Camera with ProVisual Engine.");
        s24.setPrice(new BigDecimal("129999.00"));
        s24.setStockQuantity(40);
        s24.setImageUrl("https://m.media-amazon.com/images/I/717Q2swzhBL._SX679_.jpg");
        s24.setCategoryId(cat_electronics.getId());
        s24.setTransportZoneId(zone_inter_state.getId());
        s24.setMaterials(List.of(
                new ProductMaterialDto(mat_recycled_aluminum.getId(), new BigDecimal("0.07")),
                new ProductMaterialDto(mat_glass.getId(), new BigDecimal("0.046")),
                new ProductMaterialDto(mat_battery.getId(), new BigDecimal("0.046")),
                new ProductMaterialDto(mat_polymers.getId(), new BigDecimal("0.07"))
        ));
        s24.setManufacturing(List.of(
                new ProductManufacturingDto(mfg_electronics_assembly.getId(), new BigDecimal("0.232"))
        ));
        s24.setPackaging(List.of(
                new ProductPackagingDto(pkg_cardboard_box.getId(), new BigDecimal("0.05")),
                new ProductPackagingDto(pkg_paper_wrap.getId(), new BigDecimal("0.01"))
        ));
        productService.createProductInternal(s24, sellers.get(2));

        // 7. Vinod Cookware Set (25-Piece)
        ProductCreateRequest cookware = new ProductCreateRequest();
        cookware.setName("Stainless Steel Cookware Combo Set (25-Piece)");
        cookware.setDescription("Induction & Gas Stove Friendly. Dishwasher Safe, Stackable, Durable. Essential for Indian Cooking.");
        cookware.setPrice(new BigDecimal("5999.00"));
        cookware.setStockQuantity(50);
        cookware.setImageUrl("https://m.media-amazon.com/images/I/71arlI-WZJL._SL1500_.jpg");
        cookware.setCategoryId(cat_kitchenware.getId());
        cookware.setTransportZoneId(zone_inter_state.getId());
        cookware.setMaterials(List.of(
                new ProductMaterialDto(mat_steel.getId(), new BigDecimal("15.0")),
                new ProductMaterialDto(mat_polymers.getId(), new BigDecimal("0.5")) // Handles
        ));
        cookware.setManufacturing(List.of(
                new ProductManufacturingDto(mfg_injection_molding.getId(), new BigDecimal("15.5")) // Metal forming
        ));
        cookware.setPackaging(List.of(
                new ProductPackagingDto(pkg_cardboard_box.getId(), new BigDecimal("1.0"))
        ));
        productService.createProductInternal(cookware, sellers.get(3)); // Assign to Seller 4

        // 8. Fridge Storage Boxes (6-Pack)
        ProductCreateRequest fridgeBoxes = new ProductCreateRequest();
        fridgeBoxes.setName("Fridge Storage Boxes (6-Pack, 1200ml)");
        fridgeBoxes.setDescription("Freezer & Refrigerator Organizer Containers. Durable, See Through, Stackable. Plastic.");
        fridgeBoxes.setPrice(new BigDecimal("349.00"));
        fridgeBoxes.setStockQuantity(300);
        fridgeBoxes.setImageUrl("https://m.media-amazon.com/images/I/71ouJpAkamL._SL1500_.jpg");
        fridgeBoxes.setCategoryId(cat_kitchenware.getId());
        fridgeBoxes.setTransportZoneId(zone_intra_state.getId());
        fridgeBoxes.setMaterials(List.of(
                new ProductMaterialDto(mat_polymers.getId(), new BigDecimal("0.25")) // 250g plastic
        ));
        fridgeBoxes.setManufacturing(List.of(
                new ProductManufacturingDto(mfg_injection_molding.getId(), new BigDecimal("0.25"))
        ));
        fridgeBoxes.setPackaging(List.of(
                new ProductPackagingDto(pkg_cardboard_box.getId(), new BigDecimal("0.1"))
        ));
        productService.createProductInternal(fridgeBoxes, sellers.get(4)); // Assign to Seller 5

        // 9. 3-Burner Gas Stove
        ProductCreateRequest gasStove = new ProductCreateRequest();
        gasStove.setName("3-Burner Auto Ignition Gas Stove");
        gasStove.setDescription("ISI Certified. Mild Steel body. Black. Auto Ignition.");
        gasStove.setPrice(new BigDecimal("3290.00"));
        gasStove.setStockQuantity(70);
        gasStove.setImageUrl("https://m.media-amazon.com/images/I/61OefFiMvuL._SL1500_.jpg");
        gasStove.setCategoryId(cat_home_appliances.getId()); // Fits better in Home Appliances
        gasStove.setTransportZoneId(zone_inter_state.getId());
        gasStove.setMaterials(List.of(
                new ProductMaterialDto(mat_steel.getId(), new BigDecimal("10.0")),
                new ProductMaterialDto(mat_glass.getId(), new BigDecimal("1.6")) // Glass top
        ));
        gasStove.setManufacturing(List.of(
                new ProductManufacturingDto(mfg_injection_molding.getId(), new BigDecimal("10.0")), // Metal stamping
                new ProductManufacturingDto(mfg_glass_forming.getId(), new BigDecimal("1.6"))
        ));
        gasStove.setPackaging(List.of(
                new ProductPackagingDto(pkg_cardboard_box.getId(), new BigDecimal("1.0"))
        ));
        productService.createProductInternal(gasStove, sellers.get(5));

        // 10. Denver Perfume (Pack of 2)
        ProductCreateRequest perfume = new ProductCreateRequest();
        perfume.setName("Denver Caliber & Honour Perfume For Men (100ml, Pack of 2)");
        perfume.setDescription("Long Lasting Perfume. Eau de Parfum. AROMA DE FRANCE.");
        perfume.setPrice(new BigDecimal("511.00"));
        perfume.setStockQuantity(150);
        perfume.setImageUrl("https://m.media-amazon.com/images/I/61LZXs32AlL._SL1500_.jpg");
        perfume.setCategoryId(cat_personal_care.getId());
        perfume.setTransportZoneId(zone_intra_state.getId()); // Assumed from mfg
        perfume.setMaterials(List.of(
                new ProductMaterialDto(mat_glass.getId(), new BigDecimal("0.2")), // 2x 100g bottles
                new ProductMaterialDto(mat_polymers.getId(), new BigDecimal("0.1")) // 2x 50g liquid/cap
        ));
        perfume.setManufacturing(List.of(
                new ProductManufacturingDto(mfg_glass_forming.getId(), new BigDecimal("0.2"))
        ));
        perfume.setPackaging(List.of(
                new ProductPackagingDto(pkg_cardboard_box.getId(), new BigDecimal("0.1")) // 400g total - 300g product
        ));
        productService.createProductInternal(perfume, sellers.get(6)); // Assign to Seller 7

        // 11. AGARO Electric Toothbrush
        ProductCreateRequest eToothbrush = new ProductCreateRequest();
        eToothbrush.setName("AGARO COSMIC MAX Sonic Electric Toothbrush");
        eToothbrush.setDescription("5 modes, 7 brush heads, 1 Interdental head. 4 hours charge lasting up to 25 days.");
        eToothbrush.setPrice(new BigDecimal("1499.00"));
        eToothbrush.setStockQuantity(100);
        eToothbrush.setImageUrl("https://m.media-amazon.com/images/I/71K54H3WtvL._SL1500_.jpg");
        eToothbrush.setCategoryId(cat_personal_care.getId());
        eToothbrush.setTransportZoneId(zone_inter_state.getId()); // Imported from China
        eToothbrush.setMaterials(List.of(
                new ProductMaterialDto(mat_polymers.getId(), new BigDecimal("0.060")),
                new ProductMaterialDto(mat_battery.getId(), new BigDecimal("0.015")) // 15g battery/motor
        ));
        eToothbrush.setManufacturing(List.of(
                new ProductManufacturingDto(mfg_injection_molding.getId(), new BigDecimal("0.060")),
                new ProductManufacturingDto(mfg_electronics_assembly.getId(), new BigDecimal("0.015"))
        ));
        eToothbrush.setPackaging(List.of(
                new ProductPackagingDto(pkg_cardboard_box.getId(), new BigDecimal("0.05")),
                new ProductPackagingDto(pkg_polybag.getId(), new BigDecimal("0.02")) // Proxy for plastic tray
        ));
        productService.createProductInternal(eToothbrush, sellers.get(7)); // Assign to Seller 8

        // 12. Boldfit Posture Corrector
        ProductCreateRequest posture = new ProductCreateRequest();
        posture.setName("Boldfit Posture Corrector for Back Pain");
        posture.setDescription("Adjustable back support for men and women. Made from durable Neoprene.");
        posture.setPrice(new BigDecimal("399.00"));
        posture.setStockQuantity(200);
        posture.setImageUrl("https://m.media-amazon.com/images/I/71uXdA3k7WL._SL1500_.jpg");
        posture.setCategoryId(cat_personal_care.getId());
        posture.setTransportZoneId(zone_inter_state.getId()); // Bangalore
        posture.setMaterials(List.of(
                new ProductMaterialDto(mat_polymers.getId(), new BigDecimal("0.3")) // 300g Neoprene
        ));
        posture.setManufacturing(List.of(
                new ProductManufacturingDto(mfg_injection_molding.getId(), new BigDecimal("0.3")), // Molding
                new ProductManufacturingDto(mfg_cut_sew.getId(), new BigDecimal("0.3")) // Sewing
        ));
        posture.setPackaging(List.of(
                new ProductPackagingDto(pkg_cardboard_box.getId(), new BigDecimal("0.05"))
        ));
        productService.createProductInternal(posture, sellers.get(8));

        // --- CATEGORY 5: HOME APPLIANCES (3 Products) ---
        // (12. Gas Stove, 13. Air Fryer, 14. Vacuum, 15. Chimney)

        ProductCreateRequest gasStove1 = new ProductCreateRequest();
        gasStove1.setName("3-Burner Auto Ignition Gas Stove");
        gasStove1.setDescription("ISI Certified. 11.6kg. Mild Steel body. Black. Auto Ignition.");
        gasStove1.setPrice(new BigDecimal("3290.00"));
        gasStove1.setStockQuantity(70);
        gasStove1.setImageUrl("https://m.media-amazon.com/images/I/61OefFiMvuL._SL1500_.jpg");
        gasStove1.setCategoryId(cat_home_appliances.getId());
        gasStove1.setTransportZoneId(zone_inter_state.getId());
        gasStove1.setMaterials(List.of(new ProductMaterialDto(mat_steel.getId(), new BigDecimal("10.0")), new ProductMaterialDto(mat_glass.getId(), new BigDecimal("1.6"))));
        gasStove1.setManufacturing(List.of(new ProductManufacturingDto(mfg_injection_molding.getId(), new BigDecimal("10.0")), new ProductManufacturingDto(mfg_glass_forming.getId(), new BigDecimal("1.6"))));
        gasStove1.setPackaging(List.of(new ProductPackagingDto(pkg_cardboard_box.getId(), new BigDecimal("1.0"))));
        productService.createProductInternal(gasStove1, sellers.get(5));

        ProductCreateRequest airFryer = new ProductCreateRequest();
        airFryer.setName("SOLARA 12L Air Fryer Oven for Home Kitchen");
        airFryer.setDescription("1800W OTG Air Fryer Oven 12 Litre with 12 Presets, 6 Accessories. 7kg. Imported from China.");
        airFryer.setPrice(new BigDecimal("6999.00"));
        airFryer.setStockQuantity(40);
        airFryer.setImageUrl("https://m.media-amazon.com/images/I/81nj934mZvL._SL1500_.jpg");
        airFryer.setCategoryId(cat_home_appliances.getId());
        airFryer.setTransportZoneId(zone_inter_state.getId());
        airFryer.setMaterials(List.of(
                new ProductMaterialDto(mat_polymers.getId(), new BigDecimal("3.0")),
                new ProductMaterialDto(mat_steel.getId(), new BigDecimal("3.5")),
                new ProductMaterialDto(mat_battery.getId(), new BigDecimal("0.5")) // Proxy for electronics
        ));
        airFryer.setManufacturing(List.of(
                new ProductManufacturingDto(mfg_injection_molding.getId(), new BigDecimal("3.0")),
                new ProductManufacturingDto(mfg_injection_molding.getId(), new BigDecimal("3.5")), // Proxy for metal forming
                new ProductManufacturingDto(mfg_electronics_assembly.getId(), new BigDecimal("0.5"))
        ));
        airFryer.setPackaging(List.of(new ProductPackagingDto(pkg_cardboard_box.getId(), new BigDecimal("1.0"))));
        productService.createProductInternal(airFryer, sellers.get(7));

        ProductCreateRequest vacuum = new ProductCreateRequest();
        vacuum.setName("Cordless Stick Vacuum Cleaner 350W");
        vacuum.setDescription("28KPA Suction, Digital Touch Display, 3 Speed Control. 3.03kg. Imported from China.");
        vacuum.setPrice(new BigDecimal("7999.00"));
        vacuum.setStockQuantity(30);
        vacuum.setImageUrl("https://m.media-amazon.com/images/I/51csEhoQqRL._SL1200_.jpg");
        vacuum.setCategoryId(cat_home_appliances.getId());
        vacuum.setTransportZoneId(zone_inter_state.getId());
        vacuum.setMaterials(List.of(
                new ProductMaterialDto(mat_polymers.getId(), new BigDecimal("1.5")),
                new ProductMaterialDto(mat_steel.getId(), new BigDecimal("0.5")),
                new ProductMaterialDto(mat_battery.getId(), new BigDecimal("0.53")) // Battery + Motor
        ));
        vacuum.setManufacturing(List.of(
                new ProductManufacturingDto(mfg_injection_molding.getId(), new BigDecimal("1.5")),
                new ProductManufacturingDto(mfg_electronics_assembly.getId(), new BigDecimal("1.03"))
        ));
        vacuum.setPackaging(List.of(new ProductPackagingDto(pkg_cardboard_box.getId(), new BigDecimal("0.5"))));
        productService.createProductInternal(vacuum, sellers.get(8));

        ProductCreateRequest chimney = new ProductCreateRequest();
        chimney.setName("Filterless Kitchen Chimney");
        chimney.setDescription("Curved Glass, 1450 m3/hr Suction, Touch & Motion Sensor. 11.9kg.");
        chimney.setPrice(new BigDecimal("12990.00"));
        chimney.setStockQuantity(25);
        chimney.setImageUrl("https://m.media-amazon.com/images/I/51BV81FpwHL._SL1500_.jpg");
        chimney.setCategoryId(cat_home_appliances.getId());
        chimney.setTransportZoneId(zone_inter_state.getId());
        chimney.setMaterials(List.of(
                new ProductMaterialDto(mat_steel.getId(), new BigDecimal("8.0")),
                new ProductMaterialDto(mat_glass.getId(), new BigDecimal("2.0")),
                new ProductMaterialDto(mat_battery.getId(), new BigDecimal("1.9")) // Proxy for motor/electronics
        ));
        chimney.setManufacturing(List.of(
                new ProductManufacturingDto(mfg_injection_molding.getId(), new BigDecimal("8.0")), // Metal stamping
                new ProductManufacturingDto(mfg_glass_forming.getId(), new BigDecimal("2.0")),
                new ProductManufacturingDto(mfg_electronics_assembly.getId(), new BigDecimal("1.9"))
        ));
        chimney.setPackaging(List.of(new ProductPackagingDto(pkg_cardboard_box.getId(), new BigDecimal("1.5"))));
        productService.createProductInternal(chimney, sellers.get(9));

        // 16. AQVA Cotton Tote Bag
        ProductCreateRequest tote = new ProductCreateRequest();
        tote.setName("AQVA Heavy Cotton Canvas Tote Bag");
        tote.setDescription("Printed Multipurpose Handbag With Top Zip, Best For Shopping, Travel, Work, Beach, Office, College. 500g.");
        tote.setPrice(new BigDecimal("549.00"));
        tote.setStockQuantity(200);
        tote.setImageUrl("https://m.media-amazon.com/images/I/61Oht2FSiPL._AC_UL480_FMwebp_QL65_.jpg");
        tote.setCategoryId(cat_accessories.getId());
        tote.setTransportZoneId(zone_intra_state.getId());
        tote.setMaterials(List.of(
                new ProductMaterialDto(mat_cotton.getId(), new BigDecimal("0.5"))
        ));
        tote.setManufacturing(List.of(
                new ProductManufacturingDto(mfg_spinning.getId(), new BigDecimal("0.5")),
                new ProductManufacturingDto(mfg_dyeing.getId(), new BigDecimal("0.5")),
                new ProductManufacturingDto(mfg_cut_sew.getId(), new BigDecimal("0.5"))
        ));
        tote.setPackaging(List.of(
                new ProductPackagingDto(pkg_polybag.getId(), new BigDecimal("0.02"))
        ));
        productService.createProductInternal(tote, sellers.get(4)); // Assign to Seller 5

        // 17. Vegan Leather Wallet Set
        ProductCreateRequest walletSet = new ProductCreateRequest();
        walletSet.setName("Personalized Vegan Leather Gift Set");
        walletSet.setDescription("Wallet, Pen, Key Chain & Cardholder Gift Set. 480g total weight.");
        walletSet.setPrice(new BigDecimal("899.00"));
        walletSet.setStockQuantity(150);
        walletSet.setImageUrl("https://m.media-amazon.com/images/I/718T-uNFdVL._SL1500_.jpg"); // Replaced broken link
        walletSet.setCategoryId(cat_accessories.getId());
        walletSet.setTransportZoneId(zone_inter_state.getId());
        walletSet.setMaterials(List.of(
                new ProductMaterialDto(mat_polymers.getId(), new BigDecimal("0.25")), // Vegan Leather
                new ProductMaterialDto(mat_steel.getId(), new BigDecimal("0.03")) // Pen/Keychain
        ));
        walletSet.setManufacturing(List.of(
                new ProductManufacturingDto(mfg_injection_molding.getId(), new BigDecimal("0.25")),
                new ProductManufacturingDto(mfg_cut_sew.getId(), new BigDecimal("0.28"))
        ));
        walletSet.setPackaging(List.of(
                new ProductPackagingDto(pkg_cardboard_box.getId(), new BigDecimal("0.20")) // 200g box
        ));
        productService.createProductInternal(walletSet, sellers.get(5)); // Assign to Seller 6

        // 18. OLEVS Men's Steel Watch
        ProductCreateRequest watch = new ProductCreateRequest();
        watch.setName("OLEVS Men's Business Watch Gold Blue");
        watch.setDescription("Large Easy-Read Analog Quartz Date Display. Luxury Stainless Steel Band. 120g.");
        watch.setPrice(new BigDecimal("1599.00"));
        watch.setStockQuantity(100);
        watch.setImageUrl("https://m.media-amazon.com/images/I/71NeiRc-kwL._SX569_.jpg");
        watch.setCategoryId(cat_accessories.getId());
        watch.setTransportZoneId(zone_inter_state.getId()); // Imported from China
        watch.setMaterials(List.of(
                new ProductMaterialDto(mat_steel.getId(), new BigDecimal("0.08")),
                new ProductMaterialDto(mat_glass.getId(), new BigDecimal("0.01")),
                new ProductMaterialDto(mat_battery.getId(), new BigDecimal("0.03")) // Battery + Quartz
        ));
        watch.setManufacturing(List.of(
                new ProductManufacturingDto(mfg_injection_molding.getId(), new BigDecimal("0.08")), // Metal forming
                new ProductManufacturingDto(mfg_electronics_assembly.getId(), new BigDecimal("0.03"))
        ));
        watch.setPackaging(List.of(
                new ProductPackagingDto(pkg_cardboard_box.getId(), new BigDecimal("0.1"))
        ));
        productService.createProductInternal(watch, sellers.get(6));

        // 19. Recycled Paper Pens
        ProductCreateRequest paperPens = new ProductCreateRequest();
        paperPens.setName("100% Recycled Paper Pens (30 Pack)");
        paperPens.setDescription("Plastic-Free Eco Pens, Natural Kraft Body. Low Viscosity Ink. 150g total.");
        paperPens.setPrice(new BigDecimal("499.00"));
        paperPens.setStockQuantity(300);
        paperPens.setImageUrl("https://m.media-amazon.com/images/I/51hMDmUN6NL._SX522_.jpg");
        paperPens.setCategoryId(cat_stationary.getId());
        paperPens.setTransportZoneId(zone_intra_state.getId());
        paperPens.setMaterials(List.of(new ProductMaterialDto(mat_bamboo.getId(), new BigDecimal("0.14")), new ProductMaterialDto(mat_polymers.getId(), new BigDecimal("0.01"))));
        paperPens.setManufacturing(List.of(new ProductManufacturingDto(mfg_cut_sew.getId(), new BigDecimal("0.15"))));
        paperPens.setPackaging(List.of(new ProductPackagingDto(pkg_cardboard_box.getId(), new BigDecimal("0.05"))));
        productService.createProductInternal(paperPens, sellers.get(9));

        // 20. Plantable Seed Pens
        ProductCreateRequest seedPens = new ProductCreateRequest();
        seedPens.setName("Sow and Grow Plantable Seed Paper Pen (Pack of 10)");
        seedPens.setDescription("Eco-Friendly pens with seeds. Plant the pen after use. 220g pack.");
        seedPens.setPrice(new BigDecimal("299.00"));
        seedPens.setStockQuantity(400);
        seedPens.setImageUrl("https://m.media-amazon.com/images/I/81l8nbAqvvL._SL1500_.jpg");
        seedPens.setCategoryId(cat_stationary.getId());
        seedPens.setTransportZoneId(zone_inter_state.getId());
        seedPens.setMaterials(List.of(new ProductMaterialDto(mat_bamboo.getId(), new BigDecimal("0.1"))));
        seedPens.setManufacturing(List.of(new ProductManufacturingDto(mfg_cut_sew.getId(), new BigDecimal("0.1"))));
        seedPens.setPackaging(List.of(new ProductPackagingDto(pkg_paper_wrap.getId(), new BigDecimal("0.12"))));
        productService.createProductInternal(seedPens, sellers.get(9));

        // 21. Microservices Book
        ProductCreateRequest bookSpring = new ProductCreateRequest();
        bookSpring.setName("Hands-On Microservices with Spring Boot");
        bookSpring.setDescription("Build and deploy Java microservices using Spring Cloud, Istio, and Kubernetes. 668 pages.");
        bookSpring.setPrice(new BigDecimal("2499.00"));
        bookSpring.setStockQuantity(50);
        bookSpring.setImageUrl("https://m.media-amazon.com/images/G/31/ui/loadIndicators/loading-large_labeled._CB485921387_.gif"); // Fixed URL
        bookSpring.setCategoryId(cat_stationary.getId());
        bookSpring.setTransportZoneId(zone_inter_state.getId());
        bookSpring.setMaterials(List.of(new ProductMaterialDto(mat_cotton.getId(), new BigDecimal("1.13")))); // Paper proxy
        bookSpring.setManufacturing(List.of(new ProductManufacturingDto(mfg_dyeing.getId(), new BigDecimal("1.13")))); // Printing proxy
        bookSpring.setPackaging(List.of(new ProductPackagingDto(pkg_polybag.getId(), new BigDecimal("0.01"))));
        productService.createProductInternal(bookSpring, sellers.get(6));

        // 22. Rich Dad Poor Dad
        ProductCreateRequest bookRichDad = new ProductCreateRequest();
        bookRichDad.setName("Rich Dad Poor Dad");
        bookRichDad.setDescription("What the Rich Teach Their Kids About Money. 336 pages. 205g.");
        bookRichDad.setPrice(new BigDecimal("349.00"));
        bookRichDad.setStockQuantity(500);
        bookRichDad.setImageUrl("https://m.media-amazon.com/images/I/71HJj3XmheL._SL1500_.jpg");
        bookRichDad.setCategoryId(cat_stationary.getId());
        bookRichDad.setTransportZoneId(zone_inter_state.getId());
        bookRichDad.setMaterials(List.of(new ProductMaterialDto(mat_cotton.getId(), new BigDecimal("0.205"))));
        bookRichDad.setManufacturing(List.of(new ProductManufacturingDto(mfg_dyeing.getId(), new BigDecimal("0.205"))));
        bookRichDad.setPackaging(List.of());
        productService.createProductInternal(bookRichDad, sellers.get(6));

        // 23. Psychology of Money
        ProductCreateRequest bookMoney = new ProductCreateRequest();
        bookMoney.setName("The Psychology of Money");
        bookMoney.setDescription("Timeless lessons on wealth, greed, and happiness. 252 pages. 224g.");
        bookMoney.setPrice(new BigDecimal("299.00"));
        bookMoney.setStockQuantity(500);
        bookMoney.setImageUrl("https://m.media-amazon.com/images/G/31/ui/loadIndicators/loading-large_labeled._CB485921387_.gif"); // Fixed URL
        bookMoney.setCategoryId(cat_stationary.getId());
        bookMoney.setTransportZoneId(zone_inter_state.getId());
        bookMoney.setMaterials(List.of(new ProductMaterialDto(mat_cotton.getId(), new BigDecimal("0.224"))));
        bookMoney.setManufacturing(List.of(new ProductManufacturingDto(mfg_dyeing.getId(), new BigDecimal("0.224"))));
        bookMoney.setPackaging(List.of());
        productService.createProductInternal(bookMoney, sellers.get(6));

        // 24. Atomic Habits
        ProductCreateRequest bookHabits = new ProductCreateRequest();
        bookHabits.setName("Atomic Habits");
        bookHabits.setDescription("The life-changing million copy bestseller. 320 pages. 430g.");
        bookHabits.setPrice(new BigDecimal("499.00"));
        bookHabits.setStockQuantity(500);
        bookHabits.setImageUrl("https://m.media-amazon.com/images/I/817HaeblezL._SL1500_.jpg");
        bookHabits.setCategoryId(cat_stationary.getId());
        bookHabits.setTransportZoneId(zone_inter_state.getId());
        bookHabits.setMaterials(List.of(new ProductMaterialDto(mat_cotton.getId(), new BigDecimal("0.43"))));
        bookHabits.setManufacturing(List.of(new ProductManufacturingDto(mfg_dyeing.getId(), new BigDecimal("0.43"))));
        bookHabits.setPackaging(List.of());
        productService.createProductInternal(bookHabits, sellers.get(6));
    }
}