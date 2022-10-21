package com.github.playernguyen.oremaker.utils;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Represent a random module for OreMaker
 *
 * @param <T> class type to pick random
 */
public class RandomPipe<T> {

    private final Pair<Double, T>[] stack;
    private double maxCount;

    @SuppressWarnings("unchecked")
    public RandomPipe(int length, Collection<Pair<Double, T>> collections)
            throws ArrayIndexOutOfBoundsException {

        this.stack = (Pair<Double, T>[]) Array.newInstance(Pair.class, length);
        this.maxCount = 0;

        int i = 0;
        for (Pair<Double, T> object : collections) {
            maxCount += object.getFirst();
            stack[i] = Pair.of(maxCount, object.getSecond());
            i++;
        }

        for (Pair<Double, T> a : stack) {
            System.out.println(a);
        }
    }


    /**
     * Pick a random object with current ratio set.<br/>
     *
     * This algorithm using binary search with pattern a <= x < b to
     * get the exactly random value in <b>O(log(n))</b>.
     *
     * @return an object corresponding to a current range
     */
    public T pickRandom() {

        SplittableRandom r = new SplittableRandom();
        double search = r.nextDouble() * maxCount;

        int left = 0;
        int right = stack.length;

        double leftCompare;
        double rightCompare;

        // O(log(n))
        while (left < right) {
            int curIndex = (left + right) / 2;

            leftCompare = curIndex == 0 ? 0 : stack[curIndex-1].getFirst();
            rightCompare = curIndex == stack.length ? stack.length : stack[curIndex].getFirst();

            if (search >= leftCompare && search < rightCompare) {
                return stack[curIndex].getSecond();
            } else if (search < leftCompare) {
                right = curIndex;
            } else {
                left = curIndex;
            }
        }

        throw new IllegalStateException("Access to non-exist path " + search + " of " + maxCount);
    }

    /**
     * Represent a builder for RandomStack
     * @param <T> an object type of {@link RandomPipe}
     */
    public static class Builder<T> {
        private final List<Pair<Double, T>> pairList = new ArrayList<>();

        public Builder() {
        }

        /**
         * Pipe new object with frequent into a pipeline.
         *
         * @param frequent a frequent the object appears, must be greater than 0.0
         * @param object which object to randomly pick
         * @return a current instance of builder
         * @throws IllegalStateException the frequent is lower than or equal 0.0
         */
        public Builder<T> add(double frequent, T object) {
            if (frequent <= 0.0) {
                throw new IllegalStateException("The ration must be greater than 0.0");
            }

            pairList.add(Pair.of(frequent, object));
            return this;
        }

        public RandomPipe<T> build() {
            return new RandomPipe<>(pairList.size(), pairList);
        }
    }

    public static <T> RandomPipe<T> from(HashMap<T, Double> map) {

        Builder<T> objectBuilder = new Builder<>();
        for (Map.Entry<T, Double> entry : map.entrySet()) {
            objectBuilder.add(entry.getValue(), entry.getKey());
        }

        return objectBuilder.build();
    }

}
