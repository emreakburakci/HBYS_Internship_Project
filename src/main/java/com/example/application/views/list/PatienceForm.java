package com.example.application.views.list;

import java.util.List;
import com.example.application.util.ResourceBundleUtil;
import com.example.application.data.entity.Patience;
import com.example.application.data.presenter.PatiencePresenter;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.shared.Registration;

public class PatienceForm extends FormLayout {
    private Patience patience;

    private TextField name, tcno, lastName, phone;
    private ComboBox<String> gender;
    private EmailField email;
    private Button save, delete, close;
    private Binder<Patience> binder;

    private ResourceBundleUtil rb;

    public PatienceForm(String lang) {

        addClassName("Patience-form");

        rb = new ResourceBundleUtil(lang);

        tcno = new TextField(rb.getString("TCNO"));
        lastName = new TextField(rb.getString("lastName"));
        name = new TextField(rb.getString("name"));
        gender = new ComboBox<>(rb.getString("gender"), List.of(rb.getString("Erkek"),rb.getString("Kadın"),rb.getString("Diğer")));
        phone = new TextField(rb.getString("phone"));
        email = new EmailField(rb.getString("email"));

        configureBinder();

        add(tcno, name, lastName, email, gender, phone, createButtonsLayout());
    }


private void configureBinder(){
    binder = new BeanValidationBinder<>(Patience.class);

    binder.forField(tcno)
            .asRequired(rb.getString("tcnoRequiredMessage"))
            .withValidator(new RegexpValidator(rb.getString("tcnoRegexpMessage"),"^[1-9]{1}[0-9]{9}[02468]{1}$"))
            .bind(Patience::getTCNO,Patience::setTCNO);

    binder.forField(lastName)
            .asRequired(rb.getString("lastNameRequiredMessage"))
            .bind(Patience::getLastName,Patience::setLastName);

    binder.forField(name)
            .asRequired(rb.getString("nameRequiredMessage"))
            .bind(Patience::getName,Patience::setName);

    binder.forField(gender)
            .asRequired(rb.getString("genderRequiredMessage"))
            .bind(Patience::getGender,Patience::setGender);

    binder.forField(phone)
            .asRequired(rb.getString("phoneRequiredMessage"))
            .withValidator(new RegexpValidator(rb.getString("phoneRegexpMessage"),"^[1-9][0-9]{9}$"))
            .bind(Patience::getPhone,Patience::setPhone);

    binder.forField(email)
            .asRequired(rb.getString("emailRequiredMessage"))
            .withValidator(new EmailValidator(rb.getString("emailValidationMessage")))
            .bind(Patience::getEmail,Patience::setEmail);


    //binder.bindInstanceFields(this);
}
    private HorizontalLayout createButtonsLayout() {

        save = new Button(rb.getString("save"));
        delete = new Button(rb.getString("delete"));
        close = new Button(rb.getString("cancel"));

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);


        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, patience)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));


        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }


    public void setPatience(Patience patience) {
        this.patience = patience;
        binder.readBean(patience);
        if(patience != null && patience.getGender() != null){
        gender.setValue(rb.getString(patience.getGender()));
    }
    }

    private void validateAndSave() {
        try {

            binder.writeBean(patience);
            patience.setGender(PatiencePresenter.translateGender(patience.getGender()));

            fireEvent(new SaveEvent(this, patience));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }


    // Events
    public static abstract class PatienceFormEvent extends ComponentEvent<PatienceForm> {
        private Patience patience;

        protected PatienceFormEvent(PatienceForm source, Patience patience) {
            super(source, false);
            this.patience = patience;
        }

        public Patience getPatience() {
            return patience;
        }
    }

    public static class SaveEvent extends PatienceFormEvent {
        SaveEvent(PatienceForm source, Patience patience) {
            super(source, patience);
        }
    }

    public static class DeleteEvent extends PatienceFormEvent {
        DeleteEvent(PatienceForm source, Patience patience) {
            super(source, patience);
        }

    }

    public static class CloseEvent extends PatienceFormEvent {
        CloseEvent(PatienceForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}