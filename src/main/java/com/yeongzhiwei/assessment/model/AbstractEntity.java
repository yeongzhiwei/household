package com.yeongzhiwei.assessment.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@MappedSuperclass
@EqualsAndHashCode
@Getter
public abstract class AbstractEntity {
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "CUST_SEQ")
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

}
