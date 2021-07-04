import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

import static java.nio.file.Files.writeString;

public class Main {
    public static void main(String[] args) throws IOException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        File file = writeString(json);


    }

    public static File writeString(String json) {
        File file = new File("main" + File.separator + "data.json");
        try {
            file.createNewFile();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        try (FileWriter writer = new FileWriter("data.json")) {
            writer.write(json);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return file;

    }

    public static <T> String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<T>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static List<Employee> parseCSV(String[] columnMapping, String filename) throws IOException {
        try (CSVReader reader = new CSVReader(new FileReader(filename))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();
        }
    }
}