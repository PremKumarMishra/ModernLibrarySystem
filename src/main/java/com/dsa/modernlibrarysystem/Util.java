package com.dsa.modernlibrarysystem;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import javafx.scene.control.Alert;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class Util
{
    private static final String databaseURL = "jdbc:sqlite:App/Library.db";
    private final static String senderMail = "nbsbbsrlibrary@gmail.com";
    private final static String senderPass = "ffjo rnhh itkw bbjv"; //App Password Only

    private static String systemUser;
    private static int securityPin;

    public static boolean isStringNumeric(String s)
    {
        boolean res = true;
        for(int i=0;i<s.length();i++)
        {
            if(s.charAt(i) < 48 || s.charAt(i) > 57)
            {
                res = false;
                break;
            }
        }
        return res;
    }

    public static boolean isValidREGDNumber(String regd)
    {
        if (regd.length() != 10)
        {
            return false;
        }
        else return isStringNumeric(regd);
    }

    public static String getSha256Hash(String input)
    {
        String hexDigest = "";
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte hash[] = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for(byte b:hash)
            {
                String s = Integer.toHexString(0xff&b);
                if(s.length() == 1) hex.append('0');
                hex.append(s);
            }
            hexDigest = hex.toString();
        }
        catch (Exception e) {}
        return hexDigest;
    }

    public static List<List<String>> executeSQLQuery(String query,String[] fields)
    {
        List<List<String>> data = new ArrayList<List<String>>();
        try (Connection conn = DriverManager.getConnection(databaseURL);
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery(query))
        {
            while(rs.next())
            {
                List<String> colData = new ArrayList<String>();
                if (fields != null)
                {
                    for(String f:fields)
                    {
                        colData.add(rs.getString(f));
                    }
                }
                else
                {
                    colData.add(rs.getString(1));
                }
                data.add(colData);
            }
        }
        catch (Exception e)
        {
            AlertDialog.show("Database Error",e.getMessage(),AlertDialog.AlertType.ERROR);
        }
        return data;
    }

    public static void updateSQLQuery(String query)
    {
        try (Connection conn = DriverManager.getConnection(databaseURL);
            Statement stm = conn.createStatement();)
        {
            stm.executeUpdate(query);
        }
        catch (Exception e)
        {
            AlertDialog.show("Database Write Error",e.getMessage(),AlertDialog.AlertType.ERROR);
        }
    }

    public static boolean sendMail(String subject,String body,String receiver)
    {
        Properties prop = new Properties();
        prop.put("mail.smtp.host","smtp.gmail.com");
        prop.put("mail.smtp.port","587");
        prop.put("mail.smtp.auth","true");
        prop.put("mail.smtp.starttls.enable","true");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderMail,senderPass);
            }
        });
        try
        {
            Message message = new MimeMessage(session);
            message.setHeader("Content-Type", "text/html");
            message.setFrom(new InternetAddress(senderMail));
            message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(receiver));
            message.setSubject(subject);

            BodyPart messagePart = new MimeBodyPart();
            messagePart.setContent(body,"text/html; charset=utf-8;");

            Multipart multipart = new MimeMultipart(); //Arg For Using Both Plain Text And HTML
            multipart.addBodyPart(messagePart);

            message.setContent(multipart,"text/html; charset=utf-8;");
            Transport.send(message);
            return true;

        } catch (Exception e)
        {
            return false;
        }

    }

    public static void setSystemUser(String user)
    {
        systemUser = user;
    }

    public static String getSystemUser()
    {
        return systemUser;
    }

    public static void generateSecurityPin()
    {
        SecureRandom srandom = new SecureRandom();
        securityPin = srandom.nextInt(1000,9999);
    }

    public static int getSecurityPin()
    {
        return securityPin;
    }

    public static String getSecurityMailText(int sCode)
    {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<body style='font-family: Arial; background-color:#f4f4f4; padding:20px;'>" +
                "<div style='max-width:500px; margin:auto; background:white; padding:20px; border-radius:10px;'>" +
                "<h2 style='color:#333;'>Your Security Code</h2>" +
                "<p style='font-size:16px;'>Use the following code to proceed:</p>" +
                "<div style='font-size:24px; font-weight:bold; color:#2E86C1; margin:20px 0;'>" + sCode  + "</div>" +
                "<p style='color:#777;'>This code is valid for 1 minute.</p>" +
                "<hr>" +
                "<p style='font-size:12px; color:#aaa;'>If you didn't request this, ignore this email.</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    public static String getOverdueMailText(String title,String author,String member,String issuer,String issueDate,String dueDate)
    {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<body style='font-family: Arial; background-color:#f4f4f4; padding:20px;'>" +

                "<div style='max-width:500px; margin:auto; background:white; padding:20px; border-radius:10px;'>" +

                "<h2 style='color:#d9534f;'>Overdue Book Reminder</h2>" +

                "<p style='font-size:16px;'>Dear <b>" + member + "</b>,</p>" +

                "<p style='font-size:15px;'>The following book issued to you is now <b style='color:red;'>OVERDUE</b>. " +
                "Please return it as soon as possible.</p>" +

                "<div style='margin-top:15px;'>" +
                "<p><b>📚 Title:</b> " + title + "</p>" +
                "<p><b>✍️ Author:</b> " + author + "</p>" +
                "<p><b>👤 Issued By:</b> " + issuer + "</p>" +
                "<p><b>📅 Issue Date:</b> " + issueDate + "</p>" +
                "<p><b>⏰ Due Date:</b> <span style='color:red;'><b>" + dueDate + "</b></span></p>" +
                "</div>" +

                "<p style='margin-top:20px;'>Kindly return the book to avoid penalties or contact the library if you need assistance.</p>" +

                "<hr>" +

                "<p style='font-size:12px; color:#777;'>Library Management System, MITM</p>" +

                "</div>" +
                "</body>" +
                "</html>";
    }

    public static boolean doesBookExists(String title)
    {
        String query = "SELECT TITLE FROM BOOKS WHERE TITLE=\"%s\";".formatted(title);
        List<List<String>> result = Util.executeSQLQuery(query,null);
        System.out.println(result.isEmpty());
        return !result.isEmpty();
    }

    public static boolean isNumeric(String num)
    {
        for (int i=0;i<num.length();i++)
        {
            int c = num.charAt(i);
            if(c < 48  || c > 57)
            {
                return false;
            }
        }
        return true;
    }

}
