# EstateHub Backend - Complete Setup & Execution Guide

## Quick Start (2 steps)

### Step 1: Navigate to Backend Folder
```bash
cd EstateHub/backend
```

### Step 2: Start Everything
```bash
docker compose up -d --build
```

**That's it!** The application should be running within 15-20 seconds.

---

## Verify It's Working

### Check Containers Are Running
```bash
docker ps
```

Should show:
```
estatehub-backend    (Port 8080) - Running
estatehub-mysql      (Port 3306) - Healthy
```

### Test Admin Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@test.com","password":"admin123"}'
```

**Success Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "91fd3884-c1e3...",
  "user": {
    "id": 1,
    "email": "admin@test.com",
    "role": "ADMIN"
  }
}
```

---

## Test Credentials (Already Seeded)

### Administrators
- **Email:** `admin@test.com`
- **Password:** `admin123`
- **Access:** All users, all data

### Landlords (Property Owners)
1. **Jean Proprio**
   - Email: `landlord@test.com`
   - Password: `password123`
   - Owns: 2 buildings in Paris & Lyon

2. **Marie Immobilière**
   - Email: `landlord2@test.com`
   - Password: `password123`
   - Owns: 1 building in Marseille

### Tenants (Renters)
1. **Alice Locataire**
   - Email: `tenant@test.com`
   - Password: `password123`
   - Lease: Unit T2 (€750/month)

2. **Bob Locataire**
   - Email: `tenant2@test.com`
   - Password: `password123`
   - Lease: Unit T3 (€950/month)

3. **Sophie Locataire**
   - Email: `tenant3@test.com`
   - Password: `password123`
   - Lease: Unit T2 (€850/month)

---

## Database Auto-Seeding

When the application starts, it automatically inserts test data:

- **Users:** 1 Admin + 2 Landlords + 3 Tenants
- **Buildings:** 3 rental properties
- **Units:** 10 rental units (STUDIO, T1-T6)
- **Amenities:** 6 building features (parking, gym, pool, etc.)
- **Leases:** 4 active and terminated leases
- **Invoices:** 7 invoices with multiple statuses
  - ✅ PAID (3 invoices)
  - 📤 SENT (2 invoices)
  - ⚠️ OVERDUE (1 invoice)
  - ⏳ AT_RISK (1 invoice)

**File:** `src/main/java/com/estatehub/backend/bootstrap/DatabaseSeeder.java`

---

## View Seeding Output in Logs

```bash
docker logs estatehub-backend | grep -i "initialisation\|données\|succès"
```

Expected output:
```
🌱 Initialisation des données de test (Seeding)...
✅ Données de test insérées avec succès !
   - 6 utilisateurs (1 Admin, 2 Landlords, 3 Tenants)
   - 3 bâtiments avec 10 unités
   - 6 commodités
   - 4 baux (3 actifs, 1 terminé)
   - 7 factures avec différents statuts
```

---

## Docker Compose Details

**File:** `docker-compose.yml`

### Services
- **estatehub-backend** (Spring Boot App)
  - Port: `8080`
  - Image: `backend-app:latest`
  - Depends on: MySQL
  - Auto-seeding: Yes

- **estatehub-mysql** (Database)
  - Port: `3306`
  - Image: `mysql:8.0`
  - Database: `estatehub_db`
  - Volume: `mysql_data` (persisted)

### Volumes
- `mysql_data` → `/var/lib/mysql` (stores database files)

### Networks
- `backend_default` (internal Docker network)

---

## Stopping the Application

### Stop All Containers (Keep Data)
```bash
docker compose down
```

### Stop & Delete All Data (Fresh Reset)
```bash
docker compose down -v
```

---

## Troubleshooting

### "Connection refused" Error
**Issue:** Port 8080 is already in use

**Solution:**
```bash
docker kill estatehub-backend
docker compose up -d --build
```

### "MySQL connection failed"
**Issue:** Database not ready

**Solution:** Wait 10-15 seconds for MySQL healthcheck to pass
```bash
docker compose ps
# Wait for "Healthy" status on mysql container
```

### "No test data" after startup
**Issue:** Database volume not deleted

**Solution:** Clear and restart
```bash
docker compose down -v
docker compose up -d --build
```

### View Full Application Logs
```bash
docker logs estatehub-backend -f
```

### View Database Logs
```bash
docker logs estatehub-mysql -f
```

---

## Architecture Components

### API Layers
```
HTTP Requests
    ↓
Controllers (REST Endpoints)
    ↓
JwtAuthenticationFilter (Security)
    ↓
ServiceLayer (Business Logic)
    ↓
Repositories (Spring Data JPA)
    ↓
MySQL Database
```

### Main Endpoints

| Method | Endpoint | Role | Purpose |
|--------|----------|------|---------|
| POST | /api/auth/login | ALL | Login & get JWT token |
| GET | /api/buildings | LANDLORD | List landlord's buildings |
| GET | /api/leases | ALL | List leases |
| GET | /api/invoices | ALL | List invoices for user |
| GET | /api/invoices/my-unpaid | TENANT | Unpaid invoices |
| PATCH | /api/invoices/{id}/pay | TENANT | Mark invoice as paid |
| GET | /api/stats/summary | LANDLORD | Revenue & occupancy stats |
| GET | /api/admin/users | ADMIN | View all users |

---

## Deployment Methods

### Option 1: Docker Compose (Recommended)
```bash
docker compose up -d --build
```
✅ Easiest | ✅ Production-ready | ✅ Full CI/CD

### Option 2: Manual Maven Build
```bash
mvn clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
# Requires MySQL running separately at localhost:3306
```

### Option 3: Cloud Deployment (Not included)
- AWS ECS / Lambda
- Google Cloud Run
- Azure Container Registry
- Deploy via GitHub Actions (CI/CD pipeline)

---

## Configuration Files

- **pom.xml** - Maven dependencies (Spring Boot 3.4.3, Java 17)
- **application.properties** - Database URL, JWT secret, CORS settings
- **Dockerfile** - Multi-stage Docker build (Maven → JRE)
- **docker-compose.yml** - App + MySQL orchestration
- **.github/workflows/build-test.yml** - CI pipeline

---

## Development Workflow

### Local Development (Without Docker)
```bash
# Terminal 1: Start MySQL
mysql -u root -p
CREATE DATABASE estatehub_db;

# Terminal 2: Run Spring Boot
mvn spring-boot:run
```

### Testing
```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=RefreshTokenServiceTest
```

### Building for Production
```bash
# Build JAR
mvn clean package

# View JAR
ls -lah target/backend-0.0.1-SNAPSHOT.jar

# Build Docker image
docker build -t my-estatehub:latest .

# Push to registry
docker push my-estatehub:latest
```

---

## Next Steps

1. **For Jury Presentation:** See [JURY_PRESENTATION_GUIDE.md](./JURY_PRESENTATION_GUIDE.md)
2. **For Class Diagram:** See [CLASS_DIAGRAM.mmd](./CLASS_DIAGRAM.mmd)
3. **For Architecture:** See [ARCHITECTURE.md](./ARCHITECTURE.md)
4. **For API Testing:** Use [EstateHub.postman_collection.json](./EstateHub.postman_collection.json)

---

**Status:** ✅ Ready for Jury Presentation  
**Last Updated:** March 30, 2026  
**Tested:** Yes - All endpoints working  
**Database:** Auto-seeded with realistic test data
