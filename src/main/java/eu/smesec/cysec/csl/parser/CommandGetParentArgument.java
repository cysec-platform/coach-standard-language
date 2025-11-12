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

import eu.smesec.cysec.platform.bridge.execptions.CacheException;
import eu.smesec.cysec.platform.bridge.generated.Metadata;
import java.util.List;

/**
 * <div class="command-doc">
 *   <div class="command-header">
 *     <h2 class="command-name">getParentArgument</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">STRING</span> getParentArgument()</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>
 *         Returns the parent argument of the current subcoach. The subcoach system works like this:
 *         Parents of a subcoach can pass an argument to the subcoach.
 *         This mechanism can be used for all sorts of things.
 *         This command enables the subcoach to read out the argument that was passed by the parent.
 *     </p>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Parameters</h3>
 *     <em>No Parameters</em>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Return Value</h3>
 *     <p><code>STRING</code> - The argument that was passed by the parent coach to this subcoach.</p>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Examples</h3>
 *     <div class="example">
 *       <h4>Basic Usage</h4>
 *       <pre><code>set("isSecureProtocol", or(equals(getParentArgument(), "https"), equals(getParentArgument(), "ssh")))</code></pre>
 *       <p class="example-description">In this example the parent coach passes the protocol of the subcoach. The subcoach can use this information to implement logic based on the protocol.</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>The command only works in subcoaches. In coaches without a parent argument this command will return an empty string.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandGetParentArgument extends Command {

    public Atom execute(List<Atom> a, CoachContext coachContext) {

        try {
            Metadata parentMetadata = coachContext.getCal().getMetadata(coachContext.getFqcn(), "subcoach-data");
            if (parentMetadata != null) {
                return parentMetadata.getMvalue().stream()
                        .filter(mval -> mval.getKey().equals("parent-argument"))
                        .findFirst()
                        .map(mval -> mval.getStringValueOrBinaryValue().getValue())
                        .map(argument -> new Atom(Atom.AtomType.STRING, argument, null))
                        .orElse(new Atom(Atom.AtomType.STRING, "", null));
            }
        } catch (CacheException e) {
            coachContext
                    .getLogger()
                    .severe("There was an error while executing command 'getParentArgument': " + e.getMessage());
        }
        return new Atom(Atom.AtomType.STRING, "", null);
    }
}
