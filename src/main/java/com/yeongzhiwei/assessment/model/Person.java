package com.yeongzhiwei.assessment.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Table(name="family_member")
@Entity
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"spouse", "household"})
public class Person extends AbstractEntity {
    
    public enum Gender {
        MALE, FEMALE
    }
    
    public enum MartialStatus {
        SINGLE, MARRIED, DIVORCED, SEPARATED, WIDOWED
    }

    public enum OccupationType {
        UNEMPLOYED, STUDENT, EMPLOYED
    }

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "martial_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MartialStatus martialStatus;

    @OneToOne
    private Person spouse;

    @Column(name = "occupation_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OccupationType occupationType;

    @Column(name = "annual_income", nullable = false)
    private Integer annualIncome;

    @Column(name = "dob", nullable = false)
    private LocalDate dob;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;
    
}
