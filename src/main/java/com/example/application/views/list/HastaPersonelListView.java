package com.example.application.views.list;

import com.example.application.data.entity.Hasta;
import com.example.application.data.entity.Personel;
import com.example.application.data.presenter.HastaPresenter;
import com.example.application.data.presenter.PersonelPresenter;
import com.example.application.util.ResourceBundleUtil;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

import javax.annotation.security.PermitAll;

@Component
@Scope("prototype")
@Route(value = "hasta-personel", layout = MainLayout.class)
@PageTitle("Hasta Personel Listesi | Emre HBYS")
@PermitAll
public class HastaPersonelListView extends VerticalLayout {

    Grid<Hasta> hastaGrid = new Grid<>(Hasta.class);
    Grid<Personel> personelGrid = new Grid<>(Personel.class);

    TextField hastaFilterText = new TextField();
    TextField personelFilterText = new TextField();

    Button resetGrids;

    ResourceBundleUtil rb;

    // HorizontalLayout labelLayout = new HorizontalLayout();
    H3 label = new H3("");

    HastaPresenter hastaPresenter;
    PersonelPresenter personelPresenter;

    public HastaPersonelListView(HastaPresenter hastaPresenter, PersonelPresenter personelPresenter) {

        this.hastaPresenter = hastaPresenter;
        this.personelPresenter = personelPresenter;
        rb = new ResourceBundleUtil((VaadinSession.getCurrent().getAttribute("language").toString()));

        addClassName("list-view");
        setSizeFull();
        configureHastaGrid();
        configurePersonelGrid();

        add(getToolbar(), label, getContent());
        updateHastaList();
        updatePersonelList();
        // closeEditor();
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(hastaGrid, personelGrid);

        content.setFlexGrow(2, hastaGrid);
        content.setFlexGrow(2, personelGrid);

        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void configureHastaGrid() {

        hastaGrid.addClassNames("hasta-grid");
        hastaGrid.setSizeFull();
        hastaGrid.setColumns("TCNO", "isim", "soyisim", "email");
        hastaGrid.addColumn(hasta -> HastaPresenter.formatPhoneNumber(hasta.getTelefon())).setKey("telefon");

        hastaGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        hastaGrid.addComponentColumn(hasta -> {
            HorizontalLayout genderField = new HorizontalLayout();
            Label label = new Label(hasta.getCinsiyet());
            genderField.add(label, createGenderIcon(hasta.getCinsiyet()));
            return genderField;

        }).setHeader("Cinsiyet").setAutoWidth(true);

        hastaGrid.getColumnByKey("isim").setHeader("İsim");
        hastaGrid.getColumnByKey("TCNO").setHeader("TC Kimlik No");

        hastaGrid.setItems(hastaPresenter.findAllHasta(hastaFilterText.getValue()));
        personelGrid.setItems(personelPresenter.findAllPersonel(personelFilterText.getValue()));

        hastaGrid.asSingleSelect().addValueChangeListener(event -> {
            showRelatedPersonel(event.getValue());
            if (event.getValue() != null) {
                String msg = rb.getString("patiencePersonnelRelation");
                msg = MessageFormat.format(msg, event.getValue().getIsim() + " " + event.getValue().getSoyisim());
                label.setText(msg);

            }
        });

    }

    private void showRelatedPersonel(Hasta hasta) {

        if (hasta != null) {
            personelGrid.setItems(hasta.getPersonelSet());
            hastaGrid.setItems(hasta);
        }


    }

    private void showRelatedHasta(Personel personel) {

        if (personel != null) {
            hastaGrid.setItems(personel.getHastaSet());
            personelGrid.setItems(personel);
        }

    }

    private void configurePersonelGrid() {

        personelGrid.addClassNames("contact-grid");
        personelGrid.setSizeFull();
        personelGrid.setColumns("personelId", "isim", "soyisim");
        personelGrid.addColumn(personel -> PersonelPresenter.formatPhoneNumber(personel.getTelefon())).setKey("telefon");

        personelGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        personelGrid.asSingleSelect()
                .addValueChangeListener(event -> {
                    showRelatedHasta(event.getValue());
                    if (event.getValue() != null) {
                        String msg = rb.getString("personnelPatienceRelation");
                        msg = MessageFormat.format(msg,
                                event.getValue().getIsim() + " " + event.getValue().getSoyisim());
                        label.setText(msg);
                    }
                });

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
            icon = VaadinIcon.QUESTION.create();
            icon.setColor("gray");
        }
        return icon;
    }

    private HorizontalLayout getToolbar() {
        resetGrids = new Button(rb.getString("updateGrids"));

        hastaFilterText.setPlaceholder(rb.getString("patienceFilterText"));
        hastaFilterText.setClearButtonVisible(true);
        hastaFilterText.setValueChangeMode(ValueChangeMode.LAZY);
        hastaFilterText.addValueChangeListener(e -> updateHastaList());

        personelFilterText.setPlaceholder(rb.getString("personnelFilterText"));
        personelFilterText.setClearButtonVisible(true);
        personelFilterText.setValueChangeMode(ValueChangeMode.LAZY);
        personelFilterText.addValueChangeListener(e -> updatePersonelList());

        resetGrids.addClickListener(e -> {
            label.setText("");
            updateHastaList();
            updatePersonelList();
        });

        HorizontalLayout toolbar = new HorizontalLayout(hastaFilterText, personelFilterText, resetGrids, label);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void updateHastaList() {

        hastaGrid.setItems(hastaPresenter.findAllHasta(hastaFilterText.getValue()));
    }

    private void updatePersonelList() {

        personelGrid.setItems(personelPresenter.findAllPersonel(personelFilterText.getValue()));
    }
}
