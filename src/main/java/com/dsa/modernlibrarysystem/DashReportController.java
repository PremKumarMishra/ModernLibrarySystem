package com.dsa.modernlibrarysystem;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

public class DashReportController
{
    //Statistics
    @FXML
    private Label totalBooksLabel;
    @FXML
    private Label totalMembersLabel;
    @FXML
    private Label currentIssuedLabel;
    @FXML
    private Label overdueLabel;

    //Summary
    @FXML
    private Label issueMonthLabel;
    @FXML
    private Label returnMonthLabel;
    @FXML
    private Label newMembersLabel;
    @FXML
    private Label netActivityLabel;

    @FXML
    public void initialize()
    {
        loadAnalyticsReport();
    }

    protected void loadAnalyticsReport()
    {
        String query = "SELECT " +
                "(SELECT COUNT(*) FROM BOOKS) as TOTAL_BOOKS,"+
                "(SELECT COUNT(*) FROM MEMBERS) as TOTAL_MEMBERS," +
                "(SELECT COUNT(*) FROM LOGS WHERE STATUS = 'ISSUED') as TOTAL_ISSUES," +
                "(SELECT COUNT(*) FROM LOGS WHERE STATUS = 'OVERDUE') as TOTAL_OVERDUE," +
                "(SELECT COUNT(*) FROM LOGS WHERE STATUS = 'ISSUED' AND strftime('%m',ISSUE_DATE) = strftime('%m','now','localtime') AND strftime('%Y',ISSUE_DATE) = strftime('%Y','now','localtime')) as ISSUES_THIS_MONTH,"+
                "(SELECT COUNT(*) FROM LOGS WHERE STATUS = 'RETURNED' AND strftime('%m',RETURN) = strftime('%m','now','localtime') AND strftime('%Y',RETURN) = strftime('%Y','now','localtime')) as RETURNS_THIS_MONTH," +
                "(SELECT COUNT(*) FROM MEMBERS WHERE STATUS = 'ACTIVE' AND strftime('%m',JOINED_DATE) = strftime('%m','now','localtime') AND strftime('%Y',JOINED_DATE) = strftime('%Y','now','localtime')) as NEW_MEMBERS," +
                "((SELECT COUNT(*) FROM LOGS WHERE STATUS = 'ISSUED' AND strftime('%m',ISSUE_DATE) = strftime('%m','now','localtime') AND strftime('%Y',ISSUE_DATE) = strftime('%Y','now','localtime'))"+
                " - " +
                "(SELECT COUNT(*) FROM LOGS WHERE STATUS = 'RETURNED' AND strftime('%m',RETURN) = strftime('%m','now','localtime') AND strftime('%Y',RETURN) = strftime('%Y','now','localtime'))) as NET_ACTIVITY;";
        List<List<String>> result = Util.executeSQLQuery(query,new String[]{"TOTAL_BOOKS","TOTAL_MEMBERS","TOTAL_ISSUES","TOTAL_OVERDUE","ISSUES_THIS_MONTH","RETURNS_THIS_MONTH","NEW_MEMBERS","NET_ACTIVITY"});
        List<String> values = result.get(0);

        //Statistics
        totalBooksLabel.setText("Total Books: "+values.get(0));
        totalMembersLabel.setText("Total Members: "+values.get(1));
        currentIssuedLabel.setText("Currently Issued: "+values.get(2));
        overdueLabel.setText("Overdue Books: "+values.get(3));

        //Summary
        issueMonthLabel.setText("Issues This Month: "+values.get(4));
        returnMonthLabel.setText("Returns This Month: "+values.get(5));
        newMembersLabel.setText("New Members: "+values.get(6));
        netActivityLabel.setText("Net Activity: "+Math.abs(Integer.parseInt(values.get(7))));
    }

    protected void saveReport(String filename,List<List<String>> data,String[] parameters)
    {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select Path To Save Report");
        fc.setInitialFileName(filename);
        fc.setInitialDirectory(new File(System.getProperty("user.home") + File.separator + "Documents"));
        File fin = fc.showSaveDialog(totalBooksLabel.getScene().getWindow());
        try
        {
            if(fin.getName().endsWith(".xlsx"))
            {
                ExcelParser.writeExcelFile(fin,data,parameters);
            }
            else
            {
                AlertDialog.show("Save Error","Please enter a valid file name", AlertDialog.AlertType.ERROR);
            }
        }
        catch (Exception e)
        {
            AlertDialog.show("Save Error","Failed to save report : " + e.getMessage(), AlertDialog.AlertType.ERROR);
        }
    }

    @FXML
    protected void generateOverdueReport()
    {
        String query = "SELECT " +
                "i.ID," +
                "b.TITLE AS TITLE," +
                "b.AUTHOR AS AUTHOR," +
                "m.NAME AS NAME," +
                "i.LIBRARIAN," +
                "i.ISSUE_DATE," +
                "i.DUE_DATE," +
                "i.RETURN," +
                "i.STATUS " +
                "FROM LOGS i " +
                "JOIN BOOKS b ON i.BOOK_ID = b.ID " +
                "JOIN MEMBERS m ON i.MEMBER_ID = m.ID " +
                "WHERE i.STATUS = 'OVERDUE';";
        String[] parameters = new String[]{"ID","TITLE","AUTHOR","NAME","LIBRARIAN","ISSUE_DATE","DUE_DATE","RETURN","STATUS"};
        List<List<String>> result = Util.executeSQLQuery(query,parameters);
        saveReport("OverdueBooksReport.xlsx",result,parameters);
    }

    @FXML
    protected void generateIssuedReport()
    {
        String query = "SELECT " +
                "i.ID," +
                "b.TITLE AS TITLE," +
                "b.AUTHOR AS AUTHOR," +
                "m.NAME AS NAME," +
                "i.LIBRARIAN," +
                "i.ISSUE_DATE," +
                "i.DUE_DATE," +
                "i.RETURN," +
                "i.STATUS " +
                "FROM LOGS i " +
                "JOIN BOOKS b ON i.BOOK_ID = b.ID " +
                "JOIN MEMBERS m ON i.MEMBER_ID = m.ID " +
                "WHERE i.STATUS = 'ISSUED';";
        String[] parameters = new String[]{"ID","TITLE","AUTHOR","NAME","LIBRARIAN","ISSUE_DATE","DUE_DATE","RETURN","STATUS"};
        List<List<String>> result = Util.executeSQLQuery(query,parameters);
        saveReport("IssuedBooksReport.xlsx",result,parameters);
    }

    @FXML
    protected void generateMembersReport()
    {
        String query = "SELECT * FROM MEMBERS;";
        String[] parameters = new String[]{"ID","NAME","EMAIL","REGD","JOINED_DATE","STATUS"};
        List<List<String>> result = Util.executeSQLQuery(query,parameters);
        saveReport("TotalMembersReport.xlsx",result,parameters);
    }

    @FXML
    protected void generateBooksReport()
    {
        String query = "SELECT * FROM BOOKS;";
        String[] parameters = new String[]{"ID","TITLE","AUTHOR","CATEGORY","ISBN","TOTAL","AVAILABLE","PUB_YEAR","ENTRY_DATE"};
        List<List<String>> result = Util.executeSQLQuery(query,parameters);
        saveReport("TotalBooksReport.xlsx",result,parameters);
    }

}
