/**
 * Created by yliang on 1/15/2016.
 *
 */
import java.util.*;
public class FTS_Hive2Mongo_Datalogger
{
    private static MongoConnection dbConnection;
    private static HiveConnection brokerConnection;
    private static String targetCollectionName;
    private static List <String> subsTopics = new LinkedList<>() ;
    public static boolean dispIncomingMessages = true;


    public static void main( String args[] )
    {
        String userCmd;
        Scanner terminalInput   = new Scanner(System.in);
        brokerConnection = new HiveConnection();//session cleaning is true by default
        dbConnection = new MongoConnection();
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

                case "condb":       connectToDatabase();
                                    break;

                case "conbroker":   connectToBroker();
                                    break;

                case "discbroker":  brokerConnection.disConnect();
                                    break;

                case "check ":      checkConnection();
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
        System.out.print("sub                   : Subscribe a new topic\n\n");
        System.out.print("uns                   : Un-subscribe a subscribed topic\n\n");
        System.out.print("check                 : Check connection status\n\n");
        System.out.print("showtopic             : Print currently subscribed topics\n\n");
        System.out.print("exit                  : to exit\n\n");
    }
    public static void connectToBroker()
    {
        String serverAdd, clientID;
        if(!dbConnection.isDBConnected)
        {
            System.out.println("Database connection required");
            return;
        }
        Scanner terminalInput   = new Scanner(System.in);
        System.out.println("host: ");
        serverAdd = terminalInput.nextLine();
        System.out.println("clientID: ");
        clientID = terminalInput.nextLine();

        brokerConnection.connect(serverAdd, clientID, true);
    }

    public static void connectToDatabase()
    {
        String serverAdd, userName, password, targetDB;
        Scanner terminalInput   = new Scanner(System.in);
        System.out.println("host: ");
        serverAdd = terminalInput.nextLine();
        System.out.println("target database: ");
        targetDB = terminalInput.nextLine();
        System.out.println("target collection: ");
        targetCollectionName = terminalInput.nextLine();
        System.out.println("user: ");
        userName = terminalInput.nextLine();
        System.out.println("password: ");
        password = terminalInput.nextLine();

        dbConnection.connect(serverAdd, userName, password, targetDB);
    }

    public static void showSubscriptions()
    {
        System.out.println(subsTopics);
    }

    public static void subscribe()
    {
        String topic;
        if(!brokerConnection.isConnected())
        {
            System.out.println("Broker not connected!");
            return;
        }
        Scanner terminalInput   = new Scanner(System.in);
        System.out.println("new topic name: ");
        topic = terminalInput.nextLine();
        if(brokerConnection.subscribe(topic) == 0)
        {
            subsTopics.add(topic);
        }
    }

    public static void unSubscribe()
    {
        String topic;
        if(!brokerConnection.isConnected())
        {
            System.out.println("Broker not connected!");
            return;
        }
        Scanner terminalInput   = new Scanner(System.in);
        System.out.println("topic name: ");
        topic = terminalInput.nextLine();
        if(brokerConnection.unSubscribe(topic) == 0)
        {
            subsTopics.remove(topic);
        }
    }

    public static void checkConnection()
    {

        System.out.println(brokerConnection.isConnected() ? "Broker Connection: " + brokerConnection.isConnected() : "Broker is not connected!");

        if(dbConnection.isDBConnected)
        {
            System.out.println("Current storage collection: "+ targetCollectionName);
            dbConnection.checkConnection();
        }
        else
        {
            System.out.println("Database not connected!");
        }
    }

    public static void dbInsertFromCallBack(String topic, String message)
    {
        dbConnection.insert(topic, message, targetCollectionName);
    }
}