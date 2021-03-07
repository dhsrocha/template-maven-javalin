package template.base.contract;

import static template.base.Exceptions.INVALID_DOMAIN;

import com.google.gson.Gson;
import io.javalin.apibuilder.CrudHandler;
import io.javalin.http.Context;
import java.util.UUID;
import lombok.NonNull;
import lombok.val;
import template.base.stereotype.Domain;

public interface Controller<D extends Domain<D>> extends CrudHandler,
                                                         Api<D, UUID> {

  abstract class Default<D extends Domain<D>> implements Controller<D>,
                                                         Domain.Ref<D> {

    private static final Gson MAPPER = new Gson();

    @Override
    public final void create(final @NonNull Context ctx) {
      val d = ctx.bodyAsClass(domainRef());
      INVALID_DOMAIN.throwIf(IllegalArgumentException::new, () -> !d.isValid());
      ctx.result(MAPPER.toJson(create(d)));
    }

    @Override
    public final void getOne(final @NonNull Context ctx,
                             final @NonNull String id) {
      ctx.result(MAPPER.toJson(getOne(UUID.fromString(id))));
    }

    @Override
    public void getAll(final @NonNull Context ctx) {
      val res = ctx.body().isBlank()
          ? getAll() : getBy(ctx.bodyAsClass(domainRef()));
      ctx.result(MAPPER.toJson(res.toArray()));
    }

    @Override
    public final void update(final @NonNull Context ctx,
                             final @NonNull String id) {
      val d = ctx.bodyAsClass(domainRef());
      INVALID_DOMAIN.throwIf(IllegalArgumentException::new, () -> !d.isValid());
      ctx.status(update(UUID.fromString(id), d) ? 204 : 404);
    }

    @Override
    public final void delete(final @NonNull Context ctx,
                             final @NonNull String id) {
      ctx.status(delete(UUID.fromString(id)) ? 204 : 404);
    }

    @Override
    public final int count() {
      return getAll().size();
    }
  }
}
