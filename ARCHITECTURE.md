# EstateHub Backend — Architecture Technique

> **Stack :** Spring Boot 3.4.3 · Java 17 · MySQL 8 · Hibernate 7 · JWT (JJWT 0.11.5) · MapStruct 1.5.5 · Lombok  
> **Dernière mise à jour :** Mars 2026

---

## 1. Vue d'ensemble de l'architecture

Le backend suit une **architecture en couches** classique, avec une séparation stricte des responsabilités :

```
┌─────────────────────────────────────────────────────────────────┐
│                          CLIENT (Angular)                       │
│                      http://localhost:4200                       │
└─────────────────────────────┬───────────────────────────────────┘
                              │  HTTP (JSON)
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  SECURITY LAYER                                                 │
│  ┌───────────────────┐  ┌──────────────┐  ┌──────────────────┐ │
│  │ SecurityConfig    │  │ JwtAuthFilter │  │ JwtService       │ │
│  │ (CORS, routes)    │  │ (OncePerReq.) │  │ (sign/verify)    │ │
│  └───────────────────┘  └──────────────┘  └──────────────────┘ │
└─────────────────────────────┬───────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  CONTROLLERS          @RestController                           │
│  AuthController · BuildingController · UnitController           │
│  LeaseController · TestController                               │
└─────────────────────────────┬───────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  SERVICES             @Service                                  │
│  AuthService · BuildingService · UnitService                    │
│  LeaseService · RefreshTokenService                             │
│  (SecurityContextHolder pour l'utilisateur connecté)            │
└─────────────────────────────┬───────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  MAPPERS              MapStruct @Mapper                         │
│  UserMapper · BuildingMapper · UnitMapper · LeaseMapper         │
└─────────────────────────────┬───────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  REPOSITORIES         JpaRepository                             │
│  UserRepo · BuildingRepo · UnitRepo · LeaseRepo                 │
│  InvoiceRepo · AmenityRepo · RefreshTokenRepo                   │
└─────────────────────────────┬───────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     MySQL 8 (estatehub_db)                      │
└─────────────────────────────────────────────────────────────────┘
```

**Gestion des erreurs transversale :** Un `@RestControllerAdvice` (`GlobalExceptionHandler`) intercepte toutes les exceptions et retourne un `ErrorResponse` JSON standardisé.

---

## 2. Schéma de la base de données

### Entités et relations

```
                    ┌──────────────────┐
                    │      USER        │
                    │──────────────────│
                    │ id (PK)          │
                    │ email (UNIQUE)   │
                    │ password (BCrypt)│
                    │ firstName        │
                    │ lastName         │
                    │ role (ENUM)      │
                    │ isActive         │
                    │ createdAt        │
                    │ updatedAt        │
                    └──────┬───────────┘
                           │
            ┌──────────────┼──────────────────┐
            │ 1:N          │ 1:1              │ N:1
            ▼              ▼                  ▼
   ┌────────────────┐  ┌───────────────┐  ┌───────────┐
   │   BUILDING     │  │ REFRESH_TOKEN │  │   LEASE   │
   │────────────────│  │───────────────│  │───────────│
   │ id (PK)        │  │ id (PK)       │  │ id (PK)   │
   │ name           │  │ token (UNIQUE)│  │ startDate │
   │ address        │  │ expiryDate    │  │ endDate   │
   │ city           │  │ user_id (FK)  │  │ baseRent  │
   │ zipCode        │  └───────────────┘  │ utility   │
   │ landlord_id(FK)│                     │ status    │
   └──────┬─────────┘                     │ tenant_id │
          │                               │ unit_id   │
          │ 1:N                 N:1       └─────┬─────┘
          ▼                                     │
   ┌──────────────┐                             │ 1:N
   │    UNIT      │◄────────────────────────────┘
   │──────────────│
   │ id (PK)      │
   │ doorNumber   │         ┌──────────────┐
   │ floor        │         │   INVOICE    │
   │ building_id  │         │──────────────│
   └──────────────┘         │ id (PK)      │
          ▲                 │ issueDate    │
          │                 │ dueDate      │
   ┌──────────────┐         │ totalAmount  │
   │   AMENITY    │         │ status       │
   │──────────────│         │ reminderLevel│
   │ id (PK)      │         │ lease_id (FK)│
   │ name         │         └──────────────┘
   │ monthlyPrice │               ▲
   │ building_id  │               │ N:1
   └──────────────┘          (from LEASE)
```

### Relations résumées

| Relation | Type | Description |
|---|---|---|
| User → Building | **1:N** | Un propriétaire possède N immeubles |
| Building → Unit | **1:N** | Un immeuble contient N appartements |
| Building → Amenity | **1:N** | Un immeuble a N équipements |
| Unit → Lease | **1:N** | Un appartement peut avoir N baux |
| User → Lease | **1:N** | Un locataire peut avoir N baux |
| Lease → Invoice | **1:N** | Un bail génère N factures |
| User → RefreshToken | **1:1** | Un utilisateur = un refresh token |

### Enums

| Enum | Valeurs |
|---|---|
| `UserRole` | `LANDLORD`, `TENANT`, `ADMIN` |
| `LeaseStatus` | `DRAFT`, `ACTIVE`, `TERMINATED` |
| `InvoiceStatus` | `SENT`, `PAID`, `OVERDUE`, `AT_RISK` |

---

## 3. Endpoints de l'API REST

### 🔐 Authentification — `/api/auth`

| Méthode | Endpoint | Auth | Description | Body → Réponse |
|---|---|---|---|---|
| `POST` | `/api/auth/register` | ❌ Public | Inscription | `RegisterRequest` → `AuthResponse` |
| `POST` | `/api/auth/login` | ❌ Public | Connexion | `AuthRequest` → `AuthResponse` |
| `POST` | `/api/auth/refresh` | ❌ Public | Rafraîchir l'access token | `TokenRefreshRequest` → `TokenRefreshResponse` |
| `POST` | `/api/auth/logout` | ✅ JWT | Révoque le refresh token | — → `"Déconnexion réussie"` |

> `AuthResponse` retourne : `{ token, refreshToken, user: UserDTO }`

### 🏢 Immeubles — `/api/buildings`

| Méthode | Endpoint | Auth | Description | Body → Réponse |
|---|---|---|---|---|
| `POST` | `/api/buildings` | ✅ JWT | Créer un immeuble | `CreateBuildingRequest` → `BuildingDTO` |
| `GET` | `/api/buildings` | ✅ JWT | Lister mes immeubles | — → `List<BuildingDTO>` |

### 🏠 Appartements — `/api/units`

| Méthode | Endpoint | Auth | Description | Body → Réponse |
|---|---|---|---|---|
| `POST` | `/api/units` | ✅ JWT | Créer un appartement | `CreateUnitRequest` → `UnitDTO` |
| `GET` | `/api/units/building/{id}` | ✅ JWT | Lister les apparts d'un immeuble | — → `List<UnitDTO>` |

### 📝 Baux — `/api/leases`

| Méthode | Endpoint | Auth | Description | Body → Réponse |
|---|---|---|---|---|
| `POST` | `/api/leases` | ✅ JWT | Créer un bail | `CreateLeaseRequest` → `LeaseDTO` |
| `GET` | `/api/leases` | ✅ JWT | Lister mes baux | — → `List<LeaseDTO>` |

### 🧪 Test

| Méthode | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/api/test/hello` | ✅ JWT | Vérifier la validité du token |

---

## 4. Sécurité — Points forts

### Architecture Zero Trust

```
Requête HTTP
     │
     ▼
┌─────────────────────────────────────────────┐
│  1. CORS Filter                             │
│     → Autorise uniquement localhost:4200    │
├─────────────────────────────────────────────┤
│  2. JwtAuthenticationFilter                 │
│     → Extrait le Bearer token               │
│     → Valide la signature HMAC-SHA256       │
│     → Charge le UserDetails depuis la BDD   │
│     → Injecte dans SecurityContextHolder    │
├─────────────────────────────────────────────┤
│  3. SecurityFilterChain                     │
│     → /api/auth/** = public                 │
│     → Tout le reste = authenticated         │
│     → Sessions STATELESS, CSRF désactivé    │
├─────────────────────────────────────────────┤
│  4. Service Layer                           │
│     → Vérification de propriété sur chaque  │
│       ressource (Building, Unit, Lease)     │
└─────────────────────────────────────────────┘
```

### Mécanismes en place

| Mécanisme | Détail |
|---|---|
| **Mots de passe** | Encodés avec `BCryptPasswordEncoder` (jamais stockés en clair) |
| **Access Token (JWT)** | Durée de vie courte (1 min), signé HMAC-SHA256, transporté via header `Authorization: Bearer` |
| **Refresh Token** | UUID stocké en BDD, durée de vie 7 jours, 1 par utilisateur, révocable |
| **Logout réel** | Suppression du refresh token en BDD (pas juste côté client) |
| **Ownership check** | Chaque service vérifie que la ressource appartient bien au `landlordEmail` extrait du `SecurityContextHolder` |
| **Sessions** | `STATELESS` — aucun état côté serveur entre les requêtes |
| **CORS** | Restreint à `http://localhost:4200` avec credentials |

### Gestion des erreurs standardisée

Toutes les erreurs retournent un `ErrorResponse` JSON :

```json
{
  "timestamp": "2026-03-02T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Building avec l'ID 99 introuvable",
  "path": "/api/buildings"
}
```

| Exception | Code HTTP | Quand |
|---|---|---|
| `ResourceNotFoundException` | `404` | Entité introuvable en BDD |
| `BusinessValidationException` | `400` | Règle métier violée (email dupliqué, accès interdit au propriétaire) |
| `TokenRefreshException` | `403` | Refresh token expiré ou introuvable |
| `MethodArgumentNotValidException` | `400` | Validation `@Valid` échouée (+ map `champ → message`) |
| `BadCredentialsException` | `401` | Login/mot de passe incorrect |
| `AccessDeniedException` | `403` | Droits insuffisants |
| `Exception` (catch-all) | `500` | Erreur inattendue (message générique, vraie erreur loggée) |

---

## 5. Données de test (Seed)

Le `DatabaseSeeder` insère automatiquement au premier lancement :

| Donnée | Détails |
|---|---|
| **Landlord** | `landlord@test.com` / `password123` (rôle `LANDLORD`) |
| **Tenant** | `tenant@test.com` / `password123` (rôle `TENANT`) |
| **Building** | "Résidence Les Lilas" — 123 Rue de la Paix, 75001 Paris |
| **Amenity** | "Parking Souterrain" (prix: 0€) |
| **Units** | Appart 101 (1er étage) + Appart 102 (1er étage) |
| **Lease** | Bail actif sur l'appart 101, loyer 800€ + charges 50€ = **850€/mois** |
| **Invoice** | Facture de 850€ (statut `SENT`) |

---

## 6. Ce qui reste à implémenter

| Fonctionnalité | Statut |
|---|---|
| Services/Controllers pour **Invoice** et **Amenity** | ❌ Non câblé |
| Contrôle d'accès par rôle (`@PreAuthorize`) | ❌ Non implémenté |
| Opérations **UPDATE / DELETE** | ❌ Non implémenté |
| **Pagination** sur les endpoints de liste | ❌ Non implémenté |
| Annotations `@Valid` sur les DTOs d'entrée | ❌ Non ajoutées (handler prêt) |
| Externaliser la **secret key JWT** (env variable) | ❌ Hardcodé |
| Nettoyage des refresh tokens expirés (cron) | ❌ Non implémenté |

