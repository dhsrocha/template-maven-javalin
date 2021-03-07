package template.base.contract;

import template.base.stereotype.Domain;
import template.base.stereotype.Entity;

@org.mapstruct.Mapper
public interface Transformer<D extends Domain<D>, E extends Entity> {

  E toEntity(final @lombok.NonNull D from);

  D fromEntity(final @lombok.NonNull E from);
}
