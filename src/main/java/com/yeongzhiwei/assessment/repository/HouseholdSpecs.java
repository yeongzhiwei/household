package com.yeongzhiwei.assessment.repository;

import javax.persistence.criteria.*;

import com.yeongzhiwei.assessment.model.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

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

    public static Specification<Household> hasPersonOlderThan(LocalDate date) {
        return (root, query, builder) -> {
            Subquery<Long> personSubquery = query.subquery(Long.class);
            Root<Person> personRoot = personSubquery.from(Person.class);

            Join<Person, Household> personHouseholdJoin = personRoot.join(Person_.HOUSEHOLD);
            personSubquery.where(
                    builder.equal(personHouseholdJoin.get(Household_.ID), root.get(Household_.ID)),
                    builder.lessThan(personRoot.get(Person_.DOB), date)
            );
            personSubquery.select(builder.count(personRoot.get(Person_.DOB)));

            return builder.greaterThan(personSubquery, 0L);
        };
    }

    public static Specification<Household> hasPersonYoungerThan(LocalDate date) {
        return (root, query, builder) -> {
            Subquery<Long> personSubquery = query.subquery(Long.class);
            Root<Person> personRoot = personSubquery.from(Person.class);

            Join<Person, Household> personHouseholdJoin = personRoot.join(Person_.HOUSEHOLD);
            personSubquery.where(
                    builder.equal(personHouseholdJoin.get(Household_.ID), root.get(Household_.ID)),
                    builder.greaterThan(personRoot.get(Person_.DOB), date)
            );
            personSubquery.select(builder.count(personRoot.get(Person_.DOB)));

            return builder.greaterThan(personSubquery, 0L);
        };
    }

    public static Specification<Household> hasCouple() {
        return (root, query, builder) -> {
            Subquery<Long> personSubquery = query.subquery(Long.class);
            Root<Person> personRoot = personSubquery.from(Person.class);

            Join<Person, Household> personHouseholdJoin = personRoot.join(Person_.HOUSEHOLD);
            personSubquery.where(
                    builder.equal(personHouseholdJoin.get(Household_.ID), root.get(Household_.ID)),
                    builder.isNotNull(personRoot.get(Person_.SPOUSE))
            );
            personSubquery.select(builder.count(personRoot.get(Person_.SPOUSE)));

            return builder.greaterThanOrEqualTo(personSubquery, 1L);
        };
    }

}
