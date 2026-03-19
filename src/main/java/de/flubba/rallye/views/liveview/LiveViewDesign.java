package de.flubba.rallye.views.liveview;

import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.flubba.rallye.views.ViewToolbar;

abstract class LiveViewDesign extends VerticalLayout {
    private final HorizontalLayout toolbarLayout = new ViewToolbar("Live View");

    protected final MessageList messageList = new MessageList();

    protected LiveViewDesign() {
        messageList.setSizeFull();

        add(toolbarLayout);
        addAndExpand(messageList);
    }
}
