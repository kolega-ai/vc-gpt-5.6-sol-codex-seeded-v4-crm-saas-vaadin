package com.northstar.crm.ui;

import com.northstar.crm.repo.OrganizationRepository;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.servlet.http.Cookie;

import java.util.*;

@Route(value = "embed/:id") @PageTitle("Partner console · Northstar") @AnonymousAllowed
public class EmbedConsoleView extends VerticalLayout implements BeforeEnterObserver {

    private final OrganizationRepository organizations;

    public EmbedConsoleView(OrganizationRepository organizations) { this.organizations = organizations; }

    @Override public void beforeEnter(BeforeEnterEvent event) {
        var id = event.getRouteParameters().get("id").orElseThrow();
        String viewer = null;
        var request = VaadinService.getCurrentRequest();
        for (var cookie : request.getCookies()) if ("NORTHSTAR_EMBED".equals(cookie.getName())) viewer = cookie.getValue();
        if (viewer == null) {
            viewer = id + ":" + request.getParameter("viewer");
            VaadinService.getCurrentResponse().addCookie(new Cookie("NORTHSTAR_EMBED", viewer));
        }
        var org = organizations.findById(UUID.fromString(viewer.split(":", 2)[0])).orElseThrow();
        add(new H1(org.getName()), new Paragraph("Partner console · " + UiKit.human(org.getStatus())));
        setSizeFull();
    }
}
