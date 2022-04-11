package de.flubba.rallye.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.function.ValueProvider;

import static com.vaadin.flow.component.button.ButtonVariant.LUMO_ERROR;
import static com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY;

public class EditDeleteButtonsProvider<E> implements ValueProvider<E, EditDeleteButtonsProvider.EntityButtons> {
    public static final double COLUMN_WIDTH = 130;

    private final DeleteButtonClickListener<E> deleteButtonClickListener;
    private final EditButtonClickListener<E> editButtonClickListener;
    private final ShowEditButtonProvider<E> showEditButtonProvider;
    private final ShowDeleteButtonProvider<E> showDeleteButtonProvider;

    public EditDeleteButtonsProvider(EditButtonClickListener<E> editButtonClickListener,
                                     DeleteButtonClickListener<E> deleteButtonClickListener,
                                     ShowEditButtonProvider<E> showEditButtonProvider,
                                     ShowDeleteButtonProvider<E> showDeleteButtonProvider) {
        this.editButtonClickListener = editButtonClickListener;
        this.deleteButtonClickListener = deleteButtonClickListener;
        this.showEditButtonProvider = showEditButtonProvider;
        this.showDeleteButtonProvider = showDeleteButtonProvider;
    }

    public EditDeleteButtonsProvider(EditButtonClickListener<E> editButtonClickListener,
                                     DeleteButtonClickListener<E> deleteButtonClickListener) {
        this.editButtonClickListener = editButtonClickListener;
        this.deleteButtonClickListener = deleteButtonClickListener;
        showEditButtonProvider = sourceEntity -> true;
        showDeleteButtonProvider = sourceEntity -> true;
    }

    public EditDeleteButtonsProvider(EditButtonClickListener<E> editButtonClickListener) {
        this.editButtonClickListener = editButtonClickListener;
        deleteButtonClickListener = null;
        showEditButtonProvider = sourceEntity -> true;
        showDeleteButtonProvider = sourceEntity -> false;
    }

    @Override
    public EntityButtons apply(E sourceEntity) {
        EntityButtons buttons = new EntityButtons(showEditButtonProvider.showEditButtonFor(sourceEntity),
                showDeleteButtonProvider.showDeleteButtonFor(sourceEntity));
        buttons.editButton.addClickListener(event -> editButtonClickListener.editButtonClick(sourceEntity));
        buttons.deleteButton.addClickListener(event -> deleteButtonClickListener.deleteButtonClick(sourceEntity));
        return buttons;
    }

    static class EntityButtons extends HorizontalLayout {
        private final Button editButton = new Button("", VaadinIcon.PENCIL.create());
        private final Button deleteButton = new Button("", VaadinIcon.TRASH.create());

        EntityButtons(boolean withEditButton, boolean withDeleteButton) {
            setSizeFull();
            editButton.addThemeVariants(LUMO_TERTIARY);
            deleteButton.addThemeVariants(LUMO_ERROR);
            deleteButton.addThemeVariants(LUMO_TERTIARY);
            if (withEditButton) {
                add(editButton);
            }
            if (withDeleteButton) {
                add(deleteButton);
            }
        }
    }

    public interface DeleteButtonClickListener<E> {
        void deleteButtonClick(E sourceEntity);
    }

    public interface EditButtonClickListener<E> {
        void editButtonClick(E sourceEntity);
    }

    public interface ShowEditButtonProvider<E> {
        boolean showEditButtonFor(E sourceEntity);
    }

    public interface ShowDeleteButtonProvider<E> {
        boolean showDeleteButtonFor(E sourceEntity);
    }
}
