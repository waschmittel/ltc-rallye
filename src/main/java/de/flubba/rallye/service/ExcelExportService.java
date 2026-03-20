package de.flubba.rallye.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.intellij.lang.annotations.Language;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelExportService {
    private final EntityManager entityManager;

    public ByteArrayInputStream exportRunnersToExcel() throws IOException {
        return exportToExcel("runners", List.of(
                "name",
                "gender",
                "number_of_laps_run",
                "bonus_points",
                "fastest",
                "average"
        ), """
                select name,
                       gender,
                       number_of_laps_run,
                       bonus_points,
                       fastest,
                       average
                from runner,
                     (select min(duration / 1000) fastest, runner_id from lap where duration > 0 group by runner_id) as fastestlap
                where fastestlap.runner_id = runner.id
                """);
    }

    public ByteArrayInputStream exportSponsorsToExcel() throws IOException {
        return exportToExcel("sponsors", List.of(
                "donor",
                "per_lap_donation",
                "one_time_donation",
                "name",
                "laps",
                "bonus_raw",
                "total",
                "total_donation",
                "room_number",
                "bonus"
        ), """
                select sponsor.name                                                                                     donor,
                       coalesce(per_lap_donation, 0)                                                                    per_lap_donation,
                       coalesce(one_time_donation, 0)                                                                   one_time_donation,
                       runner.name,
                       coalesce(runner.number_of_laps_run, 0)                                                           laps,
                       coalesce(runner.bonus_points, 0)                                                                 bonus_raw,
                       coalesce(runner.number_of_laps_run::numeric, 0) + coalesce(runner.bonus_points::numeric, 0) / 10 total,
                       total_donation,
                       runner.room_number,
                       coalesce(runner.bonus_points::numeric, 0) / 10                                                   bonus
                from sponsor,
                     runner
                where sponsor.runner_id = runner.id
                order by runner.room_number asc, runner.name asc
                """);
    }

    public ByteArrayInputStream exportToExcel(String sheetName, List<String> headers, @Language("SQL") String query) throws IOException {
        try (var workbook = new XSSFWorkbook(); var out = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet(sheetName);

            createHeaderRow(sheet, headers);

            createDataRows(query, sheet);

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private void createHeaderRow(Sheet sheet, List<String> headers) {
        var headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            headerRow.createCell(i).setCellValue(headers.get(i));
        }
    }

    @SuppressWarnings("unchecked") // queries always return List<Object[]>
    private void createDataRows(@Language("SQL") String query, Sheet sheet) {
        List<Object[]> results = entityManager.createNativeQuery(query).getResultList();
        int rowIndex = 1;
        for (Object[] row : results) {
            var dataRow = sheet.createRow(rowIndex++);
            for (int columnIndex = 0; columnIndex < row.length; columnIndex++) {
                var columnValue = row[columnIndex];
                if (columnValue != null) {
                    var cell = dataRow.createCell(columnIndex);
                    if (columnValue instanceof Number number) {
                        cell.setCellValue((number).doubleValue());
                    } else {
                        cell.setCellValue(columnValue.toString());
                    }
                }
            }
        }
    }

}
