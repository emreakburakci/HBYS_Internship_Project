package com.example.application.views.list;


import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import com.example.application.data.entity.Hasta;
import com.example.application.data.entity.Personel;
import com.example.application.data.presenter.HastaPresenter;
import com.example.application.data.presenter.PersonelPresenter;
import com.example.application.util.ResourceBundleUtil;
import com.example.application.views.MainLayout;
import com.nimbusds.jose.util.Resource;
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
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;

@Route(value = "relate/:hastaTC", layout = MainLayout.class)
@PageTitle("Personel İlişki | Emre HBYS")
public class HastaRelationView extends VerticalLayout implements HasUrlParameter<String> {

    PersonelPresenter personelPresenter;
    HastaPresenter hastaPresenter;

    Grid<Personel> relatedPersonelGrid = new Grid<>(Personel.class);
    Grid<Personel> notRelatedPersonelGrid = new Grid<>(Personel.class);
    FormLayout hastaBilgiler;
    FormLayout gridLabels;
    ResourceBundleUtil rb;

    Hasta hasta;

    public HastaRelationView(PersonelPresenter personelPresenter, HastaPresenter hastaPresenter) {

        this.personelPresenter = personelPresenter;
        this.hastaPresenter = hastaPresenter;

        rb = new ResourceBundleUtil((VaadinSession.getCurrent().getAttribute("language").toString()));


        addClassName("hasta-personel");
        setSizeFull();

        hastaBilgiler = new FormLayout();

        H3 related = new H3(rb.getString("relatedPersonnels"));
        H3 notRelated = new H3(rb.getString("notRelatedPersonnels"));

        gridLabels = new FormLayout(related, notRelated);

        add(hastaBilgiler, gridLabels, getContent());


    }

    private void configureHastaBilgileri() {

        TextField tcno = new TextField(rb.getString("TCNO"));
        tcno.setValue(hasta.getTCNO());
        tcno.setReadOnly(true);

        TextField email = new TextField(rb.getString("email"));
        email.setValue(hasta.getEmail());
        email.setReadOnly(true);

        TextField name = new TextField(rb.getString("name"));
        name.setValue(hasta.getIsim());
        name.setReadOnly(true);

        TextField surname = new TextField(rb.getString("lastName"));
        surname.setValue(hasta.getSoyisim());
        surname.setReadOnly(true);

        TextField tel = new TextField(rb.getString("phone"));
        tel.setValue(hasta.getTelefon());
        tel.setReadOnly(true);

        hastaBilgiler.add(tcno, name, surname, email, tel);

    }


    private HorizontalLayout getContent() {

        configureRelatedPersonelGrid();
        configureNotRelatedPersonelGrid();

        HorizontalLayout content = new HorizontalLayout(relatedPersonelGrid, notRelatedPersonelGrid);

        content.setFlexGrow(2, relatedPersonelGrid);

        content.setFlexGrow(2, notRelatedPersonelGrid);

        content.addClassNames("content");
        content.setSizeFull();

        return content;
    }

    private void configureNotRelatedPersonelGrid() {

        notRelatedPersonelGrid.addClassNames("personel-grid");
        notRelatedPersonelGrid.setSizeFull();
        notRelatedPersonelGrid.setColumns("personelId", "isim", "soyisim");
        notRelatedPersonelGrid.addColumn(personel -> PersonelPresenter.formatPhoneNumber(personel.getTelefon())).setKey("telefon");
        notRelatedPersonelGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        notRelatedPersonelGrid.addComponentColumn(personel -> {
            Button button = new Button("", VaadinIcon.ARROW_LEFT.create());
            button.addClickListener(e -> {


                relate(personel);
            });
            return button;
        });
        notRelatedPersonelGrid.getColumnByKey("personelId").setHeader(rb.getString("personnelId"));
        notRelatedPersonelGrid.getColumnByKey("isim").setHeader(rb.getString("name"));
        notRelatedPersonelGrid.getColumnByKey("soyisim").setHeader(rb.getString("lastName"));
        notRelatedPersonelGrid.getColumnByKey("telefon").setHeader(rb.getString("phone"));


        notRelatedPersonelGrid.asSingleSelect().addValueChangeListener(event -> {
        });
    }

    private void configureRelatedPersonelGrid() {

        relatedPersonelGrid.addClassNames("personel-grid");
        relatedPersonelGrid.setSizeFull();
        relatedPersonelGrid.setColumns("personelId", "isim", "soyisim");
        relatedPersonelGrid.addColumn(personel -> PersonelPresenter.formatPhoneNumber(personel.getTelefon())).setKey("telefon");
        relatedPersonelGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        relatedPersonelGrid.addComponentColumn(personel -> {
            Button button = new Button("", VaadinIcon.ARROW_RIGHT.create());
            button.addClickListener(e -> {

                unRelate(personel);
            });
            return button;
        });
        relatedPersonelGrid.getColumnByKey("personelId").setHeader(rb.getString("personnelId"));
        relatedPersonelGrid.getColumnByKey("isim").setHeader(rb.getString("name"));
        relatedPersonelGrid.getColumnByKey("soyisim").setHeader(rb.getString("lastName"));
        relatedPersonelGrid.getColumnByKey("telefon").setHeader(rb.getString("phone"));

        relatedPersonelGrid.asSingleSelect().addValueChangeListener(event -> {
        });
    }

    private void unRelate(Personel personel) {

        hasta.getPersonelSet().remove(personel);
        hasta = hastaPresenter.saveAndFlush(hasta);
        updateRelatedPersonelGrid();
        updateNotRelatedPersonelGrid();

    }

    private void relate(Personel personel) {

        hasta.getPersonelSet().add(personel);
        hasta = hastaPresenter.saveAndFlush(hasta);
        updateRelatedPersonelGrid();
        updateNotRelatedPersonelGrid();

    }

    private void updateRelatedPersonelGrid() {


        relatedPersonelGrid.setItems(hastaPresenter.getRelatedPersonels(hasta));

    }

    private void updateNotRelatedPersonelGrid() {


        List<Personel> all = personelPresenter.findAllPersonel("");
        Set<Personel> relatedPersonelSet = hasta.getPersonelSet();

        all.removeIf(p -> {
            for (Personel setP : relatedPersonelSet) {
                if (p.getPersonelId() == setP.getPersonelId()) {
                    return true;
                }
            }
            return false;
        });

        notRelatedPersonelGrid.setItems(all);

    }


    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {

        RouteParameters rp = event.getRouteParameters();

        hasta = hastaPresenter.findById(rp.get("hastaTC").get());

        configureHastaBilgileri();
        updateRelatedPersonelGrid();
        updateNotRelatedPersonelGrid();
    }

}
