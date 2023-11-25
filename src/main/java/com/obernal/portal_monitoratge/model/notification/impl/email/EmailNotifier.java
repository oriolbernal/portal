package com.obernal.portal_monitoratge.model.notification.impl.email;

import com.obernal.portal_monitoratge.app.clients.email.EmailClient;
import com.obernal.portal_monitoratge.model.alert.Alert;
import com.obernal.portal_monitoratge.model.monitor.MonitorResult;
import com.obernal.portal_monitoratge.model.notification.ChannelType;
import com.obernal.portal_monitoratge.model.notification.Notifier;

import java.util.ArrayList;
import java.util.List;

public class EmailNotifier<R extends MonitorResult> extends Notifier<R> {
    private final EmailClient emailClient;
    private String to;
    private String[] cc;
    private String[] bcc;

    public EmailNotifier(EmailClient emailClient, long insistAfter, String to, String[] cc, String[] bcc) {
        super(insistAfter);
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.emailClient = emailClient;
    }

    @Override
    public ChannelType getChannel() {
        return ChannelType.EMAIL;
    }

    @Override
    public Alert alert(R result, List<String> messages) {
        return new Alert(messages);
    }

    @Override
    public Alert insist(R result) {
        return new Alert(new ArrayList<>());
    }

    @Override
    public Alert recover(R result) {
        return null;
    }

}
