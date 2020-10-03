package com.yeongzhiwei.assessment.repository;

import java.util.List;

import com.yeongzhiwei.assessment.model.Household;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface HouseholdRepository extends AbstractRepository<Household>, JpaSpecificationExecutor<Household> {

    List<Household> findAll();

}
