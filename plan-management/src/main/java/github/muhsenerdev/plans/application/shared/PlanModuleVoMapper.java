package github.muhsenerdev.plans.application.shared;

import org.mapstruct.Mapper;

import github.muhsenerdev.commons.core.vo.CommonVoMapper;
import github.muhsenerdev.plans.domain.plan.PlanType;

@Mapper(componentModel = "spring", uses = { CommonVoMapper.class })
public interface PlanModuleVoMapper {

    default PlanType toPlanType(String type) {
        return type == null ? null : PlanType.fromString(type);
    }

}
