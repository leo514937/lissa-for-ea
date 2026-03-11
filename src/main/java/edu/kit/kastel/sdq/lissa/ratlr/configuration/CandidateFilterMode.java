/* Licensed under MIT 2025-2026. */
package edu.kit.kastel.sdq.lissa.ratlr.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Controls how the SLM candidate filter chain is interpreted.
 */
public enum CandidateFilterMode {
    /**
     * Pre-filter with one small model, then run a voting ensemble over the remaining stages.
     */
    @JsonProperty("voting")
    VOTING,

    /**
     * Run each stage sequentially (layered filtering).
     */
    @JsonProperty("layered")
    LAYERED
}