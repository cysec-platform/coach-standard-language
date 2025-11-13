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
package eu.smesec.cysec.csl.parser;

import eu.smesec.cysec.platform.bridge.generated.Dictionary;
import java.util.Arrays;
import java.util.List;

/**
 * <div class="command-doc">
 *   <div class="command-header">
 *     <h2 class="command-name">tn</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">STRING</span> tn(<span class="params">key: STRING</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command performs a lookup in the coach's dictionary (translation table) and returns the value associated with the given key.</p>
 *     <p>It provides a way to retrieve localized or predefined strings from the coach configuration. If the key is not found in the dictionary, or if the dictionary itself is empty/non-existent, it returns a <code>NULL</code> Atom.</p>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Parameters</h3>
 *     <table class="params-table">
 *       <thead>
 *         <tr>
 *           <th>Name</th>
 *           <th>Type</th>
 *           <th>Required</th>
 *           <th>Description</th>
 *         </tr>
 *       </thead>
 *       <tbody>
 *         <tr>
 *           <td><code>key</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>The string key to look up in the coach's dictionary.</td>
 *         </tr>
 *       </tbody>
 *     </table>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Return Value</h3>
 *     <p><code>STRING</code> - The string value retrieved from the dictionary corresponding to the <code>key</code>, or a <code>NULL</code> Atom if the key is not found or the dictionary is unavailable/empty.</p>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Examples</h3>
 *     <div class="example">
 *       <h4>Retrieving a translated string</h4>
 *       <pre><code>tn("greetingMessage") // e.g., "Hello!" if "greetingMessage" is mapped to "Hello!" in the dictionary</code></pre>
 *       <p class="example-description">Looks up "greetingMessage" in the dictionary and returns its associated value.</p>
 *     </div>
 *     <div class="example">
 *       <h4>Using with variables</h4>
 *       <pre><code>tn(concat("label.", get("currentLanguage")));</code></pre>
 *       <p class="example-description">Constructs a dynamic key (e.g., "label.en" or "label.de") and retrieves its value from the dictionary.</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>The <code>key</code> parameter must evaluate to a <code>STRING</code>.</li>
 *       <li>An empty string or <code>NULL</code> for the <code>key</code> will result in a <code>NULL</code> Atom being returned.</li>
 *       <li>The command's name `tn` is likely an abbreviation for "translate", reflecting its purpose in retrieving translated or pre-configured strings.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandDictionaryLookup extends Command {

    @Override
    public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
        checkNumParams(aList, 1);

        // determine key of the entry to search in the dictionary
        Atom a = checkAtomType(aList.get(0), Arrays.asList(Atom.AtomType.STRING), true, coachContext, null);

        // handle empty id (return null)
        final String key = a.getId();
        if (key == null || key.isEmpty()) {
            return Atom.NULL_ATOM;
        }

        // handle non-existent dictionary
        final Dictionary dictionary = coachContext.getCoach().getDictionary();
        if (dictionary == null || dictionary.getEntry() == null) {
            return Atom.NULL_ATOM;
        }

        // extract value corresponding to key or return null-atom
        return dictionary.getEntry().stream()
                .filter(e -> key.equals(e.getKey()))
                .findFirst()
                .map(e -> new Atom(Atom.AtomType.STRING, e.getValue(), null))
                .orElse(Atom.NULL_ATOM);
    }
}
