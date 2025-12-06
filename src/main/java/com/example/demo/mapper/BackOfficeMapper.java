package com.example.demo.mapper;

import com.example.demo.entity.BackOfficeEntity;
import com.example.demo.generated.model.BackofficeRequest;
import com.example.demo.generated.model.BackofficeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring", uses = {com.example.demo.mapper.LoanMapper.class})
public interface BackOfficeMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "loan", ignore = true)
    BackOfficeEntity toEntity(BackofficeRequest request);
    
    @org.mapstruct.Mapping(target = "loanDetails", source = "loan")
    BackofficeResponse toResponse(BackOfficeEntity entity);
    
    List<BackofficeResponse> toResponseList(List<BackOfficeEntity> entities);
}
