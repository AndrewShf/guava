/*
 * Copyright (C) 2007 The Guava Authors
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

package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.List;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.UnknownSignedness;
import org.checkerframework.dataflow.qual.Pure;
import org.checkerframework.framework.qual.AnnotatedFor;

/** An ordering that compares objects according to a given order. */
@AnnotatedFor({"nullness"})
@GwtCompatible(serializable = true)
@ElementTypesAreNonnullByDefault
final class ExplicitOrdering<T> extends Ordering<T> implements Serializable {
  final ImmutableMap<T, Integer> rankMap;

  ExplicitOrdering(List<T> valuesInOrder) {
    this(Maps.indexMap(valuesInOrder));
  }

  ExplicitOrdering(ImmutableMap<T, Integer> rankMap) {
    this.rankMap = rankMap;
  }

  @Pure
  @Override
  public int compare(T left, T right) {
    return rank(left) - rank(right); // safe because both are nonnegative
  }

  private int rank(T value) {
    Integer rank = rankMap.get(value);
    if (rank == null) {
      throw new IncomparableValueException(value);
    }
    return rank;
  }

  @Pure
  @Override
  public boolean equals(@CheckForNull Object object) {
    if (object instanceof ExplicitOrdering) {
      ExplicitOrdering<?> that = (ExplicitOrdering<?>) object;
      return this.rankMap.equals(that.rankMap);
    }
    return false;
  }

  @Pure
  @Override
  public int hashCode(@UnknownSignedness ExplicitOrdering<T> this) {
    return rankMap.hashCode();
  }

  @Pure
  @Override
  public String toString() {
    return "Ordering.explicit(" + rankMap.keySet() + ")";
  }

  private static final long serialVersionUID = 0;
}
