package template.feature.user;

import lombok.NonNull;
import org.mapstruct.factory.Mappers;
import template.base.contract.Transformer;
import template.infra.persistence.UserEntity;

interface UserTransformer extends Transformer<User, UserEntity> {

  UserTransformer INSTANCE = Mappers.getMapper(UserTransformer.class);

  @Override
  UserEntity toEntity(final @NonNull User from);

  @Override
  User fromEntity(final @NonNull UserEntity from);
}
