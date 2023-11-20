package com.obernal.portal_monitoratge.model.monitor;

import java.util.UUID;

public abstract class MonitorContext {

    protected final String id;
    protected final MonitorMetadata metadata;
    protected MonitorState state;
    protected long counter;

    protected MonitorContext(MonitorMetadata metadata) {
        this.id = UUID.randomUUID().toString();
        this.metadata = metadata;
        this.state = MonitorState.OK;
        this.counter = 0;
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
        switch (state) {
            case OK, RECOVERY -> {
                state = MonitorState.FIRST_ALERT;
                counter = 1;
            }
            case FIRST_ALERT, ALERT, INSIST -> {
                state = mustInsist() ? MonitorState.INSIST : MonitorState.ALERT;
                counter++;
            }
        }
    }

    private void changeStateAfterOk() {
        switch (state) {
            case OK, RECOVERY -> {
                state = MonitorState.OK;
                counter++;
            }
            case FIRST_ALERT, ALERT, INSIST -> {
                state = MonitorState.RECOVERY;
                counter = 1;
            }
        }
    }

    private boolean mustInsist() {
        long insistAfter = getMetadata().getInsistAfter();
        return insistAfter == 0 || (counter+1) % insistAfter == 0;
    }

}
