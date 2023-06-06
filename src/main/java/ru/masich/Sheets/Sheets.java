package ru.masich.Sheets;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class Sheets {
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
                new com.google.api.services.sheets.v4.Sheets.Builder(HTTP_TRANSPORT, Auth.JSON_FACTORY, Auth.getCredentials(HTTP_TRANSPORT))
                        .setApplicationName( Auth.APPLICATION_NAME)
                        .build();
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        return response.getValues();
    }
    public static void main(String... args) throws IOException, GeneralSecurityException {
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
