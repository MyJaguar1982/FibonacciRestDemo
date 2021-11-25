package com.emc.test.fibonacci;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jason.Wang
 */
public class AtomicFloat extends Number {
    private static final long serialVersionUID = 4992180751166673712L;

    private final AtomicInteger value;

    public AtomicFloat() {
        this(0.0f);
    }

    public AtomicFloat(float value) {
        this.value = new AtomicInteger(i(value));
    }

    public final float get() {
        return f(value.get());
    }

    public final void set(float newValue) {
        value.set(i(newValue));
    }

    public final void lazySet(float newValue) {
        value.lazySet(i(newValue));
    }

    public final float getAndSet(float newValue) {
        return f(value.getAndSet(i(newValue)));
    }

    public final boolean compareAndSet(float expect, float update) {
        return value.compareAndSet(i(expect), i(update));
    }

    public final boolean weakCompareAndSet(float expect, float update) {
        return value.weakCompareAndSet(i(expect), i(update));
    }

    public final float getAndIncrement() {
        return getAndAdd(1.0f);
    }

    public final float getAndDecrement() {
        return getAndAdd(-1.0f);
    }

    public final float getAndAdd(float delta) {
        for (;;) {
            int icurrent = value.get();
            float current = f(icurrent);
            float next = current + delta;
            int inext = i(next);
            if (value.compareAndSet(icurrent, inext)) {
                return current;
            }
        }
    }

    public final float incrementAndGet() {
        return addAndGet(1.0f);
    }

    public final float decrementAndGet() {
        return addAndGet(-1.0f);
    }

    public final float addAndGet(float delta) {
        for (;;) {
            int icurrent = value.get();
            float current = f(icurrent);
            float next = current + delta;
            int inext = i(next);
            if (value.compareAndSet(icurrent, inext)) {
                return next;
            }
        }
    }

    public String toString() {
        return Float.toString(get());
    }

    @Override
    public int intValue() {
        return (int) get();
    }

    @Override
    public long longValue() {
        return (long) get();
    }

    @Override
    public float floatValue() {
        return get();
    }

    @Override
    public double doubleValue() {
        return get();
    }

    private static final int i(final float f) {
        return Float.floatToIntBits(f);
    }

    private static final float f(final int i) {
        return Float.intBitsToFloat(i);
    }

}
