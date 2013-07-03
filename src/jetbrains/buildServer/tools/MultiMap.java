/*
 * Copyright 2000-2011 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.tools;

import java.util.*;

/**
 * @author Dmitry Avdeev
 */
public class MultiMap<K, V> {

  private final Map<K, List<V>> myMap;

  public MultiMap() {
    myMap = createMap();
  }

  protected Map<K, List<V>> createMap() {
    return new java.util.TreeMap<K, List<V>>();
  }

  protected List<V> createCollection() {
    return new ArrayList<V>();
  }

  public void putValue(K key, V value) {
    List<V> list = myMap.get(key);
    if (list == null) {
      list = createCollection();
      myMap.put(key, list);
    }
    list.add(value);
  }

  public boolean isEmpty() {
    for(Collection<V> valueList: myMap.values()) {
      if (!valueList.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  public boolean containsScalarValue(V value) {
    for(Collection<V> valueList: myMap.values()) {
      if (valueList.contains(value)) {
        return true;
      }
    }
    return false;
  }

  public List<V> get(final K key) {
    final List<V> collection = myMap.get(key);
    return collection == null ? Collections.<V>emptyList() : collection;
  }

  public void removeValue(final V value) {
    for (List<V> vs : myMap.values()) {
      vs.remove(value);
    }
  }

  public Set<K> keySet() {
    return myMap.keySet();
  }

  public int size() {
    return myMap.size();
  }

  public int getValuesSize() {
    int i = 0;
    for (Map.Entry<K, List<V>> e : myMap.entrySet()) {
      i += e.getValue().size();
    }
    return i;
  }

  public void put(final K key, final List<V> values) {
    myMap.put(key, values);
  }

  public void remove(final K key) {
    myMap.remove(key);
  }

  public void remove(final K key, final V value) {
    get(key).remove(value);
  }

  public boolean containsKey(final K elementPath) {
    return myMap.containsKey(elementPath);
  }

  public Set<Map.Entry<K, List<V>>> entrySet() {
    return myMap.entrySet();
  }

  public Collection<List<V>> values() {
    return myMap.values();
  }

  public void clear() {
    myMap.clear();
  }
}
