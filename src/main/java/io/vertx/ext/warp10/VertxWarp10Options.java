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
package io.vertx.ext.warp10;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import io.vertx.core.metrics.MetricsOptions;

import java.util.EnumSet;
import java.util.Set;


/**
 * @author Sébastien lambour
 */
@DataObject(generateConverter = true, inheritConverter = true)
public class VertxWarp10Options extends MetricsOptions {

  private Set<MetricsType> disabledMetricsTypes;
  private String clusterName;


  public VertxWarp10Options() {
    disabledMetricsTypes = EnumSet.noneOf(MetricsType.class);
  }

  public VertxWarp10Options(VertxWarp10Options other) {
    super(other);
    clusterName = other.getClusterName();
    disabledMetricsTypes = other.disabledMetricsTypes != null ? EnumSet.copyOf(other.disabledMetricsTypes) : EnumSet.noneOf(MetricsType.class);
  }
  public VertxWarp10Options(JsonObject json) {
    this();
    VertxWarp10OptionsConverter.fromJson(json, this);
  }

  public String getClusterName() {
    return clusterName;
  }

  public boolean isMetricsTypeDisabled(MetricsType metricsType) {
    return this.disabledMetricsTypes.contains(metricsType);
  }
}
