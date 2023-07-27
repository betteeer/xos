package com.inossem.oms.base.svc.enums;

public enum StoStatus {
    OPEN("open"),
    IN_TRANSIT("intransit"),
    RECEIVED("received"),
    CANCELLED("cancelled");

    private final String status;

    StoStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
