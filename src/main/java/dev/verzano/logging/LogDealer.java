package dev.verzano.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

// TODO allow this class to have settings be altered dynamically
// TODO ^^^ would require keeping references to loggers and demanding that objects remove them
// TODO require initial setup of settings or pull automatically
// TODO ^^^ less magic the better maybe...
// TODO default handlers
// TODO default formatters
// TODO log to a file instead of the console
public class LogDealer {
    private static final Map<String, Integer> LOG_COUNTER_MAP = new HashMap<>();

    private LogDealer() {
    }

    public static Logger get(Class<?> clazz) {
        return Logger.getLogger(generateLoggerNameForClass(clazz));
    }

    private static synchronized String generateLoggerNameForClass(Class<?> clazz) {
        var className = clazz.getName();
        var count = LOG_COUNTER_MAP.getOrDefault(className, 0);

        LOG_COUNTER_MAP.put(className, count + 1);

        return className + "." + count;
    }
}
