package de.flubba.rallye.views;


import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;
import de.flubba.rallye.component.AppNav;
import de.flubba.rallye.component.AppNavItem;
import de.flubba.rallye.views.liveview.LiveViewView;
import de.flubba.rallye.views.results.ResultsView;
import de.flubba.rallye.views.runners.RunnersView;
import de.flubba.rallye.views.tagassignment.TagAssignmentView;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H2 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        H2 appName = new H2("LTC Rallye");
        appName.addClassNames("app-name");
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private AppNav createNavigation() {
        return new AppNav(
                new AppNavItem("Runners", RunnersView.class, LineAwesomeIcon.RUNNING_SOLID.create()),
                new AppNavItem("Results", ResultsView.class, LineAwesomeIcon.TROPHY_SOLID.create()),
                new AppNavItem("Tag Assignment", TagAssignmentView.class, LineAwesomeIcon.BULLSEYE_SOLID.create()),
                new AppNavItem("Live View", LiveViewView.class, LineAwesomeIcon.BULLHORN_SOLID.create())
        );
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        layout.add(new Text("Powered by Flubba"));

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
