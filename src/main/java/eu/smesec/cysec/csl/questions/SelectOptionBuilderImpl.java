/*-
 * #%L
 * CYSEC Standard Coach Language
 * %%
 * Copyright (C) 2020 - 2021 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
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
package eu.smesec.cysec.csl.questions;

public final class SelectOptionBuilderImpl {
    private String id;
    private int score;
    private String nextQid;

    // hide default constructor
    private SelectOptionBuilderImpl() {}

    public static SelectOptionBuilderImpl newInstance() {
        return new SelectOptionBuilderImpl();
    }

    public SelectOptionBuilderImpl setId(String id) {
        this.id = id;
        return this;
    }

    public SelectOptionBuilderImpl setNext(String next) {
       this.nextQid = next;
       return this;
    }

    public SelectOptionBuilderImpl setScore(int score) {
        this.score = score;
        return this;
    }

    public LibSelectOption build() {
        return new LibSelectOption(id, score, nextQid);
    }
}