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
package eu.smesec.cysec.csl.demo;

import eu.smesec.cysec.platform.bridge.ILibCal;
import eu.smesec.cysec.platform.bridge.generated.Answer;
import eu.smesec.cysec.platform.bridge.generated.Question;
import eu.smesec.cysec.platform.bridge.generated.Questionnaire;
import eu.smesec.cysec.csl.AbstractLib;
import eu.smesec.cysec.csl.parser.CySeCExecutorContextFactory;
import eu.smesec.cysec.csl.parser.ExecutorContext;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class TestLogicRunner {
    private ILibCal libcal;
    private Questionnaire coach;
    private AbstractLib library;
    private String libId = "eu.smesec.cysec.coach.demo.MockLibrary";
    private Logger logger;
    private Unmarshaller unmarshaller;
    private ExecutorContext context;

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

        context = CySeCExecutorContextFactory.getExecutorContext(coach.getId());

    }

    @After
    public void tearDown() {
        library = null;
        context.reset();
        context = null;
    }

    // Test creation

    // Test onBegin

    // Test runLogic
}
