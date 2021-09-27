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
package eu.smesec.cysec.csl.demo.skills;

import eu.smesec.cysec.platform.bridge.generated.Mvalue;
import eu.smesec.cysec.platform.bridge.md.MetadataUtils;
import eu.smesec.cysec.csl.skills.Endurance;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class TestEndurance {
    private Endurance endurance;
    private Map<Long, Integer> entries = new HashMap<>();
    private String array;

    @Before
    public void setup() {
        entries.put(LocalDate.now().toEpochDay(), 1);
        entries.put(LocalDate.now().minusDays(1).toEpochDay(), 9);
        entries.put(LocalDate.now().minusDays(2).toEpochDay(), 4);
        entries.put(LocalDate.now().minusDays(3).toEpochDay(), 10);
        entries.put(LocalDate.now().minusDays(4).toEpochDay(), 2);
        entries.put(LocalDate.now().minusDays(5).toEpochDay(), 1);

        array = entries.entrySet().toString();
    }

    @After
    public void tearDown() {
        entries = null;
    }

    @Test
    // Relies on correct refresh method to restore state
    @Ignore
    // TODO: Order doesnt match but this isnt important
    public void testToString() {
        Mvalue mv = MetadataUtils.createMvalueStr("endurance", array);
        endurance = new Endurance(30);
        endurance.restore(MetadataUtils.parseMvalue(mv));

        // Check sum of integer values
        Assert.assertEquals(array, endurance.toString());
    }

    @Test
    public void testRefresh() {
        Mvalue mv = MetadataUtils.createMvalueStr("endurance", array);

        endurance = new Endurance(30);
        endurance.restore(MetadataUtils.parseMvalue(mv));
        int n = endurance.refresh();
        // Check sum of integer values
        Assert.assertEquals(27, endurance.get());
    }

    @Test
    public void testRestore() {
        Mvalue mv = MetadataUtils.createMvalueStr("endurance", array);

        endurance = new Endurance(3);
        endurance.restore(MetadataUtils.parseMvalue(mv));
        // Check sum of integer values
        Assert.assertEquals(24, endurance.get());
    }
}
