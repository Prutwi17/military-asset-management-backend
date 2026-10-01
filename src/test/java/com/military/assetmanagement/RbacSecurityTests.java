package com.military.assetmanagement;

import com.military.assetmanagement.dto.*;
import com.military.assetmanagement.entity.*;
import com.military.assetmanagement.repository.*;
import com.military.assetmanagement.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RbacSecurityTests {

    @Mock
    private TransferRepository transferRepository;
    @Mock
    private AssetRepository assetRepository;
    @Mock
    private BaseRepository baseRepository;
    @Mock
    private AuditService auditService;
    @Mock
    private AuditLogRepository auditLogRepository;
    @Mock
    private PurchaseRepository purchaseRepository;

    @InjectMocks
    private TransferService transferService;

    @InjectMocks
    private BaseService baseService;

    @InjectMocks
    private AssetService assetService;

    @InjectMocks
    private PurchaseService purchaseService;

    private User adminUser;
    private User commanderAlpha;
    private User commanderBravo;
    private User logisticsUser;
    private Base baseAlpha;
    private Base baseBravo;
    private Asset assetAlpha;
    private Asset assetBravo;

    @BeforeEach
    void setUp() {
        baseAlpha = new Base("Base Alpha", "BASE-A", "Sector 1", "HQ Base", "ACTIVE");
        baseAlpha.setId(1L);

        baseBravo = new Base("Base Bravo", "BASE-B", "Sector 2", "Outpost Base", "ACTIVE");
        baseBravo.setId(2L);

        adminUser = new User("admin", "admin@mil.internal", "pw", "General Admin", "GENERAL", Role.ADMIN, null);
        adminUser.setId(10L);

        commanderAlpha = new User("commander_a", "c_a@mil.internal", "pw", "Col. Alpha", "COLONEL", Role.BASE_COMMANDER, baseAlpha);
        commanderAlpha.setId(11L);

        commanderBravo = new User("commander_b", "c_b@mil.internal", "pw", "Col. Bravo", "COLONEL", Role.BASE_COMMANDER, baseBravo);
        commanderBravo.setId(12L);

        logisticsUser = new User("logistics", "log@mil.internal", "pw", "Major Log", "MAJOR", Role.LOGISTICS_OFFICER, baseAlpha);
        logisticsUser.setId(13L);

        assetAlpha = new Asset("AST-001", "T-90 Tank", AssetCategory.VEHICLE, "Main Battle Tank", "SN-001", 5, "Units",
                AssetStatus.AVAILABLE, baseAlpha, "Motor Pool A", "Main tank", LocalDate.now(), BigDecimal.valueOf(1000000), null, null);
        assetAlpha.setId(101L);

        assetBravo = new Asset("AST-002", "INSAS Rifle", AssetCategory.WEAPON, "Assault Rifle", "SN-002", 50, "Units",
                AssetStatus.AVAILABLE, baseBravo, "Armory B", "Rifles", LocalDate.now(), BigDecimal.valueOf(1500), null, null);
        assetBravo.setId(102L);
    }

    // ==========================================
    // 1. TRANSFER RBAC TESTS
    // ==========================================

    @Test
    @DisplayName("Logistics Officer CANNOT approve transfers (Forbidden)")
    void logisticsOfficerCannotApproveTransfer() {
        Transfer transfer = new Transfer("TRF-001", assetAlpha, "T-90 Tank", AssetCategory.VEHICLE, "Main Battle Tank",
                2, "Units", baseAlpha, baseBravo, LocalDate.now(), TransferStatus.PENDING, "logistics", 13L, "Support", null);
        transfer.setId(201L);

        when(transferRepository.findById(201L)).thenReturn(Optional.of(transfer));

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> {
            transferService.approveTransfer(201L, logisticsUser);
        });

        assertTrue(ex.getMessage().contains("Logistics Officers cannot approve transfers"));
        verify(transferRepository, never()).save(any());
    }

    @Test
    @DisplayName("Logistics Officer CANNOT reject transfers (Forbidden)")
    void logisticsOfficerCannotRejectTransfer() {
        Transfer transfer = new Transfer("TRF-001", assetAlpha, "T-90 Tank", AssetCategory.VEHICLE, "Main Battle Tank",
                2, "Units", baseAlpha, baseBravo, LocalDate.now(), TransferStatus.PENDING, "logistics", 13L, "Support", null);
        transfer.setId(201L);

        when(transferRepository.findById(201L)).thenReturn(Optional.of(transfer));

        RejectTransferRequest request = new RejectTransferRequest("Not needed");
        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> {
            transferService.rejectTransfer(201L, request, logisticsUser);
        });

        assertTrue(ex.getMessage().contains("Logistics Officers cannot reject transfers"));
        verify(transferRepository, never()).save(any());
    }

    @Test
    @DisplayName("Base Commander CANNOT approve transfers outside assigned base command")
    void baseCommanderCannotApproveUnrelatedTransfer() {
        // Transfer between Bravo and another base, Commander Alpha has no authority
        Transfer transfer = new Transfer("TRF-002", assetBravo, "INSAS Rifle", AssetCategory.WEAPON, "Assault Rifle",
                10, "Units", baseBravo, baseBravo, LocalDate.now(), TransferStatus.PENDING, "officer", 99L, "Transfer", null);
        transfer.setId(202L);

        when(transferRepository.findById(202L)).thenReturn(Optional.of(transfer));

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> {
            transferService.approveTransfer(202L, commanderAlpha);
        });

        assertTrue(ex.getMessage().contains("Commander cannot approve transfers outside assigned command"));
        verify(transferRepository, never()).save(any());
    }

    @Test
    @DisplayName("Base Commander CAN approve transfers involving their assigned base")
    void baseCommanderCanApproveTheirBaseTransfer() {
        Transfer transfer = new Transfer("TRF-003", assetAlpha, "T-90 Tank", AssetCategory.VEHICLE, "Main Battle Tank",
                1, "Units", baseAlpha, baseBravo, LocalDate.now(), TransferStatus.PENDING, "officer", 99L, "Deploy", null);
        transfer.setId(203L);

        when(transferRepository.findById(203L)).thenReturn(Optional.of(transfer));
        when(transferRepository.save(any(Transfer.class))).thenAnswer(inv -> inv.getArgument(0));

        TransferDto dto = transferService.approveTransfer(203L, commanderAlpha);

        assertNotNull(dto);
        assertEquals(TransferStatus.APPROVED, dto.getStatus());
        verify(transferRepository).save(any(Transfer.class));
    }

    @Test
    @DisplayName("Admin CAN approve any transfer")
    void adminCanApproveAnyTransfer() {
        Transfer transfer = new Transfer("TRF-004", assetBravo, "INSAS Rifle", AssetCategory.WEAPON, "Assault Rifle",
                10, "Units", baseBravo, baseAlpha, LocalDate.now(), TransferStatus.PENDING, "officer", 99L, "Deploy", null);
        transfer.setId(204L);

        when(transferRepository.findById(204L)).thenReturn(Optional.of(transfer));
        when(transferRepository.save(any(Transfer.class))).thenAnswer(inv -> inv.getArgument(0));

        TransferDto dto = transferService.approveTransfer(204L, adminUser);

        assertNotNull(dto);
        assertEquals(TransferStatus.APPROVED, dto.getStatus());
        verify(transferRepository).save(any(Transfer.class));
    }

    // ==========================================
    // 2. AUDIT LOG RBAC TESTS
    // ==========================================

    @Test
    @DisplayName("Non-Admin (Base Commander & Logistics) CANNOT access Audit Logs")
    void nonAdminCannotAccessAuditLogs() {
        AuditService service = new AuditService(auditLogRepository);

        AccessDeniedException exCmd = assertThrows(AccessDeniedException.class, () -> {
            service.getAuditLogs(commanderAlpha, null, null, null, null, null);
        });
        assertTrue(exCmd.getMessage().contains("Only ADMIN can access audit trail"));

        AccessDeniedException exLog = assertThrows(AccessDeniedException.class, () -> {
            service.getAuditLogs(logisticsUser, null, null, null, null, null);
        });
        assertTrue(exLog.getMessage().contains("Only ADMIN can access audit trail"));
    }

    @Test
    @DisplayName("Admin CAN access Audit Logs")
    void adminCanAccessAuditLogs() {
        AuditService service = new AuditService(auditLogRepository);
        when(auditLogRepository.filterAuditLogs(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of());

        List<AuditLogDto> logs = service.getAuditLogs(adminUser, null, null, null, null, null);
        assertNotNull(logs);
        verify(auditLogRepository).filterAuditLogs(any(), any(), any(), any(), any(), any(), any());
    }

    // ==========================================
    // 3. BASE ISOLATION TESTS
    // ==========================================

    @Test
    @DisplayName("Base Commander only sees assigned base in getAllBases")
    void baseCommanderGetAllBasesIsRestricted() {
        List<BaseDto> bases = baseService.getAllBases(commanderAlpha);
        assertEquals(1, bases.size());
        assertEquals("Base Alpha", bases.get(0).getName());
        verify(baseRepository, never()).findAll();
    }

    @Test
    @DisplayName("Admin sees all bases in getAllBases")
    void adminGetAllBasesSeesAll() {
        when(baseRepository.findAll()).thenReturn(List.of(baseAlpha, baseBravo));
        List<BaseDto> bases = baseService.getAllBases(adminUser);
        assertEquals(2, bases.size());
        verify(baseRepository).findAll();
    }

    @Test
    @DisplayName("Base Commander CANNOT view other base details by ID (IDOR prevention)")
    void baseCommanderCannotAccessOtherBaseById() {
        when(baseRepository.findById(2L)).thenReturn(Optional.of(baseBravo));

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> {
            baseService.getBaseById(2L, commanderAlpha);
        });

        assertTrue(ex.getMessage().contains("Base Commander can only access assigned base details"));
    }

    // ==========================================
    // 4. ASSET RBAC & ISOLATION TESTS
    // ==========================================

    @Test
    @DisplayName("Base Commander CANNOT view another base's asset by ID (IDOR prevention)")
    void baseCommanderCannotAccessOtherBaseAsset() {
        when(assetRepository.findById(102L)).thenReturn(Optional.of(assetBravo)); // Asset belongs to Base Bravo

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> {
            assetService.getAssetById(102L, commanderAlpha);
        });

        assertTrue(ex.getMessage().contains("Base Commander cannot view this asset outside of assigned base"));
    }

    @Test
    @DisplayName("Logistics Officer CANNOT delete or decommission an asset")
    void logisticsOfficerCannotDeleteAsset() {
        when(assetRepository.findById(101L)).thenReturn(Optional.of(assetAlpha));

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> {
            assetService.deleteAsset(101L, logisticsUser);
        });

        assertTrue(ex.getMessage().contains("Logistics Officers cannot delete or decommission"));
        verify(assetRepository, never()).save(any());
    }

    // ==========================================
    // 5. PURCHASE ISOLATION TESTS
    // ==========================================

    @Test
    @DisplayName("Base Commander CANNOT view purchase belonging to another base (IDOR prevention)")
    void baseCommanderCannotAccessOtherBasePurchase() {
        Purchase purchaseBravo = new Purchase("PO-001", assetBravo, "INSAS Rifle", AssetCategory.WEAPON,
                "Assault Rifle", baseBravo, 10, "Units", BigDecimal.valueOf(1500), BigDecimal.valueOf(15000),
                LocalDate.now(), "Supplier X", "Notes", "officer");
        purchaseBravo.setId(301L);

        when(purchaseRepository.findById(301L)).thenReturn(Optional.of(purchaseBravo));

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> {
            purchaseService.getPurchaseById(301L, commanderAlpha);
        });

        assertTrue(ex.getMessage().contains("Base Commander cannot view purchases for another base"));
    }

    @Test
    @DisplayName("Base Commander CANNOT create purchase for another base")
    void baseCommanderCannotCreatePurchaseForOtherBase() {
        when(baseRepository.findById(2L)).thenReturn(Optional.of(baseBravo));

        CreatePurchaseRequest request = new CreatePurchaseRequest();
        request.setReferenceNumber("PO-TEST");
        request.setAssetName("New Rifles");
        request.setCategory(AssetCategory.WEAPON);
        request.setEquipmentType("Rifle");
        request.setBaseId(2L);
        request.setQuantity(10);
        request.setUnit("Units");
        request.setUnitPrice(BigDecimal.valueOf(500));
        request.setTotalAmount(BigDecimal.valueOf(5000));
        request.setPurchaseDate(LocalDate.now());
        request.setSupplier("Supplier A");
        request.setNotes("Notes");

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> {
            purchaseService.createPurchase(request, commanderAlpha);
        });

        assertTrue(ex.getMessage().contains("Base Commander can only log purchases for assigned base"));
        verify(purchaseRepository, never()).save(any());
    }

    // ==========================================
    // 6. ASSET CREATION IMAGE VALIDATION TEST
    // ==========================================

    @Test
    @DisplayName("Asset creation requires non-empty image (Validation)")
    void assetCreationRequiresImage() {
        when(baseRepository.findById(1L)).thenReturn(Optional.of(baseAlpha));

        CreateAssetRequest request = new CreateAssetRequest();
        request.setName("Test Equipment");
        request.setCategory(AssetCategory.OTHER);
        request.setBaseId(1L);
        request.setQuantity(5);
        request.setImageUrl(""); // Empty image

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            assetService.createAsset(request, adminUser);
        });

        assertTrue(ex.getMessage().contains("Asset image is required"));
        verify(assetRepository, never()).save(any());
    }

    // ==========================================
    // 7. HEALTH CHECK VERIFICATION
    // ==========================================

    @Test
    @DisplayName("Health endpoint returns status UP and database status")
    void healthCheckReturnsUp() {
        org.springframework.jdbc.core.JdbcTemplate jdbcTemplate = mock(org.springframework.jdbc.core.JdbcTemplate.class);
        when(jdbcTemplate.queryForObject(eq("SELECT 1"), eq(Integer.class))).thenReturn(1);

        com.military.assetmanagement.controller.HealthController healthController =
                new com.military.assetmanagement.controller.HealthController(jdbcTemplate);

        org.springframework.http.ResponseEntity<java.util.Map<String, Object>> response = healthController.health();
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("UP", response.getBody().get("status"));
        assertEquals("CONNECTED", response.getBody().get("database"));
    }
}
