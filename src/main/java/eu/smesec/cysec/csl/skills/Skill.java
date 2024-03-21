/*-
 * #%L
 * CYSEC Standard Coach Language
 * %%
 * Copyright (C) 2020 - 2024 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
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
package eu.smesec.cysec.csl.skills;

/**
 * Serves as the base class for all aspects of current und yet to come skills
 */
public abstract class Skill {

    protected int value;
    protected int maxValue;

    Skill(int maxValue) {
        this.value = 0;
        this.maxValue = maxValue;
    }

    public int get() {
        return Math.max(0, Math.min(this.maxValue, this.value));
    }
    public void setValue(int value) { this.value = value; }

    public void add(int value) {
        this.value += value;
    }

    public int getMaxValue() { return maxValue; }
}
