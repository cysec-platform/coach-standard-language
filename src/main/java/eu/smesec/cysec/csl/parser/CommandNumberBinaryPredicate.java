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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * This is an abstract implementation for all commands that operate on two numbers and return a boolean value.
 * This class can be used by extending from it and implementing the {@link BiPredicate#test(Object, Object) test} method. The
 * {@link BiPredicate#test(Object, Object) test} method determines how the return value of the command is calculated.
 */
public abstract class CommandNumberBinaryPredicate extends Command implements BiPredicate<BigDecimal, BigDecimal> {
    @Override
    public Atom execute(List<Atom> atoms, CoachContext coachContext) throws ExecutorException {
        checkNumParams(atoms, 2);

        // Extract arguments
        Atom lhs;
        Atom rhs;
        try {
            lhs = checkAtomType(atoms.get(0), Arrays.asList(Atom.AtomType.FLOAT, Atom.AtomType.INTEGER), true, coachContext, "leftHandSide");
            rhs = checkAtomType(atoms.get(1), Arrays.asList(Atom.AtomType.FLOAT, Atom.AtomType.INTEGER), true, coachContext, "rightHandSide");
        } catch (ExecutorException e) {
            // If the parameters are not numbers we cannot compare them, thus the result of the comparison must be false
            return Atom.FALSE;
        }

        // Extract value and store as BigDecimal since it can store all other number types
        BigDecimal lhsVal = new BigDecimal(lhs.getId());
        BigDecimal rhsVal = new BigDecimal(rhs.getId());

        // evaluate the test method and return accordingly
        return test(lhsVal, rhsVal) ? Atom.TRUE : Atom.FALSE;
    }
}
