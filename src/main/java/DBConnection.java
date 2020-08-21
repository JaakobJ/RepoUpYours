import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

    // THIS IS ONLY FOR TESTING, CAN BE DELETED LATER


    public static void main(String[] args)
    {
        Connection connection = null;
        try
        {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\Kasutaja\\Downloads\\SQLite\\upyours.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            ResultSet rs = statement.executeQuery("select * from shows where showname = "
                    + "'namename" + "." + "mp4'");
            while(rs.next())
            {
                // read the result set
                System.out.println("showname = " + rs.getString("showname"));
                //System.out.println("name = " + rs.getString("name"));
                //System.out.println("tmdb = " + rs.getInt("tmdb"));
            }
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
        finally
        {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }
}
