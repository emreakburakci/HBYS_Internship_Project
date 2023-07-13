package com.example.application.views.list;

import com.example.application.data.entity.Hasta;
import com.example.application.data.presenter.HastaPresenter;
import com.example.application.util.ResourceBundleUtil;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
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

import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.security.PermitAll;

@Component
@Scope("prototype")
@Route(value = "", layout = MainLayout.class)
@PageTitle("Hasta Listesi | Emre HBYS")
@PermitAll
public class HastaListView extends VerticalLayout {
    private Grid<Hasta> grid = new Grid<>(Hasta.class);
    private TextField filterText = new TextField();
    private HastaForm form;
    private HastaPresenter presenter;
    private Hasta selectedHasta;
    private Button relateButton;
    private ResourceBundleUtil rb ;


    public HastaListView(HastaPresenter presenter) {
        this.presenter = presenter;
        
        rb = new ResourceBundleUtil((VaadinSession.getCurrent().getAttribute("language").toString()));
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
        form = new HastaForm();
        form.setWidth("25em");
        form.addListener(HastaForm.SaveEvent.class, this::saveHasta);
        form.addListener(HastaForm.DeleteEvent.class, this::deleteHasta);
        form.addListener(HastaForm.CloseEvent.class, e -> closeEditor());
    }

    private void configureGrid() {

        grid.addClassNames("contact-grid");
        grid.setSizeFull();
        grid.setColumns("TCNO", "isim", "soyisim", "email", "telefon");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.addComponentColumn(hasta -> {
            HorizontalLayout genderField = new HorizontalLayout();
            Label label = new Label(hasta.getCinsiyet());
            genderField.add(label, createGenderIcon(hasta.getCinsiyet()));
            return genderField;

        }).setHeader("Cinsiyet").setAutoWidth(true).setKey("cinsiyet");;


        grid.getColumnByKey("isim").setHeader(rb.getString("name"));
        grid.getColumnByKey("TCNO").setHeader(rb.getString("TCNO"));
        grid.getColumnByKey("soyisim").setHeader(rb.getString("lastName"));
        grid.getColumnByKey("email").setHeader(rb.getString("email"));
        grid.getColumnByKey("telefon").setHeader(rb.getString("phone"));
        grid.getColumnByKey("cinsiyet").setHeader(rb.getString("gender"));

        grid.asSingleSelect().addValueChangeListener(event -> {
            editHasta(event.getValue()); 
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

        Button addHastaButton = new Button(rb.getString("addHasta"));
        addHastaButton.addClickListener(click -> addHasta());
        


        relateButton = new Button(rb.getString("relate"), event -> UI.getCurrent().navigate(HastaRelationView.class, new RouteParameters("hastaTC", selectedHasta == null ? "": selectedHasta.getTCNO() )  ));
        relateButton.setEnabled(false);
        HorizontalLayout toolbar = new HorizontalLayout(filterText, addHastaButton,relateButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }




    private void saveHasta(HastaForm.SaveEvent event) {
        presenter.saveHasta(event.getHasta());
        updateList();
        closeEditor();
    }

    private void deleteHasta(HastaForm.DeleteEvent event) {
        presenter.deleteHasta(event.getHasta());
        updateList();
        closeEditor();
    }

    public void editHasta(Hasta hasta) {
        selectedHasta = hasta;
        
        if (hasta == null) {
            closeEditor();
        } else {
            hasta.setTelefon(HastaPresenter.removeParanthesisFromTel(hasta.getTelefon()));
            form.setHasta(hasta);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void addHasta() {
        grid.asSingleSelect().clear();
        editHasta(new Hasta());
    }

    private void closeEditor() {
        form.setHasta(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        grid.setItems(presenter.findAllHasta(filterText.getValue()));
    }
}