package com.example.application.views.list;

import com.example.application.data.entity.Patience;
import com.example.application.data.entity.Log;
import com.example.application.data.presenter.PatiencePresenter;
import com.example.application.data.service.LogService;
import com.example.application.util.ResourceBundleUtil;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
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
@Route(value = "", layout = MainLayout.class)
@PageTitle("Emre HBYS")
@PermitAll
public class PatienceListView extends VerticalLayout {
    private Grid<Patience> grid = new Grid<>(Patience.class);
    private TextField filterText = new TextField();
    private PatienceForm form;
    private PatiencePresenter presenter;
    private Patience selectedPatience;
    private Button relateButton;
    private ResourceBundleUtil rb ;
    private Log.OperationType operationType;
    private String currentPrincipalName;
    private Authentication authentication;
    private String lang;
    public PatienceListView(PatiencePresenter presenter) {
        System.out.println("PATIENCE CONSTRUCTOR ÇALIŞTI");
        this.presenter = presenter;

        authentication = SecurityContextHolder.getContext().getAuthentication();
        currentPrincipalName = authentication.getName();
        lang = VaadinSession.getCurrent().getAttribute("language").toString();
        rb = new ResourceBundleUtil(lang);

        UI.getCurrent().getPage().setTitle(rb.getString("patienceListTitle"));

        addClassName("list-view");
        setSizeFull();
        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
        updateList();
        closeEditor();
       
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("LOGGEDIN USER: " + authentication.getPrincipal());
        System.out.println("IS LOGGEDIN: " + authentication.isAuthenticated());

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
        form = new PatienceForm(lang);
        form.setWidth("25em");
        form.addListener(PatienceForm.SaveEvent.class, this::savePatience);
        form.addListener(PatienceForm.DeleteEvent.class, this::deletePatience);
        form.addListener(PatienceForm.CloseEvent.class, e -> closeEditor());
    }

    private void configureGrid() {

        grid.addClassNames("contact-grid");
        grid.setSizeFull();
        grid.setColumns("TCNO", "name", "lastName", "email");

        grid.addColumn(Patience -> PatiencePresenter.formatPhoneNumber(Patience.getPhone())).setKey("phone");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.addComponentColumn(patience -> {
            HorizontalLayout genderField = new HorizontalLayout();
            Label label = new Label(rb.getString(patience.getGender()));

            genderField.add(createGenderIcon(patience.getGender()),label);
            return genderField;

        }).setHeader("Cinsiyet").setAutoWidth(true).setKey("gender");

        grid.getColumnByKey("name").setHeader(rb.getString("name"));
        grid.getColumnByKey("TCNO").setHeader(rb.getString("TCNO"));
        grid.getColumnByKey("lastName").setHeader(rb.getString("lastName"));
        grid.getColumnByKey("email").setHeader(rb.getString("email"));
        grid.getColumnByKey("phone").setHeader(rb.getString("phone"));
        grid.getColumnByKey("gender").setHeader(rb.getString("gender"));

        grid.asSingleSelect().addValueChangeListener(event -> {
            editPatience(event.getValue()); 
            if(event.getValue() == null){
                relateButton.setEnabled(false);
            }else{
                relateButton.setEnabled(true);
            } 
        } );
    }


    private Icon createGenderIcon(String gender) {
        Icon icon;
        if (gender.equalsIgnoreCase("erkek")) {
            icon = VaadinIcon.MALE.create();
            icon.setColor("blue");
        } else if (gender.equalsIgnoreCase("kadın")) {
            icon = VaadinIcon.FEMALE.create();
            icon.setColor("pink");
        } else {
            icon = VaadinIcon.USER.create();
            icon.setColor("gray");
        }
        return icon;
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder(rb.getString("filterTextPlaceHolder"));
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addPatienceButton = new Button(rb.getString("addPatience"));
        addPatienceButton.addClickListener(click -> addPatience());
        addPatienceButton.addClickShortcut(Key.KEY_H);
        


        relateButton = new Button(rb.getString("relate"), event -> UI.getCurrent().navigate(PatienceRelationView.class, new RouteParameters("PatienceTC", selectedPatience == null ? "": selectedPatience.getTCNO() )  ));
        relateButton.setEnabled(false);
        HorizontalLayout toolbar = new HorizontalLayout(filterText, addPatienceButton,relateButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }




    private void savePatience(PatienceForm.SaveEvent event) {


        if(operationType == Log.OperationType.CREATE) {
            event.getPatience().setCreatedUserId(currentPrincipalName);
        }else{
            event.getPatience().setUpdatedUserId(currentPrincipalName);
        }
        presenter.savePatience(event.getPatience());
        LogService.log(currentPrincipalName, operationType,event.getPatience().getClass(),event.getPatience().getTCNO() );

        updateList();
        closeEditor();
    }

    private void hasRelationNotification(Patience h){

        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

        Div text = new Div(new Text(h.getName() + " " + rb.getString("hasRelationError")));

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> {
            notification.close();
        });

        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(Alignment.CENTER);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.setDuration(3000);
        notification.add(layout);
        notification.open();
    }
    private void deletePatience(PatienceForm.DeleteEvent event) {
        if(!event.getPatience().getPersonnelSet().isEmpty()){
            hasRelationNotification(event.getPatience());
        }
        presenter.deletePatience(event.getPatience());
        LogService.log(currentPrincipalName, Log.OperationType.DELETE ,event.getPatience().getClass(),event.getPatience().getTCNO() );

        updateList();
        closeEditor();
    }

    public void editPatience(Patience Patience) {
        selectedPatience = Patience;


        if (Patience == null) {
            closeEditor();
        } else {
            if(Patience.getTCNO() == null){
                operationType = Log.OperationType.CREATE;
            }else{
                operationType = Log.OperationType.UPDATE;
            }

            form.setPatience(Patience);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void addPatience() {
        grid.asSingleSelect().clear();
        editPatience(new Patience());
    }

    private void closeEditor() {
        form.setPatience(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        grid.setItems(presenter.findAllPatience(filterText.getValue()));
    }
}
