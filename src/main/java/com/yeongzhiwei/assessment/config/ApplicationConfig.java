package com.yeongzhiwei.assessment.config;

import com.yeongzhiwei.assessment.dto.FamilyMemberResponse;
import com.yeongzhiwei.assessment.model.Household;
import com.yeongzhiwei.assessment.model.HousingType;
import com.yeongzhiwei.assessment.model.Person;
import com.yeongzhiwei.assessment.repository.HouseholdRepository;
import com.yeongzhiwei.assessment.repository.PersonRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.Arrays;

@Configuration
public class ApplicationConfig {

    @Bean
    public ModelMapper modelMapper(@Autowired PersonRepository repository) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(Person.class, FamilyMemberResponse.class).addMapping(
                src -> src.getSpouse().getId(), FamilyMemberResponse::setSpouseId);
        return modelMapper;
    }

    @Bean
    public CommandLineRunner seedData(
            HouseholdRepository householdRepository,
            PersonRepository personRepository) {
        return args -> {
            String[][] rawHouseholds = {{"LANDED"}, {"CONDOMINIUM"}, {"HDB"}, {"HDB"}, {"HDB"}, {"HDB"}};
            String[][][] rawFamilyMembersHouseholds = {
                    // husband/wife; income > $100k/$150k; age < 5/16 yo
                    {{"Adam", "MALE", "MARRIED", "EMPLOYED", "100000", "1980-12-12"},
                            {"Anna", "FEMALE", "MARRIED", "EMPLOYED", "250000", "1980-05-05"},
                            {"Angel", "FEMALE", "SINGLE", "UNEMPLOYED", "0", "2018-05-05"},
                            {"Abbe", "FEMALE", "SINGLE", "STUDENT", "0", "2012-05-05"}},
                    // husband/wife; income < $100k/$150k; age < 16 yo
                    {{"Bob", "MALE", "MARRIED", "EMPLOYED", "25000", "1980-12-12"},
                            {"Bella", "FEMALE", "MARRIED", "EMPLOYED", "25000", "1980-05-05"},
                            {"Brianna", "FEMALE", "SINGLE", "UNEMPLOYED", "0", "2013-05-05"}},
                    // Husband/wife; Income < $100k/$150k
                    {{"Charlie", "MALE", "MARRIED", "EMPLOYED", "25000", "1980-12-12"},
                            {"Cecilia", "FEMALE", "MARRIED", "EMPLOYED", "25000", "1980-05-05"}},
                    // No husband/wife; Income < $100k/$150k, age < 5/16 yo
                    {{"Dan", "MALE", "WIDOWED", "EMPLOYED", "25000", "1980-12-12"},
                            {"Diana", "FEMALE", "SINGLE", "UNEMPLOYED", "0", "2013-05-05"}},
                    // age > 50
                    {{"Dan", "MALE", "WIDOWED", "UNEMPLOYED", "0", "1950-12-12"}},
                    // age < 50
                    {{"Ezekiel", "MALE", "WIDOWED", "UNEMPLOYED", "0", "1978-12-12"}},
            };

            for (int i = 0; i < rawHouseholds.length; i++) {
                String[] rawHousehold = rawHouseholds[i];
                String[][] rawFamilyMembersHousehold = rawFamilyMembersHouseholds[i];

                Household household = new Household();
                household.setHousingType(HousingType.valueOf(rawHousehold[0]));
                householdRepository.save(household);

                Person spouse = null;
                for (String[] rawFamilyMember : rawFamilyMembersHousehold) {
                    Person familyMember = new Person();
                    familyMember.setName(rawFamilyMember[0]);
                    familyMember.setGender(Person.Gender.valueOf(rawFamilyMember[1]));
                    familyMember.setMartialStatus(Person.MartialStatus.valueOf(rawFamilyMember[2]));
                    familyMember.setOccupationType(Person.OccupationType.valueOf(rawFamilyMember[3]));
                    familyMember.setAnnualIncome(Integer.valueOf(rawFamilyMember[4]));
                    familyMember.setDob(LocalDate.parse(rawFamilyMember[5]));
                    familyMember.setHousehold(household);
                    Person savedPerson = personRepository.save(familyMember);

                    if (rawFamilyMember[2].equals(Person.MartialStatus.MARRIED.name())) {
                        if (spouse == null) {
                            spouse = familyMember;
                        } else {
                            savedPerson.setSpouse(spouse);
                            spouse.setSpouse(savedPerson);
                            personRepository.saveAll(Arrays.asList(savedPerson, spouse));
                            spouse = null;
                        }
                    }
                }
            }
        };
    }
}
