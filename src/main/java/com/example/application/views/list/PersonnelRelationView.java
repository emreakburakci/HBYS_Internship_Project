package com.example.application.views.list;



import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import com.example.application.data.entity.Patience;
import com.example.application.data.entity.Personnel;
import com.example.application.data.presenter.PatiencePresenter;
import com.example.application.data.presenter.PersonnelPresenter;
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

public class PersonnelRelationView extends VerticalLayout implements HasUrlParameter<String> {

    PersonnelPresenter personnelPresenter;
    PatiencePresenter patiencePresenter;

    Grid<Patience> relatedPatienceGrid = new Grid<>(Patience.class);
    Grid<Patience> notRelatedPatienceGrid = new Grid<>(Patience.class);
    FormLayout personnelInfo;
    FormLayout gridLabels;
    Personnel personnel;

    ResourceBundleUtil rb;

    public PersonnelRelationView(PersonnelPresenter personnelPresenter, PatiencePresenter patiencePresenter) {

        this.personnelPresenter = personnelPresenter;
        this.patiencePresenter = patiencePresenter;

        rb = new ResourceBundleUtil((VaadinSession.getCurrent().getAttribute("language").toString()));

        addClassName("hasta-personel");
        setSizeFull();
        personnelInfo = new FormLayout();

        H3 related = new H3(rb.getString("relatedPatiences"));
        H3 notRelated = new H3(rb.getString("notRelatedPatiences"));

        gridLabels = new FormLayout(related,notRelated);

        add(personnelInfo, gridLabels, getContent());
    }

    private void configurePersonnelInfo() {
    

        TextField pid = new TextField(rb.getString("personnelId"));
        pid.setValue(Long.toString(personnel.getPersonnelId()));
        pid.setReadOnly(true);

        TextField name = new TextField(rb.getString("name"));
        name.setValue(personnel.getName());
        name.setReadOnly(true);

        TextField surname = new TextField(rb.getString("lastName"));
        surname.setValue(personnel.getLastName());
        surname.setReadOnly(true);

        TextField tel = new TextField(rb.getString("phone"));
        tel.setValue(personnel.getPhone());
        tel.setReadOnly(true);

        personnelInfo.add(pid, name, surname, tel);

    

    }



    private HorizontalLayout getContent() {

        configureRelatedPatienceGrid();
        configureNotRelatedPatienceGrid();

        HorizontalLayout content = new HorizontalLayout(relatedPatienceGrid, notRelatedPatienceGrid);
       
        content.setFlexGrow(2, relatedPatienceGrid);

        content.setFlexGrow(2, notRelatedPatienceGrid);

        content.addClassNames("content");
        content.setSizeFull();

        return content;
    }

    private void configureNotRelatedPatienceGrid() {

        notRelatedPatienceGrid.addClassNames("personel-grid");
        notRelatedPatienceGrid.setSizeFull();
        notRelatedPatienceGrid.setColumns("TCNO", "name", "lastName");
        notRelatedPatienceGrid.addColumn(patience -> PatiencePresenter.formatPhoneNumber(patience.getPhone())).setKey("phone");
        notRelatedPatienceGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        notRelatedPatienceGrid.addComponentColumn(patience -> {
            Button button = new Button("", VaadinIcon.ARROW_LEFT.create());

            button.addClickListener(e ->{
                
                relate(patience);});
            return button;
        } );

        notRelatedPatienceGrid.getColumnByKey("TCNO").setHeader(rb.getString("TCNO"));
        notRelatedPatienceGrid.getColumnByKey("name").setHeader(rb.getString("name"));
        notRelatedPatienceGrid.getColumnByKey("lastName").setHeader(rb.getString("lastName"));
        notRelatedPatienceGrid.getColumnByKey("phone").setHeader(rb.getString("phone"));

        notRelatedPatienceGrid.asSingleSelect().addValueChangeListener(event -> {
        });
    }

    private void configureRelatedPatienceGrid() {

        relatedPatienceGrid.addClassNames("personel-grid");
        relatedPatienceGrid.setSizeFull();
        relatedPatienceGrid.setColumns("TCNO", "name", "lastName");
        relatedPatienceGrid.addColumn(hasta -> PatiencePresenter.formatPhoneNumber(hasta.getPhone())).setKey("phone");
        relatedPatienceGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        relatedPatienceGrid.addComponentColumn(patience -> {
            Button button = new Button("", VaadinIcon.ARROW_RIGHT.create());
            button.addClickListener(e -> {
                
                unRelate(patience);});
            return button;
        } );

        relatedPatienceGrid.getColumnByKey("TCNO").setHeader(rb.getString("TCNO"));
        relatedPatienceGrid.getColumnByKey("name").setHeader(rb.getString("name"));
        relatedPatienceGrid.getColumnByKey("lastName").setHeader(rb.getString("lastName"));
        relatedPatienceGrid.getColumnByKey("phone").setHeader(rb.getString("phone"));


        relatedPatienceGrid.asSingleSelect().addValueChangeListener(event -> {
        });
    }

    private void unRelate(Patience patience){

        patience.getPersonnelSet().remove(personnel);
        patience = patiencePresenter.saveAndFlush(patience);
        personnel = personnelPresenter.findById(personnel.getPersonnelId()+"");
        patience = patiencePresenter.findById(patience.getTCNO());
        updateRelatedHastaGrid();
        updateNotRelatedHastaGrid();

    }

    private void relate(Patience patience){

        patience.getPersonnelSet().add(personnel);
        patience = patiencePresenter.saveAndFlush(patience);
        personnel = personnelPresenter.findById(personnel.getPersonnelId()+"");
        updateRelatedHastaGrid();
        updateNotRelatedHastaGrid();

    }

    private void updateRelatedHastaGrid() {

        relatedPatienceGrid.setItems(personnel.getPatienceSet());

    }
    private void updateNotRelatedHastaGrid() {


        List<Patience> all = patiencePresenter.findAllPatience("");
        Set<Patience> relatedPatienceSet = personnel.getPatienceSet();

        all.removeIf(p -> {
            for(Patience setP: relatedPatienceSet){
                if(p.getTCNO().equals(setP.getTCNO())){
                    return true;
                }
            }
            return false;
        } );

        notRelatedPatienceGrid.setItems(all);

    }
 

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {

        RouteParameters rp = event.getRouteParameters();

        personnel = personnelPresenter.findById(rp.get("pid").get());

        configurePersonnelInfo();
        updateRelatedHastaGrid();
        updateNotRelatedHastaGrid();
    }

}
