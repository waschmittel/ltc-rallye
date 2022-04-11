package de.flubba.rallye.views.tagassignment;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.flubba.rallye.entity.TagAssignment;

import javax.annotation.PostConstruct;

import static com.vaadin.flow.component.grid.Grid.SelectionMode.NONE;
import static com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.CENTER;

abstract class TagAssignmentsViewDesign extends VerticalLayout {
    final Grid<TagAssignment> tagAssignments = new Grid<>(TagAssignment.class);

    @PostConstruct
    private void init() {
        setDefaultHorizontalComponentAlignment(CENTER); //TODO: check if this works
        tagAssignments.removeColumn(tagAssignments.getColumnByKey("id"));
        tagAssignments.setSelectionMode(NONE);
        addAndExpand(tagAssignments);
    }
}
