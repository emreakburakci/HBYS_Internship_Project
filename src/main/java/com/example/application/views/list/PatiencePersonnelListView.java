package com.example.application.views.list;

import com.example.application.data.entity.Patience;
import com.example.application.data.entity.Personnel;
import com.example.application.data.presenter.PatiencePresenter;
import com.example.application.data.presenter.PersonnelPresenter;
import com.example.application.util.ResourceBundleUtil;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
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
public class PatiencePersonnelListView extends VerticalLayout {

    Grid<Patience> patienceGrid = new Grid<>(Patience.class);
    Grid<Personnel> personnelGrid = new Grid<>(Personnel.class);

    TextField patienceFilterText = new TextField();
    TextField personnelFilterText = new TextField();

    Button resetGrids;

    ResourceBundleUtil rb;

    FormLayout labelLayout ;
    H3 patienceLabel = new H3("");
    H3 personnelLabel = new H3("");

    PatiencePresenter patiencePresenter;
    PersonnelPresenter personnelPresenter;

    public PatiencePersonnelListView(PatiencePresenter patiencePresenter, PersonnelPresenter personnelPresenter) {

        this.patiencePresenter = patiencePresenter;
        this.personnelPresenter = personnelPresenter;
        rb = new ResourceBundleUtil((VaadinSession.getCurrent().getAttribute("language").toString()));

        addClassName("list-view");
        setSizeFull();
        configureHastaGrid();
        configurePersonelGrid();

        patienceLabel = new H3("");
        personnelLabel = new H3("");
        labelLayout = new FormLayout();
        labelLayout.add(patienceLabel, personnelLabel);
        add(getToolbar(), labelLayout, getContent());
        updateHastaList();
        updatePersonelList();
        // closeEditor();
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(patienceGrid, personnelGrid);

        content.setFlexGrow(2, patienceGrid);
        content.setFlexGrow(2, personnelGrid);

        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void configureHastaGrid() {

        patienceGrid.addClassNames("hasta-grid");
        patienceGrid.setSizeFull();
        patienceGrid.setColumns("TCNO", "name", "lastName", "email");
        patienceGrid.addColumn(hasta -> PatiencePresenter.formatPhoneNumber(hasta.getPhone())).setKey("phone");

        patienceGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        patienceGrid.addComponentColumn(hasta -> {
            HorizontalLayout genderField = new HorizontalLayout();
            Label label = new Label(hasta.getGender());
            genderField.add(label, createGenderIcon(hasta.getGender()));
            return genderField;

        }).setHeader("Cinsiyet").setAutoWidth(true);

        patienceGrid.getColumnByKey("name").setHeader("İsim");
        patienceGrid.getColumnByKey("TCNO").setHeader("TC Kimlik No");

        patienceGrid.setItems(patiencePresenter.findAllPatience(patienceFilterText.getValue()));
        personnelGrid.setItems(personnelPresenter.findAllPersonnel(personnelFilterText.getValue()));

        patienceGrid.asSingleSelect().addValueChangeListener(event -> {
            showRelatedPersonnel(event.getValue());
            if (event.getValue() != null) {
                String msg = rb.getString("patiencePersonnelRelation");
                msg = MessageFormat.format(msg, event.getValue().getName() + " " + event.getValue().getLastName());
                personnelLabel.setText(msg);
                patienceLabel.setText("");

            }
        });

    }

    private void showRelatedPersonnel(Patience patience) {

        if (patience != null) {
            personnelGrid.setItems(patience.getPersonnelSet());
            patienceGrid.setItems(patience);
        }


    }

    private void showRelatedHasta(Personnel personel) {

        if (personel != null) {
            patienceGrid.setItems(personel.getPatienceSet());
            personnelGrid.setItems(personel);
        }

    }

    private void configurePersonelGrid() {

        personnelGrid.addClassNames("contact-grid");
        personnelGrid.setSizeFull();
        personnelGrid.setColumns("personnelId", "name", "lastName");
        personnelGrid.addColumn(personel -> PersonnelPresenter.formatPhoneNumber(personel.getPhone())).setKey("phone");

        personnelGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        personnelGrid.asSingleSelect()
                .addValueChangeListener(event -> {
                    showRelatedHasta(event.getValue());
                    if (event.getValue() != null) {
                        String msg = rb.getString("personnelPatienceRelation");
                        msg = MessageFormat.format(msg,
                                event.getValue().getName() + " " + event.getValue().getLastName());
                        patienceLabel.setText(msg);
                        personnelLabel.setText("");
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

        patienceFilterText.setPlaceholder(rb.getString("patienceFilterText"));
        patienceFilterText.setClearButtonVisible(true);
        patienceFilterText.setValueChangeMode(ValueChangeMode.LAZY);
        patienceFilterText.addValueChangeListener(e -> updateHastaList());

        personnelFilterText.setPlaceholder(rb.getString("personnelFilterText"));
        personnelFilterText.setClearButtonVisible(true);
        personnelFilterText.setValueChangeMode(ValueChangeMode.LAZY);
        personnelFilterText.addValueChangeListener(e -> updatePersonelList());

        resetGrids.addClickListener(e -> {
            patienceLabel.setText("");
            personnelLabel.setText("");
            updateHastaList();
            updatePersonelList();
        });

        HorizontalLayout toolbar = new HorizontalLayout(patienceFilterText, personnelFilterText, resetGrids, labelLayout);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void updateHastaList() {

        patienceGrid.setItems(patiencePresenter.findAllPatience(patienceFilterText.getValue()));
    }

    private void updatePersonelList() {

        personnelGrid.setItems(personnelPresenter.findAllPersonnel(personnelFilterText.getValue()));
    }
}
