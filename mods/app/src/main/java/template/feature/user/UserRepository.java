package template.feature.user;

import javax.inject.Inject;
import lombok.NonNull;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.update.UpdateModel;
import template.base.contract.Repository;
import template.infra.persistence.UserEntity;
import template.infra.persistence.UserEntity.Mapper;

final class UserRepository extends Repository.Default<UserEntity, User> {

  private static final UserEntity.Table MAPPING = new UserEntity.Table();

  @Inject
  UserRepository(final @NonNull SqlSession session) {
    super(MAPPING, UserTransformer.INSTANCE, session.getMapper(Mapper.class));
  }

  @Override
  protected QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder criteria(
      final @NonNull QueryExpressionDSL<SelectModel> w, final @NonNull User c) {
    return w.where(MAPPING.name, SqlBuilder.isEqualToWhenPresent(c::getName))
            .where(MAPPING.age, SqlBuilder.isEqualToWhenPresent(c::getAge));
  }

  @Override
  protected UpdateDSL<UpdateModel> criteria(
      final @NonNull UpdateDSL<UpdateModel> update, final @NonNull User u) {
    return update.set(MAPPING.name).equalToWhenPresent(u::getName)
                 .set(MAPPING.age).equalToWhenPresent(u::getAge);
  }
}
