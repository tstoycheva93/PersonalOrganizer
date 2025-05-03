package app.exports;

import app.utils.RecentActivities;
import app.utils.RecentActivity;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportService {
    private final String FILE_NAME = "exports.xlsx";
    private final RecentActivities recentActivities;

    public ExportService(RecentActivities recentActivities) {
        this.recentActivities = recentActivities;
    }

    public byte[] exportRecentActivitiesIntoExcel() throws IOException {
        Workbook outputWorkbook = new XSSFWorkbook();
        Sheet outputSheet = outputWorkbook.createSheet("Recent Activities");
        setHeaderNames(outputSheet, outputWorkbook);
        List<RecentActivity> activities = recentActivities.getRecentActivities();
        saveDataIntoRows(activities, outputSheet);
        for (int i = 0; i < 4; i++) {
            outputSheet.autoSizeColumn(i);
        }
        return saveIntoNewFile(outputWorkbook);
    }

    private void setHeaderNames(Sheet outputSheet, Workbook outputWorkbook) {
        CellStyle headerStyle = outputWorkbook.createCellStyle();
        Font font = outputWorkbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Row headerRow = outputSheet.createRow(0);
        headerRow.createCell(0).setCellValue("Date");
        headerRow.createCell(1).setCellValue("User");
        headerRow.createCell(2).setCellValue("Action");
        headerRow.createCell(3).setCellValue("Description");
        for (int i = 0; i < 4; i++) {
            headerRow.getCell(i).setCellStyle(headerStyle);
        }
    }

    private void saveDataIntoRows(List<RecentActivity> activities, Sheet outputSheet) {
        int rowIndex = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
        CellStyle dataStyle = outputSheet.getWorkbook().createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.LEFT);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        for (RecentActivity activity : activities) {
            Row row = outputSheet.createRow(rowIndex);
            rowIndex++;
            row.createCell(0).setCellValue(activity.getDate().format(formatter));
            row.createCell(1).setCellValue(activity.getUsername());
            row.createCell(2).setCellValue(activity.getActivity());
            row.createCell(3).setCellValue(activity.getDescription());
            for (int i = 0; i < 4; i++) {
                row.getCell(i).setCellStyle(dataStyle);
            }
        }
    }

    private byte[] saveIntoNewFile(Workbook workbook) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }

}
