/*******************************************************************************
 * Mission Control Technologies, Copyright (c) 2009-2012, United States Government
 * as represented by the Administrator of the National Aeronautics and Space 
 * Administration. All rights reserved.
 *
 * The MCT platform is licensed under the Apache License, Version 2.0 (the 
 * "License"); you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations under 
 * the License.
 *
 * MCT includes source code licensed under additional open source licenses. See 
 * the MCT Open Source Licenses file included with this distribution or the About 
 * MCT Licenses dialog available at runtime from the MCT Help menu for additional 
 * information. 
 *******************************************************************************/
package gov.nasa.arc.mct.dao.service;

import gov.nasa.arc.mct.component.MockComponent;
import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.dao.persistence.strategy.ComponentSpecificationDaoStrategy;
import gov.nasa.arc.mct.dao.specifications.ComponentSpecification;
import gov.nasa.arc.mct.dao.specifications.Tag;
import gov.nasa.arc.mct.dao.specifications.TagInfo;
import gov.nasa.arc.mct.persistence.PersistenceSystemTest;
import gov.nasa.arc.mct.registry.GlobalComponentRegistry;
import gov.nasa.arc.mct.services.internal.component.Updatable;
import gov.nasa.arc.mct.services.internal.component.User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TagServiceImplTest extends PersistenceSystemTest {
    private static final String TEST_TAG = "TEST_TAG";
    private static final String TEST_TAG2 = "TEST_TAG2";
    
    private TagServiceImpl tagService;
    @Mock
    private User user;
    
    @Override
    protected void setupUser() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    protected void postSetup() {
        MockitoAnnotations.initMocks(this);
        
        GlobalContext.getGlobalContext().setSynchronousPersistenceManager(persistenceBroker);
        Mockito.when(user.getUserId()).thenReturn("Test User");
        GlobalContext.getGlobalContext().switchUser(user, null);
        tagService  = TagServiceImpl.getTagService();
    }

    @Test
    public void testAddTag() {
        List<Tag> allTags = persistenceBroker.loadAll(Tag.class);
        Assert.assertEquals(allTags.size(), 0);
        
        tagService.addTagIfNotExist(TEST_TAG);
        
        allTags = persistenceBroker.loadAll(Tag.class);
        Assert.assertEquals(allTags.size(), 1);
    }
    
    @Test
    public void testAddAllTags() {
        tagService.addTagIfNotExist(TEST_TAG);
        tagService.addTagIfNotExist(TEST_TAG2);
        Collection<String> allTags = tagService.getAllTags();
        Assert.assertEquals(allTags.size(), 2);
        Assert.assertTrue(allTags.contains(TEST_TAG));
        Assert.assertTrue(allTags.contains(TEST_TAG2));
    }
    
    @Test
    public void testRemoveTags() {
        testAddAllTags();
        
        ComponentSpecification compSpec = null;
        AbstractComponent comp = null;
        persistenceBroker.startSession("5");
        try {
            compSpec = persistenceBroker.lazilyLoad("5", ComponentSpecification.class, "5");
        } finally {
            persistenceBroker.closeSession("5");
        }
        
        AbstractComponent mctComp = new MockComponent();
        mctComp.getCapability(Updatable.class).setId(compSpec.getComponentId());
        GlobalComponentRegistry.registerComponent(mctComp);
        
        Assert.assertNotNull(compSpec);
        tagService.tag(Collections.singleton(TEST_TAG), compSpec, true);
        tagService.tag(Collections.singleton(TEST_TAG2), compSpec, true);
        
        persistenceBroker.startSession("5");
        int numOfTagProperties = 0;
        try {
            compSpec = persistenceBroker.lazilyLoad("5", ComponentSpecification.class, "5");
            comp = ComponentSpecificationDaoStrategy.fromDatabaseObjectToComponent(compSpec, false);
            numOfTagProperties = compSpec.getTags().size();
        } finally {
            persistenceBroker.closeSession("5");
        }

        Assert.assertEquals(numOfTagProperties, 2);
        
        tagService.removeTag(Collections.singleton(comp), TEST_TAG);
        
        persistenceBroker.startSession("5");
        numOfTagProperties = 0;
        try {
            compSpec = persistenceBroker.lazilyLoad("5", ComponentSpecification.class, "5");
            comp = ComponentSpecificationDaoStrategy.fromDatabaseObjectToComponent(compSpec, false);
            numOfTagProperties = compSpec.getTags().size();
        } finally {
            persistenceBroker.closeSession("5");
        }
        Assert.assertEquals(numOfTagProperties, 1);
        
        Assert.assertTrue(tagService.isTagged(Collections.singleton(TEST_TAG2), comp));
        
        GlobalComponentRegistry.removeComponent(mctComp.getComponentId());
    }
    
    @Test
    public void testTag() {
        ComponentSpecification compSpec = null;
        tagService.addTagIfNotExist(TEST_TAG);
        persistenceBroker.startSession("5");
        try {
            compSpec = persistenceBroker.lazilyLoad("5", ComponentSpecification.class, "5");
            Tag tag = persistenceBroker.lazilyLoad("5", Tag.class, TEST_TAG);
            
            Set<TagInfo<?>> tagInfos = tag.getTagInfos();
            Assert.assertEquals(tagInfos.size(), 0);
            
        } finally {
            persistenceBroker.closeSession("5");
        }
        
        AbstractComponent mctComp = new MockComponent();
        mctComp.getCapability(Updatable.class).setId(compSpec.getComponentId());
        GlobalComponentRegistry.registerComponent(mctComp);

        Assert.assertNotNull(compSpec);
        tagService.tag(Collections.singleton(TEST_TAG), compSpec, true);
        
        persistenceBroker.startSession("5");
        try {
            Tag tag = persistenceBroker.lazilyLoad("5", Tag.class, TEST_TAG);
            
            Set<TagInfo<?>> tagInfos = tag.getTagInfos();
            Assert.assertEquals(tagInfos.size(), 1);
            Assert.assertEquals(tagInfos.iterator().next().getComponent(), compSpec);
            
        } finally {
            persistenceBroker.closeSession("5");
        }
        
        Collection<AbstractComponent> tagggedComponents = tagService.getTaggedComponents(TEST_TAG);
        Assert.assertEquals(tagggedComponents.size(), 1);
        Assert.assertEquals(tagggedComponents.iterator().next().getId(), compSpec.getId().toString());
    }
}
