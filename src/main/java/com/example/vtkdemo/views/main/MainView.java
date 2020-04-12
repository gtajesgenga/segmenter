package com.example.vtkdemo.views.main;

import com.example.vtkdemo.views.filters.FiltersView;
import com.example.vtkdemo.views.pipelines.PipelinesView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

/**
 * The main view is a top-level placeholder for other views.
 */
@JsModule("./styles/shared-styles.js")
//@PWA(name = "VtkCrud", shortName = "VtkCrud")
@Theme(value = Lumo.class, variant = Lumo.LIGHT)
@RoutePrefix("ui")
public class MainView extends AppLayout {

    private static Button prev = new Button("Go back", new Icon(VaadinIcon.BACKSPACE_A));
    public static Stack<String> navigation = new Stack<>() {

        @Override
        public String push(String item) {
            String res = super.push(item);
            update();
            return res;
        }

        @Override
        public String pop() {
            String res = super.pop();
            update();
            return res;
        }

        private void update() {
            prev.setVisible(!this.isEmpty());
        }
    };

    private final Tabs menu;
    private final Label title;

    public MainView() {
        title = new Label();
        title.setId("page-title");
//        prev.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        prev.setVisible(!navigation.isEmpty());
        prev.addClickListener(e -> {
           if (!navigation.isEmpty()) {
               UI.getCurrent().navigate(navigation.pop());
           }
        });

        setPrimarySection(Section.DRAWER);
        addToNavbar(true, new DrawerToggle());
        addToNavbar(true, prev);
        addToNavbar(true, title);
        menu = createMenuTabs();
        addToDrawer(menu);
    }

    private static Tabs createMenuTabs() {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.setId("tabs");
        tabs.add(getAvailableTabs());
        return tabs;
    }

    private static Tab[] getAvailableTabs() {
        final List<Tab> tabs = new ArrayList<>();
        tabs.add(createTab("Pipelines", PipelinesView.class));
        tabs.add(createTab("Filters", FiltersView.class));
        return tabs.toArray(new Tab[tabs.size()]);
    }

    private static Tab createTab(String title, Class<? extends Component> viewClass) {
        return createTab(populateLink(new RouterLink(null, viewClass), title));
    }

    private static Tab createTab(Component content) {
        final Tab tab = new Tab();
        tab.add(content);
        return tab;
    }

    private static <T extends HasComponents> T populateLink(T a, String title) {
        a.add(title);
        return a;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        selectTab();
    }

    private void selectTab() {
        String target = RouteConfiguration.forSessionScope().getUrl(getContent().getClass());
        Optional<Component> tabToSelect = menu.getChildren().filter(tab -> {
            Component child = tab.getChildren().findFirst().get();
            return child instanceof RouterLink && ((RouterLink) child).getHref().equals(target);
        }).findFirst();
        title.setText(StringUtils.capitalize(target.substring(target.indexOf('/')+1)));
        tabToSelect.ifPresent(tab -> {
            title.setText(tab.getElement().getChildren().findFirst().get().getText());
            menu.setSelectedTab((Tab) tab);
        });
    }
}
