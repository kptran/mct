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
package gov.nasa.arc.mct.persistence;

import gov.nasa.arc.mct.persistence.strategy.DaoObject;
import gov.nasa.arc.mct.persistmgr.PersistenceBroker;
import gov.nasa.arc.mct.persistmgr.callback.PersistenceCompletedCallbackHandler;
import gov.nasa.arc.mct.test.exception.MCTTestRuntimeException;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.type.Type;
import org.mockito.MockitoAnnotations;

/**
 * This is a mock object for PersistenceBroker. The assumption for using this
 * class is that the generic type <T> for the following methods needs to be a
 * subclass of gov.nasa.arc.mct.persistence.strategy.DaoObject.
 * 
 * @author asi
 * 
 */
public class MockPersistenceBroker implements PersistenceBroker {
	private Map<Class<? extends DaoObject>, List<DaoObject>> data = new HashMap<Class<? extends DaoObject>, List<DaoObject>>();

	private final static MockPersistenceBroker instance = new MockPersistenceBroker();

	public static MockPersistenceBroker getInstance() {
		return instance;
	}

	private MockPersistenceBroker() {
		MockitoAnnotations.initMocks(this);
	}

	@Override
	public <T> boolean delete(String sessionId, T obj) {
		List<DaoObject> dataList = data.get(obj.getClass());
		return dataList.remove(obj);
	}

	@Override
	public <T> T lazilyLoad(String sessionId, Class<T> type, Serializable id) {
		return loadById(sessionId, type, id);
	}

	@Override
	public List<Object> queryNativeSQL(String sessionId, String nativeSQL,
			String[] parameters) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void executeNativeSQL(String sessionId, String nativeSQL,
			String[] parameters) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void lazilyLoadCompleted(String sessionId) {
		// do nothing
	}
	
	@Override
	public void lazilyLoadCompleted(String sessionId, boolean retrySave) {
		// do nothing
	}

	@Override
	public <T> List<T> loadAll(String sessionId, Class<T> type) {
		return loadAll(type);
	}

	@Override
	public <T> List<T> loadAll(String sessionId, Class<T> type,
			String[] propertyNames, Object[] propertyValues) {
		return loadAll(type, propertyNames, propertyValues, null, null, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> loadAll(Class<T> type) {
		List<DaoObject> dataList = data.get(type);
		return (List<T>) dataList;
	}

	private boolean isQualifyEq(DaoObject daoObject, String[] propertyNames,
			Object[] propertyValues) {
		for (int i = 0; i < propertyNames.length; i++) {
			String getFieldMethodName = "get"
					+ propertyNames[i].substring(0, 1).toUpperCase()
					+ propertyNames[i].substring(1);
			try {
				Method fieldGetter = daoObject.getClass().getMethod(
						getFieldMethodName, new Class[] {});
				Object fieldValue = fieldGetter.invoke(daoObject,
						new Object[] {});
				if (propertyValues[i] == null) {
					if (fieldValue != null) {
						return false;
					}
				} else if (!fieldValue.equals(propertyValues[i])) {
					return false;
				}
			} catch (SecurityException e) {
				throw new MCTTestRuntimeException(e);
			} catch (NoSuchMethodException e) {
				throw new MCTTestRuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new MCTTestRuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new MCTTestRuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new MCTTestRuntimeException(e);
			}
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> loadAll(Class<T> type, String[] propertyNames,
			Object[] propertyValues, String filterName, String[] filterParams,
			Object[] filterValues) {
		List<DaoObject> dataList = data.get(type);
		List<DaoObject> returnDataList = new ArrayList<DaoObject>();

		for (DaoObject daoObject : dataList) {
			if (isQualifyEq(daoObject, propertyNames, propertyValues)) {
				returnDataList.add(daoObject);
			}
		}

		return (List<T>) returnDataList;
	}

	@Override
	public <T> List<T> loadAllByLeftOuterJoin(String sessionId, Class<T> type,
			String[] joinPropertyNames, String[] joinAliases,
			String[] eqPropertyNames, Object[] eqPropertyValues,
			String filterName, String[] filterPropertyNames,
			Object[] filterPropertyValues) {

		// For now, just treat it as a loadAll()
		return loadAll(type, eqPropertyNames, eqPropertyValues, filterName, filterPropertyNames, filterPropertyValues);
	}
	
	@Override
	public List<?> loadAllByNativeSQL(String sessionId, String nativeSQL, String[] scalarAttribute, Type[] scalarType) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void executeNativeSQL(String sessionId, String nativeSQL,
			String[] scalarAttribute, Type[] scalarType) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public <T> List<T> loadByIdsEagerly(String sessionId, Class<T> type,
			String idPropertyName, Collection<Serializable> ids,
			String[] eagerlyFetchedFields) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public <T> List<T> loadAllOrderedBy(String sessionId, Class<T> type,
			String orderedByProperty, String[] eqPropertyNames,
			Object[] eqPropertyValues, String[] neqPropertyNames,
			Object[] neqPropertyValues) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<T> loadAllEagerly(String sessionId, Class<T> type,
			String[] propertyNames, Object[] propertyValues,
			String[] eagerlyFetchedFields) {
		// Treat it as a loadAll() because we have no lazy loading in mock.
		return loadAll(type, propertyNames, propertyValues, null, null, null);
	}

	private Object getProperty(Object obj, String propertyName) {
		try {
			Field field = obj.getClass().getDeclaredField(propertyName);
			return field.get(obj);
		} catch (SecurityException e) {
			return new MCTTestRuntimeException(e);
		} catch (NoSuchFieldException e) {
			return new MCTTestRuntimeException(e);
		} catch (IllegalArgumentException e) {
			return new MCTTestRuntimeException(e);
		} catch (IllegalAccessException e) {
			return new MCTTestRuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private int compare(Object first, Object second, String orderedByProperty) {
		Object valueFirst = getProperty(first, orderedByProperty);
		Object valueSecond = getProperty(second, orderedByProperty);

		return ((Comparable) valueFirst).compareTo(valueSecond);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> loadAllOrderedBy(Class<T> type, String orderedByProperty) {
		List<T> dataList = loadAll(type);
		if (dataList == null) {
			return Collections.emptyList();
		}

		Object[] dataArray = dataList.toArray();
		for (int i = 0; i < dataArray.length - 1; i++) {
			for (int j = i + 1; i < dataArray.length; j++) {
				if (compare(dataArray[i], dataArray[j], orderedByProperty) > 0) {
					Object t = dataArray[i];
					dataArray[i] = dataArray[j];
					dataArray[j] = t;
				}
			}
		}

		return Arrays.asList((T[]) dataArray);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> loadAllOrderedByDesc(Class<T> type,
			String orderedByProperty) {
		List<T> dataList = loadAll(type);
		if (dataList == null) {
			return Collections.emptyList();
		}

		Object[] dataArray = dataList.toArray();
		for (int i = 0; i < dataArray.length - 1; i++) {
			for (int j = i + 1; i < dataArray.length; j++) {
				if (compare(dataArray[i], dataArray[j], orderedByProperty) < 0) {
					Object t = dataArray[i];
					dataArray[i] = dataArray[j];
					dataArray[j] = t;
				}
			}
		}

		return Arrays.asList((T[]) dataArray);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<Object> loadByHQL(Class<T> type, String alias,
			String[] projectedPropertyNames, String[] propertyNames,
			Object[] propertyValues, String filterName, String[] filterParams,
			Object[] filterValues) {
		return (List<Object>) loadAll(type, propertyNames, propertyValues, null, null, null);
	}

	@Override
	public <T> List<T> loadChildren(String sessionId, T parent, Class<T> type,
			String propertyName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> boolean save(String sessionId, T obj,
			PersistenceCompletedCallbackHandler handler) {
		try {
			List<DaoObject> dataList = data.get(obj.getClass());
			if (dataList == null) {
				dataList = new ArrayList<DaoObject>();
				data.put(((DaoObject) obj).getClass(), dataList);
			}
			dataList.remove(obj);
			dataList.add((DaoObject) obj);
		} finally {
			if (handler != null) {
				handler.saveCompleted();
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> void saveBatch(String sessionId, List<T> daoObjects,
			PersistenceCompletedCallbackHandler handler) {
		try {
			for (T obj : daoObjects) {
				List<DaoObject> dataList = data.get(obj.getClass());
				if (dataList == null) {
					dataList = new ArrayList<DaoObject>();
					data.put(((DaoObject) obj).getClass(), dataList);

					if (DaoObject.class.isAssignableFrom(obj.getClass()
							.getSuperclass())) {
						Class<? extends DaoObject> clazz = (Class<DaoObject>) obj
								.getClass().getSuperclass();
						data.put(clazz, dataList);
					}
				}
				dataList.remove(obj);
				dataList.add((DaoObject) obj);
			}
		} finally {
			if (handler != null) {
				handler.saveCompleted();
			}
		}

	}

	public void reset() {
		data.clear();
	}
	
	@Override
	public <T> T loadByIdEagerly(String sessionId, Class<T> type,
			Serializable id, String[] eagerlyFetchedFields) {
		return loadById(sessionId, type, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T loadById(String sessionId, Class<T> type, Serializable id) {
		List<DaoObject> dataList = data.get(type);
		if (dataList != null) {
			for (DaoObject daoObject : dataList) {
				if (daoObject.getId().equals(id)) {
					return (T) daoObject;
				}
			}
		}
		return null;
	}

	@Override
	public <T extends DaoObject> T lazilyLoad(String sessionId, T daoObject) {
		return daoObject;
	}

	@Override
	public void closeSession(String sessionId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startSession(String sessionId) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> T lazilyLoad(Class<T> type, Serializable id) {
		return loadById(id.toString(), type, id);
	}

	@Override
	public <T> boolean delete(T obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> boolean save(T obj, PersistenceCompletedCallbackHandler handler) {
		return save(null, obj, handler);
	}

	@Override
	public <T extends DaoObject> boolean saveDao(String sessionId, T obj,
			PersistenceCompletedCallbackHandler handler) {
		return save(null, obj, handler);
	}

	@Override
	public <T extends DaoObject> void saveDaoBatch(String sessionId,
			List<T> daoObjects, PersistenceCompletedCallbackHandler handler) {
		saveBatch(null, daoObjects, handler);

	}

	@Override
	public <T> void forceLoad(String sessionId, T obj) {
		// TODO Auto-generated method stub

	}

	public <T> void persist(String sessionId, T daoObject) {
	}

	public <T> void attachToSession(String sessionId, T obj) {
	}

	@Override
	public void abortSession(String sessionId) {
	}
	
	@Override
	public boolean isReadOnly() {
		return false;
	}
}
