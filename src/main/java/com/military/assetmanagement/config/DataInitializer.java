package com.military.assetmanagement.config;

import com.military.assetmanagement.entity.*;
import com.military.assetmanagement.repository.AssetRepository;
import com.military.assetmanagement.repository.BaseRepository;
import com.military.assetmanagement.repository.PurchaseRepository;
import com.military.assetmanagement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final BaseRepository baseRepository;
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final PurchaseRepository purchaseRepository;
    private final com.military.assetmanagement.repository.TransferRepository transferRepository;
    private final com.military.assetmanagement.repository.AssignmentRepository assignmentRepository;
    private final com.military.assetmanagement.repository.ExpenditureRepository expenditureRepository;
    private final com.military.assetmanagement.repository.AuditLogRepository auditLogRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(BaseRepository baseRepository,
                           UserRepository userRepository,
                           AssetRepository assetRepository,
                           PurchaseRepository purchaseRepository,
                           com.military.assetmanagement.repository.TransferRepository transferRepository,
                           com.military.assetmanagement.repository.AssignmentRepository assignmentRepository,
                           com.military.assetmanagement.repository.ExpenditureRepository expenditureRepository,
                           com.military.assetmanagement.repository.AuditLogRepository auditLogRepository,
                           PasswordEncoder passwordEncoder) {
        this.baseRepository = baseRepository;
        this.userRepository = userRepository;
        this.assetRepository = assetRepository;
        this.purchaseRepository = purchaseRepository;
        this.transferRepository = transferRepository;
        this.assignmentRepository = assignmentRepository;
        this.expenditureRepository = expenditureRepository;
        this.auditLogRepository = auditLogRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        logger.info("Initializing baseline military bases, users, assets, purchases, transfers, assignments, and expenditures...");

        // 1. Initialize Bases
        Base baseAlpha = baseRepository.findByCode("BASE-001")
                .orElseGet(() -> baseRepository.save(new Base(
                        "Base Alpha",
                        "BASE-001",
                        "Northern Command, Sector 4",
                        "Primary strategic armor & defense command base",
                        "ACTIVE"
                )));

        Base baseBravo = baseRepository.findByCode("BASE-002")
                .orElseGet(() -> baseRepository.save(new Base(
                        "Base Bravo",
                        "BASE-002",
                        "Western Sector, Forward Outpost",
                        "Rapid response tactical border outpost",
                        "ACTIVE"
                )));

        Base logisticsDepot = baseRepository.findByCode("DEPOT-001")
                .orElseGet(() -> baseRepository.save(new Base(
                        "Central Logistics Depot",
                        "DEPOT-001",
                        "Central Military Command, Logistics Hub",
                        "Central procurement, storage and supply depot",
                        "ACTIVE"
                )));

        // 2. Initialize Seed Users if not present
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User(
                    "admin",
                    "admin@military.gov",
                    passwordEncoder.encode("Admin@123"),
                    "General Pruthwi Raj",
                    "General",
                    Role.ADMIN,
                    null
            );
            userRepository.save(admin);
            logger.info("Created default Admin user: admin / Admin@123");
        }

        if (!userRepository.existsByUsername("commander")) {
            User commander = new User(
                    "commander",
                    "commander@military.gov",
                    passwordEncoder.encode("Commander@123"),
                    "Col. Arjun Singh",
                    "Colonel",
                    Role.BASE_COMMANDER,
                    baseAlpha
            );
            userRepository.save(commander);
            logger.info("Created default Base Commander user: commander / Commander@123 (Base Alpha)");
        }

        if (!userRepository.existsByUsername("logistics")) {
            User logistics = new User(
                    "logistics",
                    "logistics@military.gov",
                    passwordEncoder.encode("Logistics@123"),
                    "Maj. Sarah Jenkins",
                    "Major",
                    Role.LOGISTICS_OFFICER,
                    logisticsDepot
            );
            userRepository.save(logistics);
            logger.info("Created default Logistics Officer user: logistics / Logistics@123 (Central Depot)");
        }

        // 3. Initialize Assets matching ui-reference.png Screen 3
        if (assetRepository.count() == 0) {
            logger.info("Seeding initial military assets matching ui-reference.png...");

            Asset tank = assetRepository.save(new Asset(
                    "AST-001",
                    "T-90 Tank",
                    AssetCategory.VEHICLE,
                    "Main Battle Tank",
                    "TK-2024-001",
                    12,
                    "Units",
                    AssetStatus.IN_USE,
                    baseAlpha,
                    "Base Alpha - Motor Pool 1",
                    "Main battle tank for combat operations.",
                    LocalDate.of(2024, 6, 15),
                    new BigDecimal("2400000.00"),
                    LocalDate.of(2026, 9, 10),
                    "https://images.unsplash.com/photo-1579829366248-204fe8413f31?auto=format&fit=crop&w=800&q=80"
            ));

            Asset rifle = assetRepository.save(new Asset(
                    "AST-002",
                    "INSAS Rifle",
                    AssetCategory.WEAPON,
                    "Assault Rifle",
                    "WPN-2024-042",
                    350,
                    "Rifles",
                    AssetStatus.AVAILABLE,
                    baseAlpha,
                    "Armory 1",
                    "Standard 5.56mm assault rifle for infantry personnel.",
                    LocalDate.of(2024, 1, 20),
                    new BigDecimal("1200.00"),
                    LocalDate.of(2026, 8, 15),
                    "https://images.unsplash.com/photo-1595590424283-b8f17842773f?auto=format&fit=crop&w=800&q=80"
            ));

            Asset radio = assetRepository.save(new Asset(
                    "AST-003",
                    "Radio Set",
                    AssetCategory.COMMUNICATION,
                    "Tactical Transceiver",
                    "COM-2024-118",
                    45,
                    "Sets",
                    AssetStatus.MAINTENANCE,
                    baseAlpha,
                    "Workshop",
                    "High frequency tactical radio transceiver with encryption.",
                    LocalDate.of(2024, 3, 10),
                    new BigDecimal("3000.00"),
                    LocalDate.of(2026, 9, 28),
                    "https://images.unsplash.com/photo-1516849841032-87cbac4d88f7?auto=format&fit=crop&w=800&q=80"
            ));

            Asset laptop = assetRepository.save(new Asset(
                    "AST-004",
                    "Command Laptop",
                    AssetCategory.IT_EQUIPMENT,
                    "Rugged Field Terminal",
                    "IT-2024-099",
                    60,
                    "Units",
                    AssetStatus.AVAILABLE,
                    baseAlpha,
                    "HQ Office",
                    "Ruggedized tactical field computer with secure data link.",
                    LocalDate.of(2024, 4, 5),
                    new BigDecimal("2500.00"),
                    LocalDate.of(2026, 9, 1),
                    "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&w=800&q=80"
            ));

            Asset nvg = assetRepository.save(new Asset(
                    "AST-005",
                    "Night Vision Goggles",
                    AssetCategory.OTHER,
                    "Tactical Optics",
                    "NVG-2024-055",
                    80,
                    "Pairs",
                    AssetStatus.IN_USE,
                    baseBravo,
                    "Base Bravo - Armory",
                    "Generation 3 dual-tube tactical night vision goggles.",
                    LocalDate.of(2024, 5, 18),
                    new BigDecimal("4200.00"),
                    LocalDate.of(2026, 8, 20),
                    "https://images.unsplash.com/photo-1578632767115-351597cf2477?auto=format&fit=crop&w=800&q=80"
            ));

            // 4. Initialize Purchases matching assets
            if (purchaseRepository.count() == 0) {
                logger.info("Seeding initial purchase records...");

                purchaseRepository.save(new Purchase(
                        "PO-2024-001",
                        tank,
                        "T-90 Tank",
                        AssetCategory.VEHICLE,
                        "Main Battle Tank",
                        baseAlpha,
                        2,
                        "Units",
                        new BigDecimal("2400000.00"),
                        new BigDecimal("4800000.00"),
                        LocalDate.of(2024, 6, 15),
                        "Heavy Vehicles Factory (HVF)",
                        "Strategic armor replenishment order approved by Central Command.",
                        "General Pruthwi Raj (admin)"
                ));

                purchaseRepository.save(new Purchase(
                        "PO-2024-002",
                        rifle,
                        "INSAS Rifle",
                        AssetCategory.WEAPON,
                        "Assault Rifle",
                        baseAlpha,
                        100,
                        "Rifles",
                        new BigDecimal("1200.00"),
                        new BigDecimal("120000.00"),
                        LocalDate.of(2024, 1, 20),
                        "Ordnance Factory Board",
                        "Standard infantry weapon supply contract.",
                        "Maj. Sarah Jenkins (logistics)"
                ));

                purchaseRepository.save(new Purchase(
                        "PO-2024-003",
                        radio,
                        "Radio Set",
                        AssetCategory.COMMUNICATION,
                        "Tactical Transceiver",
                        baseAlpha,
                        25,
                        "Sets",
                        new BigDecimal("3000.00"),
                        new BigDecimal("75000.00"),
                        LocalDate.of(2024, 3, 10),
                        "Bharat Electronics Limited",
                        "Encrypted communication hardware modernization.",
                        "Col. Arjun Singh (commander)"
                ));

                purchaseRepository.save(new Purchase(
                        "PO-2024-004",
                        laptop,
                        "Command Laptop",
                        AssetCategory.IT_EQUIPMENT,
                        "Rugged Field Terminal",
                        baseAlpha,
                        20,
                        "Units",
                        new BigDecimal("2500.00"),
                        new BigDecimal("50000.00"),
                        LocalDate.of(2024, 4, 5),
                        "Defense Dynamics Cyber Systems",
                        "Tactical HQ computing terminals batch delivery.",
                        "Maj. Sarah Jenkins (logistics)"
                ));
            }
        }

        // Retrieve reference assets for Phase 3 initial seeding
        Asset rifle = assetRepository.findByAssetCode("AST-002").orElse(null);
        Asset radio = assetRepository.findByAssetCode("AST-003").orElse(null);
        Asset laptop = assetRepository.findByAssetCode("AST-004").orElse(null);
        Asset nvg = assetRepository.findByAssetCode("AST-005").orElse(null);

        if (rifle != null && radio != null && laptop != null) {
            // 5. Initialize Transfers
            if (transferRepository.count() == 0) {
                logger.info("Seeding initial asset transfers...");

                transferRepository.save(new Transfer(
                        "TRF-2026-001",
                        rifle,
                        rifle.getName(),
                        rifle.getCategory(),
                        rifle.getEquipmentType(),
                        25,
                        rifle.getUnit(),
                        baseAlpha,
                        baseBravo,
                        LocalDate.of(2026, 9, 15),
                        TransferStatus.COMPLETED,
                        "Col. Arjun Singh (commander)",
                        2L,
                        "Border patrol reinforcement tactical deployment",
                        "Priority transfer cleared under Northern Command directive"
                ));

                transferRepository.save(new Transfer(
                        "TRF-2026-002",
                        radio,
                        radio.getName(),
                        radio.getCategory(),
                        radio.getEquipmentType(),
                        10,
                        radio.getUnit(),
                        baseAlpha,
                        logisticsDepot,
                        LocalDate.of(2026, 9, 22),
                        TransferStatus.APPROVED,
                        "Maj. Sarah Jenkins (logistics)",
                        3L,
                        "Secure encryption module scheduled overhaul at depot workshop",
                        "Awaiting final logistics convoy dispatch"
                ));

                transferRepository.save(new Transfer(
                        "TRF-2026-003",
                        laptop,
                        laptop.getName(),
                        laptop.getCategory(),
                        laptop.getEquipmentType(),
                        5,
                        laptop.getUnit(),
                        baseAlpha,
                        baseBravo,
                        LocalDate.of(2026, 9, 28),
                        TransferStatus.PENDING,
                        "Col. Arjun Singh (commander)",
                        2L,
                        "Tactical field command post setup at Western outpost",
                        "Pending approval from Base Bravo command terminal"
                ));
            }

            // 6. Initialize Asset Assignments
            if (assignmentRepository.count() == 0) {
                logger.info("Seeding initial personnel asset assignments...");

                assignmentRepository.save(new Assignment(
                        "ASN-2026-001",
                        rifle,
                        rifle.getName(),
                        rifle.getCategory(),
                        baseAlpha,
                        "Capt. Vikram Batra",
                        "Captain",
                        "MIL-84920",
                        "13th JAK Rifles / Delta Coy",
                        1,
                        "Rifles",
                        LocalDate.of(2026, 9, 1),
                        LocalDate.of(2026, 12, 31),
                        AssignmentStatus.ACTIVE,
                        "Col. Arjun Singh (commander)",
                        "Personal combat issue for perimeter patrol unit"
                ));

                assignmentRepository.save(new Assignment(
                        "ASN-2026-002",
                        laptop,
                        laptop.getName(),
                        laptop.getCategory(),
                        baseAlpha,
                        "Lt. Priya Sharma",
                        "Lieutenant",
                        "MIL-91244",
                        "Signal Intelligence Battalion",
                        1,
                        "Units",
                        LocalDate.of(2026, 9, 5),
                        LocalDate.of(2026, 11, 30),
                        AssignmentStatus.ACTIVE,
                        "Col. Arjun Singh (commander)",
                        "Field terminal for encrypted intelligence analysis"
                ));

                Assignment returnedAssignment = new Assignment(
                        "ASN-2026-003",
                        radio,
                        radio.getName(),
                        radio.getCategory(),
                        baseAlpha,
                        "Sub. Major Balwant Rai",
                        "Subedar Major",
                        "MIL-60211",
                        "Armored Brigade Communication Unit",
                        2,
                        "Sets",
                        LocalDate.of(2026, 8, 10),
                        LocalDate.of(2026, 9, 10),
                        AssignmentStatus.RETURNED,
                        "Col. Arjun Singh (commander)",
                        "Training exercises in forward sector"
                );
                returnedAssignment.setActualReturnDate(LocalDate.of(2026, 9, 12));
                returnedAssignment.setReturnCondition("GOOD");
                assignmentRepository.save(returnedAssignment);
            }

            // 7. Initialize Expenditures
            if (expenditureRepository.count() == 0) {
                logger.info("Seeding initial asset expenditures...");

                expenditureRepository.save(new Expenditure(
                        "EXP-2026-001",
                        rifle,
                        "5.56mm Ball Munitions (INSAS)",
                        AssetCategory.AMMUNITION,
                        "Munitions & Ordnance",
                        baseAlpha,
                        1500,
                        "Rounds",
                        "2nd Battalion Combat Team",
                        LocalDate.of(2026, 9, 18),
                        "Quarterly Live Fire Battle Readiness Qualifying Drills",
                        "EX-DRILL-402",
                        "Col. Arjun Singh (commander)",
                        2L,
                        "All rounds safely expended at Northern firing ranges"
                ));

                expenditureRepository.save(new Expenditure(
                        "EXP-2026-002",
                        nvg,
                        "Lithium Thermal Batteries & Optics Consumables",
                        AssetCategory.OTHER,
                        "Optical Consumables",
                        baseBravo,
                        40,
                        "Kits",
                        "Night Surveillance Task Force",
                        LocalDate.of(2026, 9, 25),
                        "Forward Reconnaissance Night Patrol Deployment",
                        "OP-NIGHT-WATCH-09",
                        "General Pruthwi Raj (admin)",
                        1L,
                        "Operational consumption during high-alert night surveillance"
                ));
            }

            // 8. Initialize Audit Logs
            if (auditLogRepository.count() == 0) {
                logger.info("Seeding initial security and operational audit logs...");

                auditLogRepository.save(new AuditLog(
                        "admin",
                        "General Pruthwi Raj",
                        Role.ADMIN,
                        "SYSTEM_INITIALIZE",
                        "System",
                        1L,
                        "Military Asset Management System secure terminal initialized",
                        "SUCCESS",
                        "127.0.0.1"
                ));

                auditLogRepository.save(new AuditLog(
                        "commander",
                        "Col. Arjun Singh",
                        Role.BASE_COMMANDER,
                        "CREATE_TRANSFER",
                        "Transfer",
                        1L,
                        "Initiated inter-base transfer TRF-2026-001 for 25x INSAS Rifle to Base Bravo",
                        "SUCCESS",
                        "192.168.1.10"
                ));

                auditLogRepository.save(new AuditLog(
                        "commander",
                        "Col. Arjun Singh",
                        Role.BASE_COMMANDER,
                        "CREATE_ASSIGNMENT",
                        "Assignment",
                        1L,
                        "Assigned 1x INSAS Rifle to Capt. Vikram Batra (MIL-84920)",
                        "SUCCESS",
                        "192.168.1.10"
                ));

                auditLogRepository.save(new AuditLog(
                        "commander",
                        "Col. Arjun Singh",
                        Role.BASE_COMMANDER,
                        "RECORD_EXPENDITURE",
                        "Expenditure",
                        1L,
                        "Expended 1500 rounds of 5.56mm Ball Munitions for Live Fire Drills",
                        "SUCCESS",
                        "192.168.1.10"
                ));
            }
        }

        logger.info("Military baseline data initialization complete.");
    }
}
