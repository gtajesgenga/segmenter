package com.example.vtkdemo.views.methods;

import com.example.vtkdemo.model.Method;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * A Designer generated component for the method-editor template.
 *
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@CssImport("styles/views/methods/method-editor.css")
public class MethodEditor extends Dialog {

    private final Method method;
    private final BiConsumer<Boolean, Method> callback;
    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");


    /**
     * Creates a new MethodEditor.
     * @param method
     */
    public MethodEditor(Method method, BiConsumer<Boolean, Method> callback) {
        // You can initialise any data required for the connected UI components here.
        this.method = method;
        this.callback = callback;

        FormLayout layout = new FormLayout();
        add(layout);

        createForm(layout);

        save.addClickListener(event -> {
            doCallback(true);
        });

        cancel.addClickListener(event -> doCallback(false));

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setId("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(cancel, save);
        layout.add(buttonLayout);
    }

    private void doCallback(Boolean result) {
        this.callback.accept(result, method);
        close();
    }

    private void createForm(FormLayout layout) {
        Div container = new Div();
        container.setClassName("form-title-container");
        container.setSizeFull();
        Label name = new Label();
        name.setText(method.getName());
        container.add(name);
        layout.add(container);

        if (Objects.nonNull(method.getParameters())) {
            method.getParameters().forEach(parameter -> {
                AbstractField field = null;
                if (StringUtils.isEmpty(parameter.getMultidimensional())) {
                    if (Number.class.isAssignableFrom(parameter.getDefaultCasting())) {
                        if (parameter.getDefaultCasting().getSimpleName().matches("^Short|Long|Int$")) {
                            field = new NumberField();
                            ((NumberField) field).setStep(1.0);

                            switch (parameter.getDefaultCasting().getSimpleName()) {
                                case "Short":
                                    ((NumberField) field).setMin(Short.MIN_VALUE);
                                    ((NumberField) field).setMax(Short.MAX_VALUE);
                                    ((NumberField) field).setPlaceholder("short value");
                                    break;
                                case "Int":
                                    ((NumberField) field).setMin(Double.valueOf(String.valueOf(Integer.MIN_VALUE)));
                                    ((NumberField) field).setMax(Integer.MAX_VALUE);
                                    ((NumberField) field).setPlaceholder("int value");
                                    break;
                                case "Long":
                                    ((NumberField) field).setMin(Double.valueOf(String.valueOf(Long.MIN_VALUE)));
                                    ((NumberField) field).setMax(Long.MAX_VALUE);
                                    ((NumberField) field).setPlaceholder("long value");
                                    break;
                            }
                        } else if (parameter.getDefaultCasting().getSimpleName().matches("^Float|Double$")) {
                            field = new NumberField();

                            switch (parameter.getDefaultCasting().getSimpleName()) {
                                case "Float":
                                    ((NumberField) field).setMin(Double.valueOf(String.valueOf(Float.MIN_VALUE)));
                                    ((NumberField) field).setMax(Float.MAX_VALUE);
                                    ((NumberField) field).setPlaceholder("float value");
                                    break;
                                case "Double":
                                    ((NumberField) field).setMin(-Double.MAX_VALUE);
                                    ((NumberField) field).setMax(Double.MAX_VALUE);
                                    ((NumberField) field).setPlaceholder("double value");
                                    break;
                            }
                        }
                        field.setValue(Double.valueOf(Objects.nonNull(parameter.getValue()) ? parameter.getValue() : "0.0"));
                    } else if (Boolean.class.isAssignableFrom(parameter.getDefaultCasting())) {
                        field = new Checkbox();
                        field.setValue(Boolean.valueOf(parameter.getValue()));
                    }
                } else {
                    field = new TextField();
                    ((TextField) field).setPlaceholder("tern numeric value or percent: x%,y%,z%");
                    field.setValue(Objects.nonNull(parameter.getValue()) ? parameter.getValue() : "");
                }
                field.addValueChangeListener(event -> parameter.setValue(String.valueOf(event.getValue())));

                layout.addFormItem(field, parameter.getName());
            });
        }
    }
}
