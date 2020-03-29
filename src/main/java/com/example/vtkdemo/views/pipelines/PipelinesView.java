package com.example.vtkdemo.views.pipelines;

import com.example.vtkdemo.entity.PipelineEntity;
import com.example.vtkdemo.model.PipelineRequest;
import com.example.vtkdemo.service.PipelineService;
import com.example.vtkdemo.views.filters.FiltersView;
import com.example.vtkdemo.views.main.MainView;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.stream.Collectors;

@Route(value = "pipelines", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Pipelines")
@CssImport("styles/views/pipelines/pipelines-view.css")
public class PipelinesView extends Div implements AfterNavigationObserver {

    @Autowired
    private PipelineService service;

    private SplitLayout splitLayout;
    private Grid<PipelineEntity> pipelines;

    private TextField id = new TextField();
    private TextField name = new TextField();

    private TextField filterText = new TextField();

    private Button addNew = new Button("Add new pipeline");
    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");
    private Button delete = new Button("Delete");

    private Binder<PipelineEntity> binder;
    private PipelineEntity pipeline;

    public PipelinesView() {
        setId("pipelines-view");
        // Configure Grid
        pipelines = new Grid<>(PipelineEntity.class);
//        pipelines.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        pipelines.setHeightByRows(true);
        pipelines.setMaxHeight("80%");
        pipelines.setColumns("id", "name");
        pipelines.addComponentColumn(this::createFiltersButton).setWidth("70px").setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.CENTER);
        pipelines.getColumnByKey("id").setFlexGrow(0);
        pipelines.getColumnByKey("name").setFlexGrow(1);
        //when a row is selected or deselected, populate form
        pipelines.asSingleSelect().addValueChangeListener(event -> populateForm(event.getValue()));
        pipelines.addItemDoubleClickListener(e -> navigateToFilters(e.getItem()));

        // Configure Form
        binder = new Binder<>(PipelineEntity.class);

        // Bind fields. This where you'd define e.g. validation rules
        binder.forField(id)
                .bind(pipelineEntity -> Objects.nonNull(pipelineEntity.getId()) ? pipelineEntity.getId().toString() : "", null);
        binder.bindInstanceFields(this);
        // note that password field isn't bound since that property doesn't exist in
        // Employee
        addNew.addClickListener(e -> populateForm(new PipelineEntity()));

        // the grid valueChangeEvent will clear the form too
        cancel.addClickListener(e -> {
            ((Div)splitLayout.getSecondaryComponent()).setEnabled(false);
            pipelines.asSingleSelect().clear();
        });

        save.addClickListener(e -> {
            if (binder.writeBeanIfValid(pipeline)) {
                PipelineRequest request = new PipelineRequest();
                request.setName(pipeline.getName());
                request.setPipelineDto(pipeline.getPipelineDto());

                if (pipeline.getId() == null) {
                    service.createPipeline(request);
                } else {
                    service.updateById(pipeline.getId(), request);
                }
                Notification.show("Saved!");
                listPipelines(filterText.getValue());
                pipelines.setItems(service.findAll());
                ((Div)splitLayout.getSecondaryComponent()).setEnabled(false);
            } else {
                Notification.show("Data invalid");
            }
        });

        delete.addClickListener(e -> {
            Dialog dialog = new Dialog();

            dialog.setCloseOnEsc(false);
            dialog.setCloseOnOutsideClick(false);

            Div content = new Div();

            content.setText(MessageFormat.format("Do you want to remove pipeline {0}?", pipeline.getId()));
            dialog.add(content);

            NativeButton confirmButton = new NativeButton("Confirm", event -> {
                service.deleteById(pipeline.getId());
                listPipelines(filterText.getValue());
                dialog.close();
            });
            NativeButton cancelButton = new NativeButton("Cancel", event -> dialog.close());
            dialog.add(confirmButton, cancelButton);
            dialog.open();
        });

        splitLayout = new SplitLayout();
        splitLayout.setSizeFull();
        splitLayout.setSecondaryStyle("minWidth", "30%");

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);
    }

    private Component createFiltersButton(PipelineEntity pipelineEntity) {
        Button button = new Button(new Icon(VaadinIcon.EDIT), clickEvent -> navigateToFilters(pipelineEntity));
        button.setClassName("delete-button");
        button.addThemeName("small");
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return button;
    }

    private void navigateToFilters(PipelineEntity pipelineEntity) {
        UI.getCurrent().navigate(FiltersView.class, pipelineEntity.getId());
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorDiv = new Div();
        editorDiv.setEnabled(false);
        editorDiv.setId("editor-layout");
        FormLayout formLayout = new FormLayout();
        addFormItem(editorDiv, formLayout, id, "Id");
        addFormItem(editorDiv, formLayout, name, "Name");
        createButtonLayout(editorDiv);
        splitLayout.addToSecondary(editorDiv);
    }

    private void createButtonLayout(Div editorDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setId("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        buttonLayout.add(delete, cancel, save);
        editorDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);

        Label title = new Label();
        title.setText("Pipelines");
        title.setId("page-title");

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setId("button-layout");
        titleLayout.setWidthFull();
        titleLayout.setSpacing(true);
        titleLayout.add(title);
        titleLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);


        addNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        filterText.setPlaceholder("Filter by pipeline name");
        filterText.setValueChangeMode(ValueChangeMode.EAGER);
        filterText.addValueChangeListener(e -> listPipelines(e.getValue()));

        Div filterDiv = new Div();
        filterDiv.setId("filtertext-wrapper");
        filterDiv.setSizeUndefined();
        filterDiv.add(filterText);

        HorizontalLayout actionsLayout = new HorizontalLayout();
        actionsLayout.setId("actions-layout");
        actionsLayout.setWidthFull();
        actionsLayout.setSpacing(true);
        actionsLayout.add(filterDiv, addNew);

        wrapper.add(titleLayout, actionsLayout, pipelines);
    }

    private void addFormItem(Div wrapper, FormLayout formLayout,
            AbstractField field, String fieldName) {
        formLayout.addFormItem(field, fieldName);
        wrapper.add(formLayout);
        field.getElement().getClassList().add("full-width");
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

        // Lazy init of the grid items, happens only when we are sure the view will be
        // shown to the user
        listPipelines(null);
    }

    private void listPipelines(String filterText) {
        if(StringUtils.isEmpty(filterText)) {
            pipelines.setItems(service.findAll());
        } else {
            pipelines.setItems(service.findAll().stream()
                    .filter(pipelineEntity -> pipelineEntity.getName().matches("^(?i).*" + filterText + ".*$"))
                    .collect(Collectors.toList()));
        }
    }

    private void populateForm(PipelineEntity value) {
        // Value can be null as well, that clears the form
        pipeline = value;
        delete.setEnabled(Objects.nonNull(pipeline.getId()));
        binder.readBean(pipeline);
        ((Div)splitLayout.getSecondaryComponent()).setEnabled(true);
    }
}
