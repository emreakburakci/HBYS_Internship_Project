package com.example.application.views.list;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.application.data.entity.Hasta;
import com.example.application.data.presenter.HastaPresenter;
import com.example.application.data.presenter.PersonelPresenter;
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
import com.vaadin.flow.shared.Registration;

public class HastaForm extends FormLayout {
  private Hasta hasta;

  TextField isim = new TextField("İsim");
  TextField tcno = new TextField("TC Kimlik No");
  TextField soyisim = new TextField("Soyisim");
  ComboBox<String> cinsiyet = new ComboBox<>("Cinsiyet", List.of("Erkek","Kadın","Diğer"));
  TextField telefon = new TextField("Telefon No");
  EmailField email = new EmailField("Email");
  //ComboBox<Personel> personelBox = new ComboBox<>("İlişkili Personel"); 

  Binder<Hasta> binder = new BeanValidationBinder<>(Hasta.class);

  Button save = new Button("Kaydet");
  Button delete = new Button("Sil");
  Button close = new Button("İptal");
  

  @Autowired
  PersonelPresenter personelPresenter;
  
  public HastaForm() {

    addClassName("hasta-form");
    binder.bindInstanceFields(this);

    add(tcno,
        isim,
        soyisim,
        email,
        cinsiyet,
        telefon,
        createButtonsLayout()); 
  }

  private HorizontalLayout createButtonsLayout() {
    save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
    close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
   

    save.addClickShortcut(Key.ENTER);
    close.addClickShortcut(Key.ESCAPE);

    save.addClickListener(event -> validateAndSave());
    delete.addClickListener(event -> fireEvent(new DeleteEvent(this, hasta)));
    close.addClickListener(event -> fireEvent(new CloseEvent(this)));
    

    binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

    return new HorizontalLayout(save, delete, close); 
  }




  public void setHasta(Hasta hasta) {
    this.hasta = hasta;
    binder.readBean(hasta);
  }

  private void validateAndSave() {
    try {
      
      binder.writeBean(hasta);
      fireEvent(new SaveEvent(this, hasta));
    } catch (ValidationException e) {
      e.printStackTrace();
    }
  }


  // Events
  public static abstract class HastaFormEvent extends ComponentEvent<HastaForm> {
    private Hasta hasta;

    protected HastaFormEvent(HastaForm source, Hasta hasta) {
      super(source, false);
      this.hasta = hasta;
    }

    public Hasta getHasta() {
      return hasta;
    }
  }

  public static class SaveEvent extends HastaFormEvent {
    SaveEvent(HastaForm source, Hasta hasta) {
      super(source, hasta);
    }
  }

  public static class DeleteEvent extends HastaFormEvent {
    DeleteEvent(HastaForm source, Hasta hasta) {
      super(source, hasta);
    }

  }

  public static class CloseEvent extends HastaFormEvent {
    CloseEvent(HastaForm source) {
      super(source, null);
    }
  }

  public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                ComponentEventListener<T> listener) {
    return getEventBus().addListener(eventType, listener);
  }
}