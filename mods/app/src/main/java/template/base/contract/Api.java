package template.base.contract;

import java.util.Set;
import lombok.NonNull;

public interface Api<T, I> {

  T getOne(final @NonNull I id);

  Set<T> getBy(final @NonNull T criteria);

  Set<T> getAll();

  I create(final @NonNull T t);

  boolean update(final @NonNull I id, final @NonNull T t);

  boolean delete(final @NonNull I id);

  int count();
}
