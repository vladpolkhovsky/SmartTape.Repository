package by.bsu.smarttape.utils;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;

/**
 *
 * Отвечает за получение applicationContext
 *
 * */

public class AppContext {

    private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContextMVC.xml");

    /**
     * Singleton метод. Получение контекста.
     * @return ClassPathXmlApplicationContext - context
     */

    public static ClassPathXmlApplicationContext getContext() {
        return context;
    }

}
