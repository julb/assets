package io.julb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class to launch the application.
 * <P>
 * @author Airbus.
 */
@SpringBootApplication
public class Application {

    /**
     * Method to launch the application.
     * @param args the arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
