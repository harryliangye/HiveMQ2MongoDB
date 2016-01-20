/**
 * Created by yliang on 1/15/2016.
 *
 */
import com.mongodb.*;

import java.util.Arrays;
public class MongoConnection
{
    private DB _db;
    private DBCollection _coll;
    private String _server;
    private String _username;
    private String _password;
    private String _targetDB;
    public boolean isDBConnected;

    public MongoConnection()
    {
        isDBConnected   = false;
    }

    public int connect(String serverAddress, String userName, String password, String targetDB)
    {
        try
        {
            _server         = serverAddress;
            _username       = userName;
            _password       = password;
            _targetDB       = targetDB;

            DBObject loginInfo = new BasicDBObject("name",_username).append("server",_server);
            System.out.println("Setting Credential...");
            MongoCredential credential  = MongoCredential.createCredential(_username, "admin", _password.toCharArray());
            System.out.println("Credential File Creation Success!");

            System.out.println("Setting Connection Address...");
            MongoClient mongoClient = new MongoClient(new ServerAddress(_server), Arrays.asList(credential));
            System.out.println("Set!");

            System.out.println("Connecting...");
            _db =  mongoClient.getDB(_targetDB);
            _coll = _db.getCollection("login_records");
            _coll.insert(loginInfo);
            System.out.println("Connected!");
            isDBConnected = true;
            return 0;
        }
        catch(Exception e)
        {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            isDBConnected = false;
            return -1;
        }
    }


    public void checkConnection()
    {
        try
        {
            this.insert("Connection Check", "Checked", "login_records");
            System.out.println("DB Host: " + _server);
            System.out.println("Database:" + _targetDB);
            System.out.println("Username:" + _username);
            isDBConnected =  true;
        }
        catch(Exception e)
        {
            System.out.println("Exception:" + e);
            System.out.println("Database Not Connected!");
            isDBConnected =  false;
        }

    }

    public void insert(String topic, String message, String collection)
    {
        _coll = _db.getCollection(collection);//just like a table
        DBObject doc = new BasicDBObject("name",topic)
                .append("message",message);
        _coll.insert(doc);
    }
}
