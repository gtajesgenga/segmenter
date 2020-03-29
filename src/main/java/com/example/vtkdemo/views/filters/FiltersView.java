package com.example.vtkdemo.views.filters;

import com.example.vtkdemo.entity.PipelineEntity;
import com.example.vtkdemo.model.FilterDto;
import com.example.vtkdemo.model.PipelineDto;
import com.example.vtkdemo.model.PipelineRequest;
import com.example.vtkdemo.service.FilterService;
import com.example.vtkdemo.service.PipelineService;
import com.example.vtkdemo.views.main.MainView;
import com.google.common.collect.Lists;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dnd.GridDropLocation;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import javafx.beans.property.SimpleBooleanProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Route(value = "filters", layout = MainView.class)
@PageTitle("Filters")
@CssImport("styles/views/filters/filters-view.css")
public class FiltersView extends Div implements HasUrlParameter<Long> {

    private final SimpleBooleanProperty listChanged;

    @Autowired
    private PipelineService pipelineService;

    private Grid<FilterDto> filters;

    private TextField filterText = new TextField();

    private List<FilterDto> currentFilters;
    private PipelineEntity currentPipeline;
    private Select<PipelineEntity> pipelineEntitySelect;
    private FilterDto draggedItem;

    public FiltersView() {
        setId("filters-view");

        listChanged = new SimpleBooleanProperty(false);

        // Configure Grid
        filters = new Grid<>();
//        filters.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        filters.setMaxHeight("80%");
        filters.setSelectionMode(Grid.SelectionMode.NONE);
        filters.addColumn(i -> currentFilters.indexOf(i)).setHeader("Order").setKey("order");
        filters.addColumn(filterDto -> filterDto.getFilterClass().substring(filterDto.getFilterClass().lastIndexOf('.') + 1))
                .setHeader("Filter Class").setKey("filterClass");
        filters.addComponentColumn(filterDto -> createRemoveButton(filters, filterDto)).setWidth("70px").setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.CENTER);
        filters.getColumnByKey("order").setFlexGrow(0);
        filters.getColumnByKey("filterClass").setFlexGrow(1);
        filters.getColumns().forEach(col -> col.setSortable(false));
        filters.setRowsDraggable(true);

        filters.addDragStartListener(event -> {
            draggedItem = event.getDraggedItems().get(0);
            filters.setDropMode(GridDropMode.BETWEEN);
        });

        filters.addDragEndListener(event -> {
            draggedItem = null;
            filters.setDropMode(null);
        });

        filters.addDropListener(event -> {
            FilterDto dropOverItem = event.getDropTargetItem().get();
            if (!dropOverItem.equals(draggedItem)) {
                currentFilters.remove(draggedItem);
                int dropIndex = currentFilters.indexOf(dropOverItem)
                        + (event.getDropLocation() == GridDropLocation.BELOW ? 1
                        : 0);
                currentFilters.add(dropIndex, draggedItem);
                filters.getDataProvider().refreshAll();
                listChanged.set(true);
            }
        });

        createGridLayout();
    }

    private Component createRemoveButton(Grid<FilterDto> filters, FilterDto filterDto) {
        Button button = new Button(new Icon(VaadinIcon.TRASH), clickEvent -> {
            ListDataProvider<FilterDto> dataProvider = (ListDataProvider<FilterDto>) filters
                    .getDataProvider();
            dataProvider.getItems().remove(filterDto);
            dataProvider.refreshAll();
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

        Label title = new Label();
        title.setText("Filters");
        title.setId("page-title");

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setId("button-layout");
        titleLayout.setWidthFull();
        titleLayout.setSpacing(true);
        titleLayout.add(title);
        titleLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);

        pipelineEntitySelect = new Select<>();
        pipelineEntitySelect.setPlaceholder("Select pipeline");
        pipelineEntitySelect.setItemLabelGenerator(PipelineEntity::getName);
        pipelineEntitySelect.addValueChangeListener(event -> {
            currentPipeline = event.getValue();
            listFilters(filterText.getValue());
        });

        FormLayout pipelineSelectLayout = new FormLayout();
        pipelineSelectLayout.addFormItem(pipelineEntitySelect, "Pipeline");

        Select<FilterDto> availableFiltersSelect = new Select<>();
        availableFiltersSelect.setPlaceholder("Select filter to add...");
        availableFiltersSelect.setItemLabelGenerator(item -> item.getFilterClass()
                .substring(item.getFilterClass().lastIndexOf('.') + 1));
        availableFiltersSelect.setItems(FilterService.findAll());

        Button addNew = new Button("Add new filter");
        addNew.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addNew.addClickListener(e -> {
            if (availableFiltersSelect.getValue() != null) {
                currentFilters.add(availableFiltersSelect.getValue());
                filters.getDataProvider().refreshAll();
                listChanged.set(true);
            }
        });

        Button save = new Button("Save");
        save.setEnabled(listChanged.get());
        save.setDisableOnClick(true);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(e -> {
            if (currentPipeline != null && currentFilters != null) {
                PipelineDto pipelineDto = Optional.of(currentPipeline.getPipelineDto()).orElse(new PipelineDto());
                pipelineDto.setFilters(currentFilters);
                currentPipeline.setPipelineDto(pipelineDto);

                PipelineRequest request = new PipelineRequest();
                request.setName(currentPipeline.getName());
                request.setPipelineDto(currentPipeline.getPipelineDto());

                pipelineService.updateById(currentPipeline.getId(), request);
            }
        });

        listChanged.addListener((observable, oldVal, newVal) -> {
            save.setEnabled(newVal);
        });


        filterText.setPlaceholder("Filter by filter class name");
        filterText.setValueChangeMode(ValueChangeMode.EAGER);
        filterText.addValueChangeListener(e -> listFilters(e.getValue()));

        Div filterDiv = new Div();
        filterDiv.setId("filtertext-wrapper");
        filterDiv.setSizeUndefined();
        filterDiv.add(filterText);


        HorizontalLayout actionsLayout = new HorizontalLayout();
        actionsLayout.setId("actions-layout");
        actionsLayout.setWidthFull();
        actionsLayout.setSpacing(true);
        actionsLayout.add(filterDiv, availableFiltersSelect, addNew, save);

        wrapper.add(titleLayout, pipelineSelectLayout, actionsLayout, filters);

        add(wrapper);
    }

    private void listFilters(String filterText) {
        if (currentPipeline == null || currentPipeline.getPipelineDto() == null) {
            currentFilters = Lists.newArrayList();
        } else {
            if (StringUtils.isEmpty(filterText)) {
                currentFilters = currentPipeline.getPipelineDto().getFilters();
            } else {
                currentFilters = currentPipeline.getPipelineDto().getFilters().stream()
                        .filter(filterDto -> filterDto.getFilterClass().matches("^(?i).*" + filterText + ".*$"))
                        .collect(Collectors.toList());
            }
        }
        listChanged.set(false);
        filters.setItems(currentFilters);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {
        List<PipelineEntity> all = pipelineService.findAll();
        pipelineEntitySelect.setItems(all);
        if (parameter == null) {
            pipelineEntitySelect.setValue(all.stream().findFirst().orElse(null));
        } else {
            pipelineEntitySelect.setValue(all.stream().filter(pipelineEntity -> parameter.equals(pipelineEntity.getId())).findFirst().orElse(null));
        }
    }
}
