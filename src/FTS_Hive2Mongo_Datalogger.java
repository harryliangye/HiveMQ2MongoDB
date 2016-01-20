/**
 * Created by yliang on 1/15/2016.
 *
 */
import java.util.*;
public class FTS_Hive2Mongo_Datalogger
{
    private static MongoConnection mongoConnection;
    private static HiveConnection hiveConnection;
    private static String storageCollection;
    private static List <String> subsTopics = new LinkedList<>() ;
    public static boolean dispIncomingMessages = true;
    public static boolean DBConnected     = false;
    public static boolean HiveConnected   = false;
    
    public static void main( String args[] )
    {
        String userCmd;
        Scanner terminalInput   = new Scanner(System.in);
        System.out.println("FTS HiveMQ Broker --> MongoDB Data Logger v1.0");
        System.out.println("Type \"help\" for command list");
        while(true)
        {
            System.out.print("FTS Hive2Mongo_Datalogger  >");
            userCmd = terminalInput.nextLine();
            switch(userCmd)
            {
                case "v":           dispIncomingMessages = !dispIncomingMessages;
                                    System.out.println("Display incoming messages:"+ dispIncomingMessages);
                                    break;

                case "help":        helpInfo();
                                    break;

                case "showtopic":   showSubscriptions();
                                    break;

                case "sub":         subscribe();
                                    break;

                case "uns":         unSubscribe();
                                    break;

                case "condb":       startDBConnection();
                                    break;

                case "conbroker":   startBrokerConnection();
                                    break;

                case "discbroker":  disconnectBroker();
                                    break;

                case "recbroker":   reconnectBroker();
                                    break;

                case "recdb":       reconnectDB();
                                    break;

                case "coninf":      connInfo();
                                    break;

                case "exit":        System.out.println("Bye");
                                    System.exit(0);
                                    break;

                default:            System.out.println("Incorrect command, type \"help\" to view command list ");
                                    break;
            }
        }
    }
    public static void helpInfo()
    {
        System.out.print("v                     : Enable|Disable incoming message display\n\n");
        System.out.print("condb                 : Connect to database\n\n");
        System.out.print("conbroker             : Connect to broker\n\n");
        System.out.print("discbroker            : Disconnect broker\n\n");
        System.out.print("recdb                 : Reconnect to database\n\n");
        System.out.print("recbroker             : Reconnect to broker\n\n");
        System.out.print("sub                   : Subscribe a new topic\n\n");
        System.out.print("uns                   : Un-subscribe a subscribed topic\n\n");
        System.out.print("coninf                : Show connection information\n\n");
        System.out.print("showtopic             : Print currently subscribed topics\n\n");
        System.out.print("exit                  : to exit\n\n");
    }
    public static void startBrokerConnection()
    {
        String serverAdd, clientID;
        if(!DBConnected)
        {
            System.out.println("Database connection required");
            return;
        }
        Scanner terminalInput   = new Scanner(System.in);
        System.out.println("host: ");
        serverAdd = terminalInput.nextLine();
        System.out.println("clientID: ");
        clientID = terminalInput.nextLine();

        hiveConnection = new HiveConnection(serverAdd, clientID, true);//session cleaning is true by default
        hiveConnection.connect();
    }

    public static void startDBConnection()
    {
        String serverAdd, userName, password, targetDB;
        Scanner terminalInput   = new Scanner(System.in);
        System.out.println("host: ");
        serverAdd = terminalInput.nextLine();
        System.out.println("target database: ");
        targetDB = terminalInput.nextLine();
        System.out.println("target collection: ");
        storageCollection = terminalInput.nextLine();
        System.out.println("user: ");
        userName = terminalInput.nextLine();
        System.out.println("password: ");
        password = terminalInput.nextLine();

        mongoConnection = new MongoConnection(serverAdd, userName, password, targetDB);
        if(mongoConnection.connect() == 0)
        {
            DBConnected = true;
        }
    }

    public static void showSubscriptions()
    {
        System.out.println(subsTopics);
    }

    public static void subscribe()
    {
        String topic;
        if(!HiveConnected)
        {
            System.out.println("Broker not connected!");
            return;
        }
        Scanner terminalInput   = new Scanner(System.in);
        System.out.println("new topic name: ");
        topic = terminalInput.nextLine();
        if(hiveConnection.subscribe(topic) == 0)
        {
            subsTopics.add(topic);
        }
    }

    public static void unSubscribe()
    {
        String topic;
        if(!HiveConnected)
        {
            System.out.println("Broker not connected!");
            return;
        }
        Scanner terminalInput   = new Scanner(System.in);
        System.out.println("topic name: ");
        topic = terminalInput.nextLine();
        if(hiveConnection.unSubscribe(topic) == 0)
        {
            subsTopics.remove(topic);
        }
    }

    public static void disconnectBroker()
    {
        hiveConnection.disConnect();
    }

    public static void reconnectDB()
    {
        String userCmd;
        Scanner terminalInput   = new Scanner(System.in);
        if(!DBConnected)
        {
            System.out.println("Please use \"condb\"");
            return;
        }
        System.out.println("Change storage collection?");
        userCmd = terminalInput.nextLine();
        if(userCmd.equals("y") | userCmd.equals("Y"))
        {
            System.out.println("target collection: ");
            storageCollection = terminalInput.nextLine();
        }
        if(mongoConnection.connect() == 0)
        {
            DBConnected = true;
        }
    }

    public static void reconnectBroker()
    {
        String serverAdd, clientID;
        if(!DBConnected)
        {
            System.out.println("Database connection required");
            return;
        }
        Scanner terminalInput   = new Scanner(System.in);
        System.out.println("host: ");
        serverAdd = terminalInput.nextLine();
        System.out.println("clientID: ");
        clientID = terminalInput.nextLine();
        try
        {
            hiveConnection.setBroker(serverAdd);
            hiveConnection.setClientId(clientID);
            hiveConnection.connect();
        }
        catch(Exception e)
        {
            System.out.println("Msg:" + e.getMessage());
            System.out.println("Cause:" + e.getCause());
        }

    }

    public static void connInfo()
    {
        if(HiveConnected)System.out.println("Broker Connection: " + hiveConnection.isConnected());
        else
        {
            System.out.println("Broker not connected!");
        }
        if(DBConnected)
        {
            System.out.println("Current storage collection: "+storageCollection);
            DBConnected = mongoConnection.checkConnection();
        }
        else
        {
            System.out.println("Database Not Connected!");
        }
    }

    public static void dbInsert(String topic, String message)
    {
        mongoConnection.insert(topic, message, storageCollection);
    }
}