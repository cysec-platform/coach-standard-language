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

import eu.smesec.cysec.csl.MetadataBuilder;
import eu.smesec.cysec.platform.bridge.CoachLibrary;
import eu.smesec.cysec.platform.bridge.execptions.CacheException;
import eu.smesec.cysec.platform.bridge.generated.Metadata;
import eu.smesec.cysec.platform.bridge.generated.Questionnaire;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * <div class="command-doc">
 *   <div class="command-header">
 *     <h2 class="command-name">createSubcoach</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">NULL</span> createSubcoach(<span class="params">coachId: STRING, fileIdentifier: STRING, [parentArgument: STRING]</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command dynamically creates and instantiates a new subcoach instance on the platform, establishing a parent-child relationship.</p>
 *     <p>The <code>coachId</code> refers to a coach definition (e.g., "lib-subcoach-backup") that must exist in the platform's coach directory. The <code>fileIdentifier</code> is a unique string to distinguish this specific instance of the subcoach. An optional <code>parentArgument</code> can be passed from the parent coach to the newly created subcoach.</p>
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
 *           <td>The ID of the coach to be instantiated as a subcoach.</td>
 *         </tr>
 *         <tr>
 *           <td><code>fileIdentifier</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>A unique string that identifies this specific instance of the subcoach (e.g., "www.test.ch"). This forms part of the subcoach's Fully Qualified Coach Name (FQCN).</td>
 *         </tr>
 *         <tr>
 *           <td><code>parentArgument</code></td>
 *           <td><code>STRING</code></td>
 *           <td>No</td>
 *           <td>An optional string argument to be passed to the newly created subcoach. This can be retrieved by the subcoach using <code>getParentArgument()</code>.</td>
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
 *       <h4>Creating a subcoach with a unique identifier</h4>
 *       <pre><code>createSubcoach("lib-subcoach-backup", "www.test.ch");</code></pre>
 *       <p class="example-description">Instantiates a subcoach named "lib-subcoach-backup" with the instance identifier "www.test.ch".</p>
 *     </div>
 *     <div class="example">
 *       <h4>Creating a subcoach passing a parent argument</h4>
 *       <pre><code>createSubcoach("protocol-coach", "connection1", "HTTPS");</code></pre>
 *       <p class="example-description">Creates a "protocol-coach" instance "connection1" and passes "HTTPS" as an argument. The subcoach can then retrieve "HTTPS" using <code>getParentArgument()</code>.</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>If the <code>coachId</code> does not correspond to an existing coach definition in the platform, an <code>ExecutorException</code> will be thrown.</li>
 *       <li>The <code>fileIdentifier</code> is used to construct the Fully Qualified Coach Name (FQCN) of the subcoach instance (e.g., <code>parentCoachId.coachId.fileIdentifier</code>). This FQCN ensures uniqueness.</li>
 *       <li>This command also sets the parent link, allowing the newly created subcoach to interact with its parent's context.</li>
 *       <li>Errors during subcoach creation (e.g., if the coach ID is invalid in the CAL) are logged at <code>SEVERE</code> level.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandCreateSubcoach extends Command {

    @Override
    public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
        checkNumParams(aList, 2, 3);

        // evaluate parameters
        Atom coachID = checkAtomType(aList.get(0), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "coachID");
        Atom fileIdentifier =
                checkAtomType(aList.get(1), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "fileIdentifier");
        Atom parentArgument;
        if (aList.size() == 3) {
            parentArgument = checkAtomType(
                    aList.get(2), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "parentArgument");
        } else {
            parentArgument = Atom.NULL_ATOM;
        }

        try {
            Questionnaire subcoach = coachContext.getCal().getCoach(coachID.getId());
            if (subcoach == null) {
                throw new ExecutorException("Coach id " + coachID.getId() + " does not exist");
            }
            // Append current coach id to segment: e.g lib-company.lib-subcoach-backup
            Set<String> segment = new HashSet<>();
            segment.add(fileIdentifier.getId());
            coachContext.getLogger().info("Creating subcoach " + subcoach.getId() + "" + fileIdentifier.getId());
            // pass FQCN of parent. CoachContext contains the fqcn of the current coach, which is
            // the
            // parent.
            CoachLibrary subcoachLibrary =
                    coachContext.getCal().getLibraries(subcoach.getId()).get(0);
            Metadata metadata = MetadataBuilder.newInstance(subcoachLibrary)
                    .setMvalue("parent-argument", parentArgument.getId() == null ? "" : parentArgument.getId())
                    .buildCustom("subcoach-data");
            coachContext.getCal().instantiateSubCoach(subcoach, segment, metadata);

            // set parent context of new subcoach
            coachContext
                    .getLogger()
                    .info("Setting " + coachContext.getCoach().getId() + " as parent for " + subcoach.getId());

            subcoachLibrary.setParent(coachContext.getContext());
        } catch (CacheException e) {
            coachContext.getLogger().log(Level.SEVERE, "Couldn't create subcoach via CAL", e);
            // setup coach relation for already existing coaches
            try {
                Questionnaire subcoach = coachContext.getCal().getCoach(coachID.getId());
                CoachLibrary subcoachLibrary =
                        coachContext.getCal().getLibraries(subcoach.getId()).get(0);
                subcoachLibrary.setParent(coachContext.getContext());
            } catch (CacheException ex) {
                coachContext
                        .getLogger()
                        .log(Level.SEVERE, "Error trying to setup parent relation for existing coach", e);
            }
        }

        return null;
    }
}
