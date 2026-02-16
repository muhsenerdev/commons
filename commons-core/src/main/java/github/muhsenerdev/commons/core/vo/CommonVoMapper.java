package github.muhsenerdev.commons.core.vo;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommonVoMapper {

    default <T> T fromVo(SingleVO<T> vo) {
        return vo == null ? null : vo.getValue();
    }

}
