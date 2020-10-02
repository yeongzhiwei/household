package com.yeongzhiwei.assessment.dto;

import com.yeongzhiwei.assessment.model.HousingType;

import lombok.Data;

@Data
public class HouseholdResponse {
    
    private Long id;
    private HousingType housingType;

}
