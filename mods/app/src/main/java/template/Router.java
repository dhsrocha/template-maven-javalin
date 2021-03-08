package template;

import dagger.Component;
import io.javalin.apibuilder.ApiBuilder;
import io.javalin.apibuilder.EndpointGroup;
import javax.inject.Inject;
import lombok.val;
import template.Application.Feat;
import template.base.contract.Filter;
import template.feature.auth.Auth;

@Component(modules = Auth.Module.class)
abstract class Router implements EndpointGroup {

  private final Feat[] feats;
  private final Filter.WithEndpoint<Auth> auth;

  @Inject
  Router(final Feat[] feats, final Filter.WithEndpoint<Auth> auth) {
    this.feats = feats;
    this.auth = auth;
  }

  @Override
  public final void addEndpoints() {
    for (val f : feats) {
      if (Feat.AUTH == f) {
        ApiBuilder.before(auth.filter());
        ApiBuilder.post(auth.pathTo(), auth.endpoint());
      }
    }
  }
}
