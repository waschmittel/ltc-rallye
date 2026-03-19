package de.flubba.rallye.views.filedownload;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.FileDownloadHandler;
import com.vaadin.flow.server.streams.UploadHandler;
import de.flubba.generated.i18n.I18n;
import de.flubba.rallye.component.DeleteConfirmDialog;
import de.flubba.rallye.component.UnlockDialog;
import de.flubba.rallye.configuration.RallyeProperties;
import de.flubba.rallye.views.MainLayout;
import de.flubba.rallye.views.ViewToolbar;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import static com.vaadin.flow.component.grid.ColumnTextAlign.CENTER;
import static de.flubba.rallye.Application.TITLE_SUFFIX;
import static de.flubba.rallye.util.NotificationHelper.showAndLogError;
import static de.flubba.rallye.util.NotificationHelper.showSuccess;

@PageTitle("Downloads" + TITLE_SUFFIX)
@Route(value = "downloads", layout = MainLayout.class)
@Slf4j
public class FileDownloadView extends VerticalLayout {

    private final RallyeProperties rallyeProperties;
    private final Grid<FileInfo> fileGrid;
    private boolean unlocked = false;
    private final Button uploadButton = new Button("Upload File", VaadinIcon.UPLOAD.create());
    private final Button unlockButton = new Button(VaadinIcon.UNLOCK.create());
    private final Button refreshButton = new Button(VaadinIcon.REFRESH.create());

    public FileDownloadView(RallyeProperties rallyeProperties) {
        this.rallyeProperties = rallyeProperties;
        this.fileGrid = new Grid<>(FileInfo.class, false);

        setSizeFull();
        setPadding(true);

        ViewToolbar toolbar = new ViewToolbar("Tag Manager Downloads", unlockButton, uploadButton, refreshButton);
        add(toolbar);

        setupButtons();
        setupGrid();
        loadFiles();
        updateUIState();
    }

    private void setupButtons() {
        unlockButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        unlockButton.setTooltipText("unlock file management");
        unlockButton.addClickListener(_ -> showUnlockDialog());

        uploadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        uploadButton.addClickListener(_ -> showUploadDialog());
        uploadButton.setVisible(false);

        refreshButton.addClickListener(_ -> loadFiles());
    }

    private void setupGrid() {
        fileGrid.addComponentColumn(this::createDownloadLink)
                .setHeader(I18n.DOWNLOAD_FILENAME.get())
                .setAutoWidth(true)
                .setFlexGrow(1);
        fileGrid.addColumn(FileInfo::getSize)
                .setHeader(I18n.DOWNLOAD_FILESIZE.get())
                .setAutoWidth(true)
                .setFlexGrow(0);
        fileGrid.addComponentColumn(this::createDeleteButton)
                .setWidth("120px")
                .setFlexGrow(0)
                .setTextAlign(CENTER);

        fileGrid.setSizeFull();
        add(fileGrid);
    }

    private Anchor createDownloadLink(FileInfo fileInfo) {
        FileDownloadHandler downloadHandler = new FileDownloadHandler(fileInfo.file());

        Anchor downloadLink = new Anchor();
        downloadLink.setHref(downloadHandler);
        downloadLink.setText(fileInfo.getName());
        downloadLink.setTitle(I18n.DOWNLOAD_TOOLTIP.get(fileInfo.getName()));

        return downloadLink;
    }

    private void loadFiles() {
        List<FileInfo> files = new ArrayList<>();
        String folderPath = rallyeProperties.getDownloadFolder();

        try {
            Path path = Paths.get(folderPath);
            if (!Files.exists(path)) {
                showAndLogError(I18n.DOWNLOAD_FOLDER_NOT_FOUND.get(folderPath));
                fileGrid.setItems(files);
                return;
            }

            if (!Files.isDirectory(path)) {
                showAndLogError(I18n.DOWNLOAD_NOT_DIRECTORY.get(folderPath));
                fileGrid.setItems(files);
                return;
            }

            try (var stream = Files.list(path)) {
                stream
                        .filter(Files::isRegularFile)
                        .filter(file -> !file.getFileName().toString().startsWith("."))
                        .forEach(file -> files.add(new FileInfo(file.toFile())));
            }

        } catch (IOException e) {
            showAndLogError(I18n.DOWNLOAD_READ_ERROR.get(folderPath));
        }

        fileGrid.setItems(files);
    }

    private Button createDeleteButton(FileInfo fileInfo) {
        Button deleteButton = new Button("Delete");
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        deleteButton.addClickListener(_ -> deleteFile(fileInfo));
        deleteButton.setVisible(unlocked);
        return deleteButton;
    }

    private void updateUIState() {
        unlockButton.setVisible(!unlocked);
        uploadButton.setVisible(unlocked);
        fileGrid.getDataProvider().refreshAll();
    }

    private void showUnlockDialog() {
        new UnlockDialog(rallyeProperties.getAdminPassword(), () -> {
            unlocked = true;
            updateUIState();
        }).open();
    }

    private void showUploadDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Upload File (Max 500MB, overwriting existing files)");

        Upload upload = new Upload();
        upload.setMaxFiles(1);
        upload.setDropAllowed(true);
        upload.setMaxFileSize(500 * 1024 * 1024);

        UploadHandler uploadHandler = UploadHandler.toFile((metadata, file) -> {
            String fileName = metadata.fileName();
            Path targetPath = Paths.get(rallyeProperties.getDownloadFolder(), fileName);
            try {
                Files.move(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                showSuccess("File uploaded: " + fileName);
                loadFiles();
                dialog.close();
            } catch (IOException e) {
                showAndLogError("Failed to save file: " + fileName);
            }
        }, metadata -> {
            try {
                return Files.createTempFile("upload-", "-" + metadata.fileName()).toFile();
            } catch (IOException e) {
                log.error("Error creating temp file for upload", e);
                return null;
            }
        });

        upload.setUploadHandler(uploadHandler);

        VerticalLayout layout = new VerticalLayout(upload);
        layout.setPadding(false);

        upload.addFileRejectedListener(event ->
                showAndLogError("File rejected: " + event.getErrorMessage())
        );

        Button cancelButton = new Button("Cancel", _ -> dialog.close());
        dialog.getFooter().add(cancelButton);

        dialog.add(layout);
        dialog.open();
    }

    private void deleteFile(FileInfo fileInfo) {
        new DeleteConfirmDialog(
                "Are you sure you want to delete: " + fileInfo.getName() + "?",
                "Delete",
                "Cancel",
                () -> {
                    try {
                        Files.delete(fileInfo.file().toPath());
                        showSuccess("File deleted: " + fileInfo.getName());
                        loadFiles();
                    } catch (IOException ex) {
                        showAndLogError("Failed to delete file: " + fileInfo.getName());
                    }
                }
        );
    }

    public record FileInfo(File file) {
        public String getName() {
            return file.getName();
        }

        public String getSize() {
            long bytes = file.length();
            if (bytes < 1024) {
                return bytes + " B";
            }
            if (bytes < 1024 * 1024) {
                return String.format("%.1f KB", bytes / 1024.0);
            }
            if (bytes < 1024 * 1024 * 1024) {
                return String.format("%.1f MB", bytes / (1024.0 * 1024));
            }
            return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
        }

        public String getPath() {
            return file.getAbsolutePath();
        }
    }
}
