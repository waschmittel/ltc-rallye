package de.flubba.rallye;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.aura.Aura;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * The entry point of the Spring Boot application.
 * <p>
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@StyleSheet(Aura.STYLESHEET)
@StyleSheet("rallye-style.css")
@PWA(name = "LTC Rallye", shortName = "LTC Rallye", offlineResources = {})
@Push
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {
    public static final String TITLE_SUFFIX = " - LTC Rallye";

    static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
