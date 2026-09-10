package com.cinemesh.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    // --- Schema Beans ---
    @Bean public XsdSchema authSchema() { return new SimpleXsdSchema(new ClassPathResource("schema/auth.xsd")); }
    @Bean public XsdSchema tenantSchema() { return new SimpleXsdSchema(new ClassPathResource("schema/tenant.xsd")); }
    @Bean public XsdSchema theatreSchema() { return new SimpleXsdSchema(new ClassPathResource("schema/theatre.xsd")); }
    @Bean public XsdSchema movieSchema() { return new SimpleXsdSchema(new ClassPathResource("schema/movie.xsd")); }
    @Bean public XsdSchema showSchema() { return new SimpleXsdSchema(new ClassPathResource("schema/show.xsd")); }
    @Bean public XsdSchema seatSchema() { return new SimpleXsdSchema(new ClassPathResource("schema/seat.xsd")); }
    @Bean public XsdSchema coordinationSchema() { return new SimpleXsdSchema(new ClassPathResource("schema/coordination.xsd")); }
    @Bean public XsdSchema bookingSchema() { return new SimpleXsdSchema(new ClassPathResource("schema/booking.xsd")); }
    @Bean public XsdSchema paymentSchema() { return new SimpleXsdSchema(new ClassPathResource("schema/payment.xsd")); }
    @Bean public XsdSchema ticketSchema() { return new SimpleXsdSchema(new ClassPathResource("schema/ticket.xsd")); }
    @Bean public XsdSchema notificationSchema() { return new SimpleXsdSchema(new ClassPathResource("schema/notification.xsd")); }
    @Bean public XsdSchema analyticsSchema() { return new SimpleXsdSchema(new ClassPathResource("schema/analytics.xsd")); }

    // --- WSDL 1.1 Definitions (exposed at /ws/<beanName>.wsdl) ---
    @Bean(name = "auth")
    public DefaultWsdl11Definition authWsdlDefinition(XsdSchema authSchema) {
        DefaultWsdl11Definition wsdl = new DefaultWsdl11Definition();
        wsdl.setPortTypeName("AuthPort");
        wsdl.setLocationUri("/ws");
        wsdl.setTargetNamespace("http://cinemesh.com/ws/auth");
        wsdl.setSchema(authSchema);
        return wsdl;
    }

    @Bean(name = "tenants")
    public DefaultWsdl11Definition tenantWsdlDefinition(XsdSchema tenantSchema) {
        DefaultWsdl11Definition wsdl = new DefaultWsdl11Definition();
        wsdl.setPortTypeName("TenantPort");
        wsdl.setLocationUri("/ws");
        wsdl.setTargetNamespace("http://cinemesh.com/ws/tenant");
        wsdl.setSchema(tenantSchema);
        return wsdl;
    }

    @Bean(name = "theatres")
    public DefaultWsdl11Definition theatreWsdlDefinition(XsdSchema theatreSchema) {
        DefaultWsdl11Definition wsdl = new DefaultWsdl11Definition();
        wsdl.setPortTypeName("TheatrePort");
        wsdl.setLocationUri("/ws");
        wsdl.setTargetNamespace("http://cinemesh.com/ws/theatre");
        wsdl.setSchema(theatreSchema);
        return wsdl;
    }

    @Bean(name = "movies")
    public DefaultWsdl11Definition movieWsdlDefinition(XsdSchema movieSchema) {
        DefaultWsdl11Definition wsdl = new DefaultWsdl11Definition();
        wsdl.setPortTypeName("MoviePort");
        wsdl.setLocationUri("/ws");
        wsdl.setTargetNamespace("http://cinemesh.com/ws/movie");
        wsdl.setSchema(movieSchema);
        return wsdl;
    }

    @Bean(name = "shows")
    public DefaultWsdl11Definition showWsdlDefinition(XsdSchema showSchema) {
        DefaultWsdl11Definition wsdl = new DefaultWsdl11Definition();
        wsdl.setPortTypeName("ShowPort");
        wsdl.setLocationUri("/ws");
        wsdl.setTargetNamespace("http://cinemesh.com/ws/show");
        wsdl.setSchema(showSchema);
        return wsdl;
    }

    @Bean(name = "seats")
    public DefaultWsdl11Definition seatWsdlDefinition(XsdSchema seatSchema) {
        DefaultWsdl11Definition wsdl = new DefaultWsdl11Definition();
        wsdl.setPortTypeName("SeatPort");
        wsdl.setLocationUri("/ws");
        wsdl.setTargetNamespace("http://cinemesh.com/ws/seat");
        wsdl.setSchema(seatSchema);
        return wsdl;
    }

    @Bean(name = "coordination")
    public DefaultWsdl11Definition coordinationWsdlDefinition(XsdSchema coordinationSchema) {
        DefaultWsdl11Definition wsdl = new DefaultWsdl11Definition();
        wsdl.setPortTypeName("CoordinationPort");
        wsdl.setLocationUri("/ws");
        wsdl.setTargetNamespace("http://cinemesh.com/ws/coordination");
        wsdl.setSchema(coordinationSchema);
        return wsdl;
    }

    @Bean(name = "booking")
    public DefaultWsdl11Definition bookingWsdlDefinition(XsdSchema bookingSchema) {
        DefaultWsdl11Definition wsdl = new DefaultWsdl11Definition();
        wsdl.setPortTypeName("BookingPort");
        wsdl.setLocationUri("/ws");
        wsdl.setTargetNamespace("http://cinemesh.com/ws/booking");
        wsdl.setSchema(bookingSchema);
        return wsdl;
    }

    @Bean(name = "payment")
    public DefaultWsdl11Definition paymentWsdlDefinition(XsdSchema paymentSchema) {
        DefaultWsdl11Definition wsdl = new DefaultWsdl11Definition();
        wsdl.setPortTypeName("PaymentPort");
        wsdl.setLocationUri("/ws");
        wsdl.setTargetNamespace("http://cinemesh.com/ws/payment");
        wsdl.setSchema(paymentSchema);
        return wsdl;
    }

    @Bean(name = "ticket")
    public DefaultWsdl11Definition ticketWsdlDefinition(XsdSchema ticketSchema) {
        DefaultWsdl11Definition wsdl = new DefaultWsdl11Definition();
        wsdl.setPortTypeName("TicketPort");
        wsdl.setLocationUri("/ws");
        wsdl.setTargetNamespace("http://cinemesh.com/ws/ticket");
        wsdl.setSchema(ticketSchema);
        return wsdl;
    }

    @Bean(name = "notification")
    public DefaultWsdl11Definition notificationWsdlDefinition(XsdSchema notificationSchema) {
        DefaultWsdl11Definition wsdl = new DefaultWsdl11Definition();
        wsdl.setPortTypeName("NotificationPort");
        wsdl.setLocationUri("/ws");
        wsdl.setTargetNamespace("http://cinemesh.com/ws/notification");
        wsdl.setSchema(notificationSchema);
        return wsdl;
    }

    @Bean(name = "analytics")
    public DefaultWsdl11Definition analyticsWsdlDefinition(XsdSchema analyticsSchema) {
        DefaultWsdl11Definition wsdl = new DefaultWsdl11Definition();
        wsdl.setPortTypeName("AnalyticsPort");
        wsdl.setLocationUri("/ws");
        wsdl.setTargetNamespace("http://cinemesh.com/ws/analytics");
        wsdl.setSchema(analyticsSchema);
        return wsdl;
    }
}
