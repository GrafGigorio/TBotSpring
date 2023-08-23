package ru.masich.Sheets;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.PermissionList;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GoogleSheets {
    //final static String spreadsheetId = "1U-SBrEWiB8jI4ASeiDIQMu75NTWOxadhYCKroVhCQNE";
    static Logger logger = LoggerFactory.getLogger(GoogleSheets.class);

    //1D___5bcf2EfaG_2JQAQWJgHmilv7SifNErHWrEIpkME
    public static void save(String tableID, List<List<Object>> values, String range) throws GeneralSecurityException, IOException {
        logger.info("(GoogleSheets.java:" + new Throwable().getStackTrace()[0].getLineNumber() +
                ")" + "<< save");
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        //final String range = "Категории!A3:F";
        com.google.api.services.sheets.v4.Sheets service =
                new com.google.api.services.sheets.v4.Sheets.Builder(HTTP_TRANSPORT, Auth.JSON_FACTORY, Auth.getCredentialsServiceResources())
                        .setApplicationName(Auth.APPLICATION_NAME)
                        .build();


        ValueRange valuesResponse = new ValueRange();
        valuesResponse.setValues(values);
        valuesResponse.setMajorDimension("ROWS");


        service.spreadsheets().values()
                .update(tableID,
                        range,
                        valuesResponse)
                .setValueInputOption("RAW")
                .execute();
    }

    public static List<Sheet> getSheetList(String tableID) throws GeneralSecurityException, IOException {
        logger.info("(GoogleSheets.java:" + new Throwable().getStackTrace()[0].getLineNumber() +
                ")" + "<< getSheetList");
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        com.google.api.services.sheets.v4.Sheets service =
                new com.google.api.services.sheets.v4.Sheets.Builder(HTTP_TRANSPORT, Auth.JSON_FACTORY, Auth.getCredentialsServiceResources())
                        .setApplicationName(Auth.APPLICATION_NAME)
                        .build();


        Spreadsheet sp = service.spreadsheets().get(tableID).execute();
        return sp.getSheets();
    }

    public static Sheet getSheet(String tableID, String name) throws GeneralSecurityException, IOException {
        logger.info("(GoogleSheets.java:" + new Throwable().getStackTrace()[0].getLineNumber() +
                ")" + "<< getSheet");

        List<Sheet> sheets = getSheetList(tableID);
        if (sheets != null && !sheets.isEmpty()) {
            for (Sheet sheet : sheets) {
                if (sheet.getProperties().getTitle().equals(name))
                    return sheet;
            }
        }
        return null;
    }

    public static Map<String, Sheet> getSheetMap(String tableID) throws GeneralSecurityException, IOException {
        logger.info("(GoogleSheets.java:" + new Throwable().getStackTrace()[0].getLineNumber() +
                ")" + "<< getSheet");

        Map<String, Sheet> reqe = new LinkedHashMap<>();

        List<Sheet> sheets = getSheetList(tableID);
        if (sheets != null && !sheets.isEmpty()) {
            for (Sheet sheet : sheets) {
                reqe.put(sheet.getProperties().getTitle(), sheet);
            }
            return reqe;
        }
        return null;
    }

    public static void createTemplate(String tableID) throws GeneralSecurityException, IOException {
        logger.info("(GoogleSheets.java:" + new Throwable().getStackTrace()[0].getLineNumber() +
                ")" + "<< createTemplate");
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        com.google.api.services.sheets.v4.Sheets service =
                new com.google.api.services.sheets.v4.Sheets.Builder(HTTP_TRANSPORT, Auth.JSON_FACTORY, Auth.getCredentialsServiceResources())
                        .setApplicationName(Auth.APPLICATION_NAME)
                        .build();

        //Формируем листы таблицы
        List<Request> createSheets = new ArrayList<>();

        SheetProperties category = new SheetProperties();
        category.setTitle("Категории");

        SheetProperties product = new SheetProperties();
        product.setTitle("Товары");

        SheetProperties size = new SheetProperties();
        size.setTitle("Размер");

        SheetProperties count = new SheetProperties();
        count.setTitle("Количество");

        AddSheetRequest addSheetCategory = new AddSheetRequest();
        addSheetCategory.setProperties(category);

        AddSheetRequest addSheetProduct = new AddSheetRequest();
        addSheetProduct.setProperties(product);

        AddSheetRequest addSheetSize = new AddSheetRequest();
        addSheetSize.setProperties(size);

        AddSheetRequest addSheetCount = new AddSheetRequest();
        addSheetCount.setProperties(count);


        createSheets.add(new Request().setAddSheet(addSheetCategory));
        createSheets.add(new Request().setAddSheet(addSheetProduct));
        createSheets.add(new Request().setAddSheet(addSheetSize));
        createSheets.add(new Request().setAddSheet(addSheetCount));


        BatchUpdateSpreadsheetRequest body
                = new BatchUpdateSpreadsheetRequest().setRequests(createSheets);

        logger.info("(GoogleSheets.java:" + new Throwable().getStackTrace()[0].getLineNumber() +
                ")" + "<< createTemplate Создание Листов ");
        service.spreadsheets()
                .batchUpdate(tableID, body).execute();
        //========================================

        //Получаем списк листов
        Map<String, Sheet> sheetsM = getSheetMap(tableID);

        GridRange gridCategory = new GridRange();
        gridCategory.setSheetId(sheetsM.get("Категории").getProperties().getSheetId());
        gridCategory.setStartRowIndex(0);
        gridCategory.setEndRowIndex(1);
        gridCategory.setStartColumnIndex(0);
        gridCategory.setEndColumnIndex(6);

        GridRange gridProduct = new GridRange();
        gridProduct.setSheetId(sheetsM.get("Товары").getProperties().getSheetId());
        gridProduct.setStartRowIndex(0);
        gridProduct.setEndRowIndex(1);
        gridProduct.setStartColumnIndex(0);
        gridProduct.setEndColumnIndex(6);

        GridRange gridSize = new GridRange();
        gridSize.setSheetId(sheetsM.get("Размер").getProperties().getSheetId());
        gridSize.setStartRowIndex(0);
        gridSize.setEndRowIndex(1);
        gridSize.setStartColumnIndex(0);
        gridSize.setEndColumnIndex(5);

        GridRange gridCount = new GridRange();
        gridCount.setSheetId(sheetsM.get("Количество").getProperties().getSheetId());
        gridCount.setStartRowIndex(0);
        gridCount.setEndRowIndex(1);
        gridCount.setStartColumnIndex(0);
        gridCount.setEndColumnIndex(5);

        DeleteSheetRequest deleteSheetRequest = new DeleteSheetRequest();
        deleteSheetRequest.setSheetId(0);

        CellData cellDataAll = new CellData();
        cellDataAll.setUserEnteredFormat(
                new CellFormat()
                        .setHorizontalAlignment("CENTER")
                        .setTextFormat(new TextFormat()
                                .setBold(true)
                                .setFontSize(12)));

        RepeatCellRequest repeatCellRequestCategory = new RepeatCellRequest();
        repeatCellRequestCategory.setRange(gridCategory);
        repeatCellRequestCategory.setCell(cellDataAll);
        repeatCellRequestCategory.setFields("*");
        RepeatCellRequest repeatCellRequestProduct = new RepeatCellRequest();
        repeatCellRequestProduct.setRange(gridProduct);
        repeatCellRequestProduct.setCell(cellDataAll);
        repeatCellRequestProduct.setFields("*");
        RepeatCellRequest repeatCellRequestSize = new RepeatCellRequest();
        repeatCellRequestSize.setRange(gridSize);
        repeatCellRequestSize.setCell(cellDataAll);
        repeatCellRequestSize.setFields("*");
        RepeatCellRequest repeatCellRequestCount = new RepeatCellRequest();
        repeatCellRequestCount.setRange(gridCount);
        repeatCellRequestCount.setCell(cellDataAll);
        repeatCellRequestCount.setFields("*");


        List<Request> merge = new ArrayList<>();
        merge.add(new Request().setMergeCells(new MergeCellsRequest().setRange(gridCategory).setMergeType("MERGE_ALL")));
        merge.add(new Request().setMergeCells(new MergeCellsRequest().setRange(gridProduct).setMergeType("MERGE_ALL")));
        merge.add(new Request().setMergeCells(new MergeCellsRequest().setRange(gridSize).setMergeType("MERGE_ALL")));
        merge.add(new Request().setMergeCells(new MergeCellsRequest().setRange(gridCount).setMergeType("MERGE_ALL")));

        merge.add(new Request().setDeleteSheet(deleteSheetRequest));

        merge.add(new Request().setRepeatCell(repeatCellRequestCategory));
        merge.add(new Request().setRepeatCell(repeatCellRequestProduct));
        merge.add(new Request().setRepeatCell(repeatCellRequestSize));
        merge.add(new Request().setRepeatCell(repeatCellRequestCount));

        BatchUpdateSpreadsheetRequest bodyMerge
                = new BatchUpdateSpreadsheetRequest().setRequests(merge);
        logger.info("(GoogleSheets.java:" + new Throwable().getStackTrace()[0].getLineNumber() +
                ")" + "<< createTemplate Обьеденение ячеек ");
        service.spreadsheets()
                .batchUpdate(tableID, bodyMerge).execute();


//        ValueRange valuesResponse = new ValueRange();
//        valuesResponse.setValues(values);
//        valuesResponse.setMajorDimension("ROWS");
//
//        Request repeatCellRequest = new Request();
//        repeatCellRequest.set
//
//
        List<List<Object>> categoryVals = new ArrayList<>();
        categoryVals.add(List.of("Категории"));
        categoryVals.add(List.of("Уникальный номер категории",
                "Уникальный номер родителя",
                "Заголовок",
                "Уникальный номер магазина",
                "Уровень вложенности",
                "Главное фото"));

        List<List<Object>> productVals = new ArrayList<>();
        productVals.add(List.of("Товары"));
        productVals.add(List.of("Уникальный номер",
                "Магазин",
                "Категория",
                "Заголовок",
                "Сылка на фото",
                "Еденица измерения"));

        List<List<Object>> sizeVals = new ArrayList<>();
        sizeVals.add(List.of("Размеры товара"));
        sizeVals.add(List.of("Уникальный номер товара",
                "Уникальный номер размера",
                "Действие",
                "Выбранно по умолчанию",
                "Заголовок"));

        List<List<Object>> countVals = new ArrayList<>();
        countVals.add(List.of("Настройки выбора количества"));
        countVals.add(List.of("Уникальный номер товара",
                "Уникальный номер изменения количества",
                "Действие",
                "Влияние на общее количество",
                "Заголовок"));

        ValueRange valueRangeCategory = new ValueRange();
        valueRangeCategory.setRange("Категории!A1:F2");
        valueRangeCategory.setValues(categoryVals);
        valueRangeCategory.setMajorDimension("ROWS");

        ValueRange valueRangeProduct = new ValueRange();
        valueRangeProduct.setRange("Товары!A1:F2");
        valueRangeProduct.setValues(productVals);
        valueRangeProduct.setMajorDimension("ROWS");

        ValueRange valueRangeSize = new ValueRange();
        valueRangeSize.setRange("Размер!A1:E2");
        valueRangeSize.setValues(sizeVals);
        valueRangeSize.setMajorDimension("ROWS");

        ValueRange valueRangeCount = new ValueRange();
        valueRangeCount.setRange("Количество!A1:E2");
        valueRangeCount.setValues(countVals);
        valueRangeCount.setMajorDimension("ROWS");


        List<ValueRange> valueRanges = new ArrayList<>();
        valueRanges.add(valueRangeCategory);
        valueRanges.add(valueRangeProduct);
        valueRanges.add(valueRangeSize);
        valueRanges.add(valueRangeCount);


        BatchUpdateValuesRequest vals = new BatchUpdateValuesRequest();
        vals.setData(valueRanges);
        vals.setValueInputOption("RAW");

        logger.info("(GoogleSheets.java:" + new Throwable().getStackTrace()[0].getLineNumber() +
                ")" + "<< createTemplate Заполение заголовков ");

        service.spreadsheets().values()
                .batchUpdate(tableID,
                        vals)
                .execute();

//        Request request2 = new Request();
//        request2.setDeleteSheet(new DeleteSheetRequest().setSheetId(0));


//        requests.add(request2);

//        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle()));


        //System.out.println(getProp(Auth.getCredentialsServiceResources(),tableID,range));


    }

    public static void clear(String tableID, String range) throws GeneralSecurityException, IOException {
        logger.info("(GoogleSheets.java:" + new Throwable().getStackTrace()[0].getLineNumber() +
                ")" + "<< clear");
        Credential credential = Auth.getCredentialsServiceResources();
        List<List<Object>> all = get(credential, tableID, range);
        if (all == null)
            return;
        for (List<Object> dda : all) {
            for (int i = 0; dda.size() > i; ++i) {
                dda.set(i, "");
            }
        }
        save(tableID, all, range);
    }

    //    public static List<List<Object>> getProp(Credential credential, String tableID, String range) throws GeneralSecurityException, IOException {
//        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//
//        com.google.api.services.sheets.v4.Sheets service =
//                new com.google.api.services.sheets.v4.Sheets.Builder(
//                        HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
//                        .setApplicationName( Auth.APPLICATION_NAME)
//                        .build();
//        ValueRange response = service.spreadsheets().sheets().copyTo()
//                .get(tableID, range)
//                .execute();
//
//        return response.getValues();
//    }
    public static List<List<Object>> get(Credential credential, String tableID, String range) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        com.google.api.services.sheets.v4.Sheets service =
                new com.google.api.services.sheets.v4.Sheets.Builder(
                        HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                        .setApplicationName(Auth.APPLICATION_NAME)
                        .build();
        ValueRange response = service.spreadsheets().values()
                .get(tableID, range)
                .execute();

        return response.getValues();
    }

    public static String createSpreadsheet(Credential credential, String title) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Sheets sheets =
                new com.google.api.services.sheets.v4.Sheets.Builder(
                        HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                        .setApplicationName(Auth.APPLICATION_NAME)
                        .build();

        //Folder
        //1uSe-2wzKS8nU9R9XWwyh0w4QdkJum43r

        // Create new spreadsheet with a title
        Spreadsheet spreadsheet = new Spreadsheet()
                .setProperties(new SpreadsheetProperties()
                        .setTitle(title));
        spreadsheet = sheets.spreadsheets().create(spreadsheet)
                .setFields("spreadsheetId")
                .execute();
        // Prints the new spreadsheet id
        System.out.println("Spreadsheet ID: " + spreadsheet.getSpreadsheetId());

        //moveFileToFolder(spreadsheet.getSpreadsheetId(), "1rWwz4M0kvXZgLhXg-MdejoQQV8XbqLYv");
        return spreadsheet.getSpreadsheetId();
    }

    public static List<String> moveFileToFolder(Credential credential, String fileId, String folderId)
            throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive drive =
                new Drive.Builder(
                        HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                        .setApplicationName(Auth.APPLICATION_NAME)
                        .build();

        // Retrieve the existing parents to remove
        File file = drive.files().get(fileId)
                .setFields("parents")
                .execute();
        StringBuilder previousParents = new StringBuilder();
        for (String parent : file.getParents()) {
            previousParents.append(parent);
            previousParents.append(',');
        }
        try {
            // Move the file to the new folder
            file = drive.files().update(fileId, null)
                    .setAddParents(folderId)
                    .setRemoveParents(previousParents.toString())
                    .setFields("id, parents")
                    .execute();

            return file.getParents();
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            System.err.println("Unable to move file: " + e.getDetails());
            throw e;
        }
    }

    public static String createFolder(Credential credential, String folderTitle) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Drive drive =
                new Drive.Builder(
                        HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                        .setApplicationName(Auth.APPLICATION_NAME)
                        .build();
        // File's metadata.
        File fileMetadata = new File();
        fileMetadata.setName(folderTitle);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        try {
            File file = drive.files().create(fileMetadata)
                    .setFields("id")
                    .execute();
            System.out.println("Folder ID: " + file.getId());
            return file.getId();
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            System.err.println("Unable to create folder: " + e.getDetails());
            throw e;
        }
    }

    public static void delete(Credential credential, String folderId) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Drive drive =
                new Drive.Builder(
                        HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                        .setApplicationName(Auth.APPLICATION_NAME)
                        .build();
        try {
            drive.files().delete(folderId).execute();
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            System.err.println("Unable to delete folder: " + e.getDetails());
            throw e;
        }
    }

    public static Map<String, String> getFolder(Credential credential, String folderID) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Drive drive =
                new Drive.Builder(
                        HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                        .setApplicationName(Auth.APPLICATION_NAME)
                        .build();
        Map<String, String> folders = new LinkedHashMap<>();
        String pageToken = null;
        if (folderID.equals("")) {
            folderID = "root";
        }
        do {
            FileList result = drive.files().list()
                    //.setQ("'"+folderID+"' in parents and mimeType = 'application/vnd.google-apps.folder' and trashed = false")
                    .setSpaces("drive")
                    .setFields("nextPageToken, files(id, name, parents, mimeType)")
                    .setPageToken(pageToken)
                    .execute();
            for (File file : result.getFiles()) {
                System.out.printf("Found %s: %s (%s)\n",
                        file.getMimeType(), file.getName(), file.getId());
                folders.put(file.getId(), file.getName());
            }
            pageToken = result.getNextPageToken();
        } while (pageToken != null);
        return folders;
    }

    public static Map<String, String> getFiles(Credential credential, String folderID) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Drive drive =
                new Drive.Builder(
                        HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                        .setApplicationName(Auth.APPLICATION_NAME)
                        .build();
        Map<String, String> files = new LinkedHashMap<>();
        String pageToken = null;
        if (folderID.equals("")) {
            folderID = "root";
        }
        do {
            FileList result = drive.files().list()
                    .setQ("'" + folderID + "' in parents and mimeType != 'application/vnd.google-apps.folder' and trashed = false")
                    .setSpaces("drive")
                    .setFields("nextPageToken, files(id, name, parents)")
                    .setPageToken(pageToken)
                    .execute();
            for (File file : result.getFiles()) {
                System.out.printf("Found file: %s (%s)\n",
                        file.getName(), file.getId());
                files.put(file.getId(), file.getName());
            }
            pageToken = result.getNextPageToken();
        } while (pageToken != null);
        return files;
    }

    /**
     * Возможные роли
     * organizer
     * fileOrganizer
     * writer
     * commenter
     * reader
     */
    public static void setPermissionSpreadsheet(Credential credential, String role, String fileId, String email) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Drive drive =
                new Drive.Builder(
                        HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                        .setApplicationName(Auth.APPLICATION_NAME)
                        .build();

        drive.permissions()
                .create(fileId, new Permission()
                        .setEmailAddress(email.toLowerCase())
                        .setType("user").setRole(role))
                .execute();
    }

    public static void deletePermissionSpreadsheet(Credential credential, String permissionId, String fileId) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Drive drive =
                new Drive.Builder(
                        HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                        .setApplicationName(Auth.APPLICATION_NAME)
                        .build();

        drive.permissions()
                .delete(fileId, permissionId)
                .execute();
    }

    public static List<Map<String, String>> getPermissionSpreadsheet(Credential credential, String fileId) throws IOException, GeneralSecurityException {
        logger.info("(GoogleSheets.java:" + new Throwable().getStackTrace()[0].getLineNumber() + ")" + "<< getPermissionSpreadsheet");
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Drive drive =
                new Drive.Builder(
                        HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                        .setApplicationName(Auth.APPLICATION_NAME)
                        .build();

        List<Map<String, String>> users = new ArrayList<>();

        String pageToken = null;
        do {
            PermissionList permissionList = drive.permissions().list(fileId).setFields("nextPageToken, permissions(emailAddress, role, id)").execute();
            for (Permission permission : permissionList.getPermissions()) {
                Map<String, String> perms = new LinkedHashMap<>();

                perms.put("id", permission.getId());
                perms.put("role", permission.getRole());
                perms.put("emailAddress", permission.getEmailAddress());

                users.add(perms);
                GoogleSheets.logger.debug("Permisson id: " +
                        permission.getId() + ", role: " + permission.getRole() + " email:  " + permission.getEmailAddress());
            }
            pageToken = permissionList.getNextPageToken();
        } while (pageToken != null);
        return users;
    }

    public static void main(String... args) throws IOException, GeneralSecurityException {
        Credential credential = Auth.getCredentialsServiceResources();
//1U-SBrEWiB8jI4ASeiDIQMu75NTWOxadhYCKroVhCQNE
        System.out.println();
        System.out.println(get(credential, "1U-SBrEWiB8jI4ASeiDIQMu75NTWOxadhYCKroVhCQNE", "Категории!A1:F2"));
        // System.out.println(getPermissionSpreadsheet(credential,"18aRQYXRsUDD1Jn0Djm75h3tjRoggucobDno8KiTHp60"));

        // delete(credential, "1sAMQmOE56Wu3F9Mm5_sr9-URZIXyU4EZ3aw-9nB1IaE");
        // getFolder(credential,"");
        // setPermissionSpreadsheet(credential,"");
        // getPermissionSpreadsheet(credential,"154-s8ADIAM_qAcglNBEwtMt0POqoILac0Z3qQjyO-ns");
        // getFiles(credential, "");

//        String folderId = createFolder(credential, "Test");
//        String table = createSpreadsheet(credential,"Test");
//        moveFileToFolder(credential,table,folderId);

    }
}
