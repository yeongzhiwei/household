package com.yeongzhiwei.assessment.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.yeongzhiwei.assessment.model.Person.Gender;
import com.yeongzhiwei.assessment.model.Person.MartialStatus;
import com.yeongzhiwei.assessment.model.Person.OccupationType;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class FamilyMemberResponse {
    
    private Long id;
    private String name;
    private Gender gender;
    private MartialStatus martialStatus;
    private Long spouseId;
    private OccupationType occupationType;
    private Integer annualIncome;
    private LocalDate dob;

}
