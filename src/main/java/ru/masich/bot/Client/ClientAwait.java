package ru.masich.bot.Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.masich.bot.ProxyClient;

public class ClientAwait {
    static Logger logger = LoggerFactory.getLogger(ClientAwait.class);
    public static void execute(ProxyClient proxyClient)
    {
        logger.info("<< execute");
        System.out.println(proxyClient);
    }
}
