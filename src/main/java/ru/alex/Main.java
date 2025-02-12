package ru.alex;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args){

        String fileName = "data.csv";

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};         // парсинг CSV
        List<Employee> employees = parseCSV(columnMapping, fileName);
        String json = listToJson(employees);
        writeString(json, "data.json");

        List<Employee> employees2 = parseXML("data.xml");                           // парсинг XML
        String json2 = listToJson(employees2);
        writeString(json2, "data2.json");

        String json3 = readString("new_data.json");                                 // парсинг JSON
        jsonToList(json3);
        List<Employee> employees3 = jsonToList(json3);
        employees3.forEach(System.out::println);

    }

    public static List<Employee> parseXML(String fileName) {
        List<Employee> employees = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));
            Node root = doc.getDocumentElement();
            employees = read(root);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
        return employees;
    }

    private static List<Employee> read(Node root) {
        List<Employee> employees = new ArrayList<>();
        NodeList nodeList = root.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType() && ("employee".equals(node_.getNodeName()))) {
                Element element = (Element) node_;
                long id = Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent());
                String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                String country = element.getElementsByTagName("country").item(0).getTextContent();
                int age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());
                employees.add(new Employee(id, firstName, lastName, country, age));
            }
            read(node_);
        }
        return employees;
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

    public static String readString(String fileName) {
        Path path = Paths.get(fileName);
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
            while (br.ready()) {
                sb.append(br.readLine()).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static List<Employee> jsonToList(String inputString) {

        List<Employee> employees = new ArrayList<>();
        JsonParser parser = new JsonParser();
        JsonArray jsonArray= parser.parse(inputString).getAsJsonArray();
        Gson gson = new GsonBuilder().create();

        for (int i = 0; i < jsonArray.size(); i++) {
            Employee employee = gson.fromJson(jsonArray.get(i), Employee.class);
            employees.add(employee);
        }
        return employees;
    }

    public static String listToJson(List<Employee> employees) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(employees, listType);
    }

    public static void writeString(String str, String fileName) {
        Path path = Paths.get(fileName);
        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}