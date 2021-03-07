module template.app {
  // System
  requires lombok;
  requires org.slf4j;
  // CDI
  requires java.compiler;
  requires javax.inject;
  requires dagger;
  // Application
  requires io.javalin;
  requires org.mapstruct;
  requires ehcache;
  requires gson;
  requires feign.core;
  requires feign.gson;
  // Persistence
  requires java.sql;
  requires org.mybatis;
  requires org.mybatis.migrations;
  requires org.mybatis.dynamic.sql;
  requires com.h2database;

  // Open for testing
  opens template;
  opens template.feature.user;
}
