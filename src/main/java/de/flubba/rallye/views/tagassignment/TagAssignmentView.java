package de.flubba.rallye.views.tagassignment;

import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.flubba.rallye.entity.repository.TagAssignmentRepository;
import de.flubba.rallye.views.MainLayout;
import org.springframework.data.domain.PageRequest;

@PageTitle("Tag Assignment")
@Route(value = "tag-assignment", layout = MainLayout.class)
@Uses(Icon.class)
public class TagAssignmentView extends TagAssignmentsViewDesign {
    public static final String VIEW_NAME = "tags";

    private final TagAssignmentRepository tagAssignmentRepository;

    public TagAssignmentView(TagAssignmentRepository tagAssignmentRepository) {
        super();
        tagAssignments.setItems(query -> tagAssignmentRepository.findAll(PageRequest.of(query.getPage(), query.getPageSize())).stream());
        this.tagAssignmentRepository = tagAssignmentRepository;
    }

}
