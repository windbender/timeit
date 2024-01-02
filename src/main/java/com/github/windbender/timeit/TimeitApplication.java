package com.github.windbender.timeit;

import com.github.windbender.timeit.db.EventStore;
import com.github.windbender.timeit.resources.EventResource;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterRegistration;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import java.util.EnumSet;

public class TimeitApplication extends Application<TimeitConfiguration> {

    public static void main(final String[] args) throws Exception {
        new TimeitApplication().run(args);
    }

    @Override
    public String getName() {
        return "Timeit";
    }

    private void configureCors(Environment environment) {
        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin,Authorization");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD");
        cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

    }

    @Override
    public void initialize(final Bootstrap<TimeitConfiguration> bootstrap) {
//        bootstrap.addBundle(new ConfiguredAssetsBundle("/assets/", "/"));
        bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.html"));

    }

    @Override
    public void run(final TimeitConfiguration configuration,
                    final Environment env) {
        configureCors(env);
//        env.jersey().register(new EventOutputMesssageBodyWriter());
        EventStore es = new EventStore();
        EventResource er = new EventResource(es);

        env.jersey().register(er);

    }

}
