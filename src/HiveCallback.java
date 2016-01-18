/**
 * Created by yliang on 1/15/2016.
 */

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class HiveCallback implements MqttCallback
{
    public void connectionLost(Throwable e)
    {
        System.err.println(e + "Need to reconnect?");
    }

    public void deliveryComplete(IMqttDeliveryToken token)
    {
        if(token != null)
        {
            System.err.println("delivery incomplete !");
        }
    }

    public void messageArrived(String topic, MqttMessage message)
    {
        System.out.println("topic arrived:"+topic);
        System.out.println("With message:"+message);
        FTS_Hive2Mongo_Datalogger.dbInsert(topic, message.toString());
    }
}
