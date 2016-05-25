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

import io.vertx.core.spi.metrics.PoolMetrics;
import io.vertx.ext.warp10.impl.VertxMetricsImpl;
import io.warp10.sensision.Sensision;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Sébastien lambour
 */
public class PoolMetricsImpl extends AbstractMetricsImpl implements PoolMetrics<Long> {

  public static String SENSISION_CLASS_MAX = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "pool.maxSize";

  public static String SENSISION_CLASS_SUBMITTED_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "pool.submitted.count";

  public static String SENSISION_CLASS_REJECTED_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "pool.rejected.count";
  public static String SENSISION_CLASS_REJECTED_TIME  = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "pool.rejected.time";

  public static String SENSISION_CLASS_QUEUED_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "pool.queued.count";
  public static String SENSISION_CLASS_QUEUED_TIME  = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "pool.queued.time";

  public static String SENSISION_CLASS_ERROR_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "pool.error.count";
  public static String SENSISION_CLASS_WORKING_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "pool.working.count";
  public static String SENSISION_CLASS_WORKING_TIME  = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "pool.working.time";
  public static String SENSISION_CLASS_WORKING  = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "pool.working";

  public static String SENSISION_LABEL_POOL_TYPE = "type";
  public static String SENSISION_LABEL_POOL_NAME = "name";

  private int maxPoolSize = -1;
  private final AtomicLong working = new AtomicLong(0);

  public PoolMetricsImpl(String poolType, String poolName, int maxPoolSize, Map<String,String> defaults, Boolean enabled) {
    this.enabled = enabled;
    defaultLabels.putAll(defaults);
    defaultLabels.put(SENSISION_LABEL_POOL_TYPE, poolType);
    defaultLabels.put(SENSISION_LABEL_POOL_NAME, poolName);
    this.maxPoolSize = maxPoolSize;
  }

  @Override
  public Long submitted() {
    incrementMetric(SENSISION_CLASS_SUBMITTED_COUNT, defaultLabels);
    setMetric(SENSISION_CLASS_MAX, defaultLabels, maxPoolSize);
    return System.nanoTime();
  }


  @Override
  public void rejected(Long nanoStart) {
    long waitingTime = System.nanoTime() - nanoStart;
    incrementMetric(SENSISION_CLASS_REJECTED_COUNT, defaultLabels);
    updateMetric(SENSISION_CLASS_REJECTED_TIME, defaultLabels, waitingTime);
  }

  @Override
  public Long begin(Long nanoStart) {
    long currentNanoTime =  System.nanoTime();
    long queuedTime = currentNanoTime - nanoStart;

    setMetric(SENSISION_CLASS_WORKING, defaultLabels,working.incrementAndGet());
    incrementMetric(SENSISION_CLASS_QUEUED_COUNT, defaultLabels);
    updateMetric(SENSISION_CLASS_QUEUED_TIME, defaultLabels, queuedTime);

    return currentNanoTime;
  }

  @Override
  public void end(Long nanoStart, boolean succeeded) {
    long workingTime = System.nanoTime() - nanoStart;

    setMetric(SENSISION_CLASS_WORKING, defaultLabels,working.decrementAndGet());
    if (succeeded) {
      incrementMetric(SENSISION_CLASS_WORKING_COUNT, defaultLabels);
      updateMetric(SENSISION_CLASS_WORKING_TIME, defaultLabels, workingTime);
    } else {
      incrementMetric(SENSISION_CLASS_ERROR_COUNT, defaultLabels);
    }
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public void close() {

  }
}
