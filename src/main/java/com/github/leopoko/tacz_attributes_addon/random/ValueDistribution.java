package com.github.leopoko.tacz_attributes_addon.random;

import com.github.leopoko.tacz_attributes_addon.config.CommonConfig;
import net.minecraft.util.RandomSource;

/**
 * Generates random values within a range using different distribution curves.
 */
public class ValueDistribution {

    /**
     * Sample a value in [min, max] using the configured distribution.
     * For distributions that skew toward min, positive ranges will favor small positive values
     * and negative ranges will favor small negative values (values close to 0).
     */
    public static double sample(double min, double max, CommonConfig.DistributionType type,
                                double exponent, RandomSource random) {
        if (min >= max) return min;

        double t = random.nextDouble(); // uniform [0, 1)

        switch (type) {
            case EXPONENTIAL:
                // Skew toward lower end (closer to min)
                t = Math.pow(t, exponent);
                break;
            case QUADRATIC:
                t = t * t;
                break;
            case LINEAR:
            default:
                // No transformation
                break;
        }

        return min + t * (max - min);
    }

    /**
     * Sample with absolute-value skewing: values closer to 0 are more likely regardless of sign.
     * This is useful for ranges that span both negative and positive values.
     */
    public static double sampleAbsoluteSkewed(double min, double max, CommonConfig.DistributionType type,
                                              double exponent, RandomSource random) {
        if (min >= 0) {
            return sample(min, max, type, exponent, random);
        }
        if (max <= 0) {
            // Mirror: sample positive then negate
            return -sample(-max, -min, type, exponent, random);
        }

        // Range spans 0: decide side by range proportion, then sample from that side
        double negRange = -min;
        double posRange = max;
        double total = negRange + posRange;
        if (random.nextDouble() < negRange / total) {
            // Negative side
            return -sample(0, negRange, type, exponent, random);
        } else {
            // Positive side
            return sample(0, posRange, type, exponent, random);
        }
    }

    /**
     * Round to a reasonable precision to avoid ugly floating point numbers in tooltips.
     */
    public static double roundToPercent(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
