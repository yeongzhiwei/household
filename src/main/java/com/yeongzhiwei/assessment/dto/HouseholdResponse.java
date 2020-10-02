package com.yeongzhiwei.assessment.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.yeongzhiwei.assessment.model.HousingType;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class HouseholdResponse {
    
    private Long id;
    private HousingType housingType;
    private List<FamilyMemberResponse> familyMembers;

}
