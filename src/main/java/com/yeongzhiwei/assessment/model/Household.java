package com.yeongzhiwei.assessment.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Table(name = "household")
@Entity
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"familyMembers"})
public class Household extends AbstractEntity {
    
    @Column(name = "housing_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private HousingType housingType;

    @OneToMany(mappedBy = "household", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Person> familyMembers; 

}
