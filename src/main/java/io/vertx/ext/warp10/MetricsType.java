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

import io.vertx.codegen.annotations.VertxGen;


/**
 *  Metrics types for each metrics.
 *  from vertx-hawkular-metrics
 */
@VertxGen
public enum MetricsType {

  /**
   * Type for net server metrics
   */
  NET_SERVER,
  /**
   * Type for net client metrics
   */
  NET_CLIENT,
  /**
   * Type for http server metrics
   */
  HTTP_SERVER,
  /**
   * Type for http client metrics
   */
  HTTP_CLIENT,
  /**
   * Type for datagram socket metrics
   */
  DATAGRAM_SOCKET,
  /**
   * Type for event bus metrics
   */
  EVENT_BUS,
  /**
   * Type for pool metrics
   */
  POOL
}
