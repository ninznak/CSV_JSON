package ru.alex;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        String fileName = "data.csv";
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        List<Employee> employees = parseCSV(columnMapping, fileName);
        String json = listToJson(employees);
        writeString(json);
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        ColumnPositionMappingStrategy<Employee> parseStrategy = new ColumnPositionMappingStrategy<>();
        parseStrategy.setType(Employee.class);
        parseStrategy.setColumnMapping(columnMapping);

        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(reader).withMappingStrategy(parseStrategy).build();
            return csvToBean.parse();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String listToJson(List<Employee> employees){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>(){}.getType();
        return gson.toJson(employees, listType);
    }

    public static void writeString(String str){
        Path path = Paths.get("data.json");
        try (FileWriter writer = new FileWriter(path.toFile())){
            writer.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}