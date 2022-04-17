package de.flubba.rallye.views.tagassignment;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.flubba.rallye.entity.TagAssignment;

import static com.vaadin.flow.component.grid.Grid.SelectionMode.NONE;

abstract class TagAssignmentsViewDesign extends VerticalLayout {
    final Grid<TagAssignment> tagAssignments = new Grid<>(TagAssignment.class);

    protected TagAssignmentsViewDesign() {
        tagAssignments.removeColumn(tagAssignments.getColumnByKey(TagAssignment.Fields.id));
        tagAssignments.setSelectionMode(NONE);
        addAndExpand(tagAssignments);
    }
}
