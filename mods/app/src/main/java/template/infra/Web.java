package template.infra;

import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;
import io.javalin.Javalin;
import io.javalin.plugin.openapi.annotations.ContentType;
import lombok.NonNull;
import template.Application.Mode;
import template.Router;
import template.base.contract.Builder;
import template.infra.Infra.InfraScope;
import template.infra.Web.Mod;

@InfraScope
@Subcomponent(modules = Mod.class)
interface Web {

  @Module
  interface Mod {
    @Provides
    static Javalin server(final @NonNull Mode mode,
                          final @NonNull Router routes) {
      return Javalin.create(cfg -> {
        cfg.showJavalinBanner = mode != Mode.DEV;
        cfg.defaultContentType = ContentType.JSON;
        cfg.autogenerateEtags = Boolean.TRUE;
      }).routes(routes);
    }
  }

  @Subcomponent.Builder
  interface Build extends Builder<Web> {
  }
}
