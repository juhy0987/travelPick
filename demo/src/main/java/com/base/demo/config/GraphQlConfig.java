package com.base.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.data.method.annotation.support.AnnotatedControllerConfigurer;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import graphql.scalars.ExtendedScalars;

@Configuration
public class GraphQlConfig {
  @Bean
  public RuntimeWiringConfigurer runtimeWiringConfigurer() {
    return wiringBuilder -> wiringBuilder
    // .scalar(ExtendedScalars.UUID)
    .scalar(ExtendedScalars.DateTime)
    .scalar(ExtendedScalars.Date);
  }

  @Bean
  public AnnotatedControllerConfigurer annotatedControllerConfigurer() {
    return new AnnotatedControllerConfigurer();
  }
}