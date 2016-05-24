//
//   Copyright 2016  Cityzen Data
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
//
package io.vertx.ext.warp10.impl;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.metrics.MetricsOptions;
import io.vertx.core.spi.VertxMetricsFactory;
import io.vertx.core.spi.metrics.VertxMetrics;
import io.vertx.ext.warp10.VertxWarp10Options;

/**
 * @author Sébastien lambour
 */
public class VertxMetricsFactoryImpl implements VertxMetricsFactory {
  @Override
  public VertxMetrics metrics(Vertx vertx, VertxOptions options) {
    MetricsOptions metricsOptions = options.getMetricsOptions();
    VertxWarp10Options vertxWarp10Options;
    if (metricsOptions instanceof VertxWarp10Options) {
      vertxWarp10Options = (VertxWarp10Options) metricsOptions;
    } else {
      vertxWarp10Options = new VertxWarp10Options(metricsOptions.toJson());
    }
    return new VertxMetricsImpl(vertx, vertxWarp10Options);
  }

  @Override
  public MetricsOptions newOptions() {
    return new VertxWarp10Options();
  }
}
