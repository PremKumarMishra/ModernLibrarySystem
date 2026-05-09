package com.dsa.modernlibrarysystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

import java.sql.*;
import java.security.MessageDigest;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.activation.*;
import javafx.stage.Stage;


public class MainController {
    @FXML
    private VBox bodyArea;

    @FXML
    public void initialize()
    {
        SceneManager.setRoot(bodyArea);
        SceneManager.switchView("Views/LoginView.fxml");
//        returnToLogin();
    }

    @FXML
    protected void minimizeWindow(ActionEvent e)
    {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    protected void closeWindow(ActionEvent e)
    {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void returnToLogin()
    {
//        try
//        {
//            String content = "hello world prem";
//            MessageDigest md = MessageDigest.getInstance("SHA-256");
//            byte hash[] = md.digest(content.getBytes(StandardCharsets.UTF_8));
//            System.out.println(hash.length);
//        }
//
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }

        String url = "jdbc:sqlite:Library.db";
        String sql = "CREATE TABLE IF NOT EXISTS ADMINS(" +
                      "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                      "NAME TEXT NOT NULL," +
                      "EMAIL TEXT NOT NULL," +
                      "JOINED_DATE DATE DEFAULT (date('now','localtime'))," +
                      "PASSWORD TEXT NOT NULL," +
                      "STATUS TEXT DEFAULT \"ACTIVE\");";

//        sql = "CREATE TABLE IF NOT EXISTS ADMIN_LOGS(" +
//                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
//                "NAME TEXT NOT NULL," +
//                "LOGIN DATE DEFAULT (date('now','localtime')));";

//            sql = "CREATE TABLE IF NOT EXISTS MEMBERS(" +
//                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
//                "NAME TEXT NOT NULL," +
//                "EMAIL TEXT NOT NULL," +
//                "REGD TEXT NOT NULL," +
//                "YEAR INTEGER NOT NULL," +
//                "JOINED_DATE DATE DEFAULT (date('now','localtime'))," +
//                "STATUS TEXT DEFAULT \"ACTIVE\");";

//                sql = "DROP TABLE MEMBERS;";

        sql = "CREATE TABLE IF NOT EXISTS LOGS(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "BOOK_ID INTEGER NOT NULL," +
                "MEMBER_ID INTEGER NOT NULL," +
                "LIBRARIAN TEXT NOT NULL," +
                "ISSUE_DATE DATE DEFAULT (date('now','localtime'))," +
                "DUE_DATE DATE NOT NULL," +
                "RETURN DATE NULL," +
                "STATUS TEXT DEFAULT 'ISSUED'," +
                "FOREIGN KEY (BOOK_ID) REFERENCES BOOKS,"+
                "FOREIGN KEY (MEMBER_ID) REFERENCES MEMBERS);";

//            sql = "CREATE TABLE IF NOT EXISTS BOOKS(" +
//                    "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
//                    "TITLE TEXT NOT NULL," +
//                    "AUTHOR TEXT NOT NULL," +
//                    "CATEGORY TEXT NOT NULL," +
//                    "ISBN TEXT NOT NULL," +
//                    "TOTAL INTEGER NOT NULL," +
//                    "AVAILABLE INTEGER NOT NULL," +
//                    "PUB_YEAR TEXT NOT NULL," +
//                    "ENTRY_DATE DATE DEFAULT (date('now','localtime')));";
//                sql = "DROP TABLE BOOKS;";
//                sql = "DROP TABLE LOGS;";
//        ExcelParser.readExcelFile("C:\\Users\\PC\\Downloads\\Library Book Details.xlsx");
        try
        {
            Connection conn = DriverManager.getConnection(url);
            Statement stm = conn.createStatement();
            stm.execute(sql);
            conn.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    @FXML
    protected void sendPassToMail()
    {
        final String username = "www.prem911@gmail.com";
        final String password = "voqs llnu diml jztv"; // use app password

        String to = "sauravgudu99@gmail.com";//"alishadas824@gmail.com";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );
            message.setSubject("Password Reset Code");

            // Email body
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Hello Sir\nYour security code is 5623");

            // Attachment
            MimeBodyPart attachmentPart = new MimeBodyPart();
            String filename = "C:\\Users\\PC\\Desktop\\t.png\\";
            DataSource source = new FileDataSource(filename);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName("report.png");

            // Combine parts
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);

            Transport.send(message);

            System.out.println("Email sent successfully!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


}
