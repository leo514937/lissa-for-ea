/* Licensed under MIT 2025-2026. */
package edu.kit.kastel.sdq.lissa.ratlr.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.kit.kastel.sdq.lissa.ratlr.knowledge.Element;

/**
 * Utility for generating Cartesian-product candidate pairs of source and target elements.
 * <p>
 * This generator is intentionally simple and does not apply any IR-based pre-filtering.
 * It is meant to be used together with SLM ensemble filters that operate directly on
 * explicit candidate lists.
 * </p>
 */
public final class CartesianCandidateGenerator {

    private CartesianCandidateGenerator() {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * Generates all source–target element pairs as a Cartesian product.
     *
     * @param sources the source elements
     * @param targets the target elements
     * @return list of source–target pairs
     */
    public static List<Pair<Element, Element>> generate(List<Element> sources, List<Element> targets) {
        Objects.requireNonNull(sources, "sources must not be null");
        Objects.requireNonNull(targets, "targets must not be null");

        List<Pair<Element, Element>> result = new ArrayList<>(
                sources.size() * (long) targets.size() > Integer.MAX_VALUE
                        ? Integer.MAX_VALUE
                        : sources.size() * targets.size());
        for (Element src : sources) {
            for (Element tgt : targets) {
                result.add(new Pair<>(src, tgt));
            }
        }
        return result;
    }
}
