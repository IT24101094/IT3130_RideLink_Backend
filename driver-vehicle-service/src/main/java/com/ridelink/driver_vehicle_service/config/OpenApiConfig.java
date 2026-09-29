package com.ridelink.driver_vehicle_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI driverVehicleOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("RideLink Driver & Vehicle Service API")
                        .description(
                                "REST API for managing drivers, vehicles, " +
                                "driver availability, service areas, and " +
                                "driver retrieval for the RideLink platform."
                        )
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("RideLink Development Team")));
    }
}