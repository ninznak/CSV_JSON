package ru.alex;

import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.FileReader;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        String fileName = "data.csv";

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        List<Employee> employees = parseCSV(columnMapping, fileName);
        System.out.println(employees);
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        ColumnPositionMappingStrategy<Employee> parseStrategy = new ColumnPositionMappingStrategy<>();
        parseStrategy.setType(Employee.class);
        parseStrategy.setColumnMapping(columnMapping);

        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(reader).withMappingStrategy(parseStrategy).build();
            csvToBean.setMappingStrategy(parseStrategy);
            return csvToBean.parse();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}