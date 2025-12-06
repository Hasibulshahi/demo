package com.example.demo.mapper;

import com.example.demo.entity.LoanEntity;
import com.example.demo.generated.model.LoanRequest;
import com.example.demo.generated.model.LoanResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@org.mapstruct.Mapper(componentModel = "spring", uses = {com.example.demo.mapper.UserMapper.class})
public interface LoanMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startDate", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "user", ignore = true)
    LoanEntity toEntity(LoanRequest request);

    @Mapping(target = "userId", source = "loan.user.id")
    @Mapping(target = "payableLoanAmount", source = "loan.payablelLoanAmount")
    LoanResponse toResponse(LoanEntity loan);

    @Mapping(target = "userId", source = "loan.user.id")
    @Mapping(target = "payableLoanAmount", source = "loan.payablelLoanAmount")
    List<LoanResponse> toLoansResponse(List<LoanEntity> loans);

}
