package com.github.playernguyen.oremaker;

import com.github.playernguyen.oremaker.utils.RandomPipe;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class RandomPipeTest {
    @Test
    public void initAndProbCheck() {
        RandomPipe<String> build = new RandomPipe.Builder<String>()
                .add(10, "ab")
                .add(1, "bc")
                .add(1, "de")
                .add(1, "oo")
                .build();

        int iterations = (int) 10e4;
        double acceptanceProbability = 1.0;
        Map<String, Integer> k = new HashMap<>();
        for (int i = 0; i < iterations; i++) {
            String s = build.pickRandom();
            if (!k.containsKey(s)) k.put(s, 1);
            else k.replace(s, k.get(s) + 1);
        }
        for (Map.Entry<String, Integer> entry : k.entrySet()) {
            double percentage = (double) entry.getValue() / iterations * 100.0;
            System.out.println(entry.getKey() + " = " + percentage + " %");
        }
        // Has all elements
        Assertions.assertEquals(4, k.size());

        // With match probability
        // Each base value has 1/13 prob, the "ab" has 10/13
        double minAccuracy = (76.0 - acceptanceProbability) / 100;
        double maxAccuracy = (77.0 + acceptanceProbability) / 100;
        double currentValue = (double) k.get("ab") / iterations;


        Assertions.assertTrue(currentValue >= minAccuracy && currentValue <= maxAccuracy);
        // Other distribution will be true if the line above is true
    }

    @Test
    public void throwsNonPositiveRatio() {
        // The negative value throws
        Assertions.assertThrows(IllegalStateException.class,
                () -> new RandomPipe.Builder<String>().add(-1, "a"));

        // The zero throws
        Assertions.assertThrows(IllegalStateException.class,
                () -> new RandomPipe.Builder<String>().add(0, "a"));
    }

}
