package com.thegraid.lobby.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Lobby.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    // jhipster-needle-application-properties-property
    // jhipster-needle-application-properties-property-getter
    // jhipster-needle-application-properties-property-class
    public Gamma gamma = new Gamma();

    public static class Gamma {

        public String gameLaunchUrl = "https://game5.thegraid.com:8445/api/launcher/launch/";
        public String launchAuthUrl = "https://game5.thegraid.com:8445/api/authenticate/";

        public Launch launch = new Launch();

        public static class Launch {

            public String propval = "this arbitrary string";
        }
    }
    /** actual values set in application[-env].yml */
    // consider alternatives: https://stackoverflow.com/questions/21271468/spring-propertysource-using-yaml
    // can this class be auto-composed? PropertySourcesPlaceholderConfigurer? YamlPropertiesFactoryBean
    //
    // @Bean
    // public static PropertySourcesPlaceholderConfigurer properties() {
    //     PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
    //     YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
    //     yaml.setResources(new ClassPathResource("default.yml")); // <-- or any Path/FileSystem/InputStream Resource
    //     propertySourcesPlaceholderConfigurer.setProperties(yaml.getObject());
    //     return propertySourcesPlaceholderConfigurer;
    // }
    // @PropertySource()
}
