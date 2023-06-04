package ru.masich.Sheets;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class Catalog {
    public static void save()
    {

    }
    public static void main(String... args) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId = "1kpmPraaxsfcHf6QYAFKxfR6tJipC5adivTqZPkymtZE";
        final String range = "Категории!A19:E";
        Sheets service =
                new Sheets.Builder(HTTP_TRANSPORT, Auth.JSON_FACTORY, Auth.getCredentials(HTTP_TRANSPORT))
                        .setApplicationName( Auth.APPLICATION_NAME)
                        .build();
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();




        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            System.out.println("Name, Major");
            for (List row : values) {
                // Print columns A and E, which correspond to indices 0 and 4.
                System.out.printf("%s\n", row.get(0));
            }
        }

        Sheets service2 =
                new Sheets.Builder(HTTP_TRANSPORT, Auth.JSON_FACTORY, Auth.getCredentials(HTTP_TRANSPORT))
                        .setApplicationName(Auth.APPLICATION_NAME)
                        .build();

        ValueRange d = new ValueRange();
        d.setValues(values);

        ValueRange valuesResponse = new ValueRange();


        for (List<Object> ds : values)
        {

            Object a = ds.get(0);
            a = (String) a + " + "+123;
            ds.remove(0);
            ds.add(0,a);

        }
        valuesResponse.setValues(values);
        valuesResponse.setMajorDimension("ROWS");


        UpdateValuesResponse response2 = service2.spreadsheets().values()

                .update(spreadsheetId,
                        range,
                        valuesResponse)
                .setValueInputOption("RAW")
                .execute();


        List<List<Object>> values2 = response2.getUpdatedData().getValues();
        if (values2 == null || values2.isEmpty()) {
            System.out.println("No data found.");
        } else {
            System.out.println("Name, Major");
            for (List row : values2) {
                // Print columns A and E, which correspond to indices 0 and 4.
                System.out.printf("%s, %s\n", row.get(0), row.get(1));
            }
        }
    }
}
