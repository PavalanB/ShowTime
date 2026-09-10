# 🎬 CineMesh: Distributed Multi-Tenant Movie Ticketing SaaS Using SOAP and Microsoft Azure

CineMesh is a cloud-native, multi-tenant Software-as-a-Service (SaaS) platform providing movie ticket booking and theatre management services to multiple independent theatres through a distributed network of services communicating primarily using **SOAP Web Services with WSDL contracts**, engineered for deployment across **Microsoft Azure Virtual Network (VNet)**.

---

## 🚀 How to Run the Project

### Method 1: One-Click Startup (Recommended)
Simply double-click:
```cmd
run.bat
```
or run in PowerShell:
```powershell
.\run.ps1
```

### Method 2: Maven Command Line
From the project root:
```cmd
.\mvn.cmd spring-boot:run
```

Once started, open your browser and navigate to:
👉 **[http://localhost:8080](http://localhost:8080)**

---

## 🏛️ System Architecture

### 1. Multi-Tenancy & Tenant Isolation
- Each theatre acts as an independent tenant (e.g., **PVR Grand Galada Chennai**, **INOX Megaplex Mumbai**, **Cinepolis Bengaluru**).
- Theatre owners log in to manage their own screens, show schedules, seat layouts, and independent pricing models:
  - *Example*: Movie **"Leo"** at **PVR Chennai** (6:00 PM) costs **₹220 (Standard) / ₹320 (Premium) / ₹400 (VIP)**.
  - *Example*: Movie **"Leo"** at **INOX Mumbai** (8:00 PM) costs **₹300 (Standard) / ₹420 (Premium) / ₹550 (VIP)**.
- Data queries, bookings, and revenue metrics are strictly isolated per tenant.

### 2. SOAP Web Services (12 WSDL Contracts)
All core services communicate via SOAP XML contracts defined by XML Schemas:
| Service | WSDL URL | Operations |
| :--- | :--- | :--- |
| **AuthService** | `http://localhost:8080/ws/auth.wsdl` | `LoginRequest`, `RegisterRequest`, `ValidateTokenRequest` |
| **TenantService** | `http://localhost:8080/ws/tenants.wsdl` | `CreateTenantRequest`, `GetTenantRequest`, `ListTenantsRequest` |
| **TheatreService** | `http://localhost:8080/ws/theatres.wsdl` | `CreateScreenRequest`, `GetScreensByTenantRequest` |
| **MovieService** | `http://localhost:8080/ws/movies.wsdl` | `AddMovieRequest`, `GetAllMoviesRequest` |
| **ShowService** | `http://localhost:8080/ws/shows.wsdl` | `ScheduleShowRequest`, `GetShowsByTenantRequest`, `GetShowDetailsRequest` |
| **SeatService** | `http://localhost:8080/ws/seats.wsdl` | `GetSeatLayoutRequest`, `CheckSeatAvailabilityRequest` |
| **CoordinationService** | `http://localhost:8080/ws/coordination.wsdl` | `AcquireSeatLockRequest`, `ReleaseSeatLockRequest`, `ValidateSeatLockRequest` |
| **BookingService** | `http://localhost:8080/ws/booking.wsdl` | `CreateBookingRequest`, `ConfirmBookingRequest`, `GetBookingRequest` |
| **PaymentService** | `http://localhost:8080/ws/payment.wsdl` | `ProcessPaymentRequest` |
| **TicketService** | `http://localhost:8080/ws/ticket.wsdl` | `GenerateTicketRequest`, `ValidateTicketRequest` |
| **NotificationService** | `http://localhost:8080/ws/notification.wsdl` | `SendBookingNotificationRequest` |
| **AnalyticsService** | `http://localhost:8080/ws/analytics.wsdl` | `GetTenantAnalyticsRequest` |

You can test any of these services directly in the built-in **SOAP WSDL Explorer** tab at `http://localhost:8080`.

---

## 🔒 Concurrency Control & Distributed Seat Consistency
The central technical problem:
> *How can a multi-tenant cloud-based ticketing platform maintain secure, consistent, and real-time seat availability when multiple customers and distributed services simultaneously access and modify the same theatre booking system?*

### Solution Mechanism:
1. **Temporary Seat Locking (`HELD` state)**:
   - When a user selects a seat, `CoordinationService` executes an atomic conditional database update:
     ```sql
     UPDATE show_seats SET status = 'HELD', locked_by_user_id = :userId, lock_token = :token, lock_expires_at = :exp
     WHERE show_id = :showId AND seat_number IN (:seats)
     AND (status = 'AVAILABLE' OR (status = 'HELD' AND lock_expires_at < :now))
     ```
   - If multiple requests race for the exact same seat, the atomic lock query grants access to **strictly one winner**.
   - An optimistic locking `@Version` prevents lost updates.
2. **5-Minute Hold TTL & Background Lock Reaper**:
   - Every lock has a 300-second (5 minute) TTL with a countdown timer visible in the UI.
   - `SeatLockReaper` sweeps every 5 seconds to reset expired holds back to `AVAILABLE`.
3. **Rollback on Payment Failure**:
   - If payment gateway returns a failure or is cancelled, seats immediately revert to `AVAILABLE`.
4. **Real-time Synchronization**:
   - WebSocket broadcast (`/topic/seats/{showId}`) updates other connected users immediately without page refreshes.

---

## ☁️ Microsoft Azure Multi-VM Cloud Architecture
- **Virtual Network (VNet)**: `vnet-cinemesh-prod` (10.0.0.0/16)
- **Subnets & VM Topology**:
  - `snet-gateway` (`10.0.1.0/24`): **vm-gateway** (Port 8080) - Public Reverse Proxy, Web UI, WebSocket
  - `snet-identity` (`10.0.2.0/24`): **vm-identity** (Port 8081) - Authentication & Tenant Management
  - `snet-core` (`10.0.3.0/24`): **vm-catalog** (Port 8082) - Theatre, Movie, Show Scheduling
  - `snet-core` (`10.0.3.0/24`): **vm-booking** (Port 8083) - Seat Service & Coordination Lock Node
  - `snet-fulfillment` (`10.0.4.0/24`): **vm-fulfillment** (Port 8084) - Payment, Ticket, Notification, Analytics
- **Azure Infrastructure as Code**:
  - `azure/bicep/main.bicep`: Complete Azure Bicep specification
  - `azure/arm/azuredeploy.json`: Azure Resource Manager template
  - `azure/scripts/deploy-azure-vms.ps1`: Automated Azure CLI deployment script
  - `docker-compose.yml`: Simulates the multi-VM Azure VNet locally

---

## 🧪 Testing Concurrency
To run the automated 50-thread concurrent booking stress test:
```cmd
.\mvn.cmd test -Dtest=ConcurrencyBookingStressTest
```
This spawns 50 threads firing at the exact same millisecond against the identical seats, asserting that **strictly 1 succeeds and 49 fail**, verifying zero double-booking.

---

## 🔑 Pre-Configured Demo Credentials
| Role | Username | Password | Tenant Assigned |
| :--- | :--- | :--- | :--- |
| **Platform Admin** | `admin` | `admin123` | Global Platform |
| **Theatre Owner (PVR)** | `owner_pvr` | `owner123` | `pvr-chennai` |
| **Theatre Owner (INOX)**| `owner_inox`| `owner123` | `inox-mumbai` |
| **Ticket Validator** | `val_pvr` | `val123` | `pvr-chennai` |
| **Customer** | `customer1` | `cust123` | - |
