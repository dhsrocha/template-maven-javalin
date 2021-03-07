package template.feature.user;

import java.util.UUID;
import javax.inject.Inject;
import template.base.contract.CacheManager;

final class UserCache extends CacheManager.Default<User, UUID> {

  @Inject
  UserCache() {
  }

  @Override
  protected Class<UUID> idRef() {
    return UUID.class;
  }

  @Override
  public Class<User> domainRef() {
    return User.class;
  }
}
