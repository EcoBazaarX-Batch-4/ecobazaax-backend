# EcoBazaarX Backend — Detailed Documentation

## Table of contents

- Overview
- Architecture and modules
- Data model overview
- API reference (summary of endpoints and authentication)
- DTO / payload notes and example schemas
- Configuration (secure — no secrets in repo)
- Local development
- Build, test, and run (Windows PowerShell)
- Deployment guide (container, Kubernetes, cloud pointers)
- Observability, logging & monitoring
- Security & secret handling (summary + links)
- Troubleshooting
- Maintenance and operational tasks
- Appendices: Postman, OpenAPI, Useful commands

---

## Overview

- Project: EcoBazaarX backend
- Purpose: Provide REST APIs for a multi-role e-commerce platform (Customers, Sellers, Admins) covering product catalogue, cart, checkout (Stripe), profiles, seller onboarding, admin configuration, recommendations, tracking, gamification, and insights.
- Base API path: `/api/v1`
- Main Java package: `com.ecobazaarx.v2`

## Architecture and modules

This is a modular monolithic Spring Boot application following layered architecture:

- Controllers: HTTP endpoints (package `controller`). Each controller groups related endpoints (Auth, Product, Cart, Checkout, Profile, Seller, Admin, Insights, Recommendations, Gamification, Leaderboard, Tracking, Wishlist, Address, OrderHistory).
- Services: Business logic and orchestration (package `service`). Services call repositories, 3rd-party clients (Stripe), and other services.
- Repositories / Entities: Persistence with Spring Data JPA (packages `repository`, `model`).
- DTOs: Request and response classes (package `dto`). Controllers accept DTOs; services return DTOs to decouple persistence model.
- Security: JWT auth implemented via `JwtAuthenticationFilter`, `JwtService` and `SecurityConfig`. Role-based authorization via `@PreAuthorize`.
- Initialization: `DataInitializer` seeds demo data for development.

Design notes:

- Pagination: Most list endpoints use Spring `Pageable` for page/size/sort.
- Authorization: Role checks are applied in controllers using `@PreAuthorize`.
- External integrations: Stripe for payments. No webhook endpoints are present by default; add if required.

## Data model overview

Key domain concepts (entity names approximate):

- User (roles: CUSTOMER, SELLER, ADMIN)
- Product, Category, Material, ManufacturingProcess, PackagingMaterial
- Cart and CartItem
- Order and OrderItem, PaymentRecord
- Address
- Review
- Discount, TaxRate, TransportZone
- SellerApplication, PayoutDetails
- EcoPointLedger (for gamification)

For full field-level definitions, inspect `src/main/java/com/ecobazaarx/v2/dto` and `model` packages. 

## API reference (summary)

All endpoints are mounted under `/api/v1`. Below is a grouped summary of controllers and their main routes. For exact DTO shapes, see `src/main/java/com/ecobazaarx/v2/dto`.

- Authentication: `/api/v1/auth`
	- POST `/register` — register a new user (returns authentication token)
	- POST `/login` — login (returns authentication token)

- Products: `/api/v1/products`
	- GET `/` — list products (pagination)
	- GET `/{id}` — get product details
	- GET `/search` — search with filters
	- POST `/{id}/reviews` — add review (CUSTOMER)
	- GET `/{id}/reviews` — get reviews
	- GET `/{id}/related` — related products

- Cart: `/api/v1/cart` (CUSTOMER)
	- POST `/add` — add product to cart
	- GET `/` — get cart for user
	- PUT `/update/{itemId}` — update item quantity
	- DELETE `/remove/{itemId}` — remove item
	- POST `/apply-discount` — apply discount
	- GET `/shipping-options/{addressId}` — shipping options
	- POST `/select-shipping` — select shipping option

- Checkout: `/api/v1/checkout` (CUSTOMER)
	- POST `/` — place order (integrates with Stripe)

- Profile: `/api/v1/profile`
	- GET `/me` — profile
	- PUT `/me` — update profile
	- PUT `/change-password` — change password
	- Addresses: `/api/v1/profile/addresses` (CRUD, CUSTOMER)
	- Wishlist: `/api/v1/profile/wishlist` (GET, POST, DELETE)
	- Eco-points history: `/api/v1/profile/eco-points-history` (CUSTOMER)

- Order History: `/api/v1/profile/orders` (CUSTOMER)
	- GET `/` — list orders
	- GET `/{id}` — order details

- Seller: `/api/v1/seller`
	- POST `/apply` — apply to be seller (CUSTOMER)
	- GET `/application-status` — check status
	- Product management (SELLER): POST `/products`, GET `/products`, PUT `/products/{id}`, DELETE `/products/{id}`
	- Store profile: PUT `/profile`
	- Payout details: GET/PUT `/payout-details`

- Admin: `/api/v1/admin` (ADMIN)
	- Manage categories, materials, manufacturing processes, packaging materials, transport zones
	- Manage users: GET `/users`, PUT `/users/{userId}`, POST `/create-admin`
	- Manage products (admin view): GET `/products`
	- Manage orders: GET `/orders`, PUT `/orders/{id}/status`
	- Taxes & discounts: `/config/taxes`, `/config/discounts`

- Insights: `/api/v1/insights`
	- `/profile` (CUSTOMER), `/seller` (SELLER), `/admin` (ADMIN)
	- CSV exports: `/seller/export-sales` (SELLER), `/admin/export-all` (ADMIN)

- Recommendations & Tracking:
	- `/api/v1/recommendations/homepage` — homepage recommendations
	- `/api/v1/recommendations/me/recent` — recent views (CUSTOMER)
	- `/api/v1/tracking/view/{productId}` — log view (CUSTOMER)

- Leaderboard: `/api/v1/leaderboard` — global and by-level endpoints

Authentication/Authorization notes:

- Login/register endpoints are public. Protected endpoints require `Authorization: Bearer <JWT>` header.
- Roles are enforced with `@PreAuthorize` in controllers. Ensure the token contains the proper role claim.

## DTO / payload notes and example schemas

The codebase uses DTOs in `src/main/java/com/ecobazaarx/v2/dto`. Examples of important DTOs:

- `LoginRequest`, `RegisterRequest` — auth payloads
- `ProductResponseDto`, `ProductCreateRequest` — product data
- `AddToCartRequest`, `CartResponse`, `UpdateCartRequest` — cart
- `CheckoutRequest`, `CheckoutResponse` — checkout
- `AddressDto`, `ProfileResponse`, `ProfileUpdateRequest`
- `OrderDto`, `ReviewRequest`, `ReviewResponse`

If you need, I can generate JSON Schema files for every DTO and add them to `DOCS/schemas/`.

## Configuration (secure)

Never put secrets in `application.properties` in the repository. Instead:

- Use placeholders that read environment variables, for example:

```
application.security.jwt.secret-key=${APPLICATION_SECURITY_JWT_SECRET_KEY}
stripe.api.key=${STRIPE_API_KEY}
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
```


## Local development

Prerequisites:

- Java (check `pom.xml` for required JDK version — commonly Java 17+).
- Maven (the project includes the Maven wrapper `mvnw.cmd`).
- MySQL (or an alternative DB configured via `SPRING_DATASOURCE_URL`).
- Optional: Stripe test keys for testing checkout flows.

Steps:

1. Copy `DOCS/.env.example` to a local `.env` or set environment variables in your shell. Do NOT commit `.env`.

2. Create the database specified in `SPRING_DATASOURCE_URL` (example `ecobazaarx`).

3. Start application (PowerShell example):

```powershell
$env:SPRING_DATASOURCE_URL = 'jdbc:mysql://localhost:3306/ecobazaarx'
$env:SPRING_DATASOURCE_USERNAME = 'root'
$env:SPRING_DATASOURCE_PASSWORD = 'mysql'
$env:APPLICATION_SECURITY_JWT_SECRET_KEY = 'local-dev-secret'
$env:STRIPE_API_KEY = 'sk_test'
.\mvnw.cmd spring-boot:run
```

4. Seed data: the application may include a `DataInitializer` to create demo data on startup.

Future recommendations:

- Add `@Valid` validation and `@ControllerAdvice` for consistent API errors.
- Add integration tests for checkout and admin user flows.
- Integrate OpenAPI for machine-readable API docs and client SDK generation.
- Introduce DB migrations (Flyway/Liquibase) if not present.
- Add secret scanning (pre-commit hook) to prevent accidental secret commits.

---

