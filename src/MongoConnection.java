/**
 * Created by yliang on 1/15/2016.
 */
import com.mongodb.*;

import java.time.LocalDateTime;
import java.util.Arrays;
public class MongoConnection
{
    private String SERVER;
    private String USER_NAME;
    private String PASSWORD;
    private String TARGET_DB;
    private MongoCredential credential;
    private MongoClient mongoClient;
    private DB db;
    private DBCollection coll;

    public MongoConnection(String serverAddress, String userName, String password, String targetDB)
    {
        SERVER      = serverAddress;
        USER_NAME   = userName;
        PASSWORD    = password;
        TARGET_DB   = targetDB;
    }

    public int connect()
    {
        try
        {
            System.out.println("Setting Credential...");
            credential  = MongoCredential.createCredential(USER_NAME, "admin", PASSWORD.toCharArray());
            System.out.println("Credential File Creation Success!");

            System.out.println("Setting Connection Address...");
            mongoClient = new MongoClient(new ServerAddress(SERVER), Arrays.asList(credential));
            System.out.println("Set!");

            System.out.println("Connecting...");
            this.db =  mongoClient.getDB(TARGET_DB);
            this.coll = this.db.getCollection("login_records");
            DBObject doc = new BasicDBObject("name",USER_NAME).append("server",SERVER);
            this.coll.insert(doc);
            System.out.println("Connected!");
            return 0;
        }
        catch(Exception e)
        {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            return -1;
        }
    }

    public boolean checkConnection()
    {
        try
        {
            this.insert("Connection Check", "Checked", "login_records");
            System.out.println("DB Host: " + SERVER);
            System.out.println("Database:" + TARGET_DB);
            System.out.println("Username:" + USER_NAME);
            return true;
        }
        catch(Exception e)
        {
            System.out.println(e);
            System.out.println("Database Not Connected!");
            return false;
        }

    }

    public void insert(String topic, String message, String collection)
    {
        this.coll = this.db.getCollection(collection);//just like a table
        DBObject doc = new BasicDBObject("name",topic)
                .append("message",message);
        this.coll.insert(doc);
    }
}
