/* Licensed under MIT 2025-2026. */
package edu.kit.kastel.sdq.lissa.ratlr.artifactprovider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kit.kastel.sdq.lissa.ratlr.configuration.ModuleConfiguration;
import edu.kit.kastel.sdq.lissa.ratlr.context.ContextStore;
import edu.kit.kastel.sdq.lissa.ratlr.knowledge.Artifact;

/**
 * Simple {@link ArtifactProvider} for Dronology-style JSON datasets as published
 * on Hugging Face. This provider expects a JSON array where each entry
 * represents a single artifact (e.g., requirement or code entity) with at least
 * an {@code id} and {@code text} field.
 * <p>
 * The following configuration arguments are supported:
 * <ul>
 *   <li>{@code path}: path to the JSON file</li>
 *   <li>{@code artifact_type}: logical type of the artifacts (e.g.,
 *   {@code requirement}, {@code code})</li>
 *   <li>{@code id_field}: name of the field containing the identifier
 *   (default: {@code id})</li>
 *   <li>{@code text_field}: name of the field containing the textual content
 *   (default: {@code text})</li>
 * </ul>
 */
public class DronologyDatasetArtifactProvider extends ArtifactProvider {

    private static final Logger logger = LoggerFactory.getLogger(DronologyDatasetArtifactProvider.class);

    private final File jsonFile;
    private final String artifactType;
    private final String idField;
    private final String textField;

    public DronologyDatasetArtifactProvider(ModuleConfiguration configuration, ContextStore contextStore) {
        super(contextStore);
        this.jsonFile = new File(configuration.argumentAsString("path"));
        this.artifactType = configuration.argumentAsString("artifact_type", "requirement");
        this.idField = configuration.argumentAsString("id_field", "id");
        this.textField = configuration.argumentAsString("text_field", "text");
    }

    @Override
    public List<Artifact> getArtifacts() {
        List<Artifact> artifacts = new ArrayList<>();
        if (!jsonFile.exists()) {
            logger.warn("Dronology dataset file does not exist: {}", jsonFile.getAbsolutePath());
            return artifacts;
        }

        try {
            String content = Files.readString(jsonFile.toPath());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(content);
            if (!root.isArray()) {
                logger.warn("Expected JSON array in Dronology dataset file: {}", jsonFile.getAbsolutePath());
                return artifacts;
            }
            for (JsonNode node : root) {
                JsonNode idNode = node.get(idField);
                JsonNode textNode = node.get(textField);
                if (idNode == null || textNode == null) {
                    continue;
                }
                String id = idNode.asText();
                String text = textNode.asText();
                artifacts.add(new Artifact(id, artifactType, text));
            }
        } catch (IOException e) {
            logger.error("Error reading Dronology dataset file: {}", jsonFile.getAbsolutePath(), e);
        }
        return artifacts;
    }

    @Override
    public Artifact getArtifact(String identifier) {
        return getArtifacts().stream()
                .filter(a -> a.getIdentifier().equals(identifier))
                .findFirst()
                .orElse(null);
    }
}
