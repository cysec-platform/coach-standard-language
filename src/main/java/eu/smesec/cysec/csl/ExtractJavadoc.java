/*-
 * #%L
 * CYSEC Standard Coach Language
 * %%
 * Copyright (C) 2020 - 2026 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
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

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import eu.smesec.cysec.csl.parser.Command;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class ExtractJavadoc {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Usage: java ExtractJavadoc <sourceDir> <outputDir>");
            System.exit(1);
        }

        File sourceDir = new File(args[0]);
        File outputDir = new File(args[1]);

        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            System.err.println("Error: Source directory not found: " + sourceDir);
            System.exit(1);
        }
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            System.err.println("Error: Could not create output directory: " + outputDir);
            System.exit(1);
        }

        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSourceTree(sourceDir);

        for (Map.Entry<String, Command> entry : Command.getCommands().entrySet()) {
            String key = entry.getKey();
            Object commandInstance = entry.getValue();
            Class<?> cmdClass = commandInstance.getClass();

            JavaClass javaClass = builder.getClassByName(cmdClass.getName());
            if (javaClass == null) {
                System.err.println("Warning: No source found for " + cmdClass.getName());
                continue;
            }

            String doc = javaClass.getComment();
            if (doc == null || doc.trim().isEmpty()) {
                System.out.println("Skipping " + cmdClass.getName() + " (no Javadoc)");
                continue;
            }

            File outFile = new File(outputDir, key + ".md");
            try (FileWriter writer = new FileWriter(outFile)) {
                writer.write(doc.trim() + "\n");
            }

            System.out.println("Extracted Javadoc for command: " + key + " (" + cmdClass.getSimpleName() + ")");
        }
    }
}

