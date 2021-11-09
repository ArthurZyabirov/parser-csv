import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.List;



public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        File file = writeString(json,"data.json");
        List<Employee> list2 = parseXML("data.xml");
        String json2 = listToJson(list2);
        File file2 = writeString(json, "data2.json");




    }

    private static File writeString(String json, String filename) {
        File file = new File("main" + File.separator + filename);
        try {
            file.createNewFile();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(json);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return file;

    }




    private static List<Employee> parseXML(String s) {
        List<Employee> list = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(s);
            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element element = (Element) node;
                    NamedNodeMap map = element.getAttributes();
                    for (int a = 0; a < map.getLength(); a++) {
                        int id = Integer.parseInt(element.getAttribute("id"));
                        int age = Integer.parseInt(element.getAttribute("age"));
                        Employee employee = new Employee(id, element.getAttribute("firstname"), element.getAttribute("lastname"),
                                element.getAttribute("country"), age);
                        list.add(employee);
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace(System.out);
        }
        return list;
    }


    public static <T> String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<T>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static List<Employee> parseCSV(String[] columnMapping, String filename) {
        List<Employee> list = null;
        try (CSVReader reader = new CSVReader(new FileReader(filename))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            list = csv.parse();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return list;
    }
}