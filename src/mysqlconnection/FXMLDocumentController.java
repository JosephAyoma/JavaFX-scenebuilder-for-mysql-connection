/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mysqlconnection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 *
 * @author Dockman
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private Label label;
    @FXML
    private Button submit;
    @FXML
    private Button bntConString;
    @FXML
    private Button bntTest;
    
    String url;
    String user, con;
    String pass;
    Statement state = null;
    Connection conect = null;
    
    DocumentBuilderFactory dbFactory;
    DocumentBuilder dBuilder;
    Document doc;

    File file = new File(".\\lib\\dbConnectionFile.xml");
    @FXML
    private PasswordField txtdbPassword;
    @FXML
    private TextField txtdbName;
    @FXML
    private TextField txtdbUserName;
    @FXML
    private TextField txtdbServer;
    @FXML
    private AnchorPane main;

    public void dbConnectionMethod() {

        readDbConnectionFile();//Calls the method which reads the file and set the values to the components

        //Using the components getter methods to get the values and assign the connection required strings
        pass = txtdbPassword.getText();
        url = "jdbc:mysql://" + txtdbServer.getText() + ":3306/" + txtdbName.getText();
        user = txtdbUserName.getText();
        pass = txtdbPassword.getText();

        try { //sets the connection to the database
            conect = DriverManager.getConnection(url, user, pass);
            state = conect.createStatement();
            label.setText("CONNECTED TO THE DATABASE");
            
        } catch (SQLException e) {
            label.setText("NOT CONNECTED TO THE DATABASE" + " " + e);
             
        }

    }

    public void readDbConnectionFile() {
        try {

            dbFactory = DocumentBuilderFactory.newInstance();
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("dbConnectionFile");
            
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;
                    //Using the setters to set the values of the components read from the connection file
                    txtdbUserName.setText(eElement.getElementsByTagName("user").item(0).getTextContent());
                    txtdbName.setText(eElement.getElementsByTagName("database").item(0).getTextContent());
                    txtdbPassword.setText(eElement.getElementsByTagName("password").item(0).getTextContent());
                    txtdbServer.setText(eElement.getElementsByTagName("server").item(0).getTextContent());
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException | DOMException e) {

        }
    }

    public void createDbConnectionXmlFile(Document doc, String user, String database, String password, String serverName) throws Exception {
        Element root = doc.createElement("dbConnectionFile");
        doc.appendChild(root);
        Element dbuser = doc.createElement("user");
        root.appendChild(dbuser);
        Text text1 = doc.createTextNode(user);
        dbuser.appendChild(text1);

        Element dbName = doc.createElement("database");
        root.appendChild(dbName);
        Text text2 = doc.createTextNode(database);
        dbName.appendChild(text2);

        Element dbPassword = doc.createElement("password");
        root.appendChild(dbPassword);
        Text text3 = doc.createTextNode(password);
        dbPassword.appendChild(text3);

        Element dbServer = doc.createElement("server");
        root.appendChild(dbServer);
        Text text4 = doc.createTextNode(serverName);
        dbServer.appendChild(text4);

        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);
        String xmlString = sw.toString();

        try (
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)))) {
            bw.write(xmlString);
            bw.flush();
        }
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        try {
            dbFactory = DocumentBuilderFactory.newInstance();
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.newDocument();
            if (file.exists()) {
                //Checks if the DB connection XML file already exists, if it does it deletes the file and allow of creating a new file
                file.delete();

                System.out.println("File deleted....");

                createDbConnectionXmlFile(doc, txtdbUserName.getText().trim(), txtdbName.getText().trim(),
                        txtdbPassword.getText().trim(), txtdbServer.getText().trim());
                label.setText("MySQL DB Connection File Created Successfully");

            } else {

                createDbConnectionXmlFile(doc, txtdbUserName.getText().trim(), txtdbName.getText().trim(), txtdbPassword.getText().trim(), txtdbServer.getText().trim());
                label.setText("MySQL DB Connection File Created Successfully");
            }

        } catch (Exception E) {

            label.setText("Error:" + " " + E);
             
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void getString(ActionEvent event) {
        readDbConnectionFile();

    }

    @FXML
    private void testConnectionAction(ActionEvent event) {

        dbConnectionMethod();
    }

}
