package com.example.application.views.list;

import com.example.application.data.entity.Doctor;
import com.example.application.data.entity.Patience;
import com.example.application.data.entity.Doctor;
import com.example.application.data.presenter.PatiencePresenter;
import com.example.application.data.presenter.DoctorPresenter;
import com.example.application.util.ResourceBundleUtil;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
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
@PageTitle("Emre HBYS")

@PermitAll
public class PatienceDoctorListView extends VerticalLayout {

    Grid<Patience> patienceGrid;
    Grid<Doctor> personnelGrid;
    TextField patienceFilterText, personnelFilterText;
    Button resetGrids;
    ResourceBundleUtil rb;
    FormLayout labelLayout ;
    H3 patienceLabel, personnelLabel;
    PatiencePresenter patiencePresenter;
    DoctorPresenter personnelPresenter;

    public PatienceDoctorListView(PatiencePresenter patiencePresenter, DoctorPresenter personnelPresenter) {

        this.patiencePresenter = patiencePresenter;
        this.personnelPresenter = personnelPresenter;

        patienceGrid = new Grid<>(Patience.class);
        personnelGrid = new Grid<>(Doctor.class);

        patienceFilterText = new TextField();
        personnelFilterText = new TextField();

        rb = new ResourceBundleUtil((VaadinSession.getCurrent().getAttribute("language").toString()));
        UI.getCurrent().getPage().setTitle(rb.getString("patiencePersonnelListTitle"));
        addClassName("list-view");
        setSizeFull();

        configurePatienceGrid();
        configurePersonnelGrid();
        configureLabelLayout();

        add(getToolbar(), labelLayout, getContent());
        updatePatienceList();
        updatePersonnelList();
        // closeEditor();
    }

    private void configureLabelLayout(){
        patienceLabel = new H3("");
        personnelLabel = new H3("");
        labelLayout = new FormLayout();
        labelLayout.add(patienceLabel, personnelLabel);
    }
    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(patienceGrid, personnelGrid);

        content.setFlexGrow(2, patienceGrid);
        content.setFlexGrow(2, personnelGrid);

        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void configurePatienceGrid() {

        patienceGrid.addClassNames("hasta-grid");
        patienceGrid.setSizeFull();
        patienceGrid.setColumns("TCNO", "name", "lastName", "email");
        patienceGrid.addColumn(patience -> PatiencePresenter.formatPhoneNumber(patience.getPhone()))
                .setKey("phone")
                .setHeader(rb.getString("phone"));

        patienceGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        patienceGrid.addComponentColumn(patience -> {
            HorizontalLayout genderField = new HorizontalLayout();
            Label label = new Label(rb.getString(patience.getGender()));
            genderField.add(createGenderIcon(patience.getGender()),label);
            return genderField;

        }).setHeader(rb.getString("gender")).setAutoWidth(true);

        patienceGrid.getColumnByKey("name").setHeader(rb.getString("name"));
        patienceGrid.getColumnByKey("TCNO").setHeader(rb.getString("TCNO"));

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

    private void showRelatedPatience(Doctor personnel) {

        if (personnel != null) {
            patienceGrid.setItems(personnel.getPatienceSet());
            personnelGrid.setItems(personnel);
        }

    }

    private void configurePersonnelGrid() {

        personnelGrid.addClassNames("contact-grid");
        personnelGrid.setSizeFull();
        personnelGrid.setColumns("personnelId", "name", "lastName");
        personnelGrid.addColumn(personnel -> DoctorPresenter.formatPhoneNumber(personnel.getPhone()))
                .setKey("phone")
                .setHeader(rb.getString("phone"));

        personnelGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        personnelGrid.asSingleSelect()
                .addValueChangeListener(event -> {
                    showRelatedPatience(event.getValue());
                    if (event.getValue() != null) {
                        String msg = rb.getString("personnelPatienceRelation");
                        msg = MessageFormat.format(msg,
                                event.getValue().getName() + " " + event.getValue().getLastName());
                        patienceLabel.setText(msg);
                        personnelLabel.setText("");
                    }
                });
        personnelGrid.getColumnByKey("personnelId").setHeader(rb.getString("personnelId"));
        personnelGrid.getColumnByKey("name").setHeader(rb.getString("name"));
        personnelGrid.getColumnByKey("lastName").setHeader(rb.getString("lastName"));

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
        resetGrids = new Button(rb.getString("updateGrids"));

        patienceFilterText.setPlaceholder(rb.getString("patienceFilterText"));
        patienceFilterText.setClearButtonVisible(true);
        patienceFilterText.setValueChangeMode(ValueChangeMode.LAZY);
        patienceFilterText.addValueChangeListener(e -> updatePatienceList());

        personnelFilterText.setPlaceholder(rb.getString("personnelFilterText"));
        personnelFilterText.setClearButtonVisible(true);
        personnelFilterText.setValueChangeMode(ValueChangeMode.LAZY);
        personnelFilterText.addValueChangeListener(e -> updatePersonnelList());

        resetGrids.addClickListener(e -> {
            patienceLabel.setText("");
            personnelLabel.setText("");
            updatePatienceList();
            updatePersonnelList();
        });

        HorizontalLayout toolbar = new HorizontalLayout(patienceFilterText, personnelFilterText, resetGrids, labelLayout);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void updatePatienceList() {

        patienceGrid.setItems(patiencePresenter.findAllPatience(patienceFilterText.getValue()));
    }

    private void updatePersonnelList() {

        personnelGrid.setItems(personnelPresenter.findAllPersonnel(personnelFilterText.getValue()));
    }
}
