/* Licensed under MIT 2025-2026. */
package edu.kit.kastel.sdq.lissa.ratlr.classifier;

import java.util.List;
import java.util.Objects;

import edu.kit.kastel.sdq.lissa.ratlr.configuration.ModuleConfiguration;
import edu.kit.kastel.sdq.lissa.ratlr.context.ContextStore;

/**
 * Factory for creating {@link LLMEnsembleFilter} instances from configuration.
 */
public final class LLMEnsembleFilterFactory {

    private static final double DEFAULT_MAJORITY_FRACTION = 0.5;

    private LLMEnsembleFilterFactory() {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * Creates a chained ensemble filter from the provided configuration.
     *
     * @param stages       configuration of classifier stages
     * @param contextStore shared context store
     * @return an {@link LLMEnsembleFilter} instance
     */
    public static LLMEnsembleFilter createChainedFilter(
            List<List<ModuleConfiguration>> stages, ContextStore contextStore) {
        Objects.requireNonNull(stages, "stages must not be null");
        Objects.requireNonNull(contextStore, "contextStore must not be null");
        return new ChainedLLMEnsembleFilter(stages, contextStore, DEFAULT_MAJORITY_FRACTION);
    }
}
