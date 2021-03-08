package template.base.contract;

import io.javalin.http.Handler;

public interface Endpoint {

  Handler endpoint();
}
