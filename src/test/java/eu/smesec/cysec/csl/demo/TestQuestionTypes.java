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
package eu.smesec.cysec.csl.demo;

import eu.smesec.cysec.platform.bridge.ILibCal;
import eu.smesec.cysec.platform.bridge.generated.Questionnaire;
import eu.smesec.cysec.csl.AbstractLib;
import org.junit.After;
import org.junit.Before;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.logging.Logger;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

public class TestQuestionTypes {
    private ILibCal libcal;
    private Questionnaire coach;
    private AbstractLib library;
    private String libId = "eu.smesec.cysec.coach.demo.MockLibrary";
    private Logger logger;
    private Unmarshaller unmarshaller;

    @Before
    public void setup() {
        library = new MockLibrary();
        libcal = mock(ILibCal.class);
        coach = mock(Questionnaire.class);
        logger = Logger.getGlobal();

        try (BufferedInputStream is = new BufferedInputStream(getClass().getResourceAsStream("/user_training.xml"))) {
            JAXBContext jc = JAXBContext.newInstance(Questionnaire.class);
            unmarshaller = jc.createUnmarshaller();
            coach = (Questionnaire) unmarshaller.unmarshal(is);
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
            fail();
        }
        library.init(libId, coach, libcal, logger);
    }

    @After
    public void tearDown() {
        library = null;
    }

//    @Test
//    public void testSelect() {
//        SelectQuestion typeA = (SelectQuestion) library.getQuestion("q30");
//
//        typeA.notify("q30o2", "q30");
//        typeA.notify("q30o1", "q30");
//
//        assertTrue(typeA.isAnswered());
//        assertTrue(typeA.getOptions().get("q30o1").isSelected());
//        assertFalse(typeA.getOptions().get("q30o2").isSelected());
//    }
//
//    @Test
//    public void testAstarExclusive() {
//        AstarQuestion aStarExcl = (AstarexclQuestion) library.getQuestion("q50");
//        // add some options
//        aStarExcl.notify("q50o2", "q50");
//        aStarExcl.notify("q50o1", "q50");
//        assertTrue(aStarExcl.isAnswered());
//        assertTrue(aStarExcl.getOptions().get("q50o2").isSelected());
//        assertTrue(aStarExcl.getOptions().get("q50o1").isSelected());
//
//        // deselect all with 'none'
//        aStarExcl.notify("q50oNone", "q50");
//        assertTrue(aStarExcl.isAnswered());
//        assertTrue(aStarExcl.getOptions().get("q50oNone").isSelected());
//        assertFalse(aStarExcl.getOptions().get("q50o2").isSelected());
//        assertFalse(aStarExcl.getOptions().get("q50o1").isSelected());
//    }
//
//    @Test
//    public void testAstar() {
//        AstarQuestion aStar = (AstarQuestion) library.getQuestion("q70");
//        // add some options
//        aStar.notify("q70o2", "q70");
//        aStar.notify("q70o1", "q70");
//        assertTrue(aStar.isAnswered());
//        assertTrue(aStar.getOptions().get("q70o2").isSelected());
//        assertTrue(aStar.getOptions().get("q70o1").isSelected());
//
//        // Make sure successor is correct
//        Optional<String> next = aStar.getSuccessor();
//        Assert.assertTrue(next.isPresent());
//        Assert.assertEquals("q100,q90", next.get());
//    }
//
//    @Test
//    public void testTypeA() {
//        TypeAQuestion typeAQuestion = (TypeAQuestion) library.getQuestion("q20");
//
//        // add some options
//        typeAQuestion.notify("q20o2", "q20");
//        typeAQuestion.notify("q20o1", "q20");
//        assertTrue(typeAQuestion.isAnswered());
//        assertFalse(typeAQuestion.getOptions().get("q20o2").isSelected());
//        assertTrue(typeAQuestion.getOptions().get("q20o1").isSelected());
//
//    }
//
//    @Test
//    public void testLikert() {
//        LikertQuestion likert = (LikertQuestion) library.getQuestion("q60");
//
//        // add some options
//        likert.notify("2", "q60");
//        likert.notify("3", "q60");
//        assertTrue(likert.isAnswered());
//        assertFalse(likert.getOptions().get("q60o2").isSelected());
//        assertTrue(likert.getOptions().get("q60o3").isSelected());
//
//    }
//
//    @Test
//    public void testText() {
//        TextQuestion textQuestion = (TextQuestion) library.getQuestion("q80");
//
//        // add some options
//        textQuestion.notify("", "q10");
//        assertFalse(textQuestion.isAnswered());
//
//        textQuestion.notify("This is my answer", "q10");
//        assertTrue(textQuestion.isAnswered());
//    }
}
