package examples;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class HttpVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> fut) {
    System.out.println("Metrics enabled=" + vertx.isMetricsEnabled());

    vertx
        .createHttpServer()
        .requestHandler(r -> {
          r.response().end("<h1>Hello from my first " +
              "Vert.x 3 application</h1>");
        })
        .listen(40080, result -> {
          if (result.succeeded()) {
            fut.complete();
          } else {
            fut.fail(result.cause());
          }
        });
  }
}
