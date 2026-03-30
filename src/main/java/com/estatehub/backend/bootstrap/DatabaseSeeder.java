package com.estatehub.backend.bootstrap;

import com.estatehub.backend.models.*;
import com.estatehub.backend.models.enums.*;
import com.estatehub.backend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BuildingRepository buildingRepository;
    private final UnitRepository unitRepository;
    private final AmenityRepository amenityRepository;
    private final LeaseRepository leaseRepository;
    private final InvoiceRepository invoiceRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            System.out.println("🌱 Initialisation des données de test (Seeding)...");

            // ========== 1. UTILISATEURS ==========
            User admin = User.builder()
                    .firstName("Admin")
                    .lastName("System")
                    .email("admin@test.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(UserRole.ADMIN)
                    .isActive(true)
                    .build();

            User landlord1 = User.builder()
                    .firstName("Jean")
                    .lastName("Proprio")
                    .email("landlord@test.com")
                    .password(passwordEncoder.encode("password123"))
                    .role(UserRole.LANDLORD)
                    .isActive(true)
                    .build();

            User landlord2 = User.builder()
                    .firstName("Marie")
                    .lastName("Immobilière")
                    .email("landlord2@test.com")
                    .password(passwordEncoder.encode("password123"))
                    .role(UserRole.LANDLORD)
                    .isActive(true)
                    .build();

            User tenant1 = User.builder()
                    .firstName("Alice")
                    .lastName("Locataire")
                    .email("tenant@test.com")
                    .password(passwordEncoder.encode("password123"))
                    .role(UserRole.TENANT)
                    .isActive(true)
                    .build();

            User tenant2 = User.builder()
                    .firstName("Bob")
                    .lastName("Locataire")
                    .email("tenant2@test.com")
                    .password(passwordEncoder.encode("password123"))
                    .role(UserRole.TENANT)
                    .isActive(true)
                    .build();

            User tenant3 = User.builder()
                    .firstName("Sophie")
                    .lastName("Locataire")
                    .email("tenant3@test.com")
                    .password(passwordEncoder.encode("password123"))
                    .role(UserRole.TENANT)
                    .isActive(true)
                    .build();

            userRepository.saveAll(List.of(admin, landlord1, landlord2, tenant1, tenant2, tenant3));

            // ========== 2. BÂTIMENTS ==========
            Building building1 = Building.builder()
                    .name("Résidence Les Lilas")
                    .address("123 Rue de la Paix")
                    .city("Paris")
                    .zipCode("75001")
                    .landlord(landlord1)
                    .build();

            Building building2 = Building.builder()
                    .name("Immeuble Les Étoiles")
                    .address("456 Avenue des Champs")
                    .city("Lyon")
                    .zipCode("69000")
                    .landlord(landlord1)
                    .build();

            Building building3 = Building.builder()
                    .name("Résidence Moderne")
                    .address("789 Boulevard Saint-Germain")
                    .city("Marseille")
                    .zipCode("13000")
                    .landlord(landlord2)
                    .build();

            buildingRepository.saveAll(List.of(building1, building2, building3));

            // ========== 3. COMMODITÉS ==========
            List<Amenity> amenities = List.of(
                    Amenity.builder().name("Parking Souterrain").monthlyPrice(new BigDecimal("50.00")).building(building1).build(),
                    Amenity.builder().name("Ascenseur").building(building1).build(),
                    Amenity.builder().name("Salle de Gym").monthlyPrice(new BigDecimal("30.00")).building(building1).build(),
                    Amenity.builder().name("Piscine").monthlyPrice(new BigDecimal("75.00")).building(building2).build(),
                    Amenity.builder().name("Jardins Paysagers").building(building2).build(),
                    Amenity.builder().name("Conciergerie").building(building3).build()
            );
            amenityRepository.saveAll(amenities);

            // ========== 4. UNITÉS ==========
            // Building 1 - 5 units
            Unit unit1 = Unit.builder().doorNumber("101").floor(1).rentPrice(new BigDecimal("750.00")).unitType(UnitType.T2).building(building1).build();
            Unit unit2 = Unit.builder().doorNumber("102").floor(1).rentPrice(new BigDecimal("450.00")).unitType(UnitType.STUDIO).building(building1).build();
            Unit unit3 = Unit.builder().doorNumber("201").floor(2).rentPrice(new BigDecimal("950.00")).unitType(UnitType.T3).building(building1).build();
            Unit unit4 = Unit.builder().doorNumber("202").floor(2).rentPrice(new BigDecimal("600.00")).unitType(UnitType.T2).building(building1).build();
            Unit unit5 = Unit.builder().doorNumber("301").floor(3).rentPrice(new BigDecimal("1200.00")).unitType(UnitType.T4).building(building1).build();

            // Building 2 - 3 units
            Unit unit6 = Unit.builder().doorNumber("101").floor(1).rentPrice(new BigDecimal("850.00")).unitType(UnitType.T2).building(building2).build();
            Unit unit7 = Unit.builder().doorNumber("102").floor(1).rentPrice(new BigDecimal("500.00")).unitType(UnitType.STUDIO).building(building2).build();
            Unit unit8 = Unit.builder().doorNumber("201").floor(2).rentPrice(new BigDecimal("950.00")).unitType(UnitType.T3).building(building2).build();

            // Building 3 - 2 units
            Unit unit9 = Unit.builder().doorNumber("101").floor(1).rentPrice(new BigDecimal("800.00")).unitType(UnitType.T2).building(building3).build();
            Unit unit10 = Unit.builder().doorNumber("102").floor(1).rentPrice(new BigDecimal("1100.00")).unitType(UnitType.T3).building(building3).build();

            unitRepository.saveAll(List.of(unit1, unit2, unit3, unit4, unit5, unit6, unit7, unit8, unit9, unit10));

            // ========== 5. BAUX ==========
            // Lease 1: Active lease for tenant1 on unit1
            Lease lease1 = Lease.builder()
                    .startDate(LocalDate.now().minusMonths(2))
                    .baseRentAmount(unit1.getRentPrice())
                    .utilityAmount(new BigDecimal("50.00"))
                    .status(LeaseStatus.ACTIVE)
                    .tenant(tenant1)
                    .unit(unit1)
                    .build();

            // Lease 2: Active lease for tenant2 on unit3
            Lease lease2 = Lease.builder()
                    .startDate(LocalDate.now().minusMonths(3))
                    .baseRentAmount(unit3.getRentPrice())
                    .utilityAmount(new BigDecimal("75.00"))
                    .status(LeaseStatus.ACTIVE)
                    .tenant(tenant2)
                    .unit(unit3)
                    .build();

            // Lease 3: Terminated lease for unit2
            Lease lease3 = Lease.builder()
                    .startDate(LocalDate.now().minusMonths(12))
                    .endDate(LocalDate.now().minusDays(15))
                    .baseRentAmount(unit2.getRentPrice())
                    .utilityAmount(new BigDecimal("30.00"))
                    .status(LeaseStatus.TERMINATED)
                    .tenant(tenant3)
                    .unit(unit2)
                    .build();

            // Lease 4: Active lease for tenant3 on unit6
            Lease lease4 = Lease.builder()
                    .startDate(LocalDate.now().minusMonths(6))
                    .baseRentAmount(unit6.getRentPrice())
                    .utilityAmount(new BigDecimal("60.00"))
                    .status(LeaseStatus.ACTIVE)
                    .tenant(tenant3)
                    .unit(unit6)
                    .build();

            leaseRepository.saveAll(List.of(lease1, lease2, lease3, lease4));

            // ========== 6. FACTURES ==========
            // Invoices for lease1 (3 months)
            Invoice inv1 = Invoice.builder()
                    .issueDate(LocalDate.now().withDayOfMonth(1).minusMonths(2))
                    .dueDate(LocalDate.now().withDayOfMonth(5).minusMonths(2))
                    .totalAmount(lease1.getTotalMonthlyPayment())
                    .status(InvoiceStatus.PAID)
                    .reminderLevel(0)
                    .lease(lease1)
                    .build();

            Invoice inv2 = Invoice.builder()
                    .issueDate(LocalDate.now().withDayOfMonth(1).minusMonths(1))
                    .dueDate(LocalDate.now().withDayOfMonth(5).minusMonths(1))
                    .totalAmount(lease1.getTotalMonthlyPayment())
                    .status(InvoiceStatus.PAID)
                    .reminderLevel(0)
                    .lease(lease1)
                    .build();

            Invoice inv3 = Invoice.builder()
                    .issueDate(LocalDate.now().withDayOfMonth(1))
                    .dueDate(LocalDate.now().withDayOfMonth(5))
                    .totalAmount(lease1.getTotalMonthlyPayment())
                    .status(InvoiceStatus.SENT)
                    .reminderLevel(0)
                    .lease(lease1)
                    .build();

            // Invoices for lease2 (2 months)
            Invoice inv4 = Invoice.builder()
                    .issueDate(LocalDate.now().withDayOfMonth(1).minusMonths(1))
                    .dueDate(LocalDate.now().withDayOfMonth(5).minusMonths(1))
                    .totalAmount(lease2.getTotalMonthlyPayment())
                    .status(InvoiceStatus.OVERDUE)
                    .reminderLevel(2)
                    .lease(lease2)
                    .build();

            Invoice inv5 = Invoice.builder()
                    .issueDate(LocalDate.now().withDayOfMonth(1))
                    .dueDate(LocalDate.now().withDayOfMonth(5))
                    .totalAmount(lease2.getTotalMonthlyPayment())
                    .status(InvoiceStatus.SENT)
                    .reminderLevel(0)
                    .lease(lease2)
                    .build();

            // Invoices for lease4
            Invoice inv6 = Invoice.builder()
                    .issueDate(LocalDate.now().withDayOfMonth(1).minusMonths(1))
                    .dueDate(LocalDate.now().withDayOfMonth(5).minusMonths(1))
                    .totalAmount(lease4.getTotalMonthlyPayment())
                    .status(InvoiceStatus.PAID)
                    .reminderLevel(0)
                    .lease(lease4)
                    .build();

            Invoice inv7 = Invoice.builder()
                    .issueDate(LocalDate.now().withDayOfMonth(1))
                    .dueDate(LocalDate.now().withDayOfMonth(5))
                    .totalAmount(lease4.getTotalMonthlyPayment())
                    .status(InvoiceStatus.SENT)
                    .reminderLevel(0)
                    .lease(lease4)
                    .build();

            invoiceRepository.saveAll(List.of(inv1, inv2, inv3, inv4, inv5, inv6, inv7));

            System.out.println("✅ Données de test insérées avec succès !");
            System.out.println("   - 6 utilisateurs (1 Admin, 2 Landlords, 3 Tenants)");
            System.out.println("   - 3 bâtiments avec 10 unités");
            System.out.println("   - 6 commodités");
            System.out.println("   - 4 baux (3 actifs, 1 terminé)");
            System.out.println("   - 7 factures avec différents statuts");
        }
    }
}