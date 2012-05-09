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
/**
 * AbstractComponent.java Aug 18, 2008
 * 
 * This code is property of the National Aeronautics and Space Administration
 * and was produced for the Mission Control Technologies (MCT) Project.
 * 
 */
package gov.nasa.arc.mct.components;

import gov.nasa.arc.mct.context.GlobalContext;
import gov.nasa.arc.mct.gui.MCTMutableTreeNode;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.lock.manager.LockManager;
import gov.nasa.arc.mct.persistence.strategy.DaoObject;
import gov.nasa.arc.mct.persistence.strategy.DaoStrategy;
import gov.nasa.arc.mct.persistence.strategy.OptimisticLockException;
import gov.nasa.arc.mct.platform.spi.PersistenceService;
import gov.nasa.arc.mct.platform.spi.Platform;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.policy.PolicyInfo;
import gov.nasa.arc.mct.registry.ExternalComponentRegistryImpl;
import gov.nasa.arc.mct.registry.ExternalComponentRegistryImpl.ExtendedComponentTypeInfo;
import gov.nasa.arc.mct.registry.GlobalComponentRegistry;
import gov.nasa.arc.mct.roles.events.AddChildEvent;
import gov.nasa.arc.mct.roles.events.PropertyChangeEvent;
import gov.nasa.arc.mct.roles.events.RemoveChildEvent;
import gov.nasa.arc.mct.services.component.PolicyManager;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;
import gov.nasa.arc.mct.services.internal.component.ComponentInitializer;
import gov.nasa.arc.mct.services.internal.component.Updatable;
import gov.nasa.arc.mct.services.internal.component.User;
import gov.nasa.arc.mct.util.ComponentUtil;
import gov.nasa.arc.mct.util.IdGenerator;
import gov.nasa.arc.mct.util.MCTIcons;
import gov.nasa.arc.mct.util.WeakHashSet;
import gov.nasa.arc.mct.util.exception.MCTRuntimeException;

import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The superclass of any component type. A new component type needs to extend
 * from this class. A component is uniquely identified by its id. 0 or more viewRoles. 
 * Subclasses must be created by the platform before usage within the system, this is done using the
 * <code>ComponentRegistry</code>. A component indicates that it has persistent state by providing an instance of 
 * {@link ModelStatePersistence} through the <code>getCapabilities</code> method. 
 * 
 * <em>Important</em> subclasses must have a public no argument constructor.
 * 
 */
public abstract class AbstractComponent implements Cloneable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractComponent.class);
    
    /** A dummy component, used as a sentinel value. */
    public final static AbstractComponent NULL_COMPONENT;

    static {
        AbstractComponent tmpComp = new AbstractComponent() {
        };

        tmpComp.id = "0";
        tmpComp.getCapability(ComponentInitializer.class).initialize();
        NULL_COMPONENT = tmpComp;
    }

    /** The unique ID of the component, filled in by the framework. */
    private String id;

    private boolean shared;
    private String owner;
    private String creator;
    private Date creationDate;
    private AbstractComponent masterComponent = null;
    private String displayName = null; // human readable name for the component.
    private String externalKey = null; // reference that can be used to contain external keys
    private Map<String, ExtendedProperties> viewRoleProperties = new HashMap<String, ExtendedProperties>();
    private ComponentInitializer initializer;
    private final Set<String> pendingTags = new HashSet<String>();
    private int version;
    private transient DaoStrategy<AbstractComponent, ? extends DaoObject> daoStrategy;
    private LockManager lockManager = GlobalContext.getGlobalContext().getLockManager();
    /** The existing manifestations of this component. */
    private final WeakHashSet<View> viewManifestations = new WeakHashSet<View>();
    
    private transient List<AbstractComponent> referencedComponents; 
    private transient Set<String>             referencingComponentIds;
    
    /**
     * Creates a new component instance with model, and sharing.
     * 
     * @param isShared
     *            if true, the component participates in object-sharing
     */
    public AbstractComponent(boolean isShared) {
        this.shared = isShared;
        
        referencedComponents = Collections.<AbstractComponent> emptyList();
        referencingComponentIds = Collections.<String> emptySet();
    }

    /**
     * The new component instance will not participate in object-sharing. 
     * 
     */
    public AbstractComponent() {
        this(false);
    }

    /**
     * Initialize the component by registering it with the component registry
     * and creating a new, empty set of view roles.
     */
    protected void initialize() {
        performInitialization();
        getCapability(Initializer.class).setInitialized();
    }

    private void performInitialization() {
        if (this.id == null) {
            this.id = IdGenerator.nextComponentId();
        }
        GlobalComponentRegistry.registerComponent(this);
    }
    
    /**
     * Verifies the class requirements are met for this component class.
     * 
     * @param componentClass
     *            class to verify, must not be null
     * @throws IllegalArgumentException
     *             if the class does not meet the requirements
     */
    public static void checkBaseComponentRequirements(Class<? extends AbstractComponent> componentClass)
                    throws IllegalArgumentException {
        try {
            // ensure that a public no argument constructor exists. If this is
            // an inner non static class
            // there is an implicit argument of the enclosing class so this
            // scenario should be covered
            componentClass.getConstructor(new Class[0]);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(componentClass.getName()
                            + " must provide a public no argument constructor");
        }
    }
    
    /**
     * Get the type ID string for the type of the component. Component type IDs
     * are unique for each registered component type.
     * 
     * @return the component type ID
     */
    public String getComponentTypeID() {
        return this.getClass().getName();
    }

    /**
     * Returns the views for the desired view type. This method will apply the <code>PolicyInfo.CategoryType.FILTER_VIEW_ROLE</code> policy
     * and the <code>PolicyInfo.CategoryType.PREFERRED_VIEW</code> policy before returning the appropriate list of views.
     * @param type of view to discover.
     * @return views that are appropriate for this component. 
     */
    public Set<ViewInfo> getViewInfos(ViewType type) {
        Set<ViewInfo> possibleViewInfos =  PlatformAccess.getPlatform().getComponentRegistry().getViewInfos(getComponentTypeID(), type);
        Set<ViewInfo> filteredViewInfos = new LinkedHashSet<ViewInfo>();
        
        Platform platform = PlatformAccess.getPlatform();
        PolicyManager policyManager = platform.getPolicyManager();
        PolicyContext context = new PolicyContext();
        context.setProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName(), this);
        context.setProperty(PolicyContext.PropertyName.VIEW_TYPE.getName(), type);
        for (ViewInfo viewInfo : possibleViewInfos) {
            context.setProperty(PolicyContext.PropertyName.TARGET_VIEW_INFO.getName(), viewInfo);
            if (policyManager.execute(PolicyInfo.CategoryType.FILTER_VIEW_ROLE.getKey(), context)
                            .getStatus()) {
                filteredViewInfos.add(viewInfo);
            }
        }

        // if there is a preferredView then make sure this is added first in the
        // list
        for (ViewInfo viewRole : filteredViewInfos) {
            context.setProperty(PolicyContext.PropertyName.TARGET_VIEW_INFO.getName(), viewRole);
            if (!policyManager.execute(PolicyInfo.CategoryType.PREFERRED_VIEW.getKey(), context)
                            .getStatus()) {
                Set<ViewInfo> setWithPreferredViewFirst = new LinkedHashSet<ViewInfo>();
                setWithPreferredViewFirst.add(viewRole);
                setWithPreferredViewFirst.addAll(filteredViewInfos);
                filteredViewInfos = setWithPreferredViewFirst;
                break;
            }
        }
        
        return filteredViewInfos;
    }
    
    private synchronized void addViewProperty(String viewRoleType, ExtendedProperties properties) {
        if (!viewRoleProperties.containsKey(viewRoleType)) {
            this.viewRoleProperties.put(viewRoleType, properties);
        }        
    }

    /**
     * Return the unique ID for this component.
     * 
     * @return the ID for the component
     */
    public String getId() {
        return this.id;
    }

    /**
     * Return the unique ID for this component. This is a synonym for
     * {@link #getId()}.
     * 
     * @return the ID for the component
     */
    public String getComponentId() {
        return this.id;
    }

    /**
     * Sets the ID of this component. The framework calls this method
     * automatically. It should never be called by plugin code.
     * 
     * @param id
     *            the new ID for the component
     */
    public void setId(String id) {
        if (id != null && initializer != null && initializer.isInitialized()) {
            throw new IllegalStateException("id must be set before component is initialized");
        }
        this.id = id;
    }

    /**
     * Tests whether the component is currently participating in object-sharing.
     * 
     * @return true, if the component is currently shared
     */
    public synchronized boolean isShared() {
        return this.shared;
    }

    /**
     * Tests whether the component is currently a leaf.
     * 
     * <p>
     * A leaf component is defined as a component that will never have any
     * children. This is an <i>immutable</i> property of a component instance
     * and must not change during its lifetime. The default implementation
     * returns false (thus the default component will be a container), a
     * subclass that should identify itself as a leaf should override this
     * method and return true.
     * 
     * @return true, if the component is currently a leaf. False otherwise.
     */
    public boolean isLeaf() {
        return false;
    }
    
    /**
     * Determines if this component can be <em>twiddled</em>.
     * @return false by default.
     */
    public boolean isTwiddleEnabled() {
        return false;
    }

    /**
     * Sets whether the component is currently participating in object-sharing.
     * 
     * @param shared
     *            true or false, depending on whether the component is shared
     */
    public synchronized void setShared(boolean shared) {
        if (!this.shared) {
            this.shared = shared;
        }
    }

    /**
     * Sets the user who is the owner of the component.
     * 
     * @param user
     *            the owner of the component
     */
    public synchronized void setOwner(User user) {
        PropertyChangeEvent event = new PropertyChangeEvent(this);
        event.setProperty("OWNER", user.getUserId());
        firePropertyChangeEvent(event);
        this.owner = user.getUserId();
    }

    /**
     * Sets the user ID of the owner of the component.
     * 
     * @param owner
     *            the user ID of the owner of the component
     */
    public synchronized void setOwner(String owner) {
        this.owner = owner;
        save();
    }

    /**
     * Gets the user ID of the owner of the component.
     * 
     * @return the component owner user ID
     */
    public synchronized String getOwner() {
        return this.owner;
    }
    
    /**
     * Gets the creator of this component. 
     * @return the creator of this component
     */
    public synchronized String getCreator() {
        return creator;
    }
    
    /**
     * Gets the creation time of this component.
     * @return when this component was created
     */
    public synchronized Date getCreationDate() {
        return creationDate;
    }

    /**
     * Adds a new delegate (child) component. The model of the new delegate
     * component will be added as a new delegate model, and all view role
     * instances will be updated. The new child will be added after all existing
     * children. If the child is already present, this will move the child to
     * the end.
     * 
     * @param childComponent
     *            the new child component
     */
    public final void addDelegateComponent(AbstractComponent childComponent) {
        addDelegateComponents(Collections.singleton(childComponent));
    }

    /**
     * Refreshes all view manifestations of this component.
     * 
     */
    public final synchronized void refreshViewManifestations() {
        GlobalContext globalContext = GlobalContext.getGlobalContext();
        if (globalContext == null)
            return;

        Runnable refreshRunnable = new Runnable() {
            @Override
            public void run() {
                fireComponentChanged();
                additionalRefresh();
            }
        };

        if (SwingUtilities.isEventDispatchThread()) {
            refreshRunnable.run();
        } else {
            SwingUtilities.invokeLater(refreshRunnable);
        }
    }
    
    private void fireComponentChanged() {
        Set<View> guiComponents = getAllViewManifestations();
        if (guiComponents != null) {
            for (View gui : guiComponents) {
                if (gui.getParent() != null || gui.getClientProperty(MCTMutableTreeNode.PARENT_CLIENT_PROPERTY_NAME) != null) { // if it has no parent, the gui widget will eventually get garbage collected.
                    gui.updateMonitoredGUI();
                }
            }
        }
    }
    
    /**
     * Refreshes manifestations of this view.
     * @param viewInfo type instances to refresh
     */
    public synchronized void refreshManifestations(final ViewInfo viewInfo) {
        GlobalContext globalContext = GlobalContext.getGlobalContext();
        if (globalContext == null)
            return;
                

        Runnable refreshRunnable = new Runnable() {
            @Override
            public void run() {
                fireSettingsChanged(viewInfo);
            }
        };

        if (SwingUtilities.isEventDispatchThread()) {
            refreshRunnable.run();
        } else {
            SwingUtilities.invokeLater(refreshRunnable);
        }
    }

    private void fireSettingsChanged(ViewInfo vf) {
        Set<View> guiComponents = getAllViewManifestations();
        if (guiComponents != null) {
            for (View gui : guiComponents) {
                if (vf.equals(gui.getInfo()) && (gui.getParent() != null || gui.getClientProperty(MCTMutableTreeNode.PARENT_CLIENT_PROPERTY_NAME) != null)) { // if it has no parent, the gui widget will eventually get garbage collected.
                    gui.updateMonitoredGUI();
                }
            }
        }
    }
    
    /**
     * Gets the components which are referencing this component. Currently, only delegate components are considered.
     * The result of this method is transient and thus requires overhead for each execution. 
     * @return collection which can be empty but never null of components which reference this component. 
     */
    public Collection<AbstractComponent> getReferencingComponents() {
        PersistenceService persistenceService = PlatformAccess.getPlatform().getPersistenceService();
        return persistenceService.getReferences(isTwiddledComponent() ? getMasterComponent() : this);
    }

    /**
     * Adds a delegate (child) component to this component. The model of the
     * delegate component will be added as a delegate model, if not already
     * present, and all the view roles will be updated by sending an
     * {@link AddChildEvent} to the view role listeners. If the model is already
     * present, the child model will be moved to the given position.
     * 
     * @param childIndex
     *            the index within the children to add the new component, or -1
     *            to add at the end
     * @param childComponent
     *            the new delegate component
     */
    private void processAddDelegateComponent(int childIndex, AbstractComponent childComponent) {
        if (childIndex < 0) {
            childIndex = getComponents().size();
        }

        // If the child already exists, remove it, and adjust the insert index
        // if needed.
        int existingIndex = -1;
        for (int i = 0; i < getComponents().size(); i++) {
            if (getComponents().get(i).getComponentId().equals(childComponent.getComponentId())) {
                existingIndex = i;
            }
        }
        if (existingIndex >= 0) {
            removeComponent(childComponent);
            if (existingIndex < childIndex) {
                --childIndex;
            }
        }

        // Now add it again.
        addComponentAt(childIndex, childComponent);
    }

    /**
     * Delete this component.
     * 
     * @return true if deletion succeeds, false otherwise.
     */
    protected synchronized final boolean deleteComponent() {
        if (!canBeDeleted()) {
            return false;
        }

        // Make a copy of referenced components, to unhook and potentially delete afterwards
        List<AbstractComponent> removedChildComponents = new LinkedList<AbstractComponent>();
        for (AbstractComponent childComponent : getComponents()) {            
            removedChildComponents.add(childComponent);
        }
        removeDelegateComponents(removedChildComponents);
        for (AbstractComponent childComp : removedChildComponents) {
            if (!childComp.hasParents()) {
                childComp.delete();
            }
        }
        
        // Inform the parents
        for (String parentComponentId : new ArrayList<String>(referencingComponentIds)) {
            AbstractComponent parentComp = ExternalComponentRegistryImpl.getInstance()
                            .getComponent(parentComponentId);
            parentComp.removeDelegateComponent(this);
        }

        PlatformAccess.getPlatform().getWindowManager().closeWindows(id);
        return true;
    }

    /**
     * Determine by policy if this component can be deleted.
     * 
     * @return true if this component can be deleted, false otherwise.
     */
    public final boolean canBeDeleted() {
        Platform platform = PlatformAccess.getPlatform();
        PolicyManager policyManager = platform.getPolicyManager();

        PolicyContext context = new PolicyContext();
        context.setProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName(), this);
        context.setProperty(PolicyContext.PropertyName.ACTION.getName(), 'w');
        String compositionKey = PolicyInfo.CategoryType.CAN_DELETE_COMPONENT_POLICY_CATEGORY
                        .getKey();
        return policyManager.execute(compositionKey, context).getStatus();
    }

    private boolean hasParents() {
        return !referencingComponentIds.isEmpty();
    }

    /**
     * Refresh all view manifestations following the insertion of child
     * components. This method is called automatically by the framework when
     * child components are added to a shared component on one MCT instance in a
     * cluster. It is called only on the other machines in the cluster, to
     * refresh the view manifestations. (The view manifestations on the original
     * machine have already been updated by the call to
     * {@link #addDelegateComponent(AbstractComponent)} or
     * {@link #addDelegateComponents(Collection)}.
     * 
     * <p>
     * The view manifestations are updated by sending an {@link AddChildEvent}
     * to each view role listener.
     * 
     * @param childIndex
     *            the index among the children at which the insertion occurred
     * @param childComponents
     *            the child components added
     */
    public final synchronized void refreshManifestationFromInsert(final int childIndex,
                    final Collection<AbstractComponent> childComponents) {
        GlobalContext globalContext = GlobalContext.getGlobalContext();
        if (globalContext == null)
            return;

        Runnable refreshRunnable = new Runnable() {
            @Override
            public void run() {
                for (AbstractComponent childComponent : childComponents) {
                    AddChildEvent event = new AddChildEvent(AbstractComponent.this, childComponent,
                                    childIndex);
                    fireAddChildEvent(event);
                }

                additionalRefreshFromInsert();
            }
        };

        if (SwingUtilities.isEventDispatchThread()) {
            refreshRunnable.run();
        } else {
            SwingUtilities.invokeLater(refreshRunnable);
        }
    }
    
    private void fireAddChildEvent(AddChildEvent event) {
        Set<View> guiComponents = getAllViewManifestations();
        if (guiComponents != null) {
            for (View gui : guiComponents) {
                gui.updateMonitoredGUI(event);
            }
        }
    }

    /**
     * Refresh all view manifestations following the removal of child
     * components. This method is called automatically by the framework when
     * child components are removed from a shared component on one MCT instance
     * in a cluster. It is called only on the other machines in the cluster, to
     * refresh the view manifestations. (The view manifestations on the original
     * machine have already been updated by the call to
     * {@link #removeDelegateComponent(AbstractComponent)} or
     * {@link #removeDelegateComponents(Collection)}.
     * 
     * <p>
     * The view manifestations are updated by sending an {@link AddChildEvent}
     * to each view role listener.
     * 
     * @param childComponents
     *            the child components added
     */
    public final synchronized void refreshManifestationFromRemove(
                    final Collection<AbstractComponent> childComponents) {
        GlobalContext globalContext = GlobalContext.getGlobalContext();
        if (globalContext == null)
            return;
        
        Runnable refreshRunnable = new Runnable() {
            @Override
            public void run() {
                for (AbstractComponent childComponent : childComponents) {
                    RemoveChildEvent event = new RemoveChildEvent(AbstractComponent.this,
                                    childComponent);
                    fireRemoveChildEvent(event);
                }
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            refreshRunnable.run();
        } else {
            SwingUtilities.invokeLater(refreshRunnable);
        }
    }
    
    private void fireRemoveChildEvent(RemoveChildEvent event) {
        Set<View> guiComponents = getAllViewManifestations();
        if (guiComponents != null) {
            for (View gui : guiComponents) {
                gui.updateMonitoredGUI(event);
            }
        }
    }

    /**
     * Adds new delegate (child) components to this component. The model for
     * each component will be added as a delegate model, if not already present.
     * Then, all view roles will be updated by sending an {@link AddChildEvent}
     * for each new delegate added. The child will be added at the end of any
     * existing children. If the child was already present, this will move the
     * child to the end.
     * 
     * <p>
     * This method is called when a drop of one or more components happens in
     * the directory tree. The canvas area of this component is unaffected.
     * 
     * @param childComponents
     *            the collection of delegate components to add
     * @return true if <code>childComponents</code> are added successfully;
     *         otherwise false.
     */
    public final boolean addDelegateComponents(Collection<AbstractComponent> childComponents) {
        return addDelegateComponents(-1, childComponents);
    }

    private void originalAddDelegateComponents(int childIndex, Collection<AbstractComponent> childComponents) {
        for (AbstractComponent childComponent : childComponents) {
            processAddDelegateComponent(childIndex, childComponent);
        }
    }
    
    /**
     * Adds new delegate (child) components to this component. The model for
     * each component will be added as a delegate model, if not already present.
     * Then, all view roles will be updated by sending an {@link AddChildEvent}
     * for each new delegate added. The child will be added at a given index
     * among the existing children. If the child was already present, this will
     * move the child to the new position.
     * 
     * <p>
     * This method is called when a drop of one or more components happens in
     * the directory tree. The canvas area of this component is unaffected.
     * 
     * @param childIndex
     *            the index with the children to add the new component, or -1 to
     *            add at the end
     * @param childComponents
     *            the collection of delegate components to add
     * @return true if <code>childComponents</code> are added successfully;
     *         otherwise false.
     */
    public final boolean addDelegateComponents(int childIndex,
                    Collection<AbstractComponent> childComponents) {
        DaoStrategy<AbstractComponent, ? extends DaoObject> daoStrategy = getDaoStrategy();

        Platform platform = PlatformAccess.getPlatform();
        PolicyManager policyManager = platform.getPolicyManager();
        PolicyContext context = new PolicyContext();
        context.setProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName(), this);
        context.setProperty(PolicyContext.PropertyName.ACTION.getName(), 'w');
        context
                        .setProperty(PolicyContext.PropertyName.SOURCE_COMPONENTS.getName(),
                                        childComponents);
        if (policyManager.execute(PolicyInfo.CategoryType.ACCEPT_DELEGATE_MODEL_CATEGORY.getKey(),
                        context).getStatus()) {
            List<AbstractComponent> sharedComponents = new ArrayList<AbstractComponent>();
            if (!isShared() || lockManager.isLockedForAllUsers(getId())
                            || lockManager.isExtendedLocking(getId()) || this
                            .isVersionedComponent()) {
                boolean retry = false;
                int maxRetries = 100;
                do {
                    try {
                        java.util.List<AbstractComponent> persistedChildComponents = new LinkedList<AbstractComponent>();
                        for (AbstractComponent childComponent : childComponents) {
                            if (shouldBeShare()) {
                                shareComponent(childComponent, this.getId(), false, sharedComponents);
                            }
                            persistedChildComponents.add(childComponent);
                        }
                        
                        // ensure children are persisted as they may not be unlocked
                        for (AbstractComponent c:sharedComponents) {
                            if (!childComponents.contains(c)) {
                                DaoStrategy<AbstractComponent, ? extends DaoObject> strategy = c.getDaoStrategy();
                                if (strategy != null) {
                                    strategy.saveObject();
                                }
                            }
                        } 
                        
                        if (daoStrategy != null) {
                            addDelegateComponentsBeforeSave(persistedChildComponents);
                            daoStrategy.saveObjects(childIndex, persistedChildComponents);
                        }
                        retry = false;
                    } catch (OptimisticLockException e) {
                        LOGGER.debug("optimistic lock problem, trying transaction again");
                        // reset components that were initially shared to ensure sharing will persist
                        for (AbstractComponent componentShared:sharedComponents) {
                            Updatable updater = componentShared.getCapability(Updatable.class);
                            if (updater != null) {
                                updater.setShared(false);
                            }
                        }
                        // if there was an optimistic lock exception then update all the components and retry. Everything needs to be merged as the problem could have
                        // been in the shared or this component. 
                        PlatformAccess.getPlatform().getPersistenceService().updateComponentsFromDatabase();
                        // for each shared component, reset the shared status
                        sharedComponents.clear();
                        retry = lockManager.isLockedForAllUsers(getId()) && maxRetries-- > 0;
                        if (!retry) {
                            LOGGER.debug("attempted to retry optimistic lock than the maximum number of retries, rethrowing exception");
                            throw e;
                        }
                    }
                } while (retry);
                
            }

            // adjust locks after the transaction has been committed, this is just a local operation
            LockManager lm = GlobalContext.getGlobalContext().getLockManager();
            for (AbstractComponent sharedComponent:sharedComponents) {
                lm.shareLock(sharedComponent.getComponentId());
            }
            
            if (childComponents.isEmpty()) {
                refreshViewManifestations();
            } else {
                originalAddDelegateComponents(childIndex, childComponents);
                refreshManifestationFromInsert(childIndex, childComponents);
            }

            addDelegateComponentsCallback(childComponents);
            return true;

        }
        addDelegateComponentsCallback(childComponents);
        return false;
    }
    
    private boolean shouldBeShare() {
        return isShared() || isVersionedComponent() && getMasterComponent().isShared();
    }
    
    private void shareComponent(AbstractComponent shareComponent, String rootComponentId, boolean shouldPersist, final List<AbstractComponent> components) {
        boolean originallyShared = shareComponent.isShared();
        // if the object is locked for all users then skip it as it is a drop box
        if (PlatformAccess.getPlatform().getLockManager().isLockedForAllUsers(shareComponent.getComponentId())) {
            return;
        }
        shareComponent.setShared(true);
       
        // if shareComponent (the component to be shared) contains the set of groups 
        // that the destination component has, then this indicates that the descendants
        // of shareComponent are all shared and visible.
        if (originallyShared) // component is visible to the same groups as the destination component
            return;
        
        components.add(shareComponent);
        
        for (AbstractComponent childComponent : shareComponent.getComponents()) {
            shareComponent(childComponent, rootComponentId, true, components);
        }
        
        if (shouldPersist) {
            getDaoStrategy().associateDelegateSessionId(shareComponent.getId(), rootComponentId);
            shareComponent.save();
        }
        
    }

    /**
     * Removes a delegate (child) component from this component. The model of
     * the delegate component is removed as a delegate model. Then, all view
     * roles are updated by sending a {@link RemoveChildEvent}.
     * 
     * @param childComponent
     *            the delegate component to remove
     */
    public final void removeDelegateComponent(AbstractComponent childComponent) {
        removeDelegateComponents(Collections.singleton(childComponent));
    }

    /**
     * Removes delegate (child) components from this component.
     * 
     * @param childComponents
     *            the delegate components to remove
     */
    public synchronized void removeDelegateComponents(Collection<AbstractComponent> childComponents) {
        if (ComponentUtil.containsChildComponents(this, childComponents)) {
            DaoStrategy<AbstractComponent, ? extends DaoObject> daoStrategy = getDaoStrategy();
            if (daoStrategy != null)
                daoStrategy.removeObjects(childComponents);
        }

        for (AbstractComponent comp : childComponents) {
            removeComponent(comp);
        }

        refreshManifestationFromRemove(childComponents);
        removeDelegateComponentsCallback(childComponents);
    }
    
    /**
     * Call back method before added components are persisted.
     * @param persistedChildComponents child components to be persisted.
     */
    protected void addDelegateComponentsBeforeSave(Collection<AbstractComponent> persistedChildComponents){
        //
    }
    
    /**
     * Call back method to handle additional semantics for adding delegate
     * components.
     * 
     * @param childComponents
     *            child components to be added
     */
    protected void addDelegateComponentsCallback(Collection<AbstractComponent> childComponents) {
        //
    }

    /**
     * Call back method to handle additional semantics for removing delegate
     * components.
     * 
     * @param childComponents
     *            components to be removed.
     */
    protected void removeDelegateComponentsCallback(Collection<AbstractComponent> childComponents) {
        //
    }

    /**
     * Get the human readable name for this component that is suitable for
     * displaying to the user in a GUI.
     * 
     * If the display name has not been set, the component ID will be returned.
     * 
     * Note: this method should <b>not</b> be relied on to retrieve component
     * IDs (or other component type specific identifiers such as PUI IDs).
     * 
     * @return the display name of the component.
     */
    public synchronized String getDisplayName() {
        if (displayName == null) {
            return getId();
        } else {
            return displayName;
        }
    }
    
    /**
     * Get the human readable name for this component that is suitable for user display. This method differs from
     * {@link #getDisplayName()} as this method will be invoked when many components are being presented together and
     * having additional information may help the user distinguish components whose display name varies by only a small amount.
     * For example, this method may be used in presenting search results that may have the same display name so this method will be
     * invoked to help differentiate the results in the interface. The default implementation of this method will invoke {@link #getDisplayName()}.
     * @return a string representing the extended display name
     */
    public String getExtendedDisplayName() {
        return getDisplayName();
    }

    /**
     * Gets the external key for this component if it exists.
     * @return key used outside MCT if it exists, null otherwise
     */
    public synchronized String getExternalKey() {
        return externalKey;
    }
    
    /**
     * Sets the external key that allow MCT components to generically reference external entities. How this is
     * used outside MCT is considered behavior of the component and will be described further by the component author. 
     * @param key used outside MCT
     */
    public synchronized void setExternalKey(String key) {
        externalKey = key;
    }
    
    /**
     * Sets the human readable display name for this component.
     * 
     * @param name
     *            the new display name
     */
    public synchronized void setDisplayName(String name) {
        this.displayName = name;
        save();
    }

    /**
     * Sets the display name of the component and updates all view
     * manifestations to match, by sending a {@link PropertyChangeEvent} to each
     * view role listener.
     * 
     * @param name
     *            the new display name
     */
    public void setAndUpdateDisplayName(String name) {
        this.setDisplayName(name);
        PropertyChangeEvent event = new PropertyChangeEvent(this);
        event.setProperty(PropertyChangeEvent.DISPLAY_NAME, this.displayName);
        firePropertyChangeEvent(event);
    }
    
    
    /**
     * Sets the coomponent's owner and updates all views.
     * 
     * @param name the owner name
     */
    public void setAndUpdateOwner(String name) {
        this.setOwner(name);
        PropertyChangeEvent event = new PropertyChangeEvent(this);
        event.setProperty(PropertyChangeEvent.OWNER, this.owner);
        firePropertyChangeEvent(event);
    }
    
    private void firePropertyChangeEvent(PropertyChangeEvent event) {
        Set<View> guiComponents = getAllViewManifestations();
        if (guiComponents != null) {
            for (View gui : guiComponents) {
                gui.updateMonitoredGUI(event);
            }
        }
    }

    /**
     * Gets the master component for this component. A component has a master
     * component if it is being edited while shared. The master component is the
     * copy that does not reflect any of the editing changes. When editing
     * changes are committed, the properties of this component are copied into
     * the master, thus updating all MCT instances in the cluster. The master
     * component continues to participate in object sharing while this private,
     * non-shared copy is being edited.
     * 
     * @return the master component, or null if no master exists
     */
    public AbstractComponent getMasterComponent() {
        return this.masterComponent;
    }

    /**
     * Return true if this component if being versioned. A component is being
     * versioned if it is unlocked for editing while shared. At the time it is
     * unlocked, the shared component becomes the master component, and a new,
     * non-shared copy becomes the component being edited. We are versioned if
     * we have a master component.
     * 
     * @return true, if this component is versioned
     */
    public boolean isVersionedComponent() {
        return this.masterComponent != null && masterComponent.getId() == getId();
    }

    /**
     * Shares this component across the cluster. This method is typically not
     * needed by developers, but is used by the platform to implement things
     * like the drop box.
     * 
     * @return true if this object was successfully shared, false otherwise
     */
    public final boolean share() {
        setShared(true);
        GlobalContext.getGlobalContext().getLockManager().shareLock(getComponentId());
        return true;
    }

    /**
     * Open this component in a new top level window.
     */
    public final void open() {
        Platform platform = PlatformAccess.getPlatform();
        assert platform != null;
        
        if (DetectGraphicsDevices.getInstance().getNumberGraphicsDevices() > DetectGraphicsDevices.MINIMUM_MONITOR_CHECK) {
            Frame frame = null;
            for (Frame f: Frame.getFrames()) {
                if (f.isActive() || f.isFocused()) {
                    frame = f;
                    break;
                }
            }
            
            if (frame != null) {            
                GraphicsConfiguration graphicsConfig = frame.getGraphicsConfiguration();
                open(graphicsConfig);
            } else {
                // Need this when MCT first startups and when there's no active window available.
                openInNewWindow(platform);
            }
            
        } else {
            openInNewWindow(platform);
        }
    }
    
    private void openInNewWindow(Platform platform) {
        assert platform != null : "Platform should not be null.";
        
        if (masterComponent != null) {
            platform.getWindowManager().openInNewWindow(masterComponent);
        }  else {
            platform.getWindowManager().openInNewWindow(this);
        }
    }
    
    /**
     * Detect multiple monitor displays and allow menu item to open this component in a new top level window.
     * @param graphicsConfig - Detect multiple display monitor devices
     */
    public final void open(GraphicsConfiguration graphicsConfig) {
        Platform platform = PlatformAccess.getPlatform();
        assert platform != null;
        
        if (masterComponent != null) {
            platform.getWindowManager().openInNewWindow(masterComponent, graphicsConfig);
        }  else {
            platform.getWindowManager().openInNewWindow(this, graphicsConfig);
        }
    }
    
    /**
     * Callback method from {@link #refreshViewManifestations()} for subclasses
     * to inject additional behaviors when {@link #refreshViewManifestations()}
     * is invoked.
     */
    protected void additionalRefresh() {
        //
    }

    /**
     * Callback method from
     * {@link #refreshManifestationFromInsert(int,Collection)} for subclasses to
     * inject additional behaviors when
     * {@link #refreshManifestationFromInsert(int,Collection)} is invoked.
     */
    protected void additionalRefreshFromInsert() {
        //
    }

    /**
     * Gets an instance of the capability. A capability is functionality that
     * can be provided by a component dynamically. For example, functionality
     * can be provided only before a component has been initialized, doing this
     * using inheritance would require introducing an exception into the method
     * signatures and additional javadoc describing the semantics. The
     * capabilities provided are specific to the component type and are not
     * constrained by the platform.
     * 
     * @param <T>
     *            Class of the capability
     * @param capability
     *            requested from the component
     * @return an instance of the capability requested or null if not provided
     */
    public final <T> T getCapability(Class<T> capability) {
        if (ComponentInitializer.class.isAssignableFrom(capability)) {
            if (initializer == null) {
                initializer = new Initializer();
            }
            return capability.cast(initializer);
        } else if (Updatable.class.isAssignableFrom(capability)) {
            if (initializer == null) {
                initializer = new Initializer();
            }
            return capability.cast(initializer);
        }
       
        return handleGetCapability(capability);
    }

    /**
     * Provides subclasses a chance to inject capabilities. The default
     * implementation does nothing by returning null. There are no requirements
     * on component providers to add capabilities.
     * 
     * @param <T>
     *            Class of the capability
     * @param capability
     *            requested from the component
     * @see AbstractComponent#getCapability(Class)
     * @return an instance of the capability requested for null if not available
     */
    protected <T> T handleGetCapability(Class<T> capability) {
        return null;
    }

    /**
     * Persist the component data (including the model). 
     */
    public void save() {
        DaoStrategy<AbstractComponent, ? extends DaoObject> daoStrategy = getDaoStrategy();

        if (daoStrategy != null) {
            if (PlatformAccess.getPlatform().getLockManager().isLocked(getComponentId())) {
                daoStrategy.saveObject();
            }
        }
        if (getCapability(ComponentInitializer.class).isInitialized()) {
            this.refreshViewManifestations();
        }
    }

    /**
     * Persist the component data (including the model). After persistence,
     * notifies only the manifestations of the specified view. 
     * @param viewInfo the <code>ViewInfo</code>
     */
    public final void save(ViewInfo viewInfo) {
        // Save properties to the database
        DaoStrategy<AbstractComponent, ? extends DaoObject> daoStrategy = getDaoStrategy();

        if (daoStrategy != null) {
            if (PlatformAccess.getPlatform().getLockManager().isLocked(getComponentId())) {
                daoStrategy.saveObject();
            }
        }
        
        // Refresh all manifestations of this view.
        refreshManifestations(viewInfo);
        
    }    
    
    /**
     * Mark this component as deleted in the persistence.
     * 
     * @return false by default
     */
    public boolean delete() {
        if (deleteComponent()) {
            DaoStrategy<AbstractComponent, ? extends DaoObject> daoStrategy = getDaoStrategy();
            if (daoStrategy != null) {
                daoStrategy.deleteObject(this);
            }
            GlobalComponentRegistry.removeComponent(this.getId());
            return true;
        }
        return false;
    }

    /**
     * Add a pending tag.
     * @param tagId the tag id to be tagged
     */
    public synchronized void pendingTag(String tagId) {
        this.pendingTags.add(tagId);
    }
    
    /**
     * Get the pending tags.
     * @return the set of pending tags to be tagged
     */
    public synchronized Set<String> getPendingTags() {
        return this.pendingTags;
    }
    
    /**
     * Clear the pending tags.
     */
    public synchronized void clearPendingTags() {
        this.pendingTags.clear();
    }

    @Override
    public AbstractComponent clone() {
        try {
            Class<? extends AbstractComponent> componentClassType = this.getClass();
            
            String newID = IdGenerator.nextComponentId();
            AbstractComponent clonedComponent = componentClassType.newInstance();
            clonedComponent.setId(newID);
            
            ModelStatePersistence persistence = getCapability(ModelStatePersistence.class);
            if (persistence != null) {
                String modelState = persistence.getModelState();
                ModelStatePersistence clonedPersistence = clonedComponent.getCapability(ModelStatePersistence.class);
                assert clonedPersistence != null;
                clonedPersistence.setModelState(modelState);
                
            }

            for (AbstractComponent child : getComponents()) {
                clonedComponent.addComponent(child);
            }
            
            for (Entry<String, ExtendedProperties> e : viewRoleProperties.entrySet()) {
                clonedComponent.viewRoleProperties.put(e.getKey(), e.getValue().clone());
            }

            clonedComponent.pendingTags.addAll(pendingTags);

            clonedComponent.shared = false;
            clonedComponent.owner = owner;
            clonedComponent.masterComponent = masterComponent;
            clonedComponent.displayName = displayName;
            clonedComponent.externalKey = externalKey;
            clonedComponent.version = version;

            DaoStrategy<AbstractComponent, ? extends DaoObject> daoStrategy = getDaoStrategy();
            if (daoStrategy != null) {
                Platform platform = PlatformAccess.getPlatform();
                platform.getPersistenceService().setComponentDaoStrategy(clonedComponent);
            }

            ComponentInitializer clonedCapability = clonedComponent.getCapability(ComponentInitializer.class);
            
            clonedCapability.initialize();
            return clonedComponent;
        } catch (SecurityException e) {
            throw new MCTRuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new MCTRuntimeException(e);
        } catch (InstantiationException e) {
            throw new MCTRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new MCTRuntimeException(e);
        } 
    }
   
    private synchronized void setViewProperties(Map<String, ExtendedProperties> properties) {
        this.viewRoleProperties = properties;
    }

    private synchronized void setViewProperty(String viewType, ExtendedProperties properties) {
        this.viewRoleProperties.put(viewType, properties);
    }

    private synchronized ExtendedProperties getViewProperties(String viewType) {
        if (viewRoleProperties == null) { return null; }
        return this.viewRoleProperties.get(viewType);
    }


    /**
     * An API to return the context of a meta data. It should be overridden by subclass if a meta data of a component
     * is expected.
     * @return the context of a meta data.
     */
    protected String getMetaDataContext() {
        return "";
    }
    
    /**
     * Indicates whether this <code>AbstractComponent</code> is a <em>twiddled</em> component.
     * @return true if this <code>AbstractComponent</code> is a <em>twiddled</em> component; false, otherwise.
     */
    public boolean isTwiddledComponent() {
        return masterComponent != null && masterComponent.getId() != getId() && !DaoStrategyFactory.isAlternativeSaveStrategyInUse(this);
    }
    
    /**
     * Sets the version of this component.
     * @param version the version of this component.
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * Returns the version of the component.
     * @return the version of the component
     */
    public int getVersion() {
        return version;
    }
    
    /**
     * Returns the icon image for this component.
     * @return an icon image
     */
    public final ImageIcon getIcon() {
        Collection<ExtendedComponentTypeInfo> infos = ExternalComponentRegistryImpl.getInstance().getComponentInfos();
        for (ExtendedComponentTypeInfo info : infos) {
            if (getClass() == info.getComponentClass()) {
                return info.getIcon();
            }
        }
        return MCTIcons.getComponent();
    }
    
    /**
     * Returns the icon based on the component type.
     * @param className of the component type
     * @return an image icon
     */
    public static ImageIcon getIconForComponentType(String className) {
        Collection<ExtendedComponentTypeInfo> infos = ExternalComponentRegistryImpl.getInstance().getComponentInfos();
        for (ExtendedComponentTypeInfo info : infos) {
            if (className.equals(info.getComponentClass().getName())) {
                return info.getIcon();
            }
        }
        return MCTIcons.getComponent();        
    }
    
    /**
     * Resets component properties.
     * @param txn the transaction to be performed atomically
     */
    public synchronized void resetComponentProperties(ResetPropertiesTransaction txn) {
        txn.perform();
    }
    
    /**
     * Adds a view manifestation that should be alerted to changes in this component.
     * @param viewManifestation to notify when changes occur.
     */
    public void addViewManifestation(View viewManifestation) {
        viewManifestations.add(viewManifestation);
    }
    
    /**
     * Gets all the currently monitored manifestations. 
     * @return all the current views of this manifestation
     */
    public Set<View> getAllViewManifestations() {
        return new HashSet<View>(viewManifestations);
    }
    
    private final class Initializer implements ComponentInitializer, Updatable {
        private boolean initialized;
        
        @Override
        public void setId(String id) {
            AbstractComponent.this.id = id;
        }

        @Override
        public void setOwner(String owner) {
            AbstractComponent.this.owner = owner;
        }
        
        @Override
        public void setViewRoleProperties(Map<String, ExtendedProperties> properties) {
            setViewProperties(properties);
        }

        @Override
        public void setViewRoleProperty(String viewRoleType, ExtendedProperties properties) {
            setViewProperty(viewRoleType, properties);
        }

        @Override
        public ExtendedProperties getViewRoleProperties(String viewType) {
            return getViewProperties(viewType);
        }

        @Override
        public void setCreationDate(Date creationDate) {
            AbstractComponent.this.creationDate = creationDate;
        }
        
        @Override
        public void setCreator(String creator) {
           AbstractComponent.this.creator = creator;
        }
        
        @Override
        public void addViewRoleProperties(String viewRoleType, ExtendedProperties properties) {
            addViewProperty(viewRoleType, properties);
        }
        
        @Override
        public void initialize() {
            checkInitialized();
            AbstractComponent.this.initialize();
        }

        public void setInitialized() {
            checkInitialized();
            initialized = true;
        }

        private void checkInitialized() {
            if (isInitialized()) {
                throw new IllegalStateException("component already initialized");
            }
        }

        @Override
        public boolean isInitialized() {
            return initialized;
        }

        @Override
        public Map<String, ExtendedProperties> getAllViewRoleProperties() {
            return Collections.unmodifiableMap(viewRoleProperties);
        }

        @Override
        public void setVersion(int version) {
            AbstractComponent.this.version = version;
        }

        @Override
        public void setBaseDisplayedName(String baseDisaplyedName) {
            AbstractComponent.this.displayName = baseDisaplyedName;
        }

        @Override
        public void setShared(boolean isShared) {
            shared = isShared;
        }

        @Override
        public void addReferences(List<AbstractComponent> references) {
            for (AbstractComponent c : references) {
                processAddDelegateComponent(-1, c);
            }
        }

        @Override
        public void removeReferences(List<AbstractComponent> references) {
            for (AbstractComponent c : references) {
                removeComponent(c);
            }
        }

        @Override
        public void removalAllAssociatedComponents() {
            clearComponents();
        }

        @Override
        public void setComponentReferences(Collection<AbstractComponent> componentReferences) {
            AbstractComponent.this.clearComponents();
            for (AbstractComponent component : componentReferences) {
                AbstractComponent.this.addComponent(component);
            }
        }

        @Override
        public void setMasterComponent(AbstractComponent masterComponent) {
            AbstractComponent.this.masterComponent = masterComponent;
        }

    }    
    
    /**
     * Loads the component from the database. 
     */
    public final synchronized void load() {
        DaoStrategy<AbstractComponent, ? extends DaoObject> daoStrategy = getDaoStrategy();
        daoStrategy.load();
    }
    
    /**
     * Gets the strategy object used for persistence of the component.
     * 
     * @return the component strategy object
     */
    public final DaoStrategy<AbstractComponent, ? extends DaoObject> getDaoStrategy() {
        ComponentInitializer initializer = this.getCapability(ComponentInitializer.class);
        if (initializer.isInitialized() && daoStrategy == null && isShared()) {
            Platform platform = PlatformAccess.getPlatform();
            platform.getPersistenceService().setComponentDaoStrategy(this);
        }
        return daoStrategy;
    }

    /**
     * Sets the strategy object used for component persistence.
     * 
     * @param strategy the persistence strategy object
     */
    public final void setDaoStrategy(DaoStrategy<AbstractComponent, ? extends DaoObject> strategy) {
        daoStrategy = strategy;
    }
    
    /**
     * Saves <code>components</code> to the database. This method only persists the components without
     * sharing or updating the GUI.
     * @param index of which the components are added
     * @param components to be saved to the database
     */
    public void saveComponentsToDatabase(int index, Collection<AbstractComponent> components) {
        addDelegateComponentsBeforeSave(components);
        getDaoStrategy().saveObjects(index, components);
    }
    
    /**
     * Defines a transaction to reset component properties. 
     */
    public interface ResetPropertiesTransaction {
        /**
         * Defines the steps to reset certain component properties.
         */
        public void perform();
    }
        
    /**
     * Get a list of all components to which this component refers.
     * Generally, a referenced component may be thought of as a child 
     * of the referencing component, but the precise interpretation of the 
     * relationship may vary among component types and view types.
     * @return a list of all referenced components
     */
    public synchronized List<AbstractComponent> getComponents() {
        ensureLoaded();
        return referencedComponents;
    }

    /**
     * Check to see if this component references any other components
     * Generally, a referenced component may be thought of as a child 
     * of the referencing component, but the precise interpretation of the 
     * relationship may vary among component types and view types.
     * @return true if this component references others; false if not
     */
    public synchronized boolean hasComponentReferences() {
        return !referencedComponents.isEmpty();
    }
    
    /**
     * Returns the component by the specified id.
     * @param id of the component to find
     * @return component with the given id or null if no component currently has the id.
     */
    public static AbstractComponent getComponentById(String id) {
        Platform platform = PlatformAccess.getPlatform();
        AbstractComponent component = platform.getComponentRegistry().getComponent(id);
        if (component != null) {
            return component;
        }
        return platform.getPersistenceService().loadComponent(id);    
    }
    
    private synchronized void ensureLoaded() {
        if (referencedComponents.size() == 1 &&
            referencedComponents.get(0) == NULL_COMPONENT) {
            clearComponents();
            load();
        }
    }
    
    /**
     * Add a reference to the specified component.
     * Generally, a referenced component may be thought of as a child 
     * of the referencing component, but the precise interpretation of the 
     * relationship may vary among component types and view types.
     * @param component the component to which to refer
     */
    private synchronized void addComponent(AbstractComponent component) {
        ensureLoaded();
        if (referencedComponents == Collections.EMPTY_LIST) {
            referencedComponents = new LinkedList<AbstractComponent>();
        }
        referencedComponents.add(component);
        component.addReferencingComponent(this);
    }
    
    /**
     * Add a reference to the specified component, at the specified index.
     * Generally, a referenced component may be thought of as a child 
     * of the referencing component, but the precise interpretation of the 
     * relationship may vary among component types and view types.
     * @param index the index at which to reference
     * @param component the component to which to refer
     */
    private synchronized void addComponentAt(int index, AbstractComponent component) {
        ensureLoaded();
        if (referencedComponents == Collections.EMPTY_LIST) {
            referencedComponents = new LinkedList<AbstractComponent>();
        }
        referencedComponents.add(index, component);
        component.addReferencingComponent(this);
    }
    
    /**
     * Clear all component references for this component.
     * Generally, a referenced component may be thought of as a child 
     * of the referencing component, but the precise interpretation of the 
     * relationship may vary among component types and view types.
     */
    private synchronized void clearComponents() {
        referencedComponents = Collections.<AbstractComponent> emptyList();
    }
    
    
    /**
     * Remove a reference to the specified component
     * Generally, a referenced component may be thought of as a child 
     * of the referencing component, but the precise interpretation of the 
     * relationship may vary among component types and view types.
     * @param component the component to dereference
     */
    private synchronized void removeComponent(AbstractComponent component) {
        ensureLoaded();
        referencedComponents.remove(component);
        component.removeReferencingComponent(this);
    }
    
  
    private synchronized void addReferencingComponent(AbstractComponent component) {
        if (referencingComponentIds == Collections.EMPTY_SET ||
            referencingComponentIds == null) {
            referencingComponentIds = new HashSet<String>();
        }
        referencingComponentIds.add(component.getComponentId());
    }
    
    private synchronized void removeReferencingComponent(AbstractComponent component) {
        if (referencingComponentIds == Collections.EMPTY_SET ||
            referencingComponentIds == null) {
            referencingComponentIds = new HashSet<String>();
        }
        referencingComponentIds.remove(component.getComponentId());
    }
    
    /** 
     * Returns a description of nuclear data that is inspectable. 
     * This ordered list of fields is rendered in MCT Platform's InfoView.
     * 
     * @return ordered list of property descriptors
     */
    public List<PropertyDescriptor> getFieldDescriptors() {
        return null;
    }

}
