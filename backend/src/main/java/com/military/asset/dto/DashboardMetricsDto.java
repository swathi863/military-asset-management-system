package com.military.asset.dto;

public class DashboardMetricsDto {
    private int openingBalance;
    private int purchasesCount;
    private int transfersIn;
    private int transfersOut;
    private int netMovement;
    private int assignedAssets;
    private int expendedAssets;
    private int closingBalance;
    private int availableStock;

    public DashboardMetricsDto() {}

    public DashboardMetricsDto(int openingBalance, int purchasesCount, int transfersIn, int transfersOut, int assignedAssets, int expendedAssets) {
        this.openingBalance = openingBalance;
        this.purchasesCount = purchasesCount;
        this.transfersIn = transfersIn;
        this.transfersOut = transfersOut;
        this.netMovement = purchasesCount + transfersIn - transfersOut;
        this.assignedAssets = assignedAssets;
        this.expendedAssets = expendedAssets;
        this.closingBalance = openingBalance + this.netMovement - expendedAssets;
        this.availableStock = Math.max(0, this.closingBalance - assignedAssets);
    }

    public int getOpeningBalance() { return openingBalance; }
    public void setOpeningBalance(int openingBalance) { this.openingBalance = openingBalance; }

    public int getPurchasesCount() { return purchasesCount; }
    public void setPurchasesCount(int purchasesCount) { this.purchasesCount = purchasesCount; }

    public int getTransfersIn() { return transfersIn; }
    public void setTransfersIn(int transfersIn) { this.transfersIn = transfersIn; }

    public int getTransfersOut() { return transfersOut; }
    public void setTransfersOut(int transfersOut) { this.transfersOut = transfersOut; }

    public int getNetMovement() { return netMovement; }
    public void setNetMovement(int netMovement) { this.netMovement = netMovement; }

    public int getAssignedAssets() { return assignedAssets; }
    public void setAssignedAssets(int assignedAssets) { this.assignedAssets = assignedAssets; }

    public int getExpendedAssets() { return expendedAssets; }
    public void setExpendedAssets(int expendedAssets) { this.expendedAssets = expendedAssets; }

    public int getClosingBalance() { return closingBalance; }
    public void setClosingBalance(int closingBalance) { this.closingBalance = closingBalance; }

    public int getAvailableStock() { return availableStock; }
    public void setAvailableStock(int availableStock) { this.availableStock = availableStock; }
}
