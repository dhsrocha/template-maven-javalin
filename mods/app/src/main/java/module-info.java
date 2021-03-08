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
  // Persistence
  requires java.sql;
  requires org.mybatis;
  requires org.mybatis.dynamic.sql;
  requires org.mybatis.migrations;
  requires com.h2database;

  // Open for testing
  opens template;
  opens template.feature.user;
}
