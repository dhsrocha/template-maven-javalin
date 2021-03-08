package template.base.contract;

import io.javalin.http.Handler;
import java.util.EnumMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;

public interface Filter<R> {

  String pathTo();

  Handler filter();

  R filter(final @NonNull Map<Header, String> headers);

  interface WithEndpoint<R> extends Filter<R>, Endpoint {

  }

  @Getter
  @AllArgsConstructor
  enum Header {
    BEARER("Bearer");
    private final String header;
  }

  abstract class Default<R> implements Filter<R> {

    protected static Map<Header, String> treat(final @NonNull Handler ctx) {
      val headers = new EnumMap<Header, String>(Header.class);
      ctx.headerMap().forEach((h, v) -> headers.put(Header.valueOf(h), v));
      return headers;
    }

    @Override
    public Handler filter() {
      return ctx -> {
        val map = treat(ctx);
        val result =  filter(treat(ctx));
        // TODO how to resolve
        if (null != result) {
          ctx.redirect(pathTo());
        }
      };
    }
  }
}
