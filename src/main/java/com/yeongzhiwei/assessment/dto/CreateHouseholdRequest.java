package com.yeongzhiwei.assessment.dto;

import javax.validation.constraints.NotNull;

import com.yeongzhiwei.assessment.model.HousingType;

import lombok.Data;

@Data
public class CreateHouseholdRequest {
    
    @NotNull
    private HousingType housingType;

}
