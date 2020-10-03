package com.yeongzhiwei.assessment.repository;

import javax.persistence.criteria.*;

import com.yeongzhiwei.assessment.model.*;

import org.springframework.data.jpa.domain.Specification;

public class HouseholdSpecs {

    public static Specification<Household> householdIncomeGreaterThan(Integer amount) {
        return (root, query, builder) -> builder.greaterThan(getHouseholdIncomeSubquery(root, query, builder), amount);
    }

    public static Specification<Household> householdIncomeLessThan(Integer amount) {
        return (root, query, builder) -> builder.lessThan(getHouseholdIncomeSubquery(root, query, builder), amount);
    }

    private static Subquery<Integer> getHouseholdIncomeSubquery(Root<Household> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Subquery<Integer> incomeSubquery = query.subquery(Integer.class);
        Root<Person> personRoot = incomeSubquery.from(Person.class);

        Join<Person, Household> personHouseholdJoin = personRoot.join(Person_.HOUSEHOLD);
        incomeSubquery.where(builder.equal(personHouseholdJoin.get(Household_.ID), root.get(Household_.ID)));

        incomeSubquery.select(builder.sum(personRoot.get(Person_.ANNUAL_INCOME)));
        return incomeSubquery;
    }

}
