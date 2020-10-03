package com.yeongzhiwei.assessment.repository;

import com.yeongzhiwei.assessment.model.Person;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PersonRepository extends AbstractRepository<Person>, JpaSpecificationExecutor<Person> {

}
