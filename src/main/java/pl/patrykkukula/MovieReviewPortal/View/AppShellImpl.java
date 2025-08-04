package pl.patrykkukula.MovieReviewPortal.View;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.theme.Theme;

@Theme(value = "global-theme")
public class AppShellImpl implements AppShellConfigurator {

    @Override
    public void configurePage(AppShellSettings settings) {
        settings.addFavIcon("icon","icons/favicon.jpeg","any");
        settings.addLink("shortcut icon", "icons/favicon.jpeg");
    }
}
