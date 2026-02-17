package github.muhsenerdev.users.core.application.user.shared;

import org.mapstruct.Mapper;

import github.muhsenerdev.commons.core.vo.CommonVoMapper;
import github.muhsenerdev.users.core.domain.users.SocialProvider;

@Mapper(componentModel = "spring", uses = { CommonVoMapper.class })
public interface UserModuleVoMapper {

    default SocialProvider toSocialProvider(String provider) {
        return SocialProvider.fromStringOrNull(provider);
    }

}
