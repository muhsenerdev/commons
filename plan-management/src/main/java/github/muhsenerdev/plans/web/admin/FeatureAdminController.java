package github.muhsenerdev.plans.web.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.muhsenerdev.plans.application.feature.shared.FeatureResponse;
import github.muhsenerdev.plans.domain.feature.FeatureRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/features")
@RequiredArgsConstructor
@Tag(name = "Feature Admin", description = "Endpoints for managing global features")
public class FeatureAdminController {

    private final FeatureRepository featureRepository;

    @GetMapping()
    @Operation(summary = "List all core features", description = "Returns a list of all available features in the system")
    public List<FeatureResponse> listFeatures() {
        return featureRepository.findAll().stream()
                .map(f -> new FeatureResponse(f.getId(), f.getCode(), f.getName(), f.getType()))
                .collect(Collectors.toList());
    }
}
