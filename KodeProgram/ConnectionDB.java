import java.util.*;
import java.sql.*;
import java.util.logging.*;

public class ConnectionDB {
    public String query;
    public Statement stat;
    ResultSet rs;

    public ConnectionDB() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String dbur1 =

                    "jdbc:sqlserver://ASUS-VIVOBOOK:1433;DatabaseName=master;encrypt=true;trustServerCertificate=true;integratedSecurity=true";

            Connection connection = DriverManager.getConnection(dbur1);
            this.stat = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String exeQuery(int column, int variableperpage) {
        String result = "";
        int j = 0;
        try {
            rs = stat.executeQuery(this.query);
            while (rs.next() && j < variableperpage) {
                j++;
                for (int i = 1; i <= column; i++) {
                    result += rs.getString(i) + " ";
                }
                result += "\n";
            }
        } catch (SQLException e) {
        }
        return result;
    }

    public void exeInsertQuery() {
        try {
            stat.executeQuery(query);
        } catch (SQLException e) {
            // do nothing

        }
    }

    public String exeQuerywithNumber(int column, int variableperpage) {
        String result = "";
        int j = 0;
        try {
            rs = stat.executeQuery(this.query);
            while (rs.next() && j < variableperpage) {
                j++;
                result += "[" + j + "] ";
                for (int i = 1; i <= column; i++) {
                    result += rs.getString(i) + " ";
                }
                result += "\n";
            }
        } catch (SQLException e) {
        }
        return result.trim();
    }

    public void printQuery(int column, int variableperpage) {
        int j = 0;
        try {
            rs = stat.executeQuery(this.query);
            while (rs.next() && j < variableperpage) {
                j++;
                for (int i = 1; i <= column; i++) {
                    if (i == 1)
                        System.out.printf("%s ", rs.getString(i));
                    else
                        System.out.printf("%-20s", rs.getString(i));
                }
                System.out.println();
            }
        } catch (SQLException e) {
        }
    }

    public void printQuerywithNumber(int column, int variableperpage) {
        int j = 0;
        try {
            rs = stat.executeQuery(this.query);
            while (rs.next() && j < variableperpage) {
                j++;
                System.out.print(j + " ");
                for (int i = 1; i <= column; i++) {
                    System.out.printf("%-20s", rs.getString(i));
                }
                System.out.println();
            }
        } catch (SQLException e) {
        }
    }
}