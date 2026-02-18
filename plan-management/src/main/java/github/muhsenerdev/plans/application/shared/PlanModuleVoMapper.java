package github.muhsenerdev.plans.application.shared;

import org.mapstruct.Mapper;

import github.muhsenerdev.commons.core.vo.CommonVoMapper;

@Mapper(componentModel = "spring", uses = { CommonVoMapper.class })
public interface PlanModuleVoMapper {

}
