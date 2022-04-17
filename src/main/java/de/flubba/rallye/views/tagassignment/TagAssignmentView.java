package de.flubba.rallye.views.tagassignment;

import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.flubba.rallye.entity.repository.TagAssignmentRepository;
import de.flubba.rallye.views.MainLayout;

@PageTitle("Tag Assignment")
@Route(value = "tag-assignment", layout = MainLayout.class)
@Uses(Icon.class)
public class TagAssignmentView extends TagAssignmentsViewDesign {
    public static final String VIEW_NAME = "tags";

    private final TagAssignmentRepository tagAssignmentRepository;

    public TagAssignmentView(TagAssignmentRepository tagAssignmentRepository) {
        super();
        this.tagAssignmentRepository = tagAssignmentRepository;
        tagAssignments.setDataProvider(new ListDataProvider<>(tagAssignmentRepository.findAll())); //TODO replace
    }

}
