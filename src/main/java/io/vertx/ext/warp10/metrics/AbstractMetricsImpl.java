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

import io.warp10.sensision.Sensision;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sébastien lambour
 */
public class AbstractMetricsImpl {

  protected Boolean enabled = false;

  protected final Map<String,String> defaultLabels = new HashMap<>();

  private final static Double defaultLatLong = Double.valueOf(0.0D / 0.0);

  public void setEnabled( Boolean value) {
    enabled = value;
  }

  public void incrementMetric(String classname, Map<String, String> labels) {
    updateMetric(classname, labels, 1);
  }

  public void updateMetric(String classname, Map<String, String> labels, Number value) {
    if (enabled) {
      Sensision.update(classname, labels, value);
    }
  }

  public void setMetric(String classname, Map<String, String> labels, Number value) {
    if (enabled) {
      Sensision.set(classname, labels, value);
    }
  }

  public void eventMetric(long timestamp, String classname, Map<String, String> labels, Number value) {
    if (enabled) {
      Sensision.event(timestamp, defaultLatLong, defaultLatLong, (Long) null, classname, labels, value);
    }
  }
  public void eventMetric(long timestamp, String classname, Map<String, String> labels, String value) {
    if (enabled) {
      Sensision.event(timestamp, defaultLatLong, defaultLatLong, (Long)null, classname, labels, value);
    }
  }

  public void eventMetric(String classname, Map<String, String> labels, Object value) {
    if (enabled) {
      Sensision.event(classname, labels, value);
    }
  }
}
