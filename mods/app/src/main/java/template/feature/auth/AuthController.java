package template.feature.auth;

import com.google.gson.Gson;
import io.javalin.http.Handler;
import java.util.Map;
import lombok.NonNull;
import template.base.contract.Filter;
import template.base.contract.Filter.WithEndpoint;

final class AuthController extends Filter.Default<Auth>
    implements WithEndpoint<Auth> {

  private static final Gson MAPPER = new Gson();

  @Override
  public Auth filter(final @NonNull Map<Header, String> map) {
    return new Auth(Token.from(map.get(Header.BEARER)));
  }

  @Override
  public Handler endpoint() {
    return ctx -> {
      // Retrieve user
      // Validate
      // return
      ctx.result(MAPPER.toJson(handle(headers)));
    };
  }
}
