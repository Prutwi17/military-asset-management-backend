package com.military.assetmanagement.service;

import com.military.assetmanagement.dto.*;
import com.military.assetmanagement.entity.*;
import com.military.assetmanagement.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportsService {

    private final AssetRepository assetRepository;
    private final BaseRepository baseRepository;
    private final PurchaseRepository purchaseRepository;
    private final TransferRepository transferRepository;
    private final AssignmentRepository assignmentRepository;
    private final ExpenditureRepository expenditureRepository;

    public ReportsService(AssetRepository assetRepository,
                          BaseRepository baseRepository,
                          PurchaseRepository purchaseRepository,
                          TransferRepository transferRepository,
                          AssignmentRepository assignmentRepository,
                          ExpenditureRepository expenditureRepository) {
        this.assetRepository = assetRepository;
        this.baseRepository = baseRepository;
        this.purchaseRepository = purchaseRepository;
        this.transferRepository = transferRepository;
        this.assignmentRepository = assignmentRepository;
        this.expenditureRepository = expenditureRepository;
    }

    @Transactional(readOnly = true)
    public ReportsDto.FullReportsResponseDto getReports(User currentUser, Long baseId, AssetCategory category,
                                                       LocalDate startDate, LocalDate endDate) {
        Long effectiveBaseId = baseId;
        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            effectiveBaseId = currentUser.getBase() != null ? currentUser.getBase().getId() : null;
        }

        LocalDate start = startDate != null ? startDate : LocalDate.now().minusMonths(6);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        // 1. Fetch matching assets
        List<Asset> assets = assetRepository.filterAssets(effectiveBaseId, category, null, null, null);
        int totalQuantity = assets.stream().mapToInt(Asset::getQuantity).sum();

        // Utilization metrics
        ReportsDto.AssetUtilizationSummaryDto utilization = new ReportsDto.AssetUtilizationSummaryDto();
        utilization.setTotalAssetTypes(assets.size());
        utilization.setTotalQuantity(totalQuantity);

        int avail = 0, inUse = 0, maint = 0, dep = 0, decomm = 0;
        for (Asset a : assets) {
            switch (a.getStatus()) {
                case AVAILABLE: avail += a.getQuantity(); break;
                case IN_USE: inUse += a.getQuantity(); break;
                case MAINTENANCE: maint += a.getQuantity(); break;
                case DEPLOYED: dep += a.getQuantity(); break;
                case DECOMMISSIONED: decomm += a.getQuantity(); break;
            }
        }
        utilization.setAvailableQuantity(avail);
        utilization.setInUseQuantity(inUse);
        utilization.setMaintenanceQuantity(maint);
        utilization.setDeployedQuantity(dep);
        utilization.setDecommissionedQuantity(decomm);
        int operational = avail + inUse + dep;
        double rate = totalQuantity > 0 ? (operational * 100.0) / totalQuantity : 100.0;
        utilization.setCombatReadinessRate(Math.round(rate * 10.0) / 10.0);

        // 2. Base Inventory Summary
        List<Base> allBases = baseRepository.findAll();
        if (effectiveBaseId != null) {
            Long finalBaseId = effectiveBaseId;
            allBases = allBases.stream().filter(b -> b.getId().equals(finalBaseId)).collect(Collectors.toList());
        }

        List<ReportsDto.BaseInventorySummaryDto> baseSummaries = new ArrayList<>();
        for (Base b : allBases) {
            List<Asset> baseAssets = assetRepository.filterAssets(b.getId(), category, null, null, null);
            int baseQty = baseAssets.stream().mapToInt(Asset::getQuantity).sum();
            BigDecimal baseVal = BigDecimal.ZERO;
            for (Asset a : baseAssets) {
                if (a.getPurchasePrice() != null) {
                    baseVal = baseVal.add(a.getPurchasePrice().multiply(BigDecimal.valueOf(a.getQuantity())));
                }
            }
            List<Assignment> activeAsgn = assignmentRepository.filterAssignments(b.getId(), AssignmentStatus.ACTIVE, null, null, null);
            int asgnQty = activeAsgn.stream().mapToInt(Assignment::getQuantity).sum();

            baseSummaries.add(new ReportsDto.BaseInventorySummaryDto(
                    b.getId(),
                    b.getName(),
                    b.getCode(),
                    b.getLocation(),
                    baseAssets.size(),
                    baseQty,
                    baseVal,
                    asgnQty
            ));
        }

        // 3. Equipment Distribution Summary
        Map<AssetCategory, Integer> catQtyMap = new EnumMap<>(AssetCategory.class);
        Map<AssetCategory, Integer> catTypeMap = new EnumMap<>(AssetCategory.class);
        Map<AssetCategory, BigDecimal> catValMap = new EnumMap<>(AssetCategory.class);

        for (Asset a : assets) {
            catQtyMap.put(a.getCategory(), catQtyMap.getOrDefault(a.getCategory(), 0) + a.getQuantity());
            catTypeMap.put(a.getCategory(), catTypeMap.getOrDefault(a.getCategory(), 0) + 1);
            if (a.getPurchasePrice() != null) {
                BigDecimal itemVal = a.getPurchasePrice().multiply(BigDecimal.valueOf(a.getQuantity()));
                catValMap.put(a.getCategory(), catValMap.getOrDefault(a.getCategory(), BigDecimal.ZERO).add(itemVal));
            }
        }

        List<ReportsDto.EquipmentDistributionSummaryDto> eqDist = new ArrayList<>();
        for (Map.Entry<AssetCategory, Integer> entry : catQtyMap.entrySet()) {
            double pct = totalQuantity > 0 ? (entry.getValue() * 100.0) / totalQuantity : 0.0;
            eqDist.add(new ReportsDto.EquipmentDistributionSummaryDto(
                    entry.getKey(),
                    formatCategoryLabel(entry.getKey()),
                    entry.getValue(),
                    catTypeMap.getOrDefault(entry.getKey(), 0),
                    catValMap.getOrDefault(entry.getKey(), BigDecimal.ZERO),
                    Math.round(pct * 10.0) / 10.0
            ));
        }
        eqDist.sort((a, b) -> Integer.compare(b.getTotalQuantity(), a.getTotalQuantity()));

        // 4. Transaction Histories
        List<PurchaseDto> purchases = purchaseRepository.filterPurchases(effectiveBaseId, category, start, end, null)
                .stream().map(PurchaseDto::fromEntity).collect(Collectors.toList());

        List<TransferDto> transfers = transferRepository.filterTransfers(effectiveBaseId, effectiveBaseId, effectiveBaseId, category, null, start, end, null)
                .stream().map(TransferDto::fromEntity).collect(Collectors.toList());

        List<AssignmentDto> assignments = assignmentRepository.filterAssignments(effectiveBaseId, null, start, end, null)
                .stream().map(AssignmentDto::fromEntity).collect(Collectors.toList());

        List<ExpenditureDto> expenditures = expenditureRepository.filterExpenditures(effectiveBaseId, category, start, end, null)
                .stream().map(ExpenditureDto::fromEntity).collect(Collectors.toList());

        ReportsDto.FullReportsResponseDto full = new ReportsDto.FullReportsResponseDto();
        full.setUtilization(utilization);
        full.setBaseInventory(baseSummaries);
        full.setEquipmentDistribution(eqDist);
        full.setPurchaseHistory(purchases);
        full.setTransferHistory(transfers);
        full.setAssignmentHistory(assignments);
        full.setExpenditureHistory(expenditures);

        return full;
    }

    private String formatCategoryLabel(AssetCategory category) {
        switch (category) {
            case VEHICLE: return "Vehicles & Armor";
            case WEAPON: return "Weapons & Small Arms";
            case AMMUNITION: return "Munitions & Ordnance";
            case COMMUNICATION: return "Signal & Communications";
            case IT_EQUIPMENT: return "Tactical IT & Computing";
            case OTHER: return "Optics & Field Gear";
            default: return category.name();
        }
    }
}
