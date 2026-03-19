package de.flubba.rallye.views;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import de.flubba.rallye.views.filedownload.FileDownloadView;
import de.flubba.rallye.views.liveview.LiveViewView;
import de.flubba.rallye.views.results.ResultsView;
import de.flubba.rallye.views.runners.RunnersView;
import de.flubba.rallye.views.tagassignment.TagAssignmentView;
import org.vaadin.lineawesome.LineAwesomeIcon;

public class MainLayout extends AppLayout {

    private H2 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
    }

    private void addDrawerContent() {
        H1 appName = new H1("LTC Rallye");
        var header = new HorizontalLayout(appName);
        header.setMargin(true);
        header.setPadding(true);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();
        nav.addItem(
                new SideNavItem("Runners", RunnersView.class, LineAwesomeIcon.RUNNING_SOLID.create()),
                new SideNavItem("Results", ResultsView.class, LineAwesomeIcon.TROPHY_SOLID.create()),
                new SideNavItem("Tag Assignment", TagAssignmentView.class, LineAwesomeIcon.BULLSEYE_SOLID.create()),
                new SideNavItem("Live View", LiveViewView.class, LineAwesomeIcon.BULLHORN_SOLID.create()),
                new SideNavItem("Downloads", FileDownloadView.class, LineAwesomeIcon.DOWNLOAD_SOLID.create())
        );
        return nav;
    }

    private Component createFooter() {
        var layout = new HorizontalLayout();
        layout.setMargin(true);

        layout.add(new Text("Powered by Flubba"));

        return layout;
    }
}
