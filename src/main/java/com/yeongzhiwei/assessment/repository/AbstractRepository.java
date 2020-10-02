package com.yeongzhiwei.assessment.repository;

import com.yeongzhiwei.assessment.model.AbstractEntity;

import org.springframework.data.repository.CrudRepository;

public interface AbstractRepository<T extends AbstractEntity> extends CrudRepository<T, Long> {
    
}

