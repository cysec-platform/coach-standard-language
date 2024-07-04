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
package eu.smesec.cysec.csl.parser;

import java.math.BigDecimal;

/**
 * Command that checks whether a number is greater or equals to another number. See {@link CommandNumberBinaryPredicate}
 * for more information.
 */
public class CommandGreaterThanOrEquals extends CommandNumberBinaryPredicate {
    @Override
    public boolean test(BigDecimal lhs, BigDecimal rhs) {
        return lhs.compareTo(rhs) >= 0;
    }
}
