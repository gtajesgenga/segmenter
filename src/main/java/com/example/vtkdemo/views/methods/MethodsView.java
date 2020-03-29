package com.example.vtkdemo.views.methods;

import com.example.vtkdemo.views.main.MainView;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "methods", layout = MainView.class)
@PageTitle("Methods")
@CssImport("styles/views/methods/methods-view.css")
public class MethodsView /*extends Div implements AfterNavigationObserver*/ {

//    @Autowired
//    private BackendService service;
//
//    private Grid<Employee> employees;
//
//    private TextField firstname = new TextField();
//    private TextField lastname = new TextField();
//    private TextField email = new TextField();
//    private PasswordField password = new PasswordField();
//
//    private Button cancel = new Button("Cancel");
//    private Button save = new Button("Save");
//
//    private Binder<Employee> binder;
//
//    public MethodsView() {
//        setId("methods-view");
//        // Configure Grid
//        employees = new Grid<>();
//        employees.addThemeVariants(GridVariant.LUMO_NO_BORDER);
//        employees.setHeightFull();
//        employees.addColumn(Employee::getFirstname).setHeader("First name");
//        employees.addColumn(Employee::getLastname).setHeader("Last name");
//        employees.addColumn(Employee::getEmail).setHeader("Email");
//
//        //when a row is selected or deselected, populate form
//        employees.asSingleSelect().addValueChangeListener(event -> populateForm(event.getValue()));
//
//        // Configure Form
//        binder = new Binder<>(Employee.class);
//
//        // Bind fields. This where you'd define e.g. validation rules
//        binder.bindInstanceFields(this);
//        // note that password field isn't bound since that property doesn't exist in
//        // Employee
//
//        // the grid valueChangeEvent will clear the form too
//        cancel.addClickListener(e -> employees.asSingleSelect().clear());
//
//        save.addClickListener(e -> {
//            Notification.show("Not implemented");
//        });
//
//        SplitLayout splitLayout = new SplitLayout();
//        splitLayout.setSizeFull();
//
//        createGridLayout(splitLayout);
//        createEditorLayout(splitLayout);
//
//        add(splitLayout);
//    }
//
//    private void createEditorLayout(SplitLayout splitLayout) {
//        Div editorDiv = new Div();
//        editorDiv.setId("editor-layout");
//        FormLayout formLayout = new FormLayout();
//        addFormItem(editorDiv, formLayout, firstname, "First name");
//        addFormItem(editorDiv, formLayout, lastname, "Last name");
//        addFormItem(editorDiv, formLayout, email, "Email");
//        addFormItem(editorDiv, formLayout, password, "Password");
//        createButtonLayout(editorDiv);
//        splitLayout.addToSecondary(editorDiv);
//    }
//
//    private void createButtonLayout(Div editorDiv) {
//        HorizontalLayout buttonLayout = new HorizontalLayout();
//        buttonLayout.setId("button-layout");
//        buttonLayout.setWidthFull();
//        buttonLayout.setSpacing(true);
//        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
//        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//        buttonLayout.add(cancel, save);
//        editorDiv.add(buttonLayout);
//    }
//
//    private void createGridLayout(SplitLayout splitLayout) {
//        Div wrapper = new Div();
//        wrapper.setId("wrapper");
//        wrapper.setWidthFull();
//        splitLayout.addToPrimary(wrapper);
//        wrapper.add(employees);
//    }
//
//    private void addFormItem(Div wrapper, FormLayout formLayout,
//            AbstractField field, String fieldName) {
//        formLayout.addFormItem(field, fieldName);
//        wrapper.add(formLayout);
//        field.getElement().getClassList().add("full-width");
//    }
//
//    @Override
//    public void afterNavigation(AfterNavigationEvent event) {
//
//        // Lazy init of the grid items, happens only when we are sure the view will be
//        // shown to the user
//        employees.setItems(service.getEmployees());
//    }
//
//    private void populateForm(Employee value) {
//        // Value can be null as well, that clears the form
//        binder.readBean(value);
//
//        // The password field isn't bound through the binder, so handle that
//        password.setValue("");
//    }
}
