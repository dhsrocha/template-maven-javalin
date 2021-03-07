package template.base.contract;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.mybatis.dynamic.sql.delete.DeleteDSL;
import org.mybatis.dynamic.sql.delete.DeleteModel;
import org.mybatis.dynamic.sql.delete.render.DeleteStatementProvider;
import org.mybatis.dynamic.sql.insert.InsertDSL;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.update.UpdateModel;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import template.base.stereotype.Entity;

@org.apache.ibatis.annotations.Mapper
public interface Mapper<E extends Entity> {

  String SELECT = "select";

  @SelectProvider(type = SqlProviderAdapter.class, method = SELECT)
  E getOne(final @NonNull SelectStatementProvider statement);

  default <D> D getOne(final @NonNull Function<E, D> mapper,
                       final @NonNull QueryExpressionDSL<SelectModel>
                           .QueryExpressionWhereBuilder statement) {
    return mapper
        .apply(getOne(statement.build().render(RenderingStrategies.MYBATIS3)));
  }

  @SelectProvider(type = SqlProviderAdapter.class, method = SELECT)
  Set<E> getMany(final @NonNull SelectStatementProvider statement);

  default <D> Set<D> getMany(final @NonNull Function<E, D> mapper,
                             final @NonNull QueryExpressionDSL<SelectModel>
                                 .QueryExpressionWhereBuilder statement) {
    return getMany(statement.build().render(RenderingStrategies.MYBATIS3))
        .stream().map(mapper).collect(Collectors.toSet());
  }

  default <D> Set<D> getMany(final @NonNull Function<E, D> mapper,
                             final @NonNull QueryExpressionDSL<SelectModel> statement) {
    return getMany(statement.build().render(RenderingStrategies.MYBATIS3))
        .stream().map(mapper).collect(Collectors.toSet());
  }

  @UpdateProvider(type = SqlProviderAdapter.class, method = "update")
  int update(final @NonNull UpdateStatementProvider statement);

  default int update(
      final @NonNull UpdateDSL<UpdateModel>.UpdateWhereBuilder statement) {
    return update(statement.build().render(RenderingStrategies.MYBATIS3));
  }

  @InsertProvider(type = SqlProviderAdapter.class, method = "insert")
  int create(final @NonNull InsertStatementProvider<E> statement);

  default int create(final @NonNull InsertDSL<E> statement) {
    return create(statement.build().render(RenderingStrategies.MYBATIS3));
  }

  @DeleteProvider(type = SqlProviderAdapter.class, method = "delete")
  int delete(final @NonNull DeleteStatementProvider statement);

  default int delete(
      final @NonNull DeleteDSL<DeleteModel>.DeleteWhereBuilder statement) {
    return delete(statement.build().render(RenderingStrategies.MYBATIS3));
  }
}
