package jetbrains.buildServer.tools;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created 03.07.13 20:33
 *
 * @author Eugene Petrenko (eugene.petrenko@jetbrains.com)
 */
public abstract class LazyMap<K extends Comparable<K>, V> {
  private final Map<K, V> myMap;

  /**
   * @since 7.0
   */
  protected LazyMap() {
    this(new TreeMap<K, V>());
  }

  /**
   * @param map to store values
   * @since 8.0
   */
  protected LazyMap(@NotNull final Map<K, V> map) {
    myMap = map;
  }

  @NotNull
  protected abstract V computeValue(@NotNull final K k);

  @NotNull
  public V get(@NotNull final K k) {
    V v = myMap.get(k);
    if (v == null) {
      v = computeValue(k);
      myMap.put(k, v);
    }
    return v;
  }

  /**
   * @return map of all currently created values
   * @since 8.0
   */
  @NotNull
  public Map<K,V> getCreatedValues() {
    return myMap;
  }

  /**
   * @return allocated keys
   * @since 8.0
   */
  @NotNull
  public Set<K> keySet() {
    return myMap.keySet();
  }

  @NotNull
  public Collection<V> values() {
    return myMap.values();
  }

  public boolean isEmpty() {
    return myMap.isEmpty();
  }
}
