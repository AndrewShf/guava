/*
 * Copyright (C) 2012 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.common.reflect;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.UnknownSignedness;

/**
 * Represents a method or constructor parameter.
 *
 * <p><b>Note:</b> Since Java 8 introduced {@link java.lang.reflect.Parameter} to represent method
 * and constructor parameters, this class is no longer necessary. We intend to deprecate it in a
 * future version.
 *
 * @author Ben Yu
 * @since 14.0
 */
@Beta
@ElementTypesAreNonnullByDefault
public final class Parameter implements AnnotatedElement {

  private final Invokable<?, ?> declaration;
  private final int position;
  private final TypeToken<?> type;
  private final ImmutableList<Annotation> annotations;
  private final AnnotatedType annotatedType;

  Parameter(
      Invokable<?, ?> declaration,
      int position,
      TypeToken<?> type,
      Annotation[] annotations,
      AnnotatedType annotatedType) {
    this.declaration = declaration;
    this.position = position;
    this.type = type;
    this.annotations = ImmutableList.copyOf(annotations);
    this.annotatedType = annotatedType;
  }

  /** Returns the type of the parameter. */
  public TypeToken<?> getType() {
    return type;
  }

  /** Returns the {@link Invokable} that declares this parameter. */
  public Invokable<?, ?> getDeclaringInvokable() {
    return declaration;
  }

  @Override
  public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
    return getAnnotation(annotationType) != null;
  }

  @Override
  @CheckForNull
  public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
    checkNotNull(annotationType);
    for (Annotation annotation : annotations) {
      if (annotationType.isInstance(annotation)) {
        return annotationType.cast(annotation);
      }
    }
    return null;
  }

  @Override
  public Annotation[] getAnnotations() {
    return getDeclaredAnnotations();
  }

  /** @since 18.0 */
  // @Override on JDK8
  @Override
  public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
    return getDeclaredAnnotationsByType(annotationType);
  }

  /** @since 18.0 */
  // @Override on JDK8
  @Override
  public Annotation[] getDeclaredAnnotations() {
    return annotations.toArray(new Annotation[0]);
  }

  /** @since 18.0 */
  // @Override on JDK8
  @Override
  @CheckForNull
  public <A extends Annotation> A getDeclaredAnnotation(Class<A> annotationType) {
    checkNotNull(annotationType);
    return FluentIterable.from(annotations).filter(annotationType).first().orNull();
  }

  /** @since 18.0 */
  // @Override on JDK8
  @Override
  public <A extends Annotation> A[] getDeclaredAnnotationsByType(Class<A> annotationType) {
    @Nullable
    A[] result = FluentIterable.from(annotations).filter(annotationType).toArray(annotationType);
    @SuppressWarnings("nullness") // safe because the input list contains no nulls
    A[] cast = (A[]) result;
    return cast;
  }

  /** @since 25.1 */
  // @Override on JDK8
  public AnnotatedType getAnnotatedType() {
    return annotatedType;
  }

  @Override
  public boolean equals(@CheckForNull Object obj) {
    if (obj instanceof Parameter) {
      Parameter that = (Parameter) obj;
      return position == that.position && declaration.equals(that.declaration);
    }
    return false;
  }

  @Override
  public int hashCode(@UnknownSignedness Parameter this) {
    return position;
  }

  @Override
  public String toString() {
    return type + " arg" + position;
  }
}
