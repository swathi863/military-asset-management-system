package com.military.asset.dto;

import com.military.asset.entity.Purchase;
import com.military.asset.entity.Transfer;

import java.util.List;

public class NetMovementDetailDto {
    private int totalPurchasesQuantity;
    private int totalTransfersInQuantity;
    private int totalTransfersOutQuantity;
    private int netMovement;

    private List<Purchase> purchases;
    private List<Transfer> transfersIn;
    private List<Transfer> transfersOut;

    public NetMovementDetailDto() {}

    public NetMovementDetailDto(int totalPurchasesQuantity, int totalTransfersInQuantity, int totalTransfersOutQuantity, List<Purchase> purchases, List<Transfer> transfersIn, List<Transfer> transfersOut) {
        this.totalPurchasesQuantity = totalPurchasesQuantity;
        this.totalTransfersInQuantity = totalTransfersInQuantity;
        this.totalTransfersOutQuantity = totalTransfersOutQuantity;
        this.netMovement = totalPurchasesQuantity + totalTransfersInQuantity - totalTransfersOutQuantity;
        this.purchases = purchases;
        this.transfersIn = transfersIn;
        this.transfersOut = transfersOut;
    }

    public int getTotalPurchasesQuantity() { return totalPurchasesQuantity; }
    public void setTotalPurchasesQuantity(int totalPurchasesQuantity) { this.totalPurchasesQuantity = totalPurchasesQuantity; }

    public int getTotalTransfersInQuantity() { return totalTransfersInQuantity; }
    public void setTotalTransfersInQuantity(int totalTransfersInQuantity) { this.totalTransfersInQuantity = totalTransfersInQuantity; }

    public int getTotalTransfersOutQuantity() { return totalTransfersOutQuantity; }
    public void setTotalTransfersOutQuantity(int totalTransfersOutQuantity) { this.totalTransfersOutQuantity = totalTransfersOutQuantity; }

    public int getNetMovement() { return netMovement; }
    public void setNetMovement(int netMovement) { this.netMovement = netMovement; }

    public List<Purchase> getPurchases() { return purchases; }
    public void setPurchases(List<Purchase> purchases) { this.purchases = purchases; }

    public List<Transfer> getTransfersIn() { return transfersIn; }
    public void setTransfersIn(List<Transfer> transfersIn) { this.transfersIn = transfersIn; }

    public List<Transfer> getTransfersOut() { return transfersOut; }
    public void setTransfersOut(List<Transfer> transfersOut) { this.transfersOut = transfersOut; }
}
