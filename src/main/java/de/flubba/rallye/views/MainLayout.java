package de.flubba.rallye.views;


import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;
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
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        H1 appName = new H1("LTC Rallye");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();
        nav.addItem(
                new SideNavItem("Runners", RunnersView.class, LineAwesomeIcon.RUNNING_SOLID.create()),
                new SideNavItem("Results", ResultsView.class, LineAwesomeIcon.TROPHY_SOLID.create()),
                new SideNavItem("Tag Assignment", TagAssignmentView.class, LineAwesomeIcon.BULLSEYE_SOLID.create()),
                new SideNavItem("Live View", LiveViewView.class, LineAwesomeIcon.BULLHORN_SOLID.create())
        );
        return nav;
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
