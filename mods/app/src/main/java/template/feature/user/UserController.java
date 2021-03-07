package template.feature.user;

import io.javalin.http.NotFoundResponse;
import java.util.Set;
import java.util.UUID;
import javax.inject.Inject;
import lombok.NonNull;
import template.base.contract.CacheManager;
import template.base.contract.Controller;
import template.base.contract.Repository;

final class UserController extends Controller.Default<User> {

  private final @NonNull CacheManager<User, UUID> cache;
  private final @NonNull Repository.Cached<User, UUID> repo;

  @Inject
  UserController(final CacheManager<User, UUID> cache,
                 final Repository.Cached<User, UUID> repo) {
    this.cache = cache;
    this.repo = repo;
  }

  @Override
  public Class<User> domainRef() {
    return User.class;
  }

  @Override
  public User getOne(final @NonNull UUID id) {
    return repo.with(cache.from(User.class)).getOne(id)
               .orElseThrow(NotFoundResponse::new);
  }

  @Override
  public Set<User> getBy(final @NonNull User criteria) {
    return repo.getMany(criteria);
  }

  @Override
  public Set<User> getAll() {
    return repo.getAll();
  }

  @Override
  public UUID create(final @NonNull User user) {
    return repo.with(cache.from(User.class)).create(user);
  }

  @Override
  public boolean update(final @NonNull UUID id, final @NonNull User user) {
    return repo.with(cache.from(User.class)).update(id, user);
  }

  @Override
  public boolean delete(final @NonNull UUID id) {
    return repo.with(cache.from(User.class)).delete(id);
  }
}
