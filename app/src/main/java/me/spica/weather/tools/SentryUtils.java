package me.spica.weather.tools;



import android.content.Context;

import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;
import io.sentry.event.Event;
import io.sentry.event.EventBuilder;
import io.sentry.event.interfaces.ExceptionInterface;

/**
 * author: Tommy
 * time  : 2019/5/16
 * desc  :
 */
public class SentryUtils {


    public static void init(Context context, String sentryDsn) {
        Sentry.init(sentryDsn, new AndroidSentryClientFactory(context));
    }

    //主动发送Throwable消息
    public static void sendSentryExcepiton(Throwable throwable) {
        Sentry.capture(throwable);
    }


    public static void sendMessage(String message){
        Sentry.getStoredClient().sendMessage(message);
    }

    //主动发送Event消息
    public static void sendSentryExcepiton(Event throwable) {
        Sentry.capture(throwable);
    }

    //主动发送EventBuilder消息
    public static void sendSentryExcepiton(EventBuilder throwable) {
        Sentry.capture(throwable);
    }

    public static void sendSentryExcepiton(String logger, Throwable throwable) {
        SentryUtils.sendSentryExcepiton(new EventBuilder().withMessage("try catch msg")
                .withLevel(Event.Level.WARNING).withLogger(logger).withSentryInterface(new ExceptionInterface(throwable)));
    }
}