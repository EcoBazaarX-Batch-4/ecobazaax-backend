# 🌱 EcoBazaarX v2 - Carbon Footprint Aware E-Commerce Platform

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-6DB33F?style=flat-square&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?style=flat-square&logo=apache-maven)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)](LICENSE)

> **EcoBazaarX v2** is an innovative, full-featured e-commerce backend powered by a **dynamic Carbon Footprint Engine** that promotes environmentally conscious purchase decisions. Built with **Spring Boot 3.x**, it integrates **multi-vendor support**, **real-time carbon calculations**, **gamified eco-points**, and **secure Stripe payments** into a scalable REST API.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Key Features](#key-features)
- [Technology Stack](#technology-stack)
- [Architecture & Design](#architecture--design)
- [Database Schema](#database-schema)
- [API Endpoints](#api-endpoints)
- [Installation & Setup](#installation--setup)
- [Environment Configuration](#environment-configuration)
- [Running the Application](#running-the-application)
- [Authentication & Security](#authentication--security)
- [Core Workflows](#core-workflows)
- [API Documentation](#api-documentation)
- [Development Guide](#development-guide)
- [Production Deployment](#production-deployment)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [Support & Contact](#support--contact)

---

## 🎯 Overview

**EcoBazaarX v2** is a next-generation e-commerce platform built specifically to make sustainability measurable and rewarding. At its core is a sophisticated **Carbon Footprint Engine** that calculates the environmental impact of every product using Life Cycle Assessment (LCA) principles and standardized emission factors.

### Problem Solved
- Lack of transparency in product environmental impact
- No incentive mechanism for eco-conscious purchases
- Fragmented multi-vendor e-commerce with no unified carbon tracking
- Difficulty in making informed sustainable shopping decisions

### Solution Provided
A complete e-commerce backend that seamlessly integrates:
- Real-time carbon footprint calculations per product
- Gamified rewards system (Eco Points & Leaderboards)
- Multi-vendor marketplace with seller onboarding
- Secure checkout with Stripe integration
- Role-based administration and analytics dashboards

---

## 🚀 Key Features

### 🌍 Carbon Footprint Engine
The heart of EcoBazaarX—calculates cradle-to-warehouse environmental impact dynamically:

| Component | Calculation Method | Examples |
|-----------|-------------------|----------|
| **Materials** | Emission factors per kg | Organic Cotton (2.5 kg CO₂), Recycled Plastic (1.8 kg CO₂), Bamboo (1.2 kg CO₂) |
| **Manufacturing** | Process-specific multipliers | Hand-woven (+0.5 kg), Industrial Molding (-0.3 kg), Low-impact dyeing (standard) |
| **Packaging** | Material + weight-based | Recycled Cardboard (0.1 kg CO₂), Plastic (0.25 kg CO₂) |
| **Logistics** | Zone-based flat + distance | Zone A (+2 kg), Zone B (+3.5 kg), Zone C (+5 kg) |

**Result:** Total cradle-to-warehouse carbon footprint displayed per product (e.g., "Eco T-Shirt: 4.2 kg CO₂")

### 🎮 Gamification System
Motivate sustainable behavior through engaging rewards:

- **Eco Points** 
  - Earned via: product purchases (points based on carbon offset), reviews, referrals
  - Redeemable: discount on future purchases at rate $0.01 per point
  - Ledger: Full transaction history tracked per user

- **Global Leaderboard**
  - Ranks users by total Eco Points
  - Filterable by level/tier
  - Real-time updates on purchases

- **Tier Progression** (Sprout → Planet Savior)
  - Automatic rank upgrades based on cumulative points
  - Exclusive benefits and badges

### 🛍️ Multi-Vendor E-Commerce
Full marketplace architecture with distinct user roles:

| Role | Capabilities |
|------|--------------|
| **Customer** | Browse products, manage cart, place orders, earn eco-points, write reviews, track orders |
| **Seller** | Apply for seller account, create/update/archive products, view sales analytics, manage payout details |
| **Admin** | Approve sellers, manage categories/materials/processes, configure taxes/discounts/zones, view platform analytics, export reports (CSV) |

### 💳 Smart Checkout & Payments
- **Real-time Cart Calculations**
  - Automatic tax computation per item
  - Shipping estimation by zone
  - Discount code validation
  - Eco points redemption
  
- **Secure Payment Processing**
  - Stripe Payment Intents API integration
  - PCI-DSS compliant (no card data stored)
  - Payment method tokenization

- **Order Management**
  - Multi-item orders with order history
  - Order status tracking (PENDING, CONFIRMED, SHIPPED, DELIVERED)
  - Admin order status updates

### 👥 Seller Onboarding Workflow
1. User submits seller application (store name, tax ID)
2. Admin reviews and approves/rejects
3. Seller gains access to product creation & analytics
4. Seller manages payout details for commission payouts

### 🔍 Search, Recommendations & Tracking
- **Product Search** with filters: query, category, price range, sorting
- **Homepage Recommendations** (curated trending products)
- **Personalized Recommendations** (based on user view history)
- **View Tracking** (automatic logging of product views for ML-ready data)

### 📊 Analytics & Insights
- **User Insights:** Total purchases, Eco Points earned, Leaderboard rank
- **Seller Insights:** Product performance, sales trends, revenue
- **Admin Insights:** Platform statistics, user/product/order metrics, leaderboard snapshots
- **CSV Export** for sellers and admins (sales reports, platform exports)

### 🛡️ Security & Administration
- **JWT-Based Authentication** with configurable expiration
- **Role-Based Access Control (RBAC)** with Spring Security
- **Admin Dashboard** to manage all platform configurations without code changes
- **Audit Trail** via EcoPointLedger and order history

---

## 🛠️ Technology Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| **Language** | Java | 17 LTS |
| **Framework** | Spring Boot | 3.5.7 |
| **Web** | Spring MVC | 6.x |
| **Database** | MySQL | 8.0+ |
| **ORM** | Hibernate / JPA | 6.x |
| **Security** | Spring Security | 6.x |
| **Authentication** | JJWT | 0.11.5 |
| **Payments** | Stripe Java SDK | 30.1.0 |
| **Build** | Maven | 3.9+ |
| **Code Generation** | Lombok | 1.18.30 |
| **CSV Export** | OpenCSV | 5.9 |
| **Testing** | JUnit 5 + Mockito | 5.10+ |
| **API Documentation** | Postman Collection | included |

---

## 🏗️ Architecture & Design

### Layered Architecture
```
┌─────────────────────────────────────────┐
│   REST Controllers (@RestController)    │  ← HTTP Request Entry Point
├─────────────────────────────────────────┤
│   Services (@Service)                   │  ← Business Logic & Transactions
├─────────────────────────────────────────┤
│   Repositories (@Repository)            │  ← Data Access (Spring Data JPA)
├─────────────────────────────────────────┤
│   Entities (@Entity)                    │  ← Domain Models
├─────────────────────────────────────────┤
│   MySQL Database                        │  ← Persistent Storage
└─────────────────────────────────────────┘
```

### Key Design Patterns
- **DTOs (Data Transfer Objects):** Decouple API contracts from domain models
- **Service Layer:** Encapsulate complex business logic (carbon calculations, checkout flow)
- **Specification Pattern:** Flexible product search with JPA Criteria API
- **Transactional Consistency:** `@Transactional` ensures ACID compliance
- **JWT Stateless Auth:** No server-side sessions, scalable across instances

### Project Structure
```
src/main/java/com/ecobazaarx/v2/
├── config/                    # Spring configs (Security, JWT, Data Init)
│   ├── SecurityConfig.java
│   ├── ApplicationConfig.java
│   ├── JwtService.java
│   ├── JwtAuthenticationFilter.java
│   └── DataInitializer.java
├── controller/                # REST Endpoints (14 controllers)
│   ├── AuthenticationController.java
│   ├── ProductController.java
│   ├── CartController.java
│   ├── CheckoutController.java
│   ├── AdminController.java
│   ├── SellerController.java
│   └── ... (8 more)
├── service/                   # Business Logic (17 services)
│   ├── ProductService.java      (Carbon calculations)
│   ├── CheckoutService.java     (Payment processing)
│   ├── GamificationService.java (Eco points logic)
│   ├── CartService.java         (Shopping cart)
│   └── ... (13 more)
├── dto/                       # Data Transfer Objects (~30 DTOs)
│   ├── ProductResponseDto.java
│   ├── CheckoutRequest.java
│   ├── CartResponse.java
│   └── ... (27 more)
├── model/                     # JPA Entities (26 models)
│   ├── User.java              (Multi-role users)
│   ├── Product.java           (Carbon footprint field)
│   ├── Order.java             (Order management)
│   ├── Cart.java              (Shopping cart)
│   └── ... (22 more)
└── repository/                # Spring Data Repositories (26 repos)
    ├── ProductRepository.java
    ├── UserRepository.java
    └── ... (24 more)

src/main/resources/
├── application.properties      # DB, JWT, Stripe configs
└── static/                     # Static assets (if needed)

DOCS/                           # Documentation & Postman
├── DETAILED_DOCUMENTATION.md
├── EcoBazaarX_full.postman_collection.json
├── .env.example
└── postman_bodies.json
```

---

## 📊 Database Schema

### Core Entities

```sql
-- Users with multi-role support
CREATE TABLE users_tb (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(100) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  first_name VARCHAR(50),
  last_name VARCHAR(50),
  eco_points INT DEFAULT 0,           -- Gamification
  seller_application_status ENUM(...),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Products with carbon footprint
CREATE TABLE products (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(200) NOT NULL,
  description TEXT,
  price DECIMAL(10,2),
  cradle_to_warehouse_footprint DECIMAL(10,2),  -- Key carbon field
  eco_points INT DEFAULT 0,
  seller_id BIGINT FOREIGN KEY,
  category_id BIGINT FOREIGN KEY,
  transport_zone_id BIGINT FOREIGN KEY,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Carbon factors (for dynamic calculation)
CREATE TABLE materials (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100),
  emission_factor DECIMAL(10,4)  -- kg CO₂ per unit
);

CREATE TABLE manufacturing_processes (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100),
  carbon_multiplier DECIMAL(5,2)  -- e.g., 1.0 = standard, 0.8 = efficient
);

CREATE TABLE packaging_materials (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100),
  carbon_footprint DECIMAL(10,4)
);

CREATE TABLE transport_zones (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50),
  regions VARCHAR(255),
  flat_carbon_footprint DECIMAL(10,4)  -- Base carbon for zone
);

-- Orders & Cart
CREATE TABLE carts (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT UNIQUE FOREIGN KEY,
  subtotal DECIMAL(10,2),
  tax_amount DECIMAL(10,2),
  shipping_cost DECIMAL(10,2),
  discount_amount DECIMAL(10,2)
);

CREATE TABLE orders_tb (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT FOREIGN KEY,
  order_status ENUM('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED'),
  grand_total DECIMAL(10,2),
  placed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Gamification
CREATE TABLE eco_point_ledger (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT FOREIGN KEY,
  points_changed INT,
  reason VARCHAR(255),  -- e.g., "Purchase", "Review", "Referral"
  transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Leaderboard data
-- (Derived from eco_points in users_tb; no separate table)
```

### Entity Relationships Diagram
```
User (1) ──── (many) Product (seller)
User (1) ──── (many) Order
User (1) ──── (1) Cart
User (1) ──── (many) EcoPointLedger
User (1) ──── (many) Wishlist
User (1) ──── (many) ProductReview
User (1) ──── (many) Address

Product (many) ──── (1) Category
Product (many) ──── (1) TransportZone
Product (many) ──── (many) Material (through ProductMaterial)
Product (many) ──── (many) ManufacturingProcess (through ProductManufacturing)
Product (many) ──── (many) PackagingMaterial (through ProductPackaging)
Product (1) ──── (many) ProductReview
Product (1) ──── (many) CartItem

Order (1) ──── (many) OrderItem
OrderItem (many) ──── (1) Product

Cart (1) ──── (many) CartItem
CartItem (many) ──── (1) Product

Discount (many) ──── (1) DiscountType
TaxRate (many) ──── (1) Category
```

---

## 📡 API Endpoints

The application exposes **89 REST endpoints** organized into functional groups:

### Authentication (2 endpoints)
| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| POST | `/api/v1/auth/register` | Public | User registration |
| POST | `/api/v1/auth/login` | Public | JWT token generation |

### Products (7 endpoints)
| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| GET | `/api/v1/products` | Public | List all products (paginated) |
| GET | `/api/v1/products/{id}` | Public | Get product details with carbon footprint |
| GET | `/api/v1/products/search` | Public | Search with filters (category, price, query) |
| POST | `/api/v1/products/{id}/reviews` | CUSTOMER | Add product review |
| GET | `/api/v1/products/{id}/reviews` | Public | Get product reviews (paginated) |
| GET | `/api/v1/products/{id}/related` | Public | Get related products |
| GET | `/api/v1/recommendations/homepage` | Public | Homepage product recommendations |

### Cart (6 endpoints)
| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| POST | `/api/v1/cart/add` | CUSTOMER | Add item to cart |
| GET | `/api/v1/cart` | CUSTOMER | View current cart |
| PUT | `/api/v1/cart/update/{itemId}` | CUSTOMER | Update cart item quantity |
| DELETE | `/api/v1/cart/remove/{itemId}` | CUSTOMER | Remove item from cart |
| POST | `/api/v1/cart/apply-discount` | CUSTOMER | Apply discount code |
| GET | `/api/v1/cart/shipping-options/{addressId}` | CUSTOMER | Get shipping cost by zone |

### Checkout (1 endpoint)
| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| POST | `/api/v1/checkout` | CUSTOMER | Place order with Stripe payment |

### Profile (7 endpoints)
| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| GET | `/api/v1/profile/me` | AUTHENTICATED | Get user profile |
| PUT | `/api/v1/profile/me` | AUTHENTICATED | Update profile info |
| PUT | `/api/v1/profile/change-password` | AUTHENTICATED | Change password |
| GET | `/api/v1/profile/addresses` | AUTHENTICATED | List saved addresses |
| POST | `/api/v1/profile/addresses` | AUTHENTICATED | Add new address |
| PUT | `/api/v1/profile/addresses/{id}` | AUTHENTICATED | Update address |
| DELETE | `/api/v1/profile/addresses/{id}` | AUTHENTICATED | Delete address |

### Orders & Wishlist (6 endpoints)
| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| GET | `/api/v1/profile/orders` | CUSTOMER | View order history (paginated) |
| GET | `/api/v1/profile/orders/{id}` | CUSTOMER | Get order details |
| GET | `/api/v1/profile/wishlist` | CUSTOMER | View saved wishlist |
| POST | `/api/v1/profile/wishlist/{productId}` | CUSTOMER | Add product to wishlist |
| DELETE | `/api/v1/profile/wishlist/{productId}` | CUSTOMER | Remove from wishlist |

### Gamification (2 endpoints)
| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| GET | `/api/v1/leaderboard/global` | Public | Global Eco Points leaderboard |
| GET | `/api/v1/leaderboard/level/{level}` | Public | Leaderboard by tier/level |
| GET | `/api/v1/profile/eco-points-history` | CUSTOMER | Personal Eco Points ledger |

### Seller (8 endpoints)
| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| POST | `/api/v1/seller/apply` | CUSTOMER | Apply to become seller |
| GET | `/api/v1/seller/application-status` | CUSTOMER | Check seller application status |
| POST | `/api/v1/seller/products` | SELLER | Create new product |
| GET | `/api/v1/seller/products` | SELLER | List seller's products (paginated) |
| PUT | `/api/v1/seller/products/{id}` | SELLER | Update product |
| DELETE | `/api/v1/seller/products/{id}` | SELLER | Archive/delete product |
| PUT | `/api/v1/seller/profile` | SELLER | Update store profile |
| PUT | `/api/v1/seller/payout-details` | SELLER | Set bank account for payouts |

### Admin (36 endpoints)
**Seller Approvals**
| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| GET | `/api/v1/admin/seller-applications` | ADMIN | List pending seller applications |
| POST | `/api/v1/admin/seller-applications/approve/{userId}` | ADMIN | Approve seller |
| POST | `/api/v1/admin/seller-applications/reject/{userId}` | ADMIN | Reject seller |

**Categories** (CRUD)
| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| POST | `/api/v1/admin/categories` | ADMIN | Create category |
| GET | `/api/v1/admin/categories` | ADMIN/SELLER | List categories |
| PUT | `/api/v1/admin/categories/{id}` | ADMIN | Update category |
| DELETE | `/api/v1/admin/categories/{id}` | ADMIN | Delete category |

**Materials** (CRUD - 4 endpoints), **Manufacturing Processes** (CRUD - 4 endpoints), **Packaging Materials** (CRUD - 4 endpoints), **Transport Zones** (CRUD + GET by ID - 5 endpoints)

**User Management** (3 endpoints)
| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| GET | `/api/v1/admin/users` | ADMIN | List all users (paginated) |
| PUT | `/api/v1/admin/users/{userId}` | ADMIN | Update user info/role |
| POST | `/api/v1/admin/create-admin` | ADMIN | Create new admin user |

**Order & Product Management** (5 endpoints)
| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| GET | `/api/v1/admin/products` | ADMIN | List all products |
| GET | `/api/v1/admin/orders` | ADMIN | List all orders (with filters) |
| PUT | `/api/v1/admin/orders/{id}/status` | ADMIN | Update order status |

**Taxes & Discounts** (6 endpoints)
| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| POST | `/api/v1/admin/config/taxes` | ADMIN | Create tax rate |
| GET | `/api/v1/admin/config/taxes` | ADMIN | List tax rates |
| PUT | `/api/v1/admin/config/taxes/{id}` | ADMIN | Update tax rate |
| DELETE | `/api/v1/admin/config/taxes/{id}` | ADMIN | Delete tax rate |
| POST | `/api/v1/admin/config/discounts` | ADMIN | Create discount |
| GET | `/api/v1/admin/config/discounts` | ADMIN | List discounts |

### Insights & Analytics (6 endpoints)
| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| GET | `/api/v1/insights/profile` | CUSTOMER | User spending & eco stats |
| GET | `/api/v1/insights/seller` | SELLER | Seller dashboard stats |
| GET | `/api/v1/insights/seller/product-performance` | SELLER | Per-product analytics |
| GET | `/api/v1/insights/admin` | ADMIN | Platform-wide statistics |
| GET | `/api/v1/insights/admin/leaderboards` | ADMIN | Leaderboard snapshots |
| GET | `/api/v1/insights/seller/export-sales` | SELLER | Export sales report (CSV) |

### Tracking & Recommendations (2 endpoints)
| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| POST | `/api/v1/tracking/view/{productId}` | CUSTOMER | Log product view |
| GET | `/api/v1/recommendations/me/recent` | CUSTOMER | Get personalized recent recommendations |

---

## ⚙️ Installation & Setup

### Prerequisites
- **Java Development Kit (JDK) 17+** ([Download](https://www.oracle.com/java/technologies/downloads/#java17))
- **MySQL Server 8.0+** ([Download](https://dev.mysql.com/downloads/mysql/))
- **Maven 3.9+** ([Download](https://maven.apache.org/download.cgi)) or use `mvnw`
- **Git** ([Download](https://git-scm.com/))
- **Stripe Account** (Test mode for development) ([Sign up free](https://stripe.com))

### Step 1: Clone the Repository
```bash
git clone https://github.com/EcoBazaarX-Batch-4/ecobazaax-backend.git
cd eco-bazaar-x-v2
```

### Step 2: Create MySQL Database
```bash
mysql -u root -p

# In MySQL CLI:
CREATE DATABASE ecobazaarx CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'ecobazaar_user'@'localhost' IDENTIFIED BY 'SecurePassword123!';
GRANT ALL PRIVILEGES ON ecobazaarx.* TO 'ecobazaar_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### Step 3: Configure Environment
Create or update `src/main/resources/application.properties`:

```properties
# Application
spring.application.name=eco-bazaar-x-v2
server.port=8080

# Database Configuration (IMPORTANT: Use strong password!)
spring.datasource.url=jdbc:mysql://localhost:3306/ecobazaarx?createDatabaseIfNotExist=true
spring.datasource.username=ecobazaar_user
spring.datasource.password=SecurePassword123!

# Hibernate Auto Schema Update (Recommendation: use 'validate' in production)
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.show-sql=false

# JWT Security (256-bit secret - Generate a strong random string!)
application.security.jwt.secret-key=your-very-long-secure-secret-key-min-256-bits-recommended-here
application.security.jwt.expiration=86400000

# Stripe Integration (Get from https://dashboard.stripe.com/keys)
stripe.api.key=sk_test_YOUR_STRIPE_SECRET_KEY
stripe.api.public_key=pk_test_YOUR_STRIPE_PUBLIC_KEY

# Logging
logging.level.org.springframework.security=INFO
logging.level.org.hibernate.SQL=INFO
```

> ⚠️ **Security Note:** Never commit sensitive credentials to version control. Use environment variables or a secret manager in production.

### Step 4: Build the Application
```bash
# Using Maven wrapper (Windows)
mvnw.cmd clean install

# Or using system Maven (macOS/Linux)
mvn clean install

# Or using system Maven (Windows)
mvn.cmd clean install
```

This will:
- Download dependencies
- Compile source code
- Run unit tests
- Build `eco-bazaar-x-v2-0.0.1-SNAPSHOT.jar` in `target/`

---

## 🏃 Running the Application

### Option 1: Maven Spring Boot Plugin
```bash
mvn spring-boot:run
```

### Option 2: Run JAR Directly
```bash
java -jar target/eco-bazaar-x-v2-0.0.1-SNAPSHOT.jar
```

### Option 3: IDE (IntelliJ IDEA / Eclipse)
Right-click `EcoBazaarXV2Application.java` → Run or Debug

### Verify Server is Running
```bash
# Should return 200 OK
curl http://localhost:8080/api/v1/products

# Response:
# {"content": [...], "totalElements": X, "totalPages": Y, ...}
```

---

## 🔐 Authentication & Security

### JWT-Based Stateless Authentication

EcoBazaarX uses **JSON Web Tokens (JWT)** for stateless, scalable authentication:

#### Login Flow
```
1. POST /api/v1/auth/login
   Body: { "email": "user@example.com", "password": "password123" }

2. Response: { "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", "expiresIn": 86400000 }

3. Client stores token in local storage/session

4. All subsequent requests include:
   Header: Authorization: Bearer <token>
```

#### Token Structure
```
Header: { "alg": "HS256", "typ": "JWT" }
Payload: { "email": "user@example.com", "roles": ["ROLE_CUSTOMER"], "iat": 1702123456, "exp": 1702209856 }
Signature: HMAC-SHA256(secret-key)
```

#### Security Features
- **Expiration:** Default 24 hours (configurable via `application.security.jwt.expiration`)
- **Secret Key:** 256-bit HMAC key (stored securely, never exposed)
- **Filter:** `JwtAuthenticationFilter` validates token on every request
- **Stateless:** No server-side session storage (scalable across load balancers)

### Role-Based Access Control (RBAC)
```
┌─────────────────────────────────────────────────┐
│            Spring Security Chain                │
├─────────────────────────────────────────────────┤
│ 1. JwtAuthenticationFilter                      │
│    → Extract token from "Authorization" header  │
│    → Validate signature & expiration            │
│    → Set SecurityContext with roles             │
├─────────────────────────────────────────────────┤
│ 2. AuthorizationFilter (@PreAuthorize)          │
│    → Check required roles before method call    │
│    → e.g., @PreAuthorize("hasRole('ADMIN')")   │
├─────────────────────────────────────────────────┤
│ 3. SecurityConfig Routes                        │
│    → Public: /auth/**, /products (GET)         │
│    → CUSTOMER: /cart/**, /checkout/**          │
│    → SELLER: /seller/**                        │
│    → ADMIN: /admin/**                          │
└─────────────────────────────────────────────────┘
```

### Available Roles
| Role | Capabilities | API Access |
|------|--------------|-----------|
| `ROLE_CUSTOMER` | Browse, buy, review, earn points | `/profile/**`, `/cart/**`, `/checkout/**`, `/recommendations/me/**` |
| `ROLE_SELLER` | Create products, view sales, manage payouts | `/seller/**`, `/insights/seller/**` |
| `ROLE_ADMIN` | Approve sellers, configure platform, manage users | `/admin/**`, `/insights/admin/**` |

---

## 💼 Core Workflows

### 1. User Registration & Authentication
```
POST /api/v1/auth/register
{
  "email": "alice@example.com",
  "password": "Secret123!",
  "firstName": "Alice",
  "lastName": "Green"
}

Response: { "message": "User registered. Please login." }

POST /api/v1/auth/login
{
  "email": "alice@example.com",
  "password": "Secret123!"
}

Response: { "token": "eyJhbGciOi...", "expiresIn": 86400000 }
```

### 2. Product Browsing with Carbon Footprint
```
GET /api/v1/products?page=0&size=10

Response:
{
  "content": [
    {
      "id": 1,
      "name": "Eco T-Shirt",
      "price": 29.99,
      "cradleToWarehouseFootprint": 4.2,  // kg CO₂
      "ecoPoints": 42,                     // Eco points available
      "category": "Apparel",
      "imageUrl": "...",
      "seller": {
        "id": 10,
        "firstName": "Alice",
        "lastName": "Green"
      }
    }
  ],
  "totalElements": 245,
  "totalPages": 25
}
```

### 3. Shopping Cart & Checkout
```
// Add item to cart
POST /api/v1/cart/add
Headers: Authorization: Bearer <token>
Body: {
  "productId": 1,
  "quantity": 2
}

// View cart with calculations
GET /api/v1/cart
Headers: Authorization: Bearer <token>

Response:
{
  "items": [
    {
      "id": 101,
      "product": { "id": 1, "name": "Eco T-Shirt", "price": 29.99 },
      "quantity": 2,
      "subtotal": 59.98
    }
  ],
  "subtotal": 59.98,
  "taxAmount": 4.80,
  "shippingCost": 5.00,
  "discountAmount": 0,
  "totalCarbonOffset": 8.4,  // kg CO₂ saved
  "ecoPointsRedeemed": 0,
  "grandTotal": 69.78
}

// Apply discount
POST /api/v1/cart/apply-discount
Headers: Authorization: Bearer <token>
Body: { "discountCode": "WELCOME10" }

// Place order with Stripe
POST /api/v1/checkout
Headers: Authorization: Bearer <token>
Body: {
  "addressId": 5,
  "paymentMethod": "STRIPE",
  "stripePaymentMethodId": "pm_card_visa",
  "ecoPointsToRedeem": 100,  // Optional
  "notes": "Leave at reception"
}

Response:
{
  "orderId": 501,
  "orderStatus": "PENDING",
  "grandTotal": 69.78,
  "ecoPointsRedeemed": 100,
  "ecoPointsEarned": 42,
  "carbonOffsetKg": 8.4,
  "placedAt": "2024-12-02T15:30:00Z"
}
```

### 4. Seller Onboarding
```
// User applies to become seller
POST /api/v1/seller/apply
Headers: Authorization: Bearer <token>
Body: {
  "storeName": "Alice Eco Crafts",
  "storeDescription": "Handmade sustainable products",
  "taxId": "TAX12345"
}

// Admin approves
POST /api/v1/admin/seller-applications/approve/123
Headers: Authorization: Bearer <admin-token>

// Seller creates product
POST /api/v1/seller/products
Headers: Authorization: Bearer <seller-token>
Body: {
  "title": "Bamboo Cutting Board",
  "description": "Eco-friendly bamboo board",
  "price": 24.99,
  "categoryId": 3,
  "stock": 50,
  "transportZoneId": 1,
  "materials": [2],          // Material IDs
  "manufacturing": [1],      // Process IDs
  "packaging": [1]           // Packaging IDs
}

Response includes calculated:
{
  "id": 201,
  "cradleToWarehouseFootprint": 3.8  // Auto-calculated
}
```

### 5. Eco Points & Leaderboard
```
// User earns points from purchases
// Points += (cradleToWarehouseFootprint * 10) [e.g., 8.4 kg CO₂ → 84 points]

// View personal history
GET /api/v1/profile/eco-points-history?page=0&size=10
Headers: Authorization: Bearer <token>

Response:
{
  "content": [
    {
      "pointsChanged": 84,
      "reason": "Purchase - Order #501",
      "transactionDate": "2024-12-02T15:30:00Z"
    },
    {
      "pointsChanged": 50,
      "reason": "Referral bonus",
      "transactionDate": "2024-11-28T10:00:00Z"
    }
  ]
}

// View global leaderboard
GET /api/v1/leaderboard/global?page=0&size=20
Response:
{
  "content": [
    {
      "rank": 1,
      "userName": "Alice Green",
      "ecoPoints": 5420,
      "tier": "Eco Champion"
    },
    {
      "rank": 2,
      "userName": "Bob Brown",
      "ecoPoints": 4980,
      "tier": "Eco Leader"
    }
  ]
}
```

### 6. Admin Configuration
```
// Create product category
POST /api/v1/admin/categories
Headers: Authorization: Bearer <admin-token>
Body: {
  "name": "Accessories",
  "description": "Eco-friendly accessories"
}

// Create material factor
POST /api/v1/admin/materials
Headers: Authorization: Bearer <admin-token>
Body: {
  "name": "Organic Cotton",
  "description": "Certified organic cotton",
  "emissionFactor": 2.5  // kg CO₂ per unit
}

// Create tax rate
POST /api/v1/admin/config/taxes
Headers: Authorization: Bearer <admin-token>
Body: {
  "name": "GST",
  "rate": 0.08  // 8%
}

// Create transport zone
POST /api/v1/admin/transport-zones
Headers: Authorization: Bearer <admin-token>
Body: {
  "name": "Zone A",
  "regions": ["US-IL", "US-IN"],
  "basePrice": 5.0,              // Shipping cost
  "flatCarbonFootprint": 2.0     // Carbon for this zone
}
```

---

## 📚 API Documentation

### Complete Postman Collection
**Location:** `DOCS/EcoBazaarX_full.postman_collection.json`

Includes:
- 89 endpoints fully documented
- Pre-configured variables (`{{base_url}}`, `{{jwt}}`, etc.)
- Request/response examples
- Authentication headers pre-filled

### Swagger/OpenAPI (Optional Future Enhancement)
To auto-generate OpenAPI documentation, add to `pom.xml`:
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.0.4</version>
</dependency>
```

Then access at: `http://localhost:8080/swagger-ui.html`

### Sample Request/Response Bodies
**Location:** `DOCS/postman_bodies.json` and `DOCS/DETAILED_DOCUMENTATION.md`

Includes ~40 sample JSON payloads for all write operations.

---

## 👨‍💻 Development Guide

### Project Dependencies Overview
```
Core Framework:
├── spring-boot-starter-web (REST controllers)
├── spring-boot-starter-data-jpa (ORM)
├── spring-boot-starter-security (auth)
├── spring-boot-starter-validation (bean validation)
├── spring-boot-starter-mail (email notifications)
└── spring-boot-starter-test (unit tests)

Utilities:
├── lombok (code generation)
├── jjwt (JWT tokens)
├── stripe-java (payment processing)
├── opencsv (CSV export)
└── mysql-connector-j (database driver)
```

### Building & Testing
```bash
# Full build with tests
mvn clean install

# Skip tests
mvn clean install -DskipTests

# Run specific test
mvn test -Dtest=ProductServiceTest

# Generate code coverage report
mvn clean test jacoco:report
# Report: target/site/jacoco/index.html
```

### Key Service Methods

#### Carbon Calculation
```java
// ProductService.java
private BigDecimal calculateMaterialsCarbon(List<String> materials) {
    return materials.stream()
        .map(m -> materialRepository.findByName(m).getEmissionFactor())
        .reduce(BigDecimal.ZERO, BigDecimal::add);
}

// Usage: Called during product creation
BigDecimal totalCarbon = calculateMaterialsCarbon(request.getMaterials())
    .add(calculateManufacturingCarbon(request.getManufacturing()))
    .add(calculatePackagingCarbon(request.getPackaging()))
    .add(transportZone.getFlatCarbonFootprint());
```

#### Eco Points Logic
```java
// GamificationService.java
public void addPointsForAction(User user, int pointsToAdd, String reason) {
    user.setEcoPoints(user.getEcoPoints() + pointsToAdd);
    userRepository.save(user);
    
    EcoPointLedger ledger = new EcoPointLedger(user, pointsToAdd, reason);
    ledgerRepository.save(ledger);
}

// Called from CheckoutService
int pointsEarned = (int)(product.getCradleToWarehouseFootprint() * 10);
gamificationService.addPointsForAction(user, pointsEarned, "Purchase - Order #" + order.getId());
```

#### Stripe Payment Processing
```java
// CheckoutService.java
@PostConstruct
public void init() {
    Stripe.apiKey = stripeSecretKey;  // Initialize on startup
}

public CheckoutResponse placeOrder(...) throws StripeException {
    // Create PaymentIntent
    PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
        .setAmount((long)(finalTotal.multiply(new BigDecimal(100)).longValue()))  // Cents
        .setCurrency("usd")
        .setPaymentMethod(stripePaymentMethodId)
        .setConfirm(true)
        .build();
    
    PaymentIntent intent = PaymentIntent.create(params);
    
    if ("succeeded".equals(intent.getStatus())) {
        // Create order in DB
        Order order = new Order();
        order.setUser(user);
        order.setOrderStatus(OrderStatus.CONFIRMED);
        // ... save order
    }
}
```

### Adding a New Endpoint

**Example: Create a "Product Wishlist" endpoint**

1. **Create DTO** (`dto/WishlistDto.java`)
```java
@Data
public class WishlistDto {
    private Long productId;
    private String productName;
    private BigDecimal price;
    private LocalDateTime addedAt;
}
```

2. **Add to Existing Service** (`service/WishlistService.java`)
```java
@Service
public class WishlistService {
    @Transactional
    public void addToWishlist(User user, Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        
        Wishlist wishlist = wishlistRepository.findByUserAndProduct(user, product)
            .orElse(new Wishlist(user, product));
        
        wishlistRepository.save(wishlist);
    }
}
```

3. **Create Controller** (`controller/WishlistController.java`)
```java
@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class WishlistController {
    
    private final WishlistService wishlistService;
    
    @PostMapping("/{productId}")
    public ResponseEntity<String> addToWishlist(
        @PathVariable Long productId,
        @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow();
        wishlistService.addToWishlist(user, productId);
        
        return ResponseEntity.ok("Added to wishlist");
    }
}
```

4. **Update Security Config** (`config/SecurityConfig.java`)
```java
.requestMatchers("/api/v1/wishlist/**").hasRole("CUSTOMER")
```

5. **Write Unit Test** (`src/test/java/.../WishlistServiceTest.java`)
```java
@ExtendWith(MockitoExtension.class)
public class WishlistServiceTest {
    
    @Mock
    private WishlistRepository wishlistRepository;
    
    @InjectMocks
    private WishlistService wishlistService;
    
    @Test
    public void testAddToWishlist() {
        // Arrange
        User user = new User();
        Product product = new Product();
        
        // Act
        wishlistService.addToWishlist(user, product.getId());
        
        // Assert
        verify(wishlistRepository, times(1)).save(any(Wishlist.class));
    }
}
```

---

## 🚀 Production Deployment

### Pre-Deployment Checklist
- [ ] Change `spring.jpa.hibernate.ddl-auto` from `update` to `validate`
- [ ] Set `spring.jpa.show-sql=false` (reduces logs and overhead)
- [ ] Rotate JWT secret key to new 256-bit value
- [ ] Generate new Stripe API keys (use production, not test)
- [ ] Configure strong MySQL password & enable SSL
- [ ] Set logging level to `INFO` (not `DEBUG`)
- [ ] Enable CORS only for trusted origins
- [ ] Set up HTTPS/SSL certificate
- [ ] Configure health check endpoint for load balancer

### Docker Deployment Example
```dockerfile
# Dockerfile
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY . .
RUN ./mvnw clean install -DskipTests

FROM eclipse-temurin:17-jre-alpine
COPY --from=builder /app/target/eco-bazaar-x-v2-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and push:
```bash
docker build -t ecobazaarx-backend:latest .
docker run -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql-service:3306/ecobazaarx \
           -e SPRING_DATASOURCE_USERNAME=root \
           -e SPRING_DATASOURCE_PASSWORD=$DB_PASSWORD \
           -e APPLICATION_SECURITY_JWT_SECRET_KEY=$JWT_SECRET \
           -e STRIPE_API_KEY=$STRIPE_KEY \
           -p 8080:8080 ecobazaarx-backend:latest
```

### Environment Variables (Recommended for Secrets)
```bash
# .env (never commit to git!)
export SPRING_DATASOURCE_URL=jdbc:mysql://db-server:3306/ecobazaarx
export SPRING_DATASOURCE_USERNAME=ecobazaar_user
export SPRING_DATASOURCE_PASSWORD=VeryStrongPassword123!
export APPLICATION_SECURITY_JWT_SECRET_KEY=your-256-bit-secret-key-here
export STRIPE_API_KEY=sk_live_YOUR_LIVE_KEY
export STRIPE_API_PUBLIC_KEY=pk_live_YOUR_LIVE_KEY
```

Use with:
```bash
source .env
mvn spring-boot:run
```

### Monitoring & Logging
```yaml
# application-prod.properties
logging.level.root=INFO
logging.level.com.ecobazaarx=INFO
logging.file.name=logs/eco-bazaar-x.log
logging.file.max-size=10MB
logging.file.max-history=30
```

---

## 🔧 Troubleshooting

### Common Issues

#### 1. Database Connection Error
```
Exception: com.mysql.jdbc.exceptions.jdbc4.CommunicationsException: 
Communications link failure
```
**Solution:**
- Verify MySQL is running: `mysql -u root -p`
- Check credentials in `application.properties`
- Ensure database exists: `SHOW DATABASES;`

#### 2. JWT Token Expired/Invalid
```
Exception: io.jsonwebtoken.ExpiredJwtException: JWT claims set to an error
```
**Solution:**
- Re-login to get new token: `POST /api/v1/auth/login`
- Verify `application.security.jwt.expiration` value (in milliseconds)
- Check secret key matches between generation and validation

#### 3. Stripe Payment Failure
```
Exception: com.stripe.exception.InvalidRequestException: 
No such payment_method
```
**Solution:**
- Verify `stripe.api.key` is set correctly (use test key for dev)
- Check `stripePaymentMethodId` is valid: `pm_card_visa`, `pm_card_mastercard`, etc.
- For real card testing: use Stripe test card numbers (4242 4242 4242 4242)

#### 4. 403 Forbidden on Admin Endpoints
```
Exception: org.springframework.security.access.AccessDeniedException: 
Access is denied
```
**Solution:**
- Verify user has `ROLE_ADMIN` in database: `SELECT roles FROM users_tb WHERE email='admin@example.com';`
- Check JWT token includes correct roles
- Confirm `@PreAuthorize` decorator on controller method

#### 5. Build Failure - Dependency Issues
```
[ERROR] Failed to execute goal on project eco-bazaar-x-v2:
Could not resolve dependencies
```
**Solution:**
```bash
# Clear Maven cache and retry
mvn clean install -U

# Or check for network issues
mvn dependency:resolve
```

### Debug Mode
Enable verbose logging:
```properties
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.security.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

Then run:
```bash
mvn spring-boot:run -Dlogging.level.root=DEBUG
```

---

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. **Fork the repository** on GitHub
2. **Create a feature branch:** `git checkout -b feature/your-feature-name`
3. **Commit changes:** `git commit -m "Add detailed feature description"`
4. **Push to branch:** `git push origin feature/your-feature-name`
5. **Open a Pull Request** with a clear description

### Code Style Guidelines
- Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Use meaningful variable names and comments
- Add unit tests for new features (minimum 80% coverage)
- Update documentation as needed

---

## 📞 Support & Contact

### Getting Help
- **Issues:** Report bugs via [GitHub Issues](https://github.com/EcoBazaarX-Batch-4/ecobazaax-backend/issues)
- **Discussions:** Ask questions in [GitHub Discussions](https://github.com/EcoBazaarX-Batch-4/ecobazaax-backend/discussions)
- **Documentation:** See `DOCS/` folder for detailed guides

### Team
- **Project Lead:** EcoBazaarX Team
- **Repository:** [ecobazaax-backend](https://github.com/EcoBazaarX-Batch-4/ecobazaax-backend)

### Additional Resources
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Stripe API Reference](https://stripe.com/docs/api)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [JWT.io](https://jwt.io/) - JWT Debugger

---

## 📄 License

This project is licensed under the **MIT License**. See [LICENSE](LICENSE) file for details.

---

## 🌟 Acknowledgments

- Built with [Spring Framework](https://spring.io/)
- Payment processing by [Stripe](https://stripe.com/)
- Database by [MySQL](https://www.mysql.com/)
- Inspired by sustainable e-commerce best practices

---

**Last Updated:** December 2, 2024  
**Version:** 2.0.0-SNAPSHOT  
**Maintained by:** EcoBazaarX Development Team

---

### 🚀 Quick Start One-Liner (After Prerequisites)
```bash
git clone https://github.com/EcoBazaarX-Batch-4/ecobazaax-backend.git && \
cd eco-bazaar-x-v2 && \
mvn clean install && \
mvn spring-boot:run
```

Then visit: `http://localhost:8080/api/v1/products` ✨

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
