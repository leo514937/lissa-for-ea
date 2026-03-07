/* Licensed under MIT 2025-2026. */
package edu.kit.kastel.sdq.lissa.ratlr.classifier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.kastel.sdq.lissa.ratlr.configuration.ModuleConfiguration;
import edu.kit.kastel.sdq.lissa.ratlr.context.ContextStore;
import edu.kit.kastel.sdq.lissa.ratlr.knowledge.Element;
import edu.kit.kastel.sdq.lissa.ratlr.utils.Pair;

/**
 * An {@link LLMEnsembleFilter} implementation that applies a configurable
 * chain of classifier stages to filter candidate element pairs.
 * <p>
 * Each stage consists of one or more {@link Classifier classifiers} that
 * vote on the candidates. A candidate must receive at least a majority of
 * positive votes in a stage to proceed to the next one. This reuses the
 * majority-voting semantics of {@link PipelineClassifier}, but operates
 * directly on explicit candidate pairs instead of generating them from
 * element stores.
 * </p>
 */
public class ChainedLLMEnsembleFilter implements LLMEnsembleFilter {

    private static final Logger logger = LoggerFactory.getLogger(ChainedLLMEnsembleFilter.class);

    private final List<List<Classifier>> stages;
    private final double majorityFraction;

    /**
     * Creates a new chained ensemble filter.
     *
     * @param configs          configuration of classifier stages; each inner list represents a stage
     * @param contextStore     shared context store used to create classifiers
     * @param majorityFraction fraction in (0,1] used as voting threshold; 0.5 means strict majority
     */
    public ChainedLLMEnsembleFilter(
            List<List<ModuleConfiguration>> configs, ContextStore contextStore, double majorityFraction) {
        Objects.requireNonNull(configs, "configs must not be null");
        Objects.requireNonNull(contextStore, "contextStore must not be null");
        if (configs.isEmpty()) {
            throw new IllegalArgumentException("At least one stage must be configured for the ensemble filter.");
        }
        boolean hasEmptyStage = configs.stream().anyMatch(List::isEmpty);
        if (hasEmptyStage) {
            throw new IllegalArgumentException(
                    "Each stage in the ensemble filter must contain at least one classifier.");
        }
        if (majorityFraction <= 0.0 || majorityFraction > 1.0) {
            throw new IllegalArgumentException("majorityFraction must be in (0,1], but was " + majorityFraction);
        }
        this.stages = configs.stream()
                .map(stageConfigs -> stageConfigs.stream()
                        .map(cfg -> Classifier.createClassifier(cfg, contextStore))
                        .toList())
                .toList();
        this.majorityFraction = majorityFraction;
    }

    @Override
    public List<Pair<Element, Element>> filterCandidates(List<Pair<Element, Element>> candidates) {
        Objects.requireNonNull(candidates, "candidates must not be null");
        List<Pair<Element, Element>> current = new ArrayList<>(candidates);

        int stageIndex = 0;
        for (List<Classifier> stage : stages) {
            int beforeStage = current.size();
            logger.info(
                    "SLM ensemble filter: invoking stage {} with {} classifiers and {} candidates",
                    stageIndex,
                    stage.size(),
                    beforeStage);
            if (current.isEmpty()) {
                logger.info("SLM ensemble filter: no candidates left before stage {}, stopping.", stageIndex);
                break;
            }
            current = filterStage(current, stage);
            logger.info(
                    "SLM ensemble filter: reduced candidates at stage {} from {} to {}",
                    stageIndex,
                    beforeStage,
                    current.size());
            stageIndex++;
        }
        return current;
    }

    private List<Pair<Element, Element>> filterStage(
            List<Pair<Element, Element>> candidates, List<Classifier> classifiers) {
        Map<Pair<Element, Element>, AtomicInteger> counter = new LinkedHashMap<>();
        for (var candidate : candidates) {
            counter.put(candidate, new AtomicInteger(0));
        }

        for (Classifier classifier : classifiers) {
            if (candidates.isEmpty()) {
                break;
            }
            // delegate classification to the classifier using its existing task-based API
            List<ClassificationTask> tasks = candidates.stream()
                    .map(pair -> new ClassificationTask(pair.first(), pair.second(), true))
                    .toList();
            List<ClassificationResult> classificationResults =
                    classifier.classify(tasks).stream().filter(Objects::nonNull).toList();

            for (var result : classificationResults) {
                Pair<Element, Element> key = new Pair<>(result.source(), result.target());
                AtomicInteger votes = counter.get(key);
                if (votes != null) {
                    votes.incrementAndGet();
                }
            }
        }

        List<Pair<Element, Element>> remaining = new ArrayList<>();
        int majorityThreshold = calculateMajorityThreshold(classifiers.size());
        for (var entry : counter.entrySet()) {
            if (entry.getValue().get() >= majorityThreshold) {
                remaining.add(entry.getKey());
            }
        }
        return remaining;
    }

    private int calculateMajorityThreshold(int classifierCount) {
        if (majorityFraction == 0.5d) {
            // Strict majority for the common voting setup: e.g. 2->2, 3->2, 4->3.
            return (classifierCount / 2) + 1;
        }
        return (int) Math.ceil(classifierCount * majorityFraction);
    }
}
