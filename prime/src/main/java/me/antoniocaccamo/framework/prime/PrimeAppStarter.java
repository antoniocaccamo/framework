package me.antoniocaccamo.framework.prime;


import me.antoniocaccamo.framework.core.AppStarter;

import java.util.logging.Logger;

/**
 * @author antoniocaccamo  on 27/11/2020
 */
public class PrimeAppStarter extends AppStarter {

    private static final Logger LOGGER = Logger.getLogger(PrimeAppStarter.class.getName());

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        LOGGER.info(String.format("%s started.. jea ", PrimeAppStarter.class.getSimpleName()));
    }
}
