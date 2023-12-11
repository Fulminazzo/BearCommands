package it.angrybear.interfaces.configurations;

import it.angrybear.enums.BearLoggingMessage;
import it.angrybear.enums.Language;
import it.angrybear.interfaces.IBearConfigPlugin;
import it.angrybear.interfaces.functions.BiFunctionException;
import it.angrybear.interfaces.functions.FunctionException;
import it.angrybear.utils.StringUtils;
import it.fulminazzo.reflectionutils.objects.ReflObject;
import it.fulminazzo.yamlparser.interfaces.IConfiguration;
import it.fulminazzo.yamlparser.objects.configurations.FileConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * An interface designed for enums containing options
 * from the main lang.yml file (whether it is single or multiple).
 */
public interface IBearMessage extends IBearConfig {

    /**
     * Gets message.
     *
     * @return the message
     */
    default String getMessage() {
        return getMessage(null);
    }

    /**
     * Gets message.
     *
     * @param lang the language of the config
     * @return the message
     */
    default String getMessage(Language lang) {
        return getMessage(false, lang);
    }

    /**
     * Gets message.
     *
     * @param prefix if true, prepend prefix to the message
     * @return the message
     */
    default String getMessage(boolean prefix) {
        return getMessage(prefix, null);
    }

    /**
     * Gets message.
     *
     * @param prefix if true, prepend prefix to the message
     * @param lang   the language of the config
     * @return the message
     */
    default String getMessage(boolean prefix, Language lang) {
        return get(prefix, lang, IConfiguration::getString, s -> s, (s, p) -> p + s, StringUtils::parseMessage);
    }

    /**
     * Gets messages.
     *
     * @return the message
     */
    default List<String> getMessages() {
        return getMessages(null);
    }

    /**
     * Gets messages.
     *
     * @param lang the language of the config
     * @return the message
     */
    default List<String> getMessages(Language lang) {
        return getMessages(false, lang);
    }

    /**
     * Gets messages.
     *
     * @param prefix if true, prepend prefix to the message
     * @return the message
     */
    default List<String> getMessages(boolean prefix) {
        return getMessages(prefix, null);
    }

    /**
     * Gets messages.
     *
     * @param prefix if true, prepend prefix to the message
     * @param lang   the language of the config
     * @return the message
     */
    default List<String> getMessages(boolean prefix, Language lang) {
        return get(prefix, lang, IConfiguration::getStringList, Arrays::asList,
                (l, p) -> l.stream().map(s -> p + s).collect(Collectors.toList()),
                s -> s.stream().map(StringUtils::parseMessage).collect(Collectors.toList()));
    }

    /**
     * A general method to get an object as a formatted message.
     *
     * @param <O>                the type parameter
     * @param prefix             if true, prepend prefix to the message
     * @param lang               the language of the config
     * @param getFunction        the function to get the object from the language file
     * @param stringToOConverter the function to convert a string into an object O
     * @param prefixApplier      the function to apply the prefix to O
     * @param messageParser      the function to format O
     * @return the o
     */
    default <O> O get(boolean prefix, Language lang,
                      BiFunctionException<FileConfiguration, String, O> getFunction,
                      Function<String, O> stringToOConverter, 
                      BiFunction<O, String, O> prefixApplier,
                      Function<O, O> messageParser
    ) {
        O message;
        IBearConfigPlugin plugin = getPlugin();
        if (plugin == null) message = stringToOConverter.apply(BearLoggingMessage.MESSAGE_PLUGIN_ERROR.getMessage());
        else {
            String path = getPath();
            if (path == null) message = stringToOConverter.apply(BearLoggingMessage.MESSAGE_PATH_ERROR.getMessage());
            else {
                FileConfiguration langConfig = plugin.getLang(lang);
                if (langConfig == null) message = stringToOConverter.apply(BearLoggingMessage.MESSAGE_LANG_ERROR.getMessage());
                else {
                    try {
                        message = getFunction.apply(langConfig, path);
                    } catch (Exception e) {
                        message = stringToOConverter.apply(BearLoggingMessage.MESSAGE_INTERNAL_ERROR.getMessage(
                                "%path%", path, "%error%", e.getMessage()));
                    }
                    if (message == null) message = stringToOConverter.apply(BearLoggingMessage.MESSAGE_PATH_NOT_FOUND.getMessage("%path%", path));
                    else if (prefix) {
                        ReflObject<IBearMessage> messageReflObject = new ReflObject<>(this);
                        IBearMessage prefixObject = messageReflObject.getFieldObject("PREFIX");
                        if (prefixObject != null) message = prefixApplier.apply(message, prefixObject.getMessage(lang));
                    }
                }
            }
        }
        return messageParser.apply(message);
    }
}