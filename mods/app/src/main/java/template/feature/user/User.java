package template.feature.user;

import dagger.Binds;
import java.util.Comparator;
import java.util.UUID;
import lombok.NonNull;
import lombok.Value;
import template.base.contract.CacheManager;
import template.base.contract.Controller;
import template.base.contract.Repository;
import template.base.stereotype.Domain;

@Value
public class User implements Domain<User> {

  @NonNull String name;
  int age;

  public static User of(final String name, final int age) {
    return Domain.validate(new User(name, age));
  }

  @Override
  public final boolean isValid() {
    return !name.isBlank() && age > 0;
  }

  @Override
  public final int compareTo(final @NonNull User user) {
    return Comparator.comparing(User::getAge)
                     .thenComparing(User::getName)
                     .compare(this, user);
  }

  @dagger.Module
  public interface Mod {

    @Binds
    CacheManager<User, UUID> cacheManager(final UserCache u);

    @Binds
    Repository.Cached<User, UUID> repository(final UserRepository u);

    @Binds
    Controller<User> controller(final UserController u);
  }
}
