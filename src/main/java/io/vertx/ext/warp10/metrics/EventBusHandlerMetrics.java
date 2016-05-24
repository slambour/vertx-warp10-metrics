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
package io.vertx.ext.warp10.metrics;

/**
 * from vertx-hawkular-metrics
 * @author Sébastien lambour
 */
public class EventBusHandlerMetrics {
  private final String address;

  private long start;

  public EventBusHandlerMetrics(String address) {
    this.address = address;
  }

  /**
   * @return eventMetric bus address of the monitored handler
   */
  public String getAddress() {
    return address;
  }

  /**
   * Sets the timer state to <em>now</em>.
   */
  public void resetTimer() {
    start = System.nanoTime();
  }

  /**
   * @return the number of nanoseconds elapsed since {@link #resetTimer()} was called
   */
  public long elapsed() {
    return System.nanoTime() - start;
  }
}
