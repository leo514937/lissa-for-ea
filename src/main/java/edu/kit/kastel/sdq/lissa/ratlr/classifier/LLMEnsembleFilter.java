/* Licensed under MIT 2025-2026. */
package edu.kit.kastel.sdq.lissa.ratlr.classifier;

import java.util.List;

import edu.kit.kastel.sdq.lissa.ratlr.knowledge.Element;
import edu.kit.kastel.sdq.lissa.ratlr.utils.Pair;

/**
 * Filters a list of candidate source–target element pairs using an ensemble of
 * (typically small) language models.
 * <p>
 * Implementations may apply chained stages and majority voting per stage.
 * The contract of this interface is that the returned list is a subset of the
 * input list, potentially reordered.
 * </p>
 */
public interface LLMEnsembleFilter {

    /**
     * Filters a list of candidate source–target element pairs using an ensemble
     * of small language models.
     *
     * @param candidates the candidate element pairs to filter
     * @return a (possibly reduced) list of candidate pairs that passed the filter
     */
    List<Pair<Element, Element>> filterCandidates(List<Pair<Element, Element>> candidates);
}
