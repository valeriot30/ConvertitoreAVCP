package com.pointservice.demo.model;

public enum AttributeType {
    CIG(0), // colonna 0
    OGGETTO(1), // colonna 1
    DENOMINAZIONE(2), // ....
    COD_FISCALE(3),
    AGG_COD_FISCALE(4),
    AGG_RAGIONE_SOCIALE(5),
    IMPORTO_NETTO(6),
    DATA_INIZIO(7),
    DATA_ULTIMAZIONE(8),
    SOMME_LIQUIDATE(9),
    COD_FISCALE_INVITATO1(10),
    RAGIONE_SOCIALE_INVITATO1(11),
    RUOLO_INVITATO1(12),
    SCELTA_CONTRAENTE(13);

    private final int value;

    AttributeType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
