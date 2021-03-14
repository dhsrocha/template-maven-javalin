package template.infra;

import dagger.Module;
import io.javalin.Javalin;
import java.util.function.Supplier;
import javax.inject.Inject;
import javax.inject.Scope;
import lombok.NonNull;
import template.Application.Mode;


@Module(includes = Web.Mod.class)
public class Infra implements Supplier<Javalin> {

  @Scope
  @interface InfraScope {
  }

  private final @NonNull Mode mode;
  private final @NonNull Javalin server;

  @Inject
  Infra(final Mode mode, final Javalin server) {
    this.mode = mode;
    this.server = server;
  }

  @Override
  @InfraScope
  public Javalin get() {
    return server;
  }
}
