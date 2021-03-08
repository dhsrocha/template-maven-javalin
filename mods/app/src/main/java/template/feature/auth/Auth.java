package template.feature.auth;

import dagger.Binds;
import lombok.NonNull;
import lombok.Value;
import template.base.contract.Filter;

@Value
public class Auth {

  Token token;
  // roles/claims/entitlements

  @dagger.Module
  public interface Module {

    @Binds
    Filter.WithEndpoint<Auth> controller(final @NonNull AuthController impl);
  }
}
