package com.northstar.crm.ui;

import com.vaadin.flow.component.*;import com.vaadin.flow.component.button.*;import com.vaadin.flow.component.html.*;import com.vaadin.flow.component.icon.*;import com.vaadin.flow.component.notification.Notification;import com.vaadin.flow.component.orderedlayout.*;import com.vaadin.flow.component.textfield.TextField;import com.vaadin.flow.data.renderer.ComponentRenderer;import java.util.function.Function;
public final class UiKit {
 private UiKit(){}
 public static VerticalLayout page(String eyebrow,String title,String description,Component... content){var head=new VerticalLayout(new Span(eyebrow),new H1(title),new Paragraph(description));head.addClassName("page-head");head.setPadding(false);head.setSpacing(false);var page=new VerticalLayout(head);page.add(content);page.addClassName("page");page.setSizeFull();return page;}
 public static Div metric(String label,String value,String detail,String tone){var number=new Span(value);number.addClassName("metric-value");var note=new Span(detail);note.addClassName("metric-note");var d=new Div(new Span(label),number,note);d.addClassNames("metric",tone);return d;}
 public static Span badge(Object value){var s=new Span(human(value));s.addClassNames("badge","badge-"+String.valueOf(value).toLowerCase().replace('_','-'));return s;}
 public static String human(Object v){if(v==null)return "—";var s=String.valueOf(v).toLowerCase().replace('_',' ');return Character.toUpperCase(s.charAt(0))+s.substring(1);}
 public static Button primary(String text,ComponentEventListener<ClickEvent<Button>> click){var b=new Button(text,click);b.addThemeVariants(ButtonVariant.LUMO_PRIMARY);return b;}
 public static Button danger(String text,ComponentEventListener<ClickEvent<Button>> click){var b=new Button(text,click);b.addThemeVariants(ButtonVariant.LUMO_ERROR);return b;}
 public static void success(String text){Notification.show(text,3000,Notification.Position.BOTTOM_END).addThemeVariants(com.vaadin.flow.component.notification.NotificationVariant.LUMO_SUCCESS);}
 public static void error(Throwable ex){var message=ex.getMessage()==null?"The action could not be completed":ex.getMessage();Notification.show(message,5000,Notification.Position.BOTTOM_END).addThemeVariants(com.vaadin.flow.component.notification.NotificationVariant.LUMO_ERROR);}
 public static TextField search(String placeholder){var f=new TextField();f.setPlaceholder(placeholder);f.setPrefixComponent(VaadinIcon.SEARCH.create());f.setClearButtonVisible(true);f.setAriaLabel(placeholder);return f;}
}
