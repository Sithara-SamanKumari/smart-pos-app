package lk.ijse.dep11.pos.db;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class SingleDatabaseConnection {
    private  static SingleDatabaseConnection instance;
    private Connection connection;

    private SingleDatabaseConnection (){
        try {
            Properties properties = new Properties();
            properties.load(getClass().getResourceAsStream("/application.properties"));

            String url = properties.getProperty("app.datasource.url");
            String userName = properties.getProperty("app.datasource.username");
            String password = properties.getProperty("app.datasource.password");
            connection = DriverManager.getConnection(url,userName,password);
            generateSchema();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static SingleDatabaseConnection getInstance(){
        return (instance == null)?(instance = new SingleDatabaseConnection()):instance;
    }
    public Connection getConnection(){
        return connection;
    }
    private void generateSchema() throws Exception {
        URL resourceUrl = SingleDatabaseConnection.class.getResource("/schema.sql");
        Path path = Paths.get(resourceUrl.toURI());
        List<String> readLineList = Files.readAllLines(path);
        String dbScript= readLineList.stream().reduce((previous, current) -> previous.concat(current)).get();
        connection.createStatement().execute(dbScript);
    }
}

