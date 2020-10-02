package com.yeongzhiwei.assessment.repository;

import java.util.List;

import com.yeongzhiwei.assessment.model.Household;

public interface HouseholdRepository extends AbstractRepository<Household> {

    List<Household> findAll();

}
