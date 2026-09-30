package com.military.asset.config;

import com.military.asset.entity.*;
import com.military.asset.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {

    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final UserRepository userRepository;
    private final BaseInventoryRepository baseInventoryRepository;
    private final PurchaseRepository purchaseRepository;
    private final TransferRepository transferRepository;
    private final AssignmentRepository assignmentRepository;
    private final ExpenditureRepository expenditureRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(BaseRepository baseRepository,
                      EquipmentTypeRepository equipmentTypeRepository,
                      UserRepository userRepository,
                      BaseInventoryRepository baseInventoryRepository,
                      PurchaseRepository purchaseRepository,
                      TransferRepository transferRepository,
                      AssignmentRepository assignmentRepository,
                      ExpenditureRepository expenditureRepository,
                      PasswordEncoder passwordEncoder) {
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.userRepository = userRepository;
        this.baseInventoryRepository = baseInventoryRepository;
        this.purchaseRepository = purchaseRepository;
        this.transferRepository = transferRepository;
        this.assignmentRepository = assignmentRepository;
        this.expenditureRepository = expenditureRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (baseRepository.count() > 0) {
            return; // Data already exists
        }

        // 1. Seed Bases
        Base b1 = baseRepository.save(new Base(null, "BASE-001", "Fort Liberty (Bragg)", "North Carolina, USA", "General Marcus Vance"));
        Base b2 = baseRepository.save(new Base(null, "BASE-002", "Camp Pendleton", "California, USA", "Col. Sarah Jenkins"));
        Base b3 = baseRepository.save(new Base(null, "BASE-003", "Ramstein Air Base", "Kaiserslautern, Germany", "Brig. Gen. David Sterling"));
        Base b4 = baseRepository.save(new Base(null, "BASE-004", "Camp Humphreys", "Pyeongtaek, South Korea", "Col. Robert Chen"));

        // 2. Seed Equipment Types
        EquipmentType eq1 = equipmentTypeRepository.save(new EquipmentType(null, "WEAPONS", "M4A1 Carbine 5.56mm", "UNITS", "Standard issue automatic assault rifle"));
        EquipmentType eq2 = equipmentTypeRepository.save(new EquipmentType(null, "WEAPONS", "M249 SAW Light Machine Gun", "UNITS", "5.56mm Squad Automatic Weapon"));
        EquipmentType eq3 = equipmentTypeRepository.save(new EquipmentType(null, "VEHICLES", "JLTV Armored Vehicle", "VEHICLES", "Joint Light Tactical Vehicle with armor plating"));
        EquipmentType eq4 = equipmentTypeRepository.save(new EquipmentType(null, "VEHICLES", "M1A2 Abrams Main Battle Tank", "VEHICLES", "Armored heavy tracked battle tank"));
        EquipmentType eq5 = equipmentTypeRepository.save(new EquipmentType(null, "AMMUNITION", "5.56x45mm NATO Ammo Box", "ROUNDS", "Standard rifle ammunition crate"));
        EquipmentType eq6 = equipmentTypeRepository.save(new EquipmentType(null, "AMMUNITION", "120mm Tank Shell (High Explosive)", "ROUNDS", "Main gun heavy ammunition"));
        EquipmentType eq7 = equipmentTypeRepository.save(new EquipmentType(null, "COMMUNICATIONS", "AN/PRC-152A Tactical Radio", "SETS", "Handheld multiband satellite radio"));

        // 3. Seed Users
        String encodedPass = passwordEncoder.encode("password123");
        User admin = userRepository.save(new User(null, "admin", encodedPass, "Central Command Admin", "admin@military.gov", Role.ADMIN, null));
        User cmdrBragg = userRepository.save(new User(null, "commander_bragg", encodedPass, "General Marcus Vance", "cmdr.bragg@military.gov", Role.BASE_COMMANDER, b1));
        User cmdrPendleton = userRepository.save(new User(null, "commander_pendleton", encodedPass, "Col. Sarah Jenkins", "cmdr.pendleton@military.gov", Role.BASE_COMMANDER, b2));
        User logOfficer = userRepository.save(new User(null, "logistics_officer", encodedPass, "Captain Alex Mercer", "logistics@military.gov", Role.LOGISTICS_OFFICER, b1));

        // 4. Seed Inventories
        createInventory(b1, eq1, 500, 420, 50, 30);
        createInventory(b1, eq3, 40, 32, 5, 3);
        createInventory(b1, eq5, 10000, 7500, 500, 2000);
        createInventory(b2, eq1, 350, 280, 40, 30);
        createInventory(b2, eq2, 60, 48, 8, 4);
        createInventory(b2, eq7, 120, 100, 15, 5);
        createInventory(b3, eq3, 25, 20, 3, 2);
        createInventory(b3, eq4, 12, 10, 2, 0);
        createInventory(b4, eq1, 200, 160, 25, 15);

        // 5. Seed Purchases
        savePurchase("PO-2026-001", b1, eq1, 100, new BigDecimal("1200.00"), "Colt Defense Systems", logOfficer, "Annual rifle procurement batch");
        savePurchase("PO-2026-002", b1, eq3, 5, new BigDecimal("220000.00"), "Oshkosh Defense", logOfficer, "Tactical vehicle batch");
        savePurchase("PO-2026-003", b2, eq5, 5000, new BigDecimal("45.00"), "Lake City Ammunition Plant", logOfficer, "Live fire exercise stock");
        savePurchase("PO-2026-004", b3, eq7, 30, new BigDecimal("4500.00"), "L3Harris Technologies", logOfficer, "Comm upgrade for European Theater");

        // 6. Seed Transfers
        saveTransfer("TRF-2026-101", b1, b2, eq1, 30, TransferStatus.COMPLETED, logOfficer, cmdrBragg, "Pacific deployment redistribution");
        saveTransfer("TRF-2026-102", b2, b4, eq2, 10, TransferStatus.IN_TRANSIT, logOfficer, cmdrPendleton, "Far East squad reinforcement");
        saveTransfer("TRF-2026-103", b1, b3, eq3, 4, TransferStatus.PENDING, logOfficer, null, "NATO exercise support");

        // 7. Seed Assignments
        saveAssignment("ASN-2026-01", b1, eq1, "Sgt. John Miller", "MIL-98421", "Sergeant", 1, LocalDate.now().plusMonths(6), AssignmentStatus.ACTIVE, cmdrBragg, "Issued for perimeter defense detail");
        saveAssignment("ASN-2026-02", b1, eq3, "Lt. Commander Eric Vance", "MIL-44210", "Lieutenant", 1, LocalDate.now().plusMonths(3), AssignmentStatus.ACTIVE, cmdrBragg, "Command convoy lead vehicle");
        saveAssignment("ASN-2026-03", b2, eq7, "Cpl. Samantha Reed", "MIL-77123", "Corporal", 2, LocalDate.now().plusMonths(1), AssignmentStatus.ACTIVE, cmdrPendleton, "Comms check patrol");

        // 8. Seed Expenditures
        saveExpenditure("EXP-2026-01", b1, eq5, 2000, "TRAINING", "Exercise Valor Shield 2026", logOfficer, "Spent during Airborne qualification drill");
        saveExpenditure("EXP-2026-02", b1, eq1, 5, "DAMAGED_DISPOSAL", "N/A", cmdrBragg, "Irreparably damaged in heavy vehicle accident");
        saveExpenditure("EXP-2026-03", b2, eq5, 1500, "OPERATIONAL_USAGE", "Coastal Readiness Alpha", logOfficer, "Expenditure during marine coastal drill");
    }

    private void createInventory(Base b, EquipmentType eq, int open, int current, int assigned, int expended) {
        BaseInventory inv = new BaseInventory(b, eq, open, current);
        inv.setAssignedCount(assigned);
        inv.setExpendedCount(expended);
        baseInventoryRepository.save(inv);
    }

    private void savePurchase(String po, Base b, EquipmentType eq, int qty, BigDecimal cost, String vendor, User user, String remarks) {
        Purchase p = new Purchase();
        p.setPurchaseOrderNumber(po);
        p.setBase(b);
        p.setEquipmentType(eq);
        p.setQuantity(qty);
        p.setUnitCost(cost);
        p.setTotalCost(cost.multiply(BigDecimal.valueOf(qty)));
        p.setVendorName(vendor);
        p.setRecordedBy(user);
        p.setRemarks(remarks);
        p.setPurchaseDate(LocalDateTime.now().minusDays(10));
        purchaseRepository.save(p);
    }

    private void saveTransfer(String code, Base src, Base tgt, EquipmentType eq, int qty, TransferStatus status, User init, User app, String remarks) {
        Transfer t = new Transfer();
        t.setTransferCode(code);
        t.setSourceBase(src);
        t.setTargetBase(tgt);
        t.setEquipmentType(eq);
        t.setQuantity(qty);
        t.setStatus(status);
        t.setInitiatedBy(init);
        t.setApprovedBy(app);
        t.setRemarks(remarks);
        t.setTransferDate(LocalDateTime.now().minusDays(5));
        if (status == TransferStatus.COMPLETED) {
            t.setCompletionDate(LocalDateTime.now().minusDays(3));
        }
        transferRepository.save(t);
    }

    private void saveAssignment(String code, Base b, EquipmentType eq, String personnel, String serviceId, String rank, int qty, LocalDate returnDate, AssignmentStatus status, User user, String notes) {
        Assignment a = new Assignment();
        a.setAssignmentCode(code);
        a.setBase(b);
        a.setEquipmentType(eq);
        a.setAssignedToPersonnel(personnel);
        a.setServiceId(serviceId);
        a.setRankTitle(rank);
        a.setQuantity(qty);
        a.setExpectedReturnDate(returnDate);
        a.setStatus(status);
        a.setAssignedBy(user);
        a.setNotes(notes);
        a.setAssignmentDate(LocalDateTime.now().minusDays(7));
        assignmentRepository.save(a);
    }

    private void saveExpenditure(String code, Base b, EquipmentType eq, int qty, String reason, String opName, User user, String remarks) {
        Expenditure e = new Expenditure();
        e.setExpenditureCode(code);
        e.setBase(b);
        e.setEquipmentType(eq);
        e.setQuantity(qty);
        e.setReason(reason);
        e.setOperationName(opName);
        e.setRecordedBy(user);
        e.setRemarks(remarks);
        e.setExpenditureDate(LocalDateTime.now().minusDays(4));
        expenditureRepository.save(e);
    }
}
