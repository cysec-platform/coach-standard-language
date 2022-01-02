/*-
 * #%L
 * CYSEC Standard Coach Language
 * %%
 * Copyright (C) 2020 - 2022 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package eu.smesec.cysec.csl;

import java.util.HashMap;
import java.util.Map;

public class Score {

  private int value;
  private int maxValue;
  private Map<Object, Integer> caps;
  private int cap;

  public Score(int maxValue) {
    this.value = 0;
    this.maxValue = maxValue;
    this.caps = new HashMap<>(5);
    this.cap = maxValue;
  }

  public void setValue(int value) {    this.value = value;  }

  public int getValue() {
    return Math.max(0, Math.min(cap, value));
  }

  public int getMaxValue() {
    return maxValue;
  }

  public void add(int value) {
    this.value += value;
  }

  public void sub(int value) {
    this.value -= value;
  }

  public void limit(Object id, int value) {
    caps.put(id, value);
    cap = caps.values().stream().min(Integer::compareTo).orElse(this.maxValue);
  }

  public void unlimit(Object id) {
    caps.remove(id);
    cap = caps.values().stream().min(Integer::compareTo).orElse(this.maxValue);
  }
}
