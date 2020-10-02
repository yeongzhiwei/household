package com.yeongzhiwei.assessment.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Table(name = "household")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Household extends AbstractEntity {
    
    @Column(name = "housing_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private HousingType housingType;

}
