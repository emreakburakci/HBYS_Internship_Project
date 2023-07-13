package com.example.application.views.list;


import java.security.DrbgParameters.Reseed;
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
import com.vaadin.flow.server.VaadinSession;

@Route(value = "relp/:pid", layout = MainLayout.class)
@PageTitle("Personel İlişki | Emre HBYS")

public class PersonelRelationView extends VerticalLayout implements HasUrlParameter<String> {

    PersonelPresenter personelPresenter;
    HastaPresenter hastaPresenter;

    Grid<Hasta> relatedHastaGrid = new Grid<>(Hasta.class);
    Grid<Hasta> notRelatedHastaGrid = new Grid<>(Hasta.class);
    FormLayout personelBilgiler;
    FormLayout gridLabels;
    Personel personel;

    ResourceBundleUtil rb;

    public PersonelRelationView(PersonelPresenter personelPresenter, HastaPresenter hastaPresenter) {

        this.personelPresenter = personelPresenter;
        this.hastaPresenter = hastaPresenter;

        rb = new ResourceBundleUtil((VaadinSession.getCurrent().getAttribute("language").toString()));

        addClassName("hasta-personel");
        setSizeFull();
        personelBilgiler = new FormLayout();

        H3 related = new H3(rb.getString("relatedPatiences"));
        H3 notRelated = new H3(rb.getString("notRelatedPatiences"));

        gridLabels = new FormLayout(related,notRelated);

        add(personelBilgiler, gridLabels, getContent());
    }

    private void configurePersonelBilgileri() {
    

        TextField pid = new TextField(rb.getString("personnelId"));
        pid.setValue(Long.toString(personel.getPersonelId()));
        pid.setReadOnly(true);

        TextField name = new TextField(rb.getString("name"));
        name.setValue(personel.getIsim());
        name.setReadOnly(true);

        TextField surname = new TextField(rb.getString("lastName"));
        surname.setValue(personel.getSoyisim());
        surname.setReadOnly(true);

        TextField tel = new TextField(rb.getString("phone"));
        tel.setValue(personel.getTelefon());
        tel.setReadOnly(true);

        personelBilgiler.add(pid, name, surname, tel);

    

    }



    private HorizontalLayout getContent() {

        configureRelatedHastaGrid();
        configureNotRelatedHastaGrid();

        HorizontalLayout content = new HorizontalLayout(relatedHastaGrid, notRelatedHastaGrid);
       
        content.setFlexGrow(2, relatedHastaGrid);

        content.setFlexGrow(2, notRelatedHastaGrid);

        content.addClassNames("content");
        content.setSizeFull();

        return content;
    }

    private void configureNotRelatedHastaGrid() {

        notRelatedHastaGrid.addClassNames("personel-grid");
        notRelatedHastaGrid.setSizeFull();
        notRelatedHastaGrid.setColumns("TCNO", "isim", "soyisim", "telefon");
        notRelatedHastaGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        notRelatedHastaGrid.addComponentColumn(hasta -> {
            Button button = new Button("", VaadinIcon.ARROW_LEFT.create());
            System.out.println("RELATE BUTTONLA İLİŞKİLİ PERSONEL ID" + hasta.getTCNO());
            button.addClickListener(e ->{
                
                System.err.println("BUTTON RELATING PERSONEL " + hasta.getTCNO() + " -- " + personel.getPersonelId());
                
                relate(hasta);});
            return button;
        } );

        notRelatedHastaGrid.getColumnByKey("TCNO").setHeader(rb.getString("TCNO"));
        notRelatedHastaGrid.getColumnByKey("isim").setHeader(rb.getString("name"));
        notRelatedHastaGrid.getColumnByKey("soyisim").setHeader(rb.getString("lastName"));
        notRelatedHastaGrid.getColumnByKey("telefon").setHeader(rb.getString("phone"));

        notRelatedHastaGrid.asSingleSelect().addValueChangeListener(event -> {
        });
    }

    private void configureRelatedHastaGrid() {

        relatedHastaGrid.addClassNames("personel-grid");
        relatedHastaGrid.setSizeFull();
        relatedHastaGrid.setColumns("TCNO", "isim", "soyisim", "telefon");
        relatedHastaGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        relatedHastaGrid.addComponentColumn(hasta -> {
            Button button = new Button("", VaadinIcon.ARROW_RIGHT.create());
            System.out.println("UNRELATE BUTTONLA İLİŞKİLİ PERSONEL ID" + hasta.getTCNO());
            button.addClickListener(e -> {
                
                System.err.println("BUTTON UN-RELATING PERSONEL " + hasta.getTCNO() + " -- " + personel.getPersonelId());
                unRelate(hasta);});
            return button;
        } );

        relatedHastaGrid.getColumnByKey("TCNO").setHeader(rb.getString("TCNO"));
        relatedHastaGrid.getColumnByKey("isim").setHeader(rb.getString("name"));
        relatedHastaGrid.getColumnByKey("soyisim").setHeader(rb.getString("lastName"));
        relatedHastaGrid.getColumnByKey("telefon").setHeader(rb.getString("phone"));


        relatedHastaGrid.asSingleSelect().addValueChangeListener(event -> {
        });
    }

    private void unRelate(Hasta hasta){

        System.out.println("UNRELATE metodu");
        //Neden hata verdi?
        //personel.getHastaSet().remove(hasta);
        //personel = personelPresenter.saveAndFlush(personel);

        hasta.getPersonelSet().remove(personel);
        hasta = hastaPresenter.saveAndFlush(hasta);
        personel = personelPresenter.findById(personel.getPersonelId()+"");
        updateRelatedHastaGrid();
        updateNotRelatedHastaGrid();

    }

    private void relate(Hasta hasta){

        System.out.println("RELATE metodu");
        //Neden hata verdi?
        //personel.getHastaSet().add(hasta);
        //personel = personelPresenter.saveAndFlush(personel);

        hasta.getPersonelSet().add(personel);
        hasta = hastaPresenter.saveAndFlush(hasta);
        personel = personelPresenter.findById(personel.getPersonelId()+"");
        updateRelatedHastaGrid();
        updateNotRelatedHastaGrid();

    }

    private void updateRelatedHastaGrid() {

        System.out.println("UPDATE RELATED PERSONEL GRID");


        relatedHastaGrid.setItems(personel.getHastaSet());

    }
    private void updateNotRelatedHastaGrid() {


        System.out.println("UPDATE NOT RELATED PERSONEL GRİD");
        List<Hasta> all = hastaPresenter.findAllHasta("");
        Set<Hasta> relatedHastaSet = personel.getHastaSet();

        all.removeIf(p -> {
            for(Hasta setH: relatedHastaSet){
                if(p.getTCNO().equals(setH.getTCNO())){
                    return true;
                }
            }
            return false;
        } );

        notRelatedHastaGrid.setItems(all);

    }
 

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {

        System.out.println("SET PARAMETER METHOD");
        RouteParameters rp = event.getRouteParameters();

        personel = personelPresenter.findById(rp.get("pid").get());

        configurePersonelBilgileri();
        updateRelatedHastaGrid();
        updateNotRelatedHastaGrid();
    }

}
