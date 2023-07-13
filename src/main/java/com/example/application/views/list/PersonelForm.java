package com.example.application.views.list;

import com.example.application.data.entity.Personel;
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
import com.vaadin.flow.shared.Registration;


public class PersonelForm extends FormLayout {
  private Personel personel;

  TextField isim = new TextField("First name");
  TextField soyisim = new TextField("Last name");
  TextField personelId = new TextField("PersonelId");
  TextField telefon = new TextField("Telefon");

  Binder<Personel> binder = new BeanValidationBinder<>(Personel.class);

  Button save = new Button("Kaydet");
  Button delete = new Button("Sil");
  Button close = new Button("İptal");

  public PersonelForm() {
    addClassName("personel-form");
    binder.bindInstanceFields(this);

    binder.addStatusChangeListener(event -> {

    boolean isValid =!event.hasValidationErrors();
    boolean hasChanges = binder.hasChanges();
    save.setEnabled(isValid && hasChanges);
});


   

    isim.addValueChangeListener(event -> save.setEnabled(binder.isValid()));
    personelId.addValueChangeListener(event -> save.setEnabled(binder.isValid()));
    soyisim.addValueChangeListener(event -> save.setEnabled(binder.isValid()));
    telefon.addValueChangeListener(event -> save.setEnabled(binder.isValid()));


    add(personelId,isim,
        soyisim,telefon,
        

        createButtonsLayout()); 
  }

  private HorizontalLayout createButtonsLayout() {
    save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
    close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

    save.addClickShortcut(Key.ENTER);
    close.addClickShortcut(Key.ESCAPE);

    save.addClickListener(event -> validateAndSave());
    delete.addClickListener(event -> fireEvent(new DeleteEvent(this, personel)));
    close.addClickListener(event -> fireEvent(new CloseEvent(this)));


    binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

    return new HorizontalLayout(save, delete, close); 
  }

  public void setPersonel(Personel personel) {
    this.personel = personel;
    binder.readBean(personel);
  }

  private void validateAndSave() {
    try {
      binder.writeBean(personel);
      fireEvent(new SaveEvent(this, personel));
    } catch (ValidationException e) {
      e.printStackTrace();
    }
  }

  // Events
  public static abstract class PersonelFormEvent extends ComponentEvent<PersonelForm> {
    private Personel personel;

    protected PersonelFormEvent(PersonelForm source, Personel personel) {
      super(source, false);
      this.personel = personel;
    }

    public Personel getPersonel() {
      return personel;
    }
  }

  public static class SaveEvent extends PersonelFormEvent {
    SaveEvent(PersonelForm source, Personel personel) {
      super(source, personel);
    }
  }

  public static class DeleteEvent extends PersonelFormEvent {
    DeleteEvent(PersonelForm source, Personel personel) {
      super(source, personel);
    }

  }

  public static class CloseEvent extends PersonelFormEvent {
    CloseEvent(PersonelForm source) {
      super(source, null);
    }
  }

  public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                ComponentEventListener<T> listener) {
    return getEventBus().addListener(eventType, listener);
  }
}