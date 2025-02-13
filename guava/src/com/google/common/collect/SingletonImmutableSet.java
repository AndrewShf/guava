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
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.concurrent.LazyInit;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.UnknownSignedness;
import org.checkerframework.dataflow.qual.Pure;
import org.checkerframework.framework.qual.AnnotatedFor;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Implementation of {@link ImmutableSet} with exactly one element.
 *
 * @author Kevin Bourrillion
 * @author Nick Kralevich
 */
@AnnotatedFor({"nullness"})
@GwtCompatible(serializable = true, emulated = true)
@SuppressWarnings("serial") // uses writeReplace(), not default serialization
@ElementTypesAreNonnullByDefault
final class SingletonImmutableSet<E> extends ImmutableSet<E> {
  // We deliberately avoid caching the asList and hashCode here, to ensure that with
  // compressed oops, a SingletonImmutableSet packs all the way down to the optimal 16 bytes.

  final transient E element;

  SingletonImmutableSet(E element) {
    this.element = Preconditions.checkNotNull(element);
  }

  @Pure
  @Override
  public @NonNegative int size() {
    return 1;
  }

  @Pure
  @Override
  public boolean contains(@CheckForNull @UnknownSignedness Object target) {
    return element.equals(target);
  }

  @Override
  public UnmodifiableIterator<E> iterator() {
    return Iterators.singletonIterator(element);
  }

  @Override
  public ImmutableList<E> asList() {
    return ImmutableList.of(element);
  }

  @Override
  boolean isPartialView() {
    return false;
  }

  @Override
  int copyIntoArray(@Nullable Object[] dst, int offset) {
    dst[offset] = element;
    return offset + 1;
  }

  @Pure
  @Override
  public int hashCode(@UnknownSignedness SingletonImmutableSet<E> this) {
    return element.hashCode();
  }

  @Pure
  @Override
  public String toString() {
    return '[' + element.toString() + ']';
  }

@Pure
public boolean equals(@Nullable @UnknownSignedness Object arg0) { return super.equals(arg0); }
}
