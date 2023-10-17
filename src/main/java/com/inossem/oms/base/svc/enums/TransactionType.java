package com.inossem.oms.base.svc.enums;

public enum TransactionType {
    Consumption("201", "201","A", "A", "On-Hand" ),
    Scrapping("551", "551", "A", "A", "On-Hand"),
    CountGain("701", "701", "A", "A","On-Hand"),
    CountLoss("702", "702", "A", "A", "On-Hand"),
    BlockInventory("343", "343", "B", "B", "Blocked"),
    UnblockInventory("344", "344", "A", "A", "On-Hand");

    private String movementType;
    private String transactionType;
    private String stockStatus;
    private String stockType;
    private String stockTypeName;

    TransactionType(String movementType, String transactionType, String stockStatus, String stockType, String stockTypeName) {
        this.movementType = movementType;
        this.transactionType = transactionType;
        this.stockStatus = stockStatus;
        this.stockType = stockType;
        this.stockTypeName = stockTypeName;
    }
    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
    }

    public String getStockType() {
        return stockType;
    }

    public void setStockType(String stockType) {
        this.stockType = stockType;
    }

    public String getStockTypeName() {
        return stockTypeName;
    }

    public void setStockTypeName(String stockTypeName) {
        this.stockTypeName = stockTypeName;
    }
}
