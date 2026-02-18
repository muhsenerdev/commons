package github.muhsenerdev.plans.domain.feature;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import github.muhsenerdev.commons.jpa.entity.SoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "features")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE features SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND version = ?")
@SQLRestriction("deleted_at IS NULL")
public class Feature extends SoftDeletableEntity {

    @Column(nullable = false, unique = true)
    private String code; // e.g., "TALK_MINUTES", "AI_REWRITE"

    @Column(nullable = false)
    private String name; // e.g., "Konuşma Dakikası"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeatureType type; // QUOTA, BOOLEAN
}
