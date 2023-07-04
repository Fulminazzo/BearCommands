package it.angrybear.Objects;

import it.angrybear.Interfaces.IBearPlugin;

public class MessagingChannel {
    private final String channel;

    public MessagingChannel(IBearPlugin<?> plugin, String channel) {
        this((plugin.getName() + ":" + channel).toLowerCase());
    }

    public MessagingChannel(String channel) {
        this.channel = channel;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MessagingChannel) return channel.equalsIgnoreCase(((MessagingChannel) o).channel);
        else if (o instanceof String) return channel.equalsIgnoreCase((String) o);
        else return super.equals(o);
    }

    @Override
    public String toString() {
        return channel;
    }
}