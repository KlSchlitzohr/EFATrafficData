package de.klschlitzohr.efatrafficdata.api.logging;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.Serializable;
import java.net.URL;

/**
 * Created on 10.12.2021
 *
 * @author DerMistkaefer
 */
@Plugin(name = "Telegram", category = Node.CATEGORY, elementType = AbstractAppender.ELEMENT_TYPE, printObject = true)
public class TelegramAppender extends AbstractAppender {

    private final static String BASE_URL = "https://api.telegram.org/bot";
    private final String botToken;
    private final String chatId;

    protected TelegramAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties,
                               String botToken, String chatId) {
        super(name, filter, layout, ignoreExceptions, properties);
        this.botToken = botToken;
        this.chatId = chatId;
    }

    @PluginFactory
    public static TelegramAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Filter") Filter filter,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginAttribute("botToken") String botToken,
            @PluginAttribute("chatId") String chatId,
            Property[] properties) {
        if (botToken.trim().isEmpty()) {
            System.out.println("[log4j - TelegramAppender] No Bot Token was configured.");
            return null;
        }
        if (chatId.trim().isEmpty()) {
            System.out.println("[log4j - TelegramAppender] No Chat Id was configured.");
            return null;
        }
        return new TelegramAppender(name, filter, layout, true, properties, botToken, chatId);
    }

    @Override
    public void append(LogEvent event) {
        var message = getLayout().toSerializable(event).toString();
        sendMessage(message);
    }

    private void sendMessage(String message) {
        try {
            final String request = String.format("%s%s/sendMessage?chat_id=%s&text=%s", BASE_URL, botToken, chatId, message);
            var url = new URL(request);
            var connection = (HttpsURLConnection) url.openConnection();
            var responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                error(String.format("[log4j - TelegramAppender] - Failed to Send Telegram Message: Status Code %d - Url: %s", responseCode, request));
            }
        }
        catch (Exception ex){
            error("[log4j - TelegramAppender] - Failed to Send Telegram Message - Exception", ex);
        }
    }
}
