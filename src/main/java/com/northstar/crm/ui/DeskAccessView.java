package com.northstar.crm.ui;

import com.northstar.crm.service.AccountService;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("desk-access") @PageTitle("Desk access · Northstar") @AnonymousAllowed
public class DeskAccessView extends VerticalLayout {
    public DeskAccessView(AccountService accounts) {
        addClassName("public-form");
        var email = new EmailField("Work email"); email.setWidthFull();
        var passphrase = new PasswordField("Desk passphrase"); passphrase.setWidthFull();
        var result = new Paragraph();
        var card = new VerticalLayout(new H1("Support desk access"),
                new Paragraph("Continue with the desk passphrase issued by platform administration."),
                email, passphrase, UiKit.primary("Continue", e -> {
                    try {
                        var ok = accounts.checkDeskPassphrase(email.getValue(), passphrase.getValue());
                        result.setText(ok ? "Desk console unlocked." : "Desk access could not be confirmed.");
                    } catch (Exception ex) { UiKit.error(ex); }
                }), result);
        card.addClassName("login-card");
        add(card); setSizeFull(); setAlignItems(Alignment.CENTER); setJustifyContentMode(JustifyContentMode.CENTER);
    }
}
