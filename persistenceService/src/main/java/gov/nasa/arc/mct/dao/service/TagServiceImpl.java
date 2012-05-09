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

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.dao.persistence.strategy.ComponentSpecificationDaoStrategy;
import gov.nasa.arc.mct.dao.specifications.ComponentSpecification;
import gov.nasa.arc.mct.dao.specifications.Tag;
import gov.nasa.arc.mct.dao.specifications.TagInfo;
import gov.nasa.arc.mct.persistence.strategy.DaoObject;
import gov.nasa.arc.mct.persistence.util.HibernateUtil;
import gov.nasa.arc.mct.persistmgr.PersistenceBroker;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.services.component.ComponentTagService;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;

/**
 * Tag service implementation as Singleton pattern.
 *
 */
public class TagServiceImpl implements ComponentTagService {
    private static final String TAG_SESSION_ID = "TAG_SESSION_ID";
    private static final TagServiceImpl instance = new TagServiceImpl();
    private static final int MAX_FLUSH_SIZE_PER_COMMIT = 200;

    private PersistenceBroker persistenceBroker = GlobalContext.getGlobalContext()
                    .getSynchronousPersistenceBroker();

    private Map<String, Set<TagInfo<?>>> pendingTags = new HashMap<String, Set<TagInfo<?>>>();

    /**
     * Gets the tag service instance.
     * @return TagServiceImpl - tag service implementation.
     */
    public static TagServiceImpl getTagService() {
        return instance;
    }

    private TagServiceImpl() {
        //
    }

    @Override
    public void addTagIfNotExist(String tagId) {
        Tag tag = persistenceBroker.loadById(tagId, Tag.class, tagId);
        if (tag == null) {
            tag = new Tag();
            tag.setTagId(tagId);

            persistenceBroker.startSession(tagId);
            try {
                persistenceBroker.save(tagId, tag, null);
            } finally {
                persistenceBroker.closeSession(tagId);
            }
        }
    }
    
    @Override
    public Collection<AbstractComponent> getTaggedComponents(String tagId) {
        Collection<ComponentSpecification> componentSpecifications = Collections.emptyList();
        Tag tag = persistenceBroker.lazilyLoad(tagId, Tag.class, tagId);
        try {        
            if (tag != null) {
                componentSpecifications = new LinkedList<ComponentSpecification>();
                for (TagInfo<?> info : tag.getTagInfos()) {
                    ComponentSpecification compSpec = (ComponentSpecification)info.getComponent();
                    componentSpecifications.add(compSpec);
                }
            }        
        } finally {
            persistenceBroker.lazilyLoadCompleted(tagId);
        }
        return ComponentSpecificationDaoStrategy.lazilyTransformTo(componentSpecifications, true);
    }

    @Override
    public Collection<String> getAllTags() {
        List<Tag> tags = persistenceBroker.loadAll(Tag.class);
        List<String> tagIds = new LinkedList<String>();
        for (Tag tag : tags)
            tagIds.add(tag.getTagId());
        return tagIds;
    }

    private <T extends DaoObject> void tag(String tagId, T object) {
        Set<TagInfo<?>> tagInfos = pendingTags.get(tagId);
        if (tagInfos == null) {
            tagInfos = new HashSet<TagInfo<?>>();
            pendingTags.put(tagId, tagInfos);
        }
        TagInfo<T> tagInfo = new TagInfo<T>();
        tagInfo.setTagProperty(PlatformAccess.getPlatform().getCurrentUser().getUserId() + " (" + DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()) + ")");
        tagInfo.setComponent(object);
        if (!tagInfos.contains(tagInfo)) {
            tagInfos.add(tagInfo);
        }
    }

    @Override
    public <T extends DaoObject> void tag(Set<String> tagIds, T object, boolean autoFlush) {
        if (tagIds.isEmpty()) {
            return;
        }

        for (String tag : tagIds) {
            tag(tag, object);
        }
        if (autoFlush) {
            flush();
        }
    }

    @Override
    public void flush() {
        for (String tag : pendingTags.keySet()) {
            List<TagInfo<? extends DaoObject>> tagInfos = new LinkedList<TagInfo<? extends DaoObject>>(pendingTags.get(tag));
            int size = tagInfos.size();
            for (int i = 0; i < size; i += MAX_FLUSH_SIZE_PER_COMMIT) {
                int endIndex = i + MAX_FLUSH_SIZE_PER_COMMIT;
                flush(i, size < endIndex ? size : endIndex, tag, tagInfos);
            }     
        }
        pendingTags.clear();
    }
    
    private void flush(int start, int end, String tag, List<TagInfo<? extends DaoObject>> tagInfos) {
        persistenceBroker.startSession(TAG_SESSION_ID);
        try {
            for (int i = start; i < end; i++) {
                TagInfo<? extends DaoObject> tagInfo = tagInfos.get(i);
                DaoObject daoObject = tagInfo.getComponent();
                String tagProperty = tagInfo.getTagProperty();
                daoObject = persistenceBroker.lazilyLoad(TAG_SESSION_ID, daoObject.getClass(), daoObject.getId());
                try {
                    daoObject.addTag(tag, tagProperty);
                    persistenceBroker.save(TAG_SESSION_ID, daoObject, null);
                } finally {
                    persistenceBroker.lazilyLoadCompleted(TAG_SESSION_ID);
                }
            }
        } finally {
            persistenceBroker.closeSession(TAG_SESSION_ID);
        }
    }

    @Override
    public Collection<AbstractComponent> removeTag(Collection<AbstractComponent> comps, String tagId) {
        Collection<AbstractComponent> removedTagComponents = new HashSet<AbstractComponent>();
        persistenceBroker.startSession(TAG_SESSION_ID);
        try {
            for (AbstractComponent comp : comps) {
                ComponentSpecification compDao = persistenceBroker.lazilyLoad(TAG_SESSION_ID,
                                ComponentSpecification.class, comp.getId());
                try {
                    compDao.removeTag(tagId);
                    persistenceBroker.save(TAG_SESSION_ID, compDao, null);
                } finally {
                    persistenceBroker.lazilyLoadCompleted(TAG_SESSION_ID);
                }
            }
            return removedTagComponents;
        } finally {
            persistenceBroker.closeSession(TAG_SESSION_ID);
        }
    }

    @Override
    public void tag(String tagId, Collection<AbstractComponent> comps) {
        persistenceBroker.startSession(TAG_SESSION_ID);
        try {
            for (AbstractComponent comp : comps) {

                ComponentSpecification compDao = persistenceBroker.loadById(TAG_SESSION_ID,
                                ComponentSpecification.class, comp.getId());
                compDao.addTag(tagId, null);
                persistenceBroker.save(TAG_SESSION_ID, compDao, null);
            }
        } finally {
            persistenceBroker.closeSession(TAG_SESSION_ID);
        }
    }
    
    @Override
    public boolean isTagged(Collection<String> tagIds, AbstractComponent comp) {
        ComponentSpecification compDao = persistenceBroker.lazilyLoad(comp.getId(),
                        ComponentSpecification.class, comp.getId());
        try {
            if (compDao == null)
                return false;
            for (String tagId: tagIds) {
                if (compDao.isTagged(tagId)) { return true; }
            }
            return false;
        } finally {
            persistenceBroker.lazilyLoadCompleted(comp.getId());
        }
    }

    @Override
    public Map<String, String> getTaggedInfo(String tagId, Collection<AbstractComponent> comps) {
        persistenceBroker.startSession(TAG_SESSION_ID);
        Map<String, String> tagProperties = new HashMap<String, String>();
        try {
            for (AbstractComponent comp: comps) {
                ComponentSpecification compDao = persistenceBroker.lazilyLoad(TAG_SESSION_ID, ComponentSpecification.class, comp.getId());
                try {
                    String tagProperty = compDao.getTagProperty(tagId);
                    if (tagProperty != null) {
                        tagProperties.put(comp.getId(), tagProperty);
                    }
                } finally {
                    persistenceBroker.lazilyLoadCompleted(TAG_SESSION_ID);
                }
            }
        } finally {
            persistenceBroker.closeSession(TAG_SESSION_ID);
        }
        return tagProperties;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean hasComponentsTaggedBy(String tagId) {       
        Session session = HibernateUtil.getCurrentSession(TAG_SESSION_ID);        
        try {
            StringBuilder countQuery = new StringBuilder("select count(*) from tag_association where tag_id = :tagId ");
            countQuery.append("limit 1;");
            Query q = session.createSQLQuery(countQuery.toString());
            q.setParameter("tagId", tagId);
            List<Object> l = q.list();
            int count = ((Number) l.get(0)).intValue();
            return count > 0;
        } finally {
            HibernateUtil.closeSession(session);
        }
    }
}
