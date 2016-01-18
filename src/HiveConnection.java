/**
 * Created by yliang on 1/15/2016.
 */
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
public class HiveConnection
{
    public MqttConnectOptions connOpts;
    public MemoryPersistence memoPersis;
    private String BROKER;
    private String CLIENT_ID;
    private MqttClient dataLoggerClient;

    public HiveConnection(String hiveBroker, String clientId, boolean cleanSession)
    {
        connOpts = new MqttConnectOptions();
        memoPersis = new MemoryPersistence();
        BROKER = hiveBroker;
        CLIENT_ID = clientId;
        connOpts.setCleanSession(cleanSession);
    }

    public boolean isConnected()
    {
        return dataLoggerClient.isConnected();
    }

    public int connect()
    {
        try
        {
            HiveCallback callBack = new HiveCallback();
            dataLoggerClient = new MqttClient("tcp://" + BROKER, CLIENT_ID, memoPersis);
            System.out.println("Connecting to broker: "+BROKER);
            dataLoggerClient.connect(connOpts);
            System.out.println("Connected !");
            System.out.println("setting call back");
            dataLoggerClient.setCallback(callBack);
            System.out.println("setting successful!");
            return 0;
        }
        catch (MqttException e)
        {
            hiveExceptionHandling(e);
            return -1;
        }
    }

    public int disConnect()
    {
        try
        {
            System.out.println("Disconnecting to broker: "+BROKER);
            dataLoggerClient.disconnect();
            System.out.println("Disconnected !");
            return 0;
        }
        catch (MqttException e)
        {
            hiveExceptionHandling(e);
            return -1;
        }
    }

    public int subscribe(String topic)
    {
        try
        {
            dataLoggerClient.subscribe(topic);
            System.out.println("Subscribed: "+topic+" Successfully !");
            return 0;
        }
        catch (MqttException e)
        {
            hiveExceptionHandling(e);
            return -1;
        }
    }

    public int unsubscribe(String topic)
    {
        try
        {
            dataLoggerClient.unsubscribe(topic);
            System.out.println("Unubscribed: "+topic+" Successfully !");
            return 0;
        }
        catch (MqttException e)
        {
            hiveExceptionHandling(e);
            return -1;
        }
    }
    public int setBroker(String tcp) throws Exception
    {
        BROKER = tcp;
        return 0;
    }

    public int setClientId(String newId) throws  Exception
    {
        CLIENT_ID = newId;
        return 0;
    }

    public void hiveExceptionHandling(MqttException e)
    {
        System.out.println("reason "+e.getReasonCode());
        System.out.println("msg "+e.getMessage());
        System.out.println("loc "+e.getLocalizedMessage());
        System.out.println("cause "+e.getCause());
        System.out.println("excep "+e);
        e.printStackTrace();
    }
}
