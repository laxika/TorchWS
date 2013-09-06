package io.torch.torchws.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * Load and handle the configurations.
 */
public class Configuration extends CompositeConfiguration {

    public Configuration() {
        
        //Load the configuration from file
        XMLConfiguration xmlConf = null;

        try {
            xmlConf = new XMLConfiguration("config/config.xml");
        } catch (ConfigurationException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.addConfiguration(xmlConf);

        //Set the default configuration
        BaseConfiguration baseConfig = new BaseConfiguration();

        //Default configuration
        baseConfig.addProperty("listener.port", 8080);

        this.addConfiguration(baseConfig);
    }
}