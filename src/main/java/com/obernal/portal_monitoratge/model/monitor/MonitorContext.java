package com.obernal.portal_monitoratge.model.monitor;

import java.util.UUID;

public abstract class MonitorContext {

    protected final String id;
    protected final MonitorMetadata metadata;
    protected MonitorState state;
    protected int counter;

    protected MonitorContext(MonitorMetadata metadata) {
        this.id = UUID.randomUUID().toString();
        this.metadata = metadata;
        this.state = MonitorState.OK;
    }

    public abstract MonitorType getType();

    public String getId() {
        return id;
    }

    public MonitorMetadata getMetadata() {
        return metadata;
    }

    public void update(MonitorContext newContext) {
        this.metadata.update(newContext.getMetadata());
    }

    public void toggle() {
        metadata.toggle();
    }

    public boolean isActive() {
        return metadata.isActive();
    }

    public void changeState(boolean alert) {
         if (alert) {
             changeStateAfterAlert();
         } else {
             changeStateAfterOk();
         }
    }

    private void changeStateAfterAlert() {
        int insistAfter = getMetadata().getInsistAfter();
        boolean insist = insistAfter == 0 || counter % insistAfter == 0;
        switch (state) {
            case OK, RECOVERY -> {
                state = MonitorState.FIRST_ALERT;
                counter = 1;
            }
            case FIRST_ALERT, ALERT, INSIST -> {
                state = insist ? MonitorState.INSIST : MonitorState.ALERT;
                counter = counter + 1;
            }
        }
    }

    private void changeStateAfterOk() {
        counter = 0;
        switch (state) {
            case OK, RECOVERY -> state = MonitorState.OK;
            case FIRST_ALERT, ALERT, INSIST -> state = MonitorState.RECOVERY;
        }
    }

}
