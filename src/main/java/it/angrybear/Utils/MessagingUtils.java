package it.angrybear.Utils;

import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Objects.MessagingChannel;
import it.angrybear.Objects.Wrappers.PlayerWrapper;
import it.fulminazzo.reflectionutils.Objects.ReflObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MessagingUtils {

    public static void sendPluginMessage(IBearPlugin<?> plugin, PlayerWrapper receiver, MessagingChannel channel,
                                         Object... data) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        for (Object o : data) {
            if (o instanceof Long) dataOutputStream.writeLong((Long) o);
            else if (o instanceof Integer) dataOutputStream.writeInt((Integer) o);
            else if (o instanceof Boolean) dataOutputStream.writeBoolean((Boolean) o);
            else if (o instanceof Byte) dataOutputStream.writeByte((Byte) o);
            else if (o instanceof Character) dataOutputStream.writeChar((Character) o);
            else if (o instanceof Double) dataOutputStream.writeDouble((Double) o);
            else if (o instanceof Float) dataOutputStream.writeFloat((Float) o);
            else if (o instanceof Short) dataOutputStream.writeShort((Short) o);
            else if (o instanceof String) dataOutputStream.writeUTF((String) o);
            else {
                byte[] serialized = SerializeUtils.serialize(o);
                if (serialized == null) serialized = new byte[0];
                dataOutputStream.write(serialized);
            }
        }
        ReflObject<?> player = new ReflObject<>(receiver.getPlayer());
        if (ServerUtils.isBukkit()) player.callMethod("sendPluginMessage", plugin, channel.toString(), outputStream.toByteArray());
        else if (ServerUtils.isVelocity()) player.callMethod("sendPluginMessage", channel.toString(), outputStream.toByteArray());
        else player.callMethod("getServer").callMethod("getInfo")
                    .callMethod("sendData", channel.toString(), outputStream.toByteArray());
        dataOutputStream.close();
        outputStream.close();
    }
}