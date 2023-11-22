package com.obernal.portal_monitoratge.model.alert;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class AlertMetadata {

    private final LocalDateTime created;
    private LocalDateTime updated;
    private long insistAfter;

    public AlertMetadata(long insistAfter) {
        created = LocalDateTime.now();
        updated = null;
        this.insistAfter = insistAfter;
    }

    public AlertMetadata(LocalDateTime created, LocalDateTime updated, long insistAfter) {
        this.created = created;
        this.updated = updated;
        this.insistAfter = insistAfter;
    }

    public long getInsistAfter() {
        return insistAfter;
    }
}
