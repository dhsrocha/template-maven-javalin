package template.base.contract;

import static template.base.Exceptions.NOT_INSERTED;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.val;
import org.ehcache.Cache;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.delete.DeleteDSL;
import org.mybatis.dynamic.sql.insert.InsertDSL;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.update.UpdateModel;
import template.base.stereotype.Domain;
import template.base.stereotype.Entity;

public interface Repository<D extends Domain<D>, I> {

  I create(final @NonNull D record);

  Optional<D> getOne(final @NonNull I id);

  Set<D> getMany(final @NonNull D criteria);

  Set<D> getAll();

  boolean update(final @NonNull I id, final @NonNull D d);

  boolean delete(final @NonNull I id);

  interface Cached<D extends Domain<D>, I>
      extends Repository<D, I>,
              CacheManager.WithCache<D, I, Repository<D, I>> {
  }

  @AllArgsConstructor(access = AccessLevel.PROTECTED)
  abstract class Default<E extends Entity, T extends Domain<T>>
      implements Repository<T, UUID>,
                 Cached<T, UUID> {

    private static final String ID = "id";

    private final SqlTable table;
    private final Transformer<T, E> transformer;
    private final Mapper<E> mapper;

    @Override
    public final UUID create(final @NonNull T record) {
      val entity = transformer.toEntity(record);
      val recorded = mapper.create(InsertDSL.insert(entity).into(table));
      NOT_INSERTED.throwIf(IllegalArgumentException::new, () -> recorded != 1);
      return UUID.fromString(entity.getId());
    }

    @Override
    public final Optional<T> getOne(final @NonNull UUID id) {
      val stmt = SelectDSL.select().from(table).where(
          table.column(ID), SqlBuilder.isEqualTo(id.toString()));
      return Optional.ofNullable(mapper.getOne(transformer::fromEntity, stmt));
    }

    @Override
    public final Set<T> getMany(final @NonNull T t) {
      val stmt = criteria(SelectDSL.select().from(table), t);
      return Optional.ofNullable(mapper.getMany(transformer::fromEntity, stmt))
                     .orElseGet(Collections::emptySet);
    }

    @Override
    public final boolean update(final @NonNull UUID id, final @NonNull T t) {
      return mapper.update(criteria(UpdateDSL.update(table), t).where(
          table.column(ID), SqlBuilder.isEqualTo(id.toString()))) > 0;
    }

    @Override
    public final Set<T> getAll() {
      val stmt = SelectDSL.select().from(table);
      return Optional.ofNullable(mapper.getMany(transformer::fromEntity, stmt))
                     .orElseGet(Collections::emptySet);
    }

    @Override
    public final boolean delete(final @NonNull UUID id) {
      return mapper.delete(DeleteDSL.deleteFrom(table).where(
          table.column(ID), SqlBuilder.isEqualTo(id.toString()))) > 0;
    }

    @Override
    public final Repository<T, UUID> with(final @NonNull Cache<UUID, T> cache) {
      return new WithCache<>(cache, this);
    }

    protected abstract QueryExpressionDSL<SelectModel>
        .QueryExpressionWhereBuilder criteria(
        final @NonNull QueryExpressionDSL<SelectModel> from,
        final @NonNull T criteria);

    protected abstract UpdateDSL<UpdateModel> criteria(
        final @NonNull UpdateDSL<UpdateModel> from, final @NonNull T criteria);
  }

  @Value
  class WithCache<D extends Domain<D>, I> implements Repository<D, I> {

    Cache<I, D> cache;
    Repository<D, I> repo;

    @Override
    public Optional<D> getOne(final @NonNull I id) {
      return Optional.ofNullable(cache.get(id)).or(() -> repo.getOne(id));
    }

    @Override
    public Set<D> getMany(final @NonNull D criteria) {
      return repo.getMany(criteria);
    }

    @Override
    public Set<D> getAll() {
      return repo.getAll();
    }

    @Override
    public I create(final @NonNull D user) {
      val id = repo.create(user);
      cache.put(id, user);
      return id;
    }

    @Override
    public boolean update(final @NonNull I id, final @NonNull D user) {
      val updated = repo.update(id, user);
      if (updated) {
        cache.replace(id, user);
      }
      return updated;
    }

    @Override
    public boolean delete(final @NonNull I id) {
      val deleted = repo.delete(id);
      if (deleted) {
        cache.remove(id);
      }
      return deleted;
    }
  }
}
