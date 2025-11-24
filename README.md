🌱 Eco Bazaar X - Carbon Footprint Aware Shopping Assistant

EcoBazaarX is a smart and innovative solution for ecommerce platforms. It is a carbon footprint awareness platform that promotes gree-purchase decisions. The Carbon Footprint Engine dynamically calculates the environmental impact of every product based on the Life Cycle Assessment (LCA) studies including factors like raw materials, manufacturing process, transportation etc. 

🚀 Key Features

🌍 Carbon Engine
Dynamic Calculation: Automatically calculates cradle-to-warehouse carbon footprint.
Factor-Based Logic: Usage of standardized emission factors for:
   * Materials: (e.g., Organic Cotton vs. Polyester)
   * Manufacturing: (e.g., Hand-woven vs. Industrial Molding)
   * Packaging: (e.g., Recycled Paper vs. Plastic)
   * Logistics: Transport zones (Intra-city, Inter-state).

🎮 Eco-Gamification
  * Points System: Users earn "Eco Points" for referrals and writing reviews.
  * Global Leaderboard.
  * Ranks: Progression system from Sprout 🌱 to Planet Savior 🌍.

🛍️ Commerce & Transactions
  * Multi-Vendor Architecture: Distinct roles for Customers, Sellers, and Admins.
  * Smart Cart: Real-time tax calculation, shipping estimates, and discount code validation.
  * Secure Checkout: Integrated Stripe Payment Intents for secure credit card processing.

🛡️ Security & Administration
  * RBAC: Role-Based Access Control using Spring Security & JWT.
  * Seller Onboarding: Workflow for applying, approving, or rejecting seller accounts.
  * Config Hub: Admin panel to manage carbon factors, taxes, and platform fees without code changes.


🛠️ Tech Stack

  * Core Framework: Java 17, Spring Boot 3.x
  * Database: MySQL (Hibernate/JPA)
  * Security: Spring Security 6, JJWT (JSON Web Tokens)
  * Payment Gateway: Stripe API
  * Build Tool: Maven
  * Utilities: Lombok, OpenCSV (for report generation)


🏗️ Database Architecture
The application uses a relational model with the following core entities:
  * Users & Roles: (users_tb, roles_tb)
  * Products: (products, categories_tb)
  * Carbon Factors: (materials, manufacturing_processes, packaging_materials, transport_zones)
  * Orders & Cart: (orders_tb, order_items, carts, cart_items)
  * Financials: (tax_rates, discounts, payout_details)


⚙️ Installation & Setup

  1. Prerequisites
    * Java Development Kit (JDK) 17 or higher
    * MySQL Server
    * Maven

  2. Clone the Repository
     
    git clone https://github.com/yourusername/ecobazaarx-backend.git
    cd ecobazaarx-backend
    
  3. Configure Environment
  Update src/main/resources/application.properties with your credentials:

    # Database Configuration
    spring.datasource.url=jdbc:mysql://localhost:3306/ecobazaarx_db?createDatabaseIfNotExist=true
    spring.datasource.username=YOUR_DB_USER
    spring.datasource.password=YOUR_DB_PASSWORD
    
    # JWT Configuration (256-bit secret)
    application.security.jwt.secret-key=YOUR_VERY_LONG_SECRET_KEY_HERE
    application.security.jwt.expiration=86400000
    
    # Stripe API
    stripe.api.key=sk_test_YOUR_STRIPE_SECRET_KEY
    
    # Hibernate
    spring.jpa.hibernate.ddl-auto=update


  4. Run Application
     
     mvn spring-boot:run
  


📦 Data Seeding (Quick Start)
On the first run, the DataInitializer.java class will automatically populate the database with essential data for testing:
  
  1. Roles: ROLE_ADMIN, ROLE_SELLER, ROLE_CUSTOMER
  2. Config: GST Tax rates, Shipping Zones, Carbon Factors.
  3. Test Users:
     
     Role -> Email -> Password
     
     Admin -> admin1@amail.com -> password123!,
     
     Seller -> seller1@amail.com -> password123!,
     
     Customer -> customer1@amail.com -> password123!

📡 Some Major API Reference
The API is organized into versioned controllers (/api/v1).

🔐 Authentication
  * POST /auth/register - Create new account
  * POST /auth/login - Authenticate and get JWT

🛒 Public Storefront
  * GET /products - List all products (Pagination + Sorting)
  * GET /products/search?query={q} - Search products
  * GET /products/{id} - Get product details
  * GET /leaderboard/global - View top eco-users

👤 Customer Portal
  * GET /profile/me - Get current user profile
  * POST /cart/add - Add item to cart
  * POST /checkout - Initiate Stripe payment
  * GET /profile/orders - View order history

💼 Seller Portal
  * GET /seller/dashboard - View revenue & carbon stats
  * POST /seller/products - Create product (with Carbon definitions)
  * GET /seller/orders - View incoming orders

👑 Admin Portal
  * GET /insights/admin - Platform-wide analytics
  * GET /admin/users - User management
  * POST /admin/config/materials - Update carbon factors
     
  
Built with 💚 for a Sustainable Future.
