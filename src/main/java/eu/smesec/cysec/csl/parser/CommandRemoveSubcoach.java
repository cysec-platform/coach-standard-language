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

import eu.smesec.cysec.platform.bridge.FQCN;
import eu.smesec.cysec.platform.bridge.execptions.CacheException;
import java.util.*;
import java.util.logging.Level;

/**
 * <div class="command-doc">
 *   <div class="command-header">
 *     <h2 class="command-name">removeSubcoach</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">NULL</span> removeSubcoach(<span class="params">coachId: STRING, fileIdentifier: STRING</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command removes a previously instantiated subcoach instance from the platform.</p>
 *     <p>It requires the <code>coachId</code> of the subcoach definition and the unique <code>fileIdentifier</code> that was used during its creation to specifically target the instance to be removed.</p>
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
 *           <td><code>coachId</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>The ID of the subcoach definition (e.g., "protocol-coach").</td>
 *         </tr>
 *         <tr>
 *           <td><code>fileIdentifier</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>The unique identifier used when the subcoach was created (e.g., "ssh" or "connection1").</td>
 *         </tr>
 *       </tbody>
 *     </table>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Return Value</h3>
 *     <p><code>NULL</code> - The return value is not meaningful.</p>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Examples</h3>
 *     <div class="example">
 *       <h4>Removing a specific subcoach instance</h4>
 *       <pre><code>removeSubcoach("protocol-coach", "ssh");</code></pre>
 *       <p class="example-description">Removes the instance of "protocol-coach" that was identified as "ssh".</p>
 *     </div>
 *     <div class="example">
 *       <h4>Conditional subcoach removal</h4>
 *       <pre><code>if(not(isSelected("qNetworkProtocoloSSH")), removeSubcoach("protocol-coach", "ssh"));</code></pre>
 *       <p class="example-description">Removes the "ssh" instance of "protocol-coach" if the "SSH" option is no longer selected for "qNetworkProtocol".</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>Both <code>coachId</code> and <code>fileIdentifier</code> must be strings.</li>
 *       <li>The command logs a <code>SEVERE</code> error if it fails to remove the subcoach (e.g., if the instance does not exist or due to other platform-level issues).</li>
 *       <li>Proper FQCN (Fully Qualified Coach Name) is constructed internally using the parent coach's ID, <code>coachId</code>, and <code>fileIdentifier</code> to identify the exact subcoach instance.</li>
 *       <li>See also: <code>createSubcoach</code>.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandRemoveSubcoach extends Command {

    @Override
    public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
        checkNumParams(aList, 2);

        // evaluate parameters
        Atom coachID = checkAtomType(aList.get(0), List.of(Atom.AtomType.STRING), true, coachContext, "coachID");
        Atom fileIdentifier =
                checkAtomType(aList.get(1), List.of(Atom.AtomType.STRING), true, coachContext, "fileIdentifier");

        FQCN fqcn = FQCN.fromString(String.join(
                ".", Arrays.asList(coachContext.getCoach().getId(), coachID.getId(), fileIdentifier.getId())));

        try {
            coachContext.getCal().removeSubCoach(fqcn);
        } catch (CacheException e) {
            coachContext.getLogger().log(Level.SEVERE, "Error trying to remove sub-coach", e);
        }

        return null;
    }
}
