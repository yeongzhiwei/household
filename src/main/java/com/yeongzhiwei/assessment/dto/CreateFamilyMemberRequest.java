package com.yeongzhiwei.assessment.dto;

import java.time.LocalDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.yeongzhiwei.assessment.model.Person.Gender;
import com.yeongzhiwei.assessment.model.Person.MartialStatus;
import com.yeongzhiwei.assessment.model.Person.OccupationType;

import lombok.Data;

@Data
public class CreateFamilyMemberRequest {
    
    @NotEmpty
    private String name;

    @NotNull
    private Gender gender;

    @NotNull
    private MartialStatus martialStatus;

    private Long spouseId;

    @NotNull
    private OccupationType occupationType;

    @Min(value = 0)
    private Integer annualIncome = 0;

    @NotNull
    @JsonDeserialize(using = LocalDateDeserializer.class)  
    @JsonSerialize(using = LocalDateSerializer.class)  
    private LocalDate dob;

}
