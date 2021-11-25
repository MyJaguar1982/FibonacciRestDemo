package com.emc.test.config;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import org.springframework.boot.web.server.MimeMappings;
// import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;

//import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.emc.test.filter.GZipServletFilter;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@Configuration
public class WebConfigurer implements ServletContextInitializer{

    private final Logger log = LoggerFactory.getLogger(WebConfigurer.class);

    @Inject
    private Environment env;


    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        log.info("Web application configuration, using profiles: {}", Arrays.toString(env.getActiveProfiles()));
        EnumSet<DispatcherType> disps = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC);
        initGzipFilter(servletContext, disps);
        log.info("Web application fully configured");
    }

    /**
     * Set up Mime types.
     */
    // @Override
    // public void customize(ConfigurableServletWebServerFactory container) {
    //     MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
    //     // IE issue, see https://github.com/jhipster/generator-jhipster/pull/711
    //     mappings.add("html", "text/html;charset=utf-8");
    //     // CloudFoundry issue, see https://github.com/cloudfoundry/gorouter/issues/64
    //     mappings.add("json", "text/html;charset=utf-8");
    //     container.setMimeMappings(mappings);
    // }ß

    /**
     * Initializes the GZip filter.
     */
    private void initGzipFilter(ServletContext servletContext, EnumSet<DispatcherType> disps) {
        log.debug("Registering GZip Filter");
        FilterRegistration.Dynamic compressingFilter = servletContext.addFilter("gzipFilter", new GZipServletFilter());
        Map<String, String> parameters = new HashMap<>();
        compressingFilter.setInitParameters(parameters);
        compressingFilter.addMappingForUrlPatterns(disps, true, "/v1/rest/*");
        compressingFilter.setAsyncSupported(true);
    }

}
