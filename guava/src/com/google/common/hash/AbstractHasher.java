/*
 * Copyright (C) 2011 The Guava Authors
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

package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import org.checkerframework.checker.index.qual.LTLengthOf;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.common.value.qual.MinLen;

/**
 * An abstract implementation of {@link Hasher}, which only requires subtypes to implement {@link
 * #putByte}. Subtypes may provide more efficient implementations, however.
 *
 * @author Dimitris Andreou
 */
@CanIgnoreReturnValue
@ElementTypesAreNonnullByDefault
abstract class AbstractHasher implements Hasher {
  @Override
  public final Hasher putBoolean(boolean b) {
    return putByte(b ? (byte) 1 : (byte) 0);
  }

  @Override
  public final Hasher putDouble(double d) {
    return putLong(Double.doubleToRawLongBits(d));
  }

  @Override
  public final Hasher putFloat(float f) {
    return putInt(Float.floatToRawIntBits(f));
  }

  @Override
  public Hasher putUnencodedChars(CharSequence charSequence) {
    for (int i = 0, len = charSequence.length(); i < len; i++) {
      putChar(charSequence.charAt(i));
    }
    return this;
  }

  @Override
  @SuppressWarnings("value:argument") // Strings#getBytes() should be annotated as @PolyValue
  public Hasher putString(@MinLen(1) CharSequence charSequence, Charset charset) {
    return putBytes(charSequence.toString().getBytes(charset));
  }

  @Override
  public Hasher putBytes(byte @MinLen(1)[] bytes) {
    return putBytes(bytes, 0, bytes.length);
  }

  @Override
  public Hasher putBytes(byte[] bytes, @NonNegative @LTLengthOf(value = "#1", offset = "#3 - 1") int off, @NonNegative @LTLengthOf(value = "#1",offset = "#2 - 1") int len) {
    Preconditions.checkPositionIndexes(off, off + len, bytes.length);
    for (int i = 0; i < len; i++) {
      putByte(bytes[off + i]);
    }
    return this;
  }

  @Override
  public Hasher putBytes(ByteBuffer b) {
    if (b.hasArray()) {
      putBytes(b.array(), b.arrayOffset() + b.position(), b.remaining());
      Java8Compatibility.position(b, b.limit());
    } else {
      for (int remaining = b.remaining(); remaining > 0; remaining--) {
        putByte(b.get());
      }
    }
    return this;
  }

  @Override
  public Hasher putShort(short s) {
    putByte((byte) s);
    putByte((byte) (s >>> 8));
    return this;
  }

  @Override
  public Hasher putInt(int i) {
    putByte((byte) i);
    putByte((byte) (i >>> 8));
    putByte((byte) (i >>> 16));
    putByte((byte) (i >>> 24));
    return this;
  }

  @Override
  @SuppressWarnings("signedness:shift.unsigned")
  public Hasher putLong(long l) {
    for (int i = 0; i < 64; i += 8) {
      putByte((byte) (l >>> i));
    }
    return this;
  }

  @Override
  @SuppressWarnings("signedness:argument")
  public Hasher putChar(char c) {
    putByte((byte) c);
    putByte((byte) (c >>> 8));
    return this;
  }

  @Override
  public <T extends @Nullable Object> Hasher putObject(
      @ParametricNullness T instance, Funnel<? super T> funnel) {
    funnel.funnel(instance, this);
    return this;
  }
}
