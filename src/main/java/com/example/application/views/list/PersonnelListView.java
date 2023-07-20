package com.example.application.views.list;


import com.example.application.data.entity.Log;
import com.example.application.data.entity.Personnel;
import com.example.application.data.presenter.PersonnelPresenter;
import com.example.application.data.service.LogService;
import com.example.application.util.ResourceBundleUtil;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import javax.annotation.security.PermitAll;


@Component
@Scope("prototype")
@Route(value = "personnel", layout = MainLayout.class)
@PageTitle("Emre HBYS")

@PermitAll
public class PersonnelListView extends VerticalLayout {

    private Grid<Personnel> grid = new Grid<>(Personnel.class);
    private TextField filterText = new TextField();
    private PersonnelForm form;
    private PersonnelPresenter presenter;
    private Button relateButton;
    private Personnel selectedPersonnel;
    private ResourceBundleUtil rb;
    private String currentPrincipalName;
    private Log.OperationType operationType;

    private String lang;

    public PersonnelListView(PersonnelPresenter presenter) {
        this.presenter = presenter;
        lang = VaadinSession.getCurrent().getAttribute("language").toString();
        rb = new ResourceBundleUtil(lang);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UI.getCurrent().getPage().setTitle(rb.getString("personnelListTitle"));

        currentPrincipalName = authentication.getName();
        addClassName("list-view");
        setSizeFull();
        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        form = new PersonnelForm(lang);
        form.setWidth("25em");
        form.addListener(PersonnelForm.SaveEvent.class, this::savePersonnel);
        form.addListener(PersonnelForm.DeleteEvent.class, this::deletePersonnel);
        form.addListener(PersonnelForm.CloseEvent.class, e -> closeEditor());
    }

    private void configureGrid() {
        grid.addClassNames("contact-grid");
        grid.setSizeFull();
        grid.setColumns("personnelId", "name", "lastName");

        grid.addColumn(personnel -> PersonnelPresenter.formatPhoneNumber(personnel.getPhone())).setAutoWidth(true).setKey("phone");


        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() == null) {
                relateButton.setEnabled(false);
            } else {
                relateButton.setEnabled(true);
            }
            editPersonnel(event.getValue());
        });

        grid.getColumnByKey("personnelId").setHeader(rb.getString("personnelId"));
        grid.getColumnByKey("name").setHeader(rb.getString("name"));
        grid.getColumnByKey("lastName").setHeader(rb.getString("lastName"));
        grid.getColumnByKey("phone").setHeader(rb.getString("phone"));
    }


    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder(rb.getString("filterTextPlaceHolder"));
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addPersonnelButton = new Button(rb.getString("addPersonnel"));
        addPersonnelButton.addClickListener(click -> addPersonnel());

        relateButton = new Button(rb.getString("relate"), event -> UI.getCurrent().navigate(PersonnelRelationView.class, new RouteParameters("pid", selectedPersonnel == null ? "" : ("" + selectedPersonnel.getPersonnelId()))));
        relateButton.setEnabled(false);

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addPersonnelButton, relateButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void savePersonnel(PersonnelForm.SaveEvent event) {

        if (operationType == Log.OperationType.CREATE) {
            event.getPersonnel().setCreatedUserId(currentPrincipalName);
        } else {
            event.getPersonnel().setUpdatedUserId(currentPrincipalName);
        }
        presenter.savePersonnel(event.getPersonnel());
        LogService.log(currentPrincipalName, operationType, event.getPersonnel().getClass(), Long.toString(event.getPersonnel().getPersonnelId()));

        updateList();
        closeEditor();
    }

    private void hasRelationNotification(Personnel p) {

        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.setDuration(3000);
        Div text = new Div(new Text(p.getName() + " " + rb.getString("hasRelationError")));

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> {
            notification.close();
        });

        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(Alignment.CENTER);

        notification.add(layout);
        notification.open();
    }

    private void deletePersonnel(PersonnelForm.DeleteEvent event) {
        if (!event.getPersonnel().getPatienceSet().isEmpty()) {
            hasRelationNotification(event.getPersonnel());
        } else {
            presenter.deletePersonnel(event.getPersonnel());
            LogService.log(currentPrincipalName, Log.OperationType.DELETE, event.getPersonnel().getClass(), Long.toString(event.getPersonnel().getPersonnelId()));
            updateList();
        }

        closeEditor();
    }

    public void editPersonnel(Personnel personnel) {
        System.out.println("EDIT PERSONnEL CALISTI");
        selectedPersonnel = personnel;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        if (personnel == null) {
            closeEditor();
        } else {

            if (personnel.getPersonnelId() == null || personnel.getPersonnelId().equals(Long.valueOf(0))) {

                operationType = Log.OperationType.CREATE;
            } else {
                operationType = Log.OperationType.UPDATE;
            }


            form.setPersonnel(personnel);
            form.setVisible(true);
            addClassName("editing");

        }
    }

    private void addPersonnel() {

        grid.asSingleSelect().clear();
        editPersonnel(new Personnel());

    }

    private void closeEditor() {
        form.setPersonnel(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        grid.setItems(presenter.findAllPersonnel(filterText.getValue()));
    }
}
