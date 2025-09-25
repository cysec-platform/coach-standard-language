/*-
 * #%L
 * CYSEC Standard Coach Language
 * %%
 * Copyright (C) 2020 - 2025 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
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

import eu.smesec.cysec.csl.parser.CommandAddBadge;
import eu.smesec.cysec.platform.bridge.generated.Metadata;

/**
 * Interface for the helper class to provide concise access to create (Badge and Recommendation ) Metadata.
 * May be obsolete in the future because MetadataUtils offers annotations for Badges
 * which define how to serialize objects to metadata.
 * Checkout {@link CommandAddBadge#execute CommandAddBadge} for
 * a list of supported fields.
 */
public interface IMetadataBuilder {

    /**
     * Create a metadata object with the usual mvalues for badges
     * @return A metadta object containing a badge
     */
    Metadata buildBadge();

    /**
     * Create a metadata object with the usual mvalues for recommendations
     * @return A metadata object containing a recommendation
     */
    Metadata buildRecommendation();
}
