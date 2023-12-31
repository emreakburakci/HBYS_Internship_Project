package com.example.application.views.list;

import java.util.List;
import java.util.Set;

import com.example.application.data.entity.Doctor;
import com.example.application.data.entity.Patience;
import com.example.application.data.presenter.DoctorPresenter;
import com.example.application.data.presenter.PatiencePresenter;
import com.example.application.util.ResourceBundleUtil;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.VaadinSession;

@Route(value = "relate/:PatienceTC", layout = MainLayout.class)
@PageTitle("Emre HBYS")
public class PatienceRelationView extends VerticalLayout implements HasUrlParameter<String> {

    DoctorPresenter personnelPresenter;
    PatiencePresenter patiencePresenter;
    Grid<Doctor> relatedPersonnelGrid, notRelatedPersonnelGrid;
    FormLayout patienceInfo, gridLabels;
    ResourceBundleUtil rb;
    Patience patience;

    public PatienceRelationView(DoctorPresenter personnelPresenter, PatiencePresenter patiencePresenter) {

        this.personnelPresenter = personnelPresenter;
        this.patiencePresenter = patiencePresenter;

        relatedPersonnelGrid = new Grid<>(Doctor.class);
        notRelatedPersonnelGrid = new Grid<>(Doctor.class);

        rb = new ResourceBundleUtil((VaadinSession.getCurrent().getAttribute("language").toString()));

        addClassName("hasta-personel");
        setSizeFull();

        patienceInfo = new FormLayout();

        H3 related = new H3(rb.getString("relatedPersonnels"));
        H3 notRelated = new H3(rb.getString("notRelatedPersonnels"));

        gridLabels = new FormLayout(related, notRelated);

        add(patienceInfo, gridLabels, getContent());

    }

    private void configurePatienceInfo() {

        TextField tcno = new TextField(rb.getString("TCNO"));
        tcno.setValue(patience.getTCNO());
        tcno.setReadOnly(true);

        TextField email = new TextField(rb.getString("email"));
        email.setValue(patience.getEmail());
        email.setReadOnly(true);

        TextField name = new TextField(rb.getString("name"));
        name.setValue(patience.getName());
        name.setReadOnly(true);

        TextField surname = new TextField(rb.getString("lastName"));
        surname.setValue(patience.getLastName());
        surname.setReadOnly(true);

        TextField tel = new TextField(rb.getString("phone"));
        tel.setValue(patience.getPhone());
        tel.setReadOnly(true);

        patienceInfo.add(tcno, name, surname, email, tel);

    }


    private HorizontalLayout getContent() {

        configureRelatedPersonnelGrid();
        configureNotRelatedPersonnelGrid();

        HorizontalLayout content = new HorizontalLayout(relatedPersonnelGrid, notRelatedPersonnelGrid);

        content.setFlexGrow(2, relatedPersonnelGrid);

        content.setFlexGrow(2, notRelatedPersonnelGrid);

        content.addClassNames("content");
        content.setSizeFull();

        return content;
    }

    private void configureNotRelatedPersonnelGrid() {

        notRelatedPersonnelGrid.addClassNames("personel-grid");
        notRelatedPersonnelGrid.setSizeFull();
        notRelatedPersonnelGrid.setColumns("personnelId", "name", "lastName");
        notRelatedPersonnelGrid.addColumn(personnel -> DoctorPresenter.formatPhoneNumber(personnel.getPhone())).setKey("phone");
        notRelatedPersonnelGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        notRelatedPersonnelGrid.addComponentColumn(personnel -> {
            Button button = new Button("", VaadinIcon.ARROW_LEFT.create());
            button.addClickListener(e -> {


                relate(personnel);
            });
            return button;
        });
        notRelatedPersonnelGrid.getColumnByKey("personnelId").setHeader(rb.getString("personnelId"));
        notRelatedPersonnelGrid.getColumnByKey("name").setHeader(rb.getString("name"));
        notRelatedPersonnelGrid.getColumnByKey("lastName").setHeader(rb.getString("lastName"));
        notRelatedPersonnelGrid.getColumnByKey("phone").setHeader(rb.getString("phone"));


        notRelatedPersonnelGrid.asSingleSelect().addValueChangeListener(event -> {
        });
    }

    private void configureRelatedPersonnelGrid() {

        relatedPersonnelGrid.addClassNames("personel-grid");
        relatedPersonnelGrid.setSizeFull();
        relatedPersonnelGrid.setColumns("personnelId", "name", "lastName");
        relatedPersonnelGrid.addColumn(personnel -> DoctorPresenter.formatPhoneNumber(personnel.getPhone())).setKey("phone");
        relatedPersonnelGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        relatedPersonnelGrid.addComponentColumn(personnel -> {
            Button button = new Button("", VaadinIcon.ARROW_RIGHT.create());
            button.addClickListener(e -> {

                unRelate(personnel);
            });
            return button;
        });
        relatedPersonnelGrid.getColumnByKey("personnelId").setHeader(rb.getString("personnelId"));
        relatedPersonnelGrid.getColumnByKey("name").setHeader(rb.getString("name"));
        relatedPersonnelGrid.getColumnByKey("lastName").setHeader(rb.getString("lastName"));
        relatedPersonnelGrid.getColumnByKey("phone").setHeader(rb.getString("phone"));

        relatedPersonnelGrid.asSingleSelect().addValueChangeListener(event -> {
        });
    }

    private void unRelate(Doctor personnel) {

        patience.getPersonnelSet().remove(personnel);
        patience = patiencePresenter.saveAndFlush(patience);
        updateRelatedPersonnelGrid();
        updateNotRelatedPersonnelGrid();

    }

    private void relate(Doctor personnel) {

        patience.getPersonnelSet().add(personnel);
        patience = patiencePresenter.saveAndFlush(patience);
        updateRelatedPersonnelGrid();
        updateNotRelatedPersonnelGrid();

    }

    private void updateRelatedPersonnelGrid() {


        relatedPersonnelGrid.setItems(patiencePresenter.getRelatedPersonnels(patience));

    }

    private void updateNotRelatedPersonnelGrid() {


        List<Doctor> all = personnelPresenter.findAllPersonnel("");
        Set<Doctor> relatedPersonnelSet = patience.getPersonnelSet();

        all.removeIf(p -> {
            for (Doctor setP : relatedPersonnelSet) {
                if (p.getPersonnelId() == setP.getPersonnelId()) {
                    return true;
                }
            }
            return false;
        });

        notRelatedPersonnelGrid.setItems(all);

    }


    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {

        RouteParameters rp = event.getRouteParameters();

        patience = patiencePresenter.findById(rp.get("PatienceTC").get());

        configurePatienceInfo();
        updateRelatedPersonnelGrid();
        updateNotRelatedPersonnelGrid();
    }

}
