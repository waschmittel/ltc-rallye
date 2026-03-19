package de.flubba.rallye.views.tagassignment;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.flubba.rallye.entity.TagAssignment;
import de.flubba.rallye.views.ViewToolbar;

abstract class TagAssignmentsViewDesign extends VerticalLayout {
    final Grid<TagAssignment> tagAssignments = new Grid<>(TagAssignment.class);

    protected TagAssignmentsViewDesign() {
        tagAssignments.removeColumn(tagAssignments.getColumnByKey(TagAssignment.Fields.id));
        tagAssignments.getColumnByKey(TagAssignment.Fields.runnerId).setFlexGrow(0);
        tagAssignments.setSortableColumns();
        add(new ViewToolbar("Tag Assignments"));
        addAndExpand(tagAssignments);
    }
}
