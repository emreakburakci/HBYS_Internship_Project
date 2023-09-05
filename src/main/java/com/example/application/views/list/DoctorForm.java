package com.example.application.views.list;

import com.example.application.data.entity.Doctor;
import com.example.application.data.entity.Doctor;
import com.example.application.util.ResourceBundleUtil;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.shared.Registration;
import org.hibernate.validator.internal.constraintvalidators.bv.NotNullValidator;

public class DoctorForm extends FormLayout {
    private Doctor personnel;
    private Binder<Doctor> binder;
    private TextField name, lastName, personnelId, phone;
    private Button save, delete, close;
    private ResourceBundleUtil rb;

    public DoctorForm(String lang) {
        addClassName("personel-form");

        rb = new ResourceBundleUtil(lang);

        name = new TextField(rb.getString("name"));
        lastName = new TextField(rb.getString("lastName"));
        personnelId = new TextField(rb.getString("personnelId"));
        phone = new TextField(rb.getString("phone"));

        save = new Button(rb.getString("save"));
        delete = new Button(rb.getString("delete"));
        close = new Button(rb.getString("cancel"));


        binder = new BeanValidationBinder<>(Doctor.class);

        binder.forField(name)
                .asRequired(rb.getString("nameRequiredMessage"))
                .bind(Doctor::getName,Doctor::setName);

        binder.forField(lastName)
                .asRequired(rb.getString("lastNameRequiredMessage"))
                .bind(Doctor::getLastName,Doctor::setLastName);

        binder.forField(personnelId)
                .asRequired(rb.getString("personnelIdRequiredMessage"))
                .withValidator(id -> !id.equals(0),rb.getString("personnelIdNotZeroMessage"))
                .withConverter(Long::valueOf,String::valueOf)
                .bind(Doctor::getPersonnelId,Doctor::setPersonnelId);



        binder.forField(phone)
                .asRequired(rb.getString("phoneRequiredMessage"))
                .withValidator(new RegexpValidator(rb.getString("phoneRegexpMessage"),"^[1-9][0-9]{9}$"))
                .bind(Doctor::getPhone,Doctor::setPhone);

        //binder.bindInstanceFields(this);

        binder.addStatusChangeListener(event -> {

            boolean isValid = !event.hasValidationErrors();
            boolean hasChanges = binder.hasChanges();
            save.setEnabled(isValid && hasChanges);
        });

        name.addValueChangeListener(event -> save.setEnabled(binder.isValid()));
        personnelId.addValueChangeListener(event -> save.setEnabled(binder.isValid()));
        lastName.addValueChangeListener(event -> save.setEnabled(binder.isValid()));
        phone.addValueChangeListener(event -> save.setEnabled(binder.isValid()));

        add(personnelId, name, lastName, phone, createButtonsLayout());


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
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, personnel)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));


        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    public void setPersonnel(Doctor personnel) {
        this.personnel = personnel;
        binder.readBean(personnel);
        if(personnel != null && personnel.getPersonnelId() == null){
        personnelId.setValue("");
        }
    }

    private void validateAndSave() {
        try {
            binder.writeBean(personnel);
            fireEvent(new SaveEvent(this, personnel));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    // Events
    public static abstract class PersonnelFormEvent extends ComponentEvent<DoctorForm> {
        private Doctor personnel;

        protected PersonnelFormEvent(DoctorForm source, Doctor personnel) {
            super(source, false);
            this.personnel = personnel;
        }

        public Doctor getPersonnel() {
            return personnel;
        }
    }

    public static class SaveEvent extends PersonnelFormEvent {
        SaveEvent(DoctorForm source, Doctor personel) {
            super(source, personel);
        }
    }

    public static class DeleteEvent extends PersonnelFormEvent {
        DeleteEvent(DoctorForm source, Doctor personel) {
            super(source, personel);
        }

    }

    public static class CloseEvent extends PersonnelFormEvent {
        CloseEvent(DoctorForm source) {
            super(source, null);
        }
    }
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}