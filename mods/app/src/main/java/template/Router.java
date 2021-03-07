package template;

import dagger.Module;
import dagger.Provides;
import io.javalin.apibuilder.ApiBuilder;
import io.javalin.apibuilder.EndpointGroup;
import lombok.NonNull;
import template.Application.Feat;
import template.base.contract.Controller;
import template.feature.user.User;

@Module
public interface Router extends EndpointGroup {

  @Provides
  static Router routes(final @NonNull Feat[] feats,
                       final @NonNull Controller<User> user) {
    return () -> {
      for (final var f : feats) {
        if (Feat.USER == f) {
          ApiBuilder.crud(user);
        }
      }
    };
  }
}
