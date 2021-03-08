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
  requires com.google.gson;
  // Auth
  requires java.jwt;
  requires jjwt.api;
  // Open for testing
  opens template;
  opens template.feature.user;
}
