package com.aman.model;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Order {
    private String ORDNAME;
    private String CURDATE;
    private String TOTQUANT;

    public static boolean createXMLFile(String fileName, String path, List<Order> orders) {
        try {
            // Create DocumentBuilderFactory and DocumentBuilder
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            // Create a new document
            Document document = documentBuilder.newDocument();

            // Create root element
            Element root = document.createElement("CartOrderFinishedGoods");
            document.appendChild(root);

            // Iterate over the list of orders
            for (Order order : orders) {
                // Create line element for each order
                Element lineElement = document.createElement("Line");
                root.appendChild(lineElement);

                // Add ordName element
                Element ordNameElement = document.createElement("ordName");
                ordNameElement.appendChild(document.createTextNode(order.getORDNAME()));
                lineElement.appendChild(ordNameElement);

                // Add curDate element
                Element curDateElement = document.createElement("curDate");
                curDateElement.appendChild(document.createTextNode(order.getCURDATE()));
                lineElement.appendChild(curDateElement);

                // Add totQuant element
                Element totQuantElement = document.createElement("totQuant");
                totQuantElement.appendChild(document.createTextNode(String.valueOf(order.getTOTQUANT())));
                lineElement.appendChild(totQuantElement);

                // Add any other elements you need here, following the same pattern
            }

            // Create the XML file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            DOMSource domSource = new DOMSource(document);

            // Specify the file name and path
            File file = new File(path + File.separator + fileName + ".xml");
            StreamResult streamResult = new StreamResult(file);

            // Transform the document to an XML file
            transformer.transform(domSource, streamResult);

            return true;

        } catch (ParserConfigurationException | javax.xml.transform.TransformerException e) {
            e.printStackTrace();
            return false;
        }
    }

}
