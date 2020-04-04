package com.example.vtkdemo.views.methods;

import com.example.vtkdemo.entity.PipelineEntity;
import com.example.vtkdemo.model.FilterDto;
import com.example.vtkdemo.model.Method;
import com.example.vtkdemo.model.PipelineRequest;
import com.example.vtkdemo.service.FilterService;
import com.example.vtkdemo.service.PipelineService;
import com.example.vtkdemo.views.main.MainView;
import com.google.common.collect.Lists;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import javafx.beans.property.SimpleBooleanProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Route(value = "methods", layout = MainView.class)
@PageTitle("Methods")
@CssImport("styles/views/methods/methods-view.css")
public class MethodsView extends Div implements HasUrlParameter<String> {

    private final PipelineService pipelineService;
    private final SimpleBooleanProperty listChanged;
    private PipelineEntity currentPipeline;
    private FilterDto currentFilter;
    private List<Method> currentMethods;

    private Grid<Method> methods;

    private TextField filterText = new TextField();

    public MethodsView(@Autowired PipelineService service) {
        setId("methods-view");
        pipelineService = service;
        listChanged = new SimpleBooleanProperty(false);

        methods = new Grid<>();
        methods.addColumn(Method::getName)
                .setHeader("Name").setKey("filterClass");
        methods.addComponentColumn(this::createEditButton).setWidth("70px").setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.CENTER);
        methods.addComponentColumn(this::createDeleteButton).setWidth("70px").setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.CENTER);
    }

    private Component createEditButton(Method method) {
        Button button = new Button(new Icon(VaadinIcon.EDIT), clickEvent -> {});
        button.setClassName("delete-button");
        button.addThemeName("small");
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return button;
    }

    private Component createDeleteButton(Method method) {
        Button button = new Button(new Icon(VaadinIcon.TRASH), clickEvent -> {
            Dialog dialog = new Dialog();

            dialog.setCloseOnEsc(false);
            dialog.setCloseOnOutsideClick(false);

            Div content = new Div();

            content.setText(MessageFormat.format("Do you want to remove method {0}?", method.getName()));
            dialog.add(content);

            Button confirmButton = new Button("Confirm", event -> {
                ListDataProvider<Method> dataProvider = (ListDataProvider<Method>) methods
                        .getDataProvider();
                dataProvider.getItems().remove(method);
                dataProvider.refreshAll();
                listChanged.set(true);
                dialog.close();
            });
            confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            Button cancelButton = new Button("Cancel", event -> dialog.close());
            cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            dialog.add(confirmButton, cancelButton);
            dialog.open();
        });
        button.setClassName("delete-button");
        button.addThemeName("small");
        button.addThemeVariants(ButtonVariant.LUMO_ERROR);
        return button;
    }

    private void createGridLayout() {

        Div wrapper = new Div();
        wrapper.setId("wrapper");
        wrapper.setSizeFull();

        ComboBox<Method> availableMethodsSelect = new ComboBox<>();
        availableMethodsSelect.setPlaceholder("Select method to add...");
        availableMethodsSelect.setItemLabelGenerator(Method::getName);
        availableMethodsSelect.setItems(FilterService.find(currentFilter.getFilterClass()).get().getMethods());
        availableMethodsSelect.setClearButtonVisible(true);

        Button addNew = new Button("Add new method");
        addNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addNew.addClickListener(e -> {
            if (availableMethodsSelect.getValue() != null) {
                Method method = Method.builder()
                        .name(availableMethodsSelect.getValue().getName())
                        .parameters(null)
                        .build();
                currentMethods.add(method);
                methods.getDataProvider().refreshAll();
                listChanged.set(true);
            }
        });

        Button save = new Button("Save");
        save.setEnabled(listChanged.get());
        save.setDisableOnClick(true);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(e -> {
            if (currentFilter != null) {
                currentFilter.setMethods(currentMethods);

                PipelineRequest request = new PipelineRequest();
                request.setName(currentPipeline.getName());
                request.setPipelineDto(currentPipeline.getPipelineDto());

                pipelineService.updateById(currentPipeline.getId(), request);
            }
        });

        listChanged.addListener((observable, oldVal, newVal) -> {
            save.setEnabled(newVal);
        });


        filterText.setPlaceholder("Filter by method name");
        filterText.setValueChangeMode(ValueChangeMode.EAGER);
        filterText.addValueChangeListener(e -> listMethods(e.getValue()));

        Div filterDiv = new Div();
        filterDiv.setId("filtertext-wrapper");
        filterDiv.setSizeUndefined();
        filterDiv.add(filterText);


        HorizontalLayout actionsLayout = new HorizontalLayout();
        actionsLayout.setId("actions-layout");
        actionsLayout.setWidthFull();
        actionsLayout.setSpacing(true);
        actionsLayout.add(filterDiv, availableMethodsSelect, addNew, save);

        wrapper.add(actionsLayout, methods);

        add(wrapper);
        listMethods(filterText.getValue());
    }

    private void listMethods(String filterText) {
        if (currentFilter == null) {
            currentMethods = Lists.newArrayList();
        } else {
            currentMethods = Optional.ofNullable(currentFilter.getMethods()).orElse(Lists.newArrayList());
            if (StringUtils.isEmpty(filterText)) {
                methods.setItems(currentMethods);
            } else {
                methods.setItems(currentMethods.stream()
                        .filter(method -> method.getName().matches("^(?i).*" + filterText + ".*$"))
                        .collect(Collectors.toList()));
            }
        }
        listChanged.set(false);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        if (!StringUtils.isEmpty(parameter)) {
            String[] split = parameter.split("-", 2);
            if (split.length == 2) {
                currentPipeline = pipelineService.findById(Long.valueOf(split[0])).orElse(null);

                if (currentPipeline != null && currentPipeline.getPipelineDto() != null) {
                    currentFilter = Optional.ofNullable(currentPipeline.getPipelineDto().getFilters()).orElse(Collections.emptyList()).stream()
                            .filter(filterDto -> filterDto.getUuid().equals(split[1]))
                            .findFirst().orElse(null);
                    if (currentFilter == null) {
                        throwException("Filter not found");
                    }
                } else {
                    throwException("Pipeline not found");
                }
            } else {
                throwException("Invalid resource id");
            }
        } else {
            throwException("Invalid url");
        }
        createGridLayout();
    }

    private void throwException(String message) {
        throw new NotFoundException(message);
    }
}
