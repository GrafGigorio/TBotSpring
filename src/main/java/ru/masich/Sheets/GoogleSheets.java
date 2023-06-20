package ru.masich.Sheets;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class GoogleSheets {
    final static String spreadsheetId = "1U-SBrEWiB8jI4ASeiDIQMu75NTWOxadhYCKroVhCQNE";

    //1D___5bcf2EfaG_2JQAQWJgHmilv7SifNErHWrEIpkME
    public static void save(List<List<Object>> values, String range) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        //final String range = "Категории!A3:F";
        com.google.api.services.sheets.v4.Sheets service =
                new com.google.api.services.sheets.v4.Sheets.Builder(HTTP_TRANSPORT, Auth.JSON_FACTORY, Auth.getCredentials(HTTP_TRANSPORT))
                        .setApplicationName( Auth.APPLICATION_NAME)
                        .build();

        ValueRange valuesResponse = new ValueRange();
        valuesResponse.setValues(values);
        valuesResponse.setMajorDimension("ROWS");

        service.spreadsheets().values()
                .update(spreadsheetId,
                        range,
                        valuesResponse)
                .setValueInputOption("RAW")
                .execute();
    }
    public static void clear(String range) throws GeneralSecurityException, IOException {
        List<List<Object>> all = get(range);
        if(all == null)
            return;
        for (List<Object> dda : all)
        {
            for (int i = 0; dda.size() > i;++i)
            {
                dda.set(i,"");
            }
        }
        save(all,range);
    }
    public static List<List<Object>> get(String range) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        com.google.api.services.sheets.v4.Sheets service =
                new com.google.api.services.sheets.v4.Sheets.Builder(
                        HTTP_TRANSPORT, Auth.JSON_FACTORY, Auth.getCredentials(HTTP_TRANSPORT))
                        .setApplicationName( Auth.APPLICATION_NAME)
                        .build();
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        return response.getValues();
    }
    public static String createSpreadsheet(GoogleCredential credential, String title) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Sheets sheets =
                new com.google.api.services.sheets.v4.Sheets.Builder(
                        HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                        .setApplicationName( Auth.APPLICATION_NAME)
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
    public static List<String> moveFileToFolder(GoogleCredential credential, String fileId, String folderId)
            throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive drive =
                new Drive.Builder(
                        HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                        .setApplicationName( Auth.APPLICATION_NAME)
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
    private static final String ddsa = "C:\\Users\\lzlyf\\IdeaProjects\\TBotSpring\\src\\main\\resources\\adminCredintion.p12";
    public static String createFolder(GoogleCredential credential) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Drive drive =
                new Drive.Builder(
                        HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                        .setApplicationName( Auth.APPLICATION_NAME)
                        .build();
        // File's metadata.
        File fileMetadata = new File();
        fileMetadata.setName("Test");
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
    public static void main(String... args) throws IOException, GeneralSecurityException {

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(HTTP_TRANSPORT)
                .setJsonFactory(Auth.JSON_FACTORY)
                .setServiceAccountId("admin-187@grazi-154419.iam.gserviceaccount.com")
                .setServiceAccountPrivateKeyFromP12File(new FileInputStream(ddsa))
                .setServiceAccountScopes(Auth.SCOPES)
                .build();

        String folderId = createFolder(credential);
        String table = createSpreadsheet(credential,"Test");
        moveFileToFolder(credential,table,folderId);

       //createSpreadsheet("test");
//moveFileToFolder("1ZSBGSDoCmzzKzV1vRDBPSSJpdaOUhmTC1eGe9IAdGQI", "1dE3nCUwdH4fqMMcQt7fV7dzdj9it3a2Z");

//        List<List<Object>> values = new ArrayList<>();
//
//        values.add(List.of("asd","543","asd2","asd2","534","asd2567"));
//        values.add(List.of("asd","a6sd2","345","4534","11","asd2567"));
//        save(values);

//        // Build a new authorized API client service.
//        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//        final String spreadsheetId = "1U-SBrEWiB8jI4ASeiDIQMu75NTWOxadhYCKroVhCQNE";
//        final String range = "Категории!A2:B3";
//        Sheets service =
//                new Sheets.Builder(HTTP_TRANSPORT, Auth.JSON_FACTORY, Auth.getCredentials(HTTP_TRANSPORT))
//                        .setApplicationName( Auth.APPLICATION_NAME)
//                        .build();
//        ValueRange response = service.spreadsheets().values()
//                .get(spreadsheetId, range)
//                .execute();
//
//
//        List<List<Object>> values = response.getValues();
//        if (values == null || values.isEmpty()) {
//            System.out.println("No data found.");
//        } else {
//            System.out.println("Name, Major");
//            for (List<Object> row : values) {
//                // Print columns A and E, which correspond to indices 0 and 4.
//                System.out.printf("%s\n", row.get(0));
//            }
//        }
//
//        Sheets service2 =
//                new Sheets.Builder(HTTP_TRANSPORT, Auth.JSON_FACTORY, Auth.getCredentials(HTTP_TRANSPORT))
//                        .setApplicationName(Auth.APPLICATION_NAME)
//                        .build();

//        for (List<Object> ds : values)
//        {
//
//            Object a = ds.get(0);
//            a = (String) a + " + "+123;
//            ds.remove(0);
//            ds.add(0,a);
//
//        }
//
//        ValueRange valuesResponse = new ValueRange();
//        valuesResponse.setValues(values);
//        valuesResponse.setMajorDimension("ROWS");
//
//        UpdateValuesResponse response2 = service2.spreadsheets().values()
//                .update(spreadsheetId,
//                        range,
//                        valuesResponse)
//                .setValueInputOption("RAW")
//                .execute();
//
//        List<List<Object>> values2 = response2.getUpdatedData().getValues();
//        if (values2 == null || values2.isEmpty()) {
//            System.out.println("No data found.");
//        } else {
//            System.out.println("Name, Major");
//            for (List row : values2) {
//                // Print columns A and E, which correspond to indices 0 and 4.
//                System.out.printf("%s, %s\n", row.get(0), row.get(1));
//            }
//        }
    }
}
