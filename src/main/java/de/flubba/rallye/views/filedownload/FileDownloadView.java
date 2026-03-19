package de.flubba.rallye.views.filedownload;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.FileDownloadHandler;
import de.flubba.generated.i18n.I18n;
import de.flubba.rallye.configuration.RallyeProperties;
import de.flubba.rallye.views.MainLayout;
import de.flubba.rallye.views.ViewToolbar;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.vaadin.flow.component.notification.Notification.Position.TOP_CENTER;
import static com.vaadin.flow.component.notification.NotificationVariant.LUMO_ERROR;
import static de.flubba.rallye.Application.TITLE_SUFFIX;

@PageTitle("Downloads" + TITLE_SUFFIX)
@Route(value = "downloads", layout = MainLayout.class)
@Slf4j
public class FileDownloadView extends VerticalLayout {

    private final RallyeProperties rallyeProperties;
    private final Grid<FileInfo> fileGrid;

    public FileDownloadView(RallyeProperties rallyeProperties) {
        this.rallyeProperties = rallyeProperties;
        this.fileGrid = new Grid<>(FileInfo.class, false);

        setSizeFull();
        setPadding(true);

        ViewToolbar toolbar = new ViewToolbar("Tag Manager Downloads");
        add(toolbar);

        setupGrid();
        loadFiles();
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
                log.error("Download folder does not exist: {}", folderPath);
                showError(I18n.DOWNLOAD_FOLDER_NOT_FOUND.get(folderPath));
                fileGrid.setItems(files);
                return;
            }

            if (!Files.isDirectory(path)) {
                log.error("Download path is not a directory: {}", folderPath);
                showError(I18n.DOWNLOAD_NOT_DIRECTORY.get(folderPath));
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
            log.error("Error reading download folder: {}", folderPath, e);
            showError(I18n.DOWNLOAD_READ_ERROR.get(folderPath));
        }

        fileGrid.setItems(files);
    }

    private void showError(String message) {
        Notification notification = new Notification(message);
        notification.setPosition(TOP_CENTER);
        notification.setDuration(5000);
        notification.addThemeVariants(LUMO_ERROR);
        notification.open();
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
