/**
 * Created by yliang on 1/15/2016.
 *
 */
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
public class HiveConnection
{
    public MqttConnectOptions connOpts;
    public MemoryPersistence memoPersis;
    private String _brokerAddress;
    private String _clientId;
    private MqttClient _dataLoggerClient;
    private HiveCallback _callBack;

    public HiveConnection()
    {
        connOpts = new MqttConnectOptions();
        memoPersis = new MemoryPersistence();
        _callBack = new HiveCallback();
    }

    public boolean isConnected()
    {
        return _dataLoggerClient.isConnected();
    }

    public int connect(String hiveBroker, String clientId, boolean cleanSession)
    {
        try
        {
            _brokerAddress = hiveBroker;
            _clientId = clientId;
            connOpts.setCleanSession(cleanSession);

            _dataLoggerClient = new MqttClient("tcp://" + _brokerAddress, _clientId, memoPersis);
            System.out.println("Connecting to broker: "+ _brokerAddress);
            _dataLoggerClient.connect(connOpts);
            System.out.println("Connected !");
            System.out.println("setting call back");
            _dataLoggerClient.setCallback(_callBack);
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
            System.out.println("Disconnecting to broker: "+ _brokerAddress);
            _dataLoggerClient.disconnect();
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
            _dataLoggerClient.subscribe(topic);
            System.out.println("Subscribed: "+topic+" Successfully !");
            return 0;
        }
        catch (Exception e)
        {
            System.out.println("msg "+e.getMessage());
            System.out.println("loc "+e.getLocalizedMessage());
            System.out.println("cause "+e.getCause());
            System.out.println("excep "+e);
            e.printStackTrace();
            return -1;
        }
    }

    public int unSubscribe(String topic)
    {
        try
        {
            _dataLoggerClient.unsubscribe(topic);
            System.out.println("Unubscribed: "+topic+" Successfully !");
            return 0;
        }
        catch (MqttException e)
        {
            hiveExceptionHandling(e);
            return -1;
        }
    }
    private void hiveExceptionHandling(MqttException e)
    {
        System.out.println("reason "+e.getReasonCode());
        System.out.println("msg "+e.getMessage());
        System.out.println("loc "+e.getLocalizedMessage());
        System.out.println("cause "+e.getCause());
        System.out.println("excep "+e);
        e.printStackTrace();
    }
}
