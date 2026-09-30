package com.military.assetmanagement.service;

import com.military.assetmanagement.dto.*;
import com.military.assetmanagement.entity.*;
import com.military.assetmanagement.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final AssetRepository assetRepository;
    private final PurchaseRepository purchaseRepository;
    private final TransferRepository transferRepository;
    private final AssignmentRepository assignmentRepository;
    private final ExpenditureRepository expenditureRepository;
    private final BaseRepository baseRepository;
    private final AuditService auditService;

    public DashboardService(AssetRepository assetRepository,
                            PurchaseRepository purchaseRepository,
                            TransferRepository transferRepository,
                            AssignmentRepository assignmentRepository,
                            ExpenditureRepository expenditureRepository,
                            BaseRepository baseRepository,
                            AuditService auditService) {
        this.assetRepository = assetRepository;
        this.purchaseRepository = purchaseRepository;
        this.transferRepository = transferRepository;
        this.assignmentRepository = assignmentRepository;
        this.expenditureRepository = expenditureRepository;
        this.baseRepository = baseRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats(User currentUser, Long baseId, AssetCategory category,
                                              String equipmentType, LocalDate startDate, LocalDate endDate) {
        Long effectiveBaseId = resolveEffectiveBaseId(currentUser, baseId);
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(30);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        // 1. Fetch assets matching the scope
        List<Asset> assets = assetRepository.filterAssets(effectiveBaseId, category, null, equipmentType, null);
        int totalTrackedAssets = assets.size();
        int currentTotalQuantity = assets.stream().mapToInt(Asset::getQuantity).sum();

        // 2. Purchases during period
        List<Purchase> purchases = purchaseRepository.filterPurchases(effectiveBaseId, category, start, end, null);
        if (equipmentType != null && !equipmentType.trim().isEmpty()) {
            purchases = purchases.stream()
                    .filter(p -> p.getEquipmentType() != null && p.getEquipmentType().toLowerCase().contains(equipmentType.toLowerCase()))
                    .collect(Collectors.toList());
        }
        int purchasesQuantity = purchases.stream().mapToInt(Purchase::getQuantity).sum();

        // 3. Completed Transfers during period
        List<Transfer> transfers = transferRepository.filterTransfers(
                effectiveBaseId, effectiveBaseId, effectiveBaseId, category, TransferStatus.COMPLETED, start, end, null
        );

        int transfersInQuantity = 0;
        int transfersOutQuantity = 0;

        if (effectiveBaseId != null) {
            for (Transfer t : transfers) {
                if (t.getDestinationBase().getId().equals(effectiveBaseId)) {
                    transfersInQuantity += t.getQuantity();
                }
                if (t.getSourceBase().getId().equals(effectiveBaseId)) {
                    transfersOutQuantity += t.getQuantity();
                }
            }
        }

        // Net Movement = Purchases + Transfers In - Transfers Out
        int netMovement = purchasesQuantity + transfersInQuantity - transfersOutQuantity;

        // 4. Expenditures during period
        List<Expenditure> expenditures = expenditureRepository.filterExpenditures(effectiveBaseId, category, start, end, null);
        if (equipmentType != null && !equipmentType.trim().isEmpty()) {
            expenditures = expenditures.stream()
                    .filter(e -> e.getEquipmentType() != null && e.getEquipmentType().toLowerCase().contains(equipmentType.toLowerCase()))
                    .collect(Collectors.toList());
        }
        int expendedQuantity = expenditures.stream().mapToInt(Expenditure::getQuantity).sum();

        // 5. Active Assignments
        List<Assignment> activeAssignments = assignmentRepository.filterAssignments(effectiveBaseId, AssignmentStatus.ACTIVE, null, null, null);
        if (category != null) {
            activeAssignments = activeAssignments.stream().filter(a -> a.getCategory() == category).collect(Collectors.toList());
        }
        int assignedQuantity = activeAssignments.stream().mapToInt(Assignment::getQuantity).sum();

        // 6. Calculate Closing and Opening Balances
        int closingBalance = currentTotalQuantity;
        if (end.isBefore(LocalDate.now())) {
            // Adjust for transactions after endDate
            List<Purchase> afterPurchases = purchaseRepository.filterPurchases(effectiveBaseId, category, end.plusDays(1), LocalDate.now(), null);
            int purchasesAfter = afterPurchases.stream().mapToInt(Purchase::getQuantity).sum();

            List<Transfer> afterTransfers = transferRepository.filterTransfers(
                    effectiveBaseId, effectiveBaseId, effectiveBaseId, category, TransferStatus.COMPLETED, end.plusDays(1), LocalDate.now(), null
            );
            int trfInAfter = 0;
            int trfOutAfter = 0;
            if (effectiveBaseId != null) {
                for (Transfer t : afterTransfers) {
                    if (t.getDestinationBase().getId().equals(effectiveBaseId)) trfInAfter += t.getQuantity();
                    if (t.getSourceBase().getId().equals(effectiveBaseId)) trfOutAfter += t.getQuantity();
                }
            }

            List<Expenditure> afterExpenditures = expenditureRepository.filterExpenditures(effectiveBaseId, category, end.plusDays(1), LocalDate.now(), null);
            int expendedAfter = afterExpenditures.stream().mapToInt(Expenditure::getQuantity).sum();

            int netMovementAfter = purchasesAfter + trfInAfter - trfOutAfter;
            closingBalance = Math.max(0, currentTotalQuantity - netMovementAfter + expendedAfter);
        }

        // Inventory Conservation: Opening + NetMovement - Expended = Closing => Opening = Closing - NetMovement + Expended
        int openingBalance = Math.max(0, closingBalance - netMovement + expendedQuantity);

        // 7. Status breakdown
        Map<AssetStatus, Integer> statusCountMap = new EnumMap<>(AssetStatus.class);
        for (AssetStatus s : AssetStatus.values()) {
            statusCountMap.put(s, 0);
        }
        for (Asset a : assets) {
            statusCountMap.put(a.getStatus(), statusCountMap.getOrDefault(a.getStatus(), 0) + a.getQuantity());
        }

        List<StatusCountDto> statusBreakdown = new ArrayList<>();
        int operationalUnits = 0;
        for (Map.Entry<AssetStatus, Integer> entry : statusCountMap.entrySet()) {
            double pct = currentTotalQuantity > 0 ? (entry.getValue() * 100.0) / currentTotalQuantity : 0.0;
            String color = getStatusColor(entry.getKey());
            statusBreakdown.add(new StatusCountDto(entry.getKey(), formatStatusLabel(entry.getKey()), entry.getValue(), Math.round(pct * 10.0) / 10.0, color));
            if (entry.getKey() == AssetStatus.AVAILABLE || entry.getKey() == AssetStatus.IN_USE || entry.getKey() == AssetStatus.DEPLOYED) {
                operationalUnits += entry.getValue();
            }
        }

        double readinessRate = currentTotalQuantity > 0 ? Math.round((operationalUnits * 100.0 / currentTotalQuantity) * 10.0) / 10.0 : 100.0;

        // 8. Category Distribution
        Map<AssetCategory, Integer> catQtyMap = new EnumMap<>(AssetCategory.class);
        Map<AssetCategory, Integer> catCountMap = new EnumMap<>(AssetCategory.class);
        for (AssetCategory c : AssetCategory.values()) {
            catQtyMap.put(c, 0);
            catCountMap.put(c, 0);
        }
        for (Asset a : assets) {
            catQtyMap.put(a.getCategory(), catQtyMap.getOrDefault(a.getCategory(), 0) + a.getQuantity());
            catCountMap.put(a.getCategory(), catCountMap.getOrDefault(a.getCategory(), 0) + 1);
        }

        List<CategoryDistributionDto> categoryDistribution = new ArrayList<>();
        for (Map.Entry<AssetCategory, Integer> entry : catQtyMap.entrySet()) {
            if (entry.getValue() > 0) {
                double pct = currentTotalQuantity > 0 ? (entry.getValue() * 100.0) / currentTotalQuantity : 0.0;
                categoryDistribution.add(new CategoryDistributionDto(
                        entry.getKey(),
                        formatCategoryLabel(entry.getKey()),
                        entry.getValue(),
                        catCountMap.get(entry.getKey()),
                        Math.round(pct * 10.0) / 10.0,
                        getCategoryColor(entry.getKey())
                ));
            }
        }

        // Sort by quantity desc
        categoryDistribution.sort((a, b) -> Integer.compare(b.getQuantity(), a.getQuantity()));

        // 9. Recent activities from audit log
        List<AuditLogDto> recentActivities = auditService.getAuditLogs(currentUser, null, null, null, null, null)
                .stream().limit(6).collect(Collectors.toList());

        String baseName = "Central Command (All Bases)";
        if (effectiveBaseId != null) {
            baseName = baseRepository.findById(effectiveBaseId).map(Base::getName).orElse("Assigned Base");
        }

        DashboardStatsDto dto = new DashboardStatsDto();
        dto.setOpeningBalance(openingBalance);
        dto.setClosingBalance(closingBalance);
        dto.setNetMovement(netMovement);
        dto.setPurchasesQuantity(purchasesQuantity);
        dto.setTransfersInQuantity(transfersInQuantity);
        dto.setTransfersOutQuantity(transfersOutQuantity);
        dto.setAssignedQuantity(assignedQuantity);
        dto.setExpendedQuantity(expendedQuantity);
        dto.setTotalTrackedAssets(totalTrackedAssets);
        dto.setTotalCurrentQuantity(currentTotalQuantity);
        dto.setReadinessRate(readinessRate);
        dto.setBaseId(effectiveBaseId);
        dto.setBaseName(baseName);
        dto.setStartDate(start);
        dto.setEndDate(end);
        dto.setStatusBreakdown(statusBreakdown);
        dto.setCategoryDistribution(categoryDistribution);
        dto.setRecentActivities(recentActivities);

        return dto;
    }

    @Transactional(readOnly = true)
    public NetMovementResponseDto getNetMovementDetails(User currentUser, Long baseId, AssetCategory category,
                                                        LocalDate startDate, LocalDate endDate) {
        Long effectiveBaseId = resolveEffectiveBaseId(currentUser, baseId);
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(30);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        List<NetMovementItemDto> items = new ArrayList<>();

        // 1. Purchases (+ quantity)
        List<Purchase> purchases = purchaseRepository.filterPurchases(effectiveBaseId, category, start, end, null);
        int totalPurchases = 0;
        for (Purchase p : purchases) {
            totalPurchases += p.getQuantity();
            NetMovementItemDto item = new NetMovementItemDto();
            item.setId(p.getId());
            item.setDate(p.getPurchaseDate());
            item.setAssetName(p.getAssetName());
            if (p.getAsset() != null) item.setAssetCode(p.getAsset().getAssetCode());
            item.setCategory(p.getCategory());
            item.setQuantity(p.getQuantity()); // Positive
            item.setUnit(p.getUnit());
            item.setBaseId(p.getBase().getId());
            item.setBaseName(p.getBase().getName());
            item.setReference(p.getReferenceNumber());
            item.setTransactionType("PURCHASE");
            item.setDetails("Procured from " + p.getSupplier());
            items.add(item);
        }

        // 2. Completed Transfers
        List<Transfer> transfers = transferRepository.filterTransfers(
                effectiveBaseId, effectiveBaseId, effectiveBaseId, category, TransferStatus.COMPLETED, start, end, null
        );

        int totalTransfersIn = 0;
        int totalTransfersOut = 0;

        for (Transfer t : transfers) {
            boolean isIn = effectiveBaseId == null || t.getDestinationBase().getId().equals(effectiveBaseId);
            boolean isOut = effectiveBaseId != null && t.getSourceBase().getId().equals(effectiveBaseId);

            if (isIn && !isOut) {
                totalTransfersIn += t.getQuantity();
                NetMovementItemDto item = new NetMovementItemDto();
                item.setId(t.getId());
                item.setDate(t.getTransferDate());
                item.setAssetName(t.getAssetName());
                if (t.getAsset() != null) item.setAssetCode(t.getAsset().getAssetCode());
                item.setCategory(t.getCategory());
                item.setQuantity(t.getQuantity()); // Positive
                item.setUnit(t.getUnit());
                item.setBaseId(t.getDestinationBase().getId());
                item.setBaseName(t.getDestinationBase().getName());
                item.setPartnerBaseName(t.getSourceBase().getName());
                item.setReference(t.getReferenceNumber());
                item.setTransactionType("TRANSFER_IN");
                item.setDetails("Received from " + t.getSourceBase().getName());
                items.add(item);
            } else if (isOut) {
                totalTransfersOut += t.getQuantity();
                NetMovementItemDto item = new NetMovementItemDto();
                item.setId(t.getId());
                item.setDate(t.getTransferDate());
                item.setAssetName(t.getAssetName());
                if (t.getAsset() != null) item.setAssetCode(t.getAsset().getAssetCode());
                item.setCategory(t.getCategory());
                item.setQuantity(-t.getQuantity()); // Negative
                item.setUnit(t.getUnit());
                item.setBaseId(t.getSourceBase().getId());
                item.setBaseName(t.getSourceBase().getName());
                item.setPartnerBaseName(t.getDestinationBase().getName());
                item.setReference(t.getReferenceNumber());
                item.setTransactionType("TRANSFER_OUT");
                item.setDetails("Dispatched to " + t.getDestinationBase().getName());
                items.add(item);
            }
        }

        // Sort by date descending
        items.sort((a, b) -> b.getDate().compareTo(a.getDate()));

        String baseName = "Central Command (All Bases)";
        if (effectiveBaseId != null) {
            baseName = baseRepository.findById(effectiveBaseId).map(Base::getName).orElse("Assigned Base");
        }

        NetMovementResponseDto response = new NetMovementResponseDto();
        response.setTotalPurchases(totalPurchases);
        response.setTotalTransfersIn(totalTransfersIn);
        response.setTotalTransfersOut(totalTransfersOut);
        response.setNetMovement(totalPurchases + totalTransfersIn - totalTransfersOut);
        response.setBaseId(effectiveBaseId);
        response.setBaseName(baseName);
        response.setStartDate(start);
        response.setEndDate(end);
        response.setItems(items);

        return response;
    }

    private Long resolveEffectiveBaseId(User currentUser, Long requestedBaseId) {
        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            return currentUser.getBase() != null ? currentUser.getBase().getId() : null;
        }
        return requestedBaseId;
    }

    private String formatStatusLabel(AssetStatus status) {
        switch (status) {
            case AVAILABLE: return "Available";
            case IN_USE: return "In Use";
            case MAINTENANCE: return "Maintenance";
            case DEPLOYED: return "Deployed";
            case DECOMMISSIONED: return "Decommissioned";
            default: return status.name();
        }
    }

    private String getStatusColor(AssetStatus status) {
        switch (status) {
            case AVAILABLE: return "#2563eb"; // Blue
            case IN_USE: return "#10b981"; // Emerald
            case MAINTENANCE: return "#f59e0b"; // Amber
            case DEPLOYED: return "#6366f1"; // Indigo
            case DECOMMISSIONED: return "#ef4444"; // Rose
            default: return "#64748b";
        }
    }

    private String formatCategoryLabel(AssetCategory category) {
        switch (category) {
            case VEHICLE: return "Vehicles";
            case WEAPON: return "Weapons";
            case AMMUNITION: return "Ammunition";
            case COMMUNICATION: return "Communication";
            case IT_EQUIPMENT: return "IT Equipment";
            case OTHER: return "Other Gear";
            default: return category.name();
        }
    }

    private String getCategoryColor(AssetCategory category) {
        switch (category) {
            case VEHICLE: return "#2563eb";
            case WEAPON: return "#10b981";
            case AMMUNITION: return "#f59e0b";
            case COMMUNICATION: return "#8b5cf6";
            case IT_EQUIPMENT: return "#06b6d4";
            case OTHER: return "#64748b";
            default: return "#94a3b8";
        }
    }
}
