package com.example.application.views.list;

import com.example.application.data.entity.Hasta;
import com.example.application.data.entity.Log;
import com.example.application.data.entity.Personel;
import com.example.application.data.presenter.HastaPresenter;
import com.example.application.data.presenter.PersonelPresenter;
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

import java.util.ResourceBundle;

import javax.annotation.security.PermitAll;


//PERSONEL ID'si SIFIR OLUNCA HATA VERİYOR!!!


@Component
@Scope("prototype")
@Route(value = "personel", layout = MainLayout.class)
@PageTitle("Personel Listesi | HBYS")
@PermitAll
public class PersonelListView extends VerticalLayout {

    private Grid<Personel> grid = new Grid<>(Personel.class);
    private TextField filterText = new TextField();
    private PersonelForm form;
    private PersonelPresenter presenter;
    private Button relateButton;
    private Personel selectedPersonel;
    private ResourceBundleUtil rb ;
    private String currentPrincipalName;
    private Log.OperationType operationType;


    public PersonelListView(PersonelPresenter presenter) {
        this.presenter = presenter;
        rb = new ResourceBundleUtil((VaadinSession.getCurrent().getAttribute("language").toString()));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
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
    form = new PersonelForm();
    form.setWidth("25em");
    form.addListener(PersonelForm.SaveEvent.class, this::savePersonel);
    form.addListener(PersonelForm.DeleteEvent.class, this::deletePersonel);
    form.addListener(PersonelForm.CloseEvent.class, e -> closeEditor());
}

    private void configureGrid() {
        grid.addClassNames("contact-grid");
        grid.setSizeFull();
        grid.setColumns("personelId","isim", "soyisim");

        grid.addColumn(personel -> PersonelPresenter.formatPhoneNumber(personel.getTelefon()))
                .setHeader("Telefon")
                .setAutoWidth(true).setKey("telefon");


        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event ->{
            if(event.getValue() == null){
                relateButton.setEnabled(false);
            }else{
                relateButton.setEnabled(true);
            } 
            editPersonel(event.getValue());
        });

        grid.getColumnByKey("personelId").setHeader(rb.getString("personnelId"));
        grid.getColumnByKey("isim").setHeader(rb.getString("name"));
        grid.getColumnByKey("soyisim").setHeader(rb.getString("lastName"));
        grid.getColumnByKey("telefon").setHeader(rb.getString("phone"));
    }



    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder(rb.getString("filterTextPlaceHolder"));
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addPersonelButton = new Button(rb.getString("addPersonnel"));
        addPersonelButton.addClickListener(click -> addPersonel());

        relateButton = new Button(rb.getString("relate"), event -> UI.getCurrent().navigate(PersonelRelationView.class, new RouteParameters("pid", selectedPersonel == null ? "": ("" + selectedPersonel.getPersonelId()) ) ));
        relateButton.setEnabled(false);

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addPersonelButton, relateButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void savePersonel(PersonelForm.SaveEvent event) {

        if(operationType == Log.OperationType.CREATE){
            event.getPersonel().setCreatedUserId(currentPrincipalName);
        }else{
            event.getPersonel().setUpdatedUserId(currentPrincipalName);
        }
        presenter.savePersonel(event.getPersonel());
        LogService.log(currentPrincipalName,operationType, event.getPersonel().getClass(),Long.toString(event.getPersonel().getPersonelId()));

        updateList();
        closeEditor();
    }
    private void hasRelationNotification(Personel p){

        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.setDuration(3000);
        Div text = new Div(new Text(p.getIsim() + " " + rb.getString("hasRelationError")));

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
    private void deletePersonel(PersonelForm.DeleteEvent event) {
        if(!event.getPersonel().getHastaSet().isEmpty()){
            hasRelationNotification(event.getPersonel());
        }else{ presenter.deletePersonel(event.getPersonel());
            LogService.log(currentPrincipalName,Log.OperationType.DELETE,event.getPersonel().getClass(),Long.toString(event.getPersonel().getPersonelId()));
            updateList();}

        closeEditor();
    }

    public void editPersonel(Personel personel) {
        selectedPersonel = personel;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        if (personel == null) {
            closeEditor();
        } else {

            if(personel.getPersonelId() == 0){
                operationType = Log.OperationType.CREATE;
            }else{
                operationType = Log.OperationType.UPDATE;
            }


            form.setPersonel(personel);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void addPersonel() {
        grid.asSingleSelect().clear();
        editPersonel(new Personel());
    }

    private void closeEditor() {
        form.setPersonel(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        grid.setItems(presenter.findAllPersonel(filterText.getValue()));
    }
}
