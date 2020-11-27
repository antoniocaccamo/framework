package me.antoniocaccamo.framework;

import me.antoniocaccamo.framework.core.AppRuntime;
import me.antoniocaccamo.framework.core.AppStarter;
import me.antoniocaccamo.framework.core.WatchDir;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

/**
 * Simple main application
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    final AtomicReference<AppRuntime> applicationReference = new AtomicReference<>();
    private final File classPathDir;
    private final String appClassName;

    public Main(File classPathDir, String appClassName)  throws IOException {
        this.classPathDir = classPathDir;
        this.appClassName = appClassName;
        LOGGER.info( String.format("%s\n", toString()));
        loadAndStartApp();
        new WatchDir( classPathDir.toPath(), () -> swapApplication() );
    }



    public static void main(String[] args) throws IOException{
        if ( args.length != 2 ) {
            System.err.println( "Usage: java Main <classpath-dir> <runnable-class>" );
            System.exit( 1 );
        }
        File classPathDir = new File( args[ 0 ] );
        String appClassName = args[ 1 ];
        new Main( classPathDir, appClassName );
    }

    private void swapApplication() {
        Optional.ofNullable(applicationReference.get())
                .ifPresent(appRuntime -> {
                    try {
                        appRuntime.getLoader().close();
                    } catch (IOException e) {
                        LOGGER.severe(e.getMessage());
                    }
                });
        loadAndStartApp();
    }

    private void loadAndStartApp() {
        load().ifPresent(appRuntime -> start(appRuntime.getAppStarter()));
    }

    private void start(Class<? extends AppStarter> appStarter) {
        try {
            LOGGER.info(String.format("starting application %s", appStarter.getSimpleName()));
            new Thread(appStarter.getConstructor().newInstance()).start();
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
            throw new RuntimeException("can't start application", e);
        }
    }

    private Optional<AppRuntime> load() {
        AppRuntime appRuntime = null;
        try {
            URLClassLoader urlClassLoader = new URLClassLoader(
                    new URL[]{this.classPathDir.toURI().toURL()},
                    ClassLoader.getSystemClassLoader()
            );
            appRuntime = AppRuntime.builder()
                    .appStarter((Class<? extends AppStarter>) Class.forName(this.appClassName, true, urlClassLoader))
                    .loader(urlClassLoader)
                    .build();
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
        }
        return Optional.ofNullable(appRuntime);
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", Main.class.getSimpleName() + "[", "]")
                .add("classPathDir=" + classPathDir)
                .add("appClassName='" + appClassName + "'")
                .toString();
    }
}
