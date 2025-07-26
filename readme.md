
# 👞 Rizo Shoe Shop - Backend Implementation Report

This project aims to digitize the previously manual management system of **Rizo Shoe Shop**, covering core business processes such as sales, inventory, employees, suppliers, and customers. The backend solution is implemented as a modern web-based enterprise application using the **Spring Framework**.

---

## 🧱 Project Setup and Technology Stack

### 🔧 Technology Stack

- **Language**: Java 17
- **Frameworks & Libraries**: Spring Boot 3.3.1, Spring Web MVC, Spring Data JPA, Spring Security, Spring Validation
- **Utilities**: Lombok, ModelMapper, Jackson
- **Database**: MySQL (via XAMPP/MariaDB)
- **Build Tool**: Gradle (Kotlin DSL)

### 🧩 Architecture

The application follows a multi-layered architecture:
- **API Layer**: Controller classes
- **Business Layer**: Service classes
- **Persistence Layer**: Repository interfaces

The project is organized per concern:
- `controller`, `service`, `repository`, `entity`, `config`, `exception`, `util`
- DTOs are grouped per entity: `dto/customer`, `dto/employee`, `dto/product`, `dto/sale`, `dto/supplier`, `dto/user`

---

## 🔍 Core Functional Modules

### 1. Supplier Management
- **Purpose**: Maintain supplier details including contact and full location.
- **Components**: `Supplier`, `SupplierRepository`, DTOs, `SupplierService`, `SupplierController`
- **Status**: Full CRUD implemented and tested.

### 2. Customer Management
- **Purpose**: Store customer profiles, loyalty level, purchase history, and DOB for birthday greetings.
- **Components**: `Customer`, `CustomerRepository`, DTOs, `CustomerService`, `CustomerController`
- **Features**: Loyalty logic and points tracking implemented.

### 3. Employee Management
- **Purpose**: Maintain employee records per branch, with role distinction (Admin vs User).
- **Components**: `Employee`, `EmployeeRepository`, DTOs, `EmployeeService`, `EmployeeController`
- **Features**: Search by branch and role.

### 4. Inventory Management
- **Purpose**: Manage product stocks, auto-update on sale, low stock warning, display product previews.
- **Components**: `Product`, `ProductRepository`, DTOs, `ProductService`, `ProductController`
- **Features**: Full CRUD, sorting, searching, low-stock alerts.

### 5. Sales Management
- **Purpose**: Handle product sales with customer and payment details (cash/card). Support refund requests (within 3 days, with receipt) and loyalty point tracking.
- **Components**: `Sale`, `SaleItem`, `Refund`, DTOs, `SaleService`, `SaleController`
- **Features**: Stock validation and deduction, total calculation, loyalty points, refund logic, dummy card payment handling.

### 6. Admin Dashboard (Backend Side)
- **Purpose**: Display total sales, profits, and top-selling items with sales trends and graphical summaries.
- **Components**: `AdminDashboardSummaryDTO`, `TopSellingItemDTO`, `SalesTrendDTO`, `AdminDashboardService`, `AdminDashboardController`
- **Features**: Aggregated summary APIs for dashboards.

### 7. User Management
- **Purpose**: Manage user accounts with secure login and access control (Admin/User roles).
- **Components**: `User`, `UserRepository`, DTOs, `AuthController`, `UserController`, `UserService`
- **Security**: Spring Security + JWT-based stateless authentication. Passwords are securely hashed.

---

## ⚙️ Non-Functional Requirements and Code Enhancements

- **Global Exception Handling**: Centralized using `@ControllerAdvice` (GlobalExceptionHandler) with custom exceptions like `ResourceNotFoundException`, `BadRequestException` for consistent error responses.
- **Comprehensive Logging**: Implemented across service and controller layers for operation tracking and debugging.
- **CORS Configuration**: Enabled via Spring Security to allow communication between front-end and back-end across origins.
- **Server-side Validation**: Done via `jakarta.validation` annotations in DTOs.
- **Model Mapping**: Handled using `ModelMapper`, with custom config and `@EntityGraph` to avoid `LazyInitializationException` for lazy-loaded relationships.

---

## ✅ Status

All core modules listed above have been successfully implemented, tested, and integrated into the backend system.

This backend is ready to be connected to a front-end client or integrated into a full-stack deployment.

