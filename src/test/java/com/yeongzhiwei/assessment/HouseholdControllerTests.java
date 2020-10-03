package com.yeongzhiwei.assessment;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeongzhiwei.assessment.dto.CreateFamilyMemberRequest;
import com.yeongzhiwei.assessment.dto.CreateHouseholdRequest;
import com.yeongzhiwei.assessment.model.Household;
import com.yeongzhiwei.assessment.model.HousingType;
import com.yeongzhiwei.assessment.model.Person;
import com.yeongzhiwei.assessment.model.Person.Gender;
import com.yeongzhiwei.assessment.model.Person.MartialStatus;
import com.yeongzhiwei.assessment.model.Person.OccupationType;
import com.yeongzhiwei.assessment.repository.HouseholdRepository;
import com.yeongzhiwei.assessment.repository.PersonRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class HouseholdControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HouseholdRepository householdRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // Warning: updating test data? Make sure you update the assertions in tests too
    private String[][] rawHouseholds = {{"LANDED"}, {"CONDOMINIUM"}, {"HDB"}, {"HDB"}, {"HDB"}, {"HDB"}};
    private String[][][] rawFamilyMembersHouseholds = {
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
    private int getFamilyMembersLength() {
        int length = 0;
        for (String[][] rawFamilyMembers : rawFamilyMembersHouseholds) {
            length += rawFamilyMembers.length;
        }
        return length;
    }

    @BeforeEach
    @Transactional
    void seedData() {
        householdRepository.deleteAll();
        personRepository.deleteAll();

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
    }

    @AfterEach
    @Transactional
    void removeData() {
        householdRepository.deleteAll();
        personRepository.deleteAll();
    }
    
    @Test
    void getHouseholds() throws Exception {
        this.mockMvc.perform(get("/households"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(rawHouseholds.length));
    }

    @Test
    void getHouseholds_incomeLessThan150000_youngerThan16yo() throws Exception {
        this.mockMvc.perform(get("/households?income_lt=150000&age_lt=16"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getHouseholds_olderThan50yo() throws Exception {
        this.mockMvc.perform(get("/households?age_gt=50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getHouseholds_youngerThan5yo() throws Exception {
        this.mockMvc.perform(get("/households?age_lt=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getHouseholds_incomeLessThan100000() throws Exception {
        this.mockMvc.perform(get("/households?income_lt=100000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    void createHousehold_Success() throws Exception {
        CreateHouseholdRequest request = new CreateHouseholdRequest();
        request.setHousingType(HousingType.CONDOMINIUM);

        this.mockMvc.perform(post("/households")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());

        assertEquals(rawHouseholds.length + 1, householdRepository.count());
    }

    @Test
    void createHousehold_EmptyBody() throws Exception {
        this.mockMvc.perform(post("/households")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        assertEquals(rawHouseholds.length, householdRepository.count());
    }

    @Test
    void createHousehold_NullHousingType() throws Exception {
        CreateHouseholdRequest request = new CreateHouseholdRequest();

        this.mockMvc.perform(post("/households")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        assertEquals(rawHouseholds.length, householdRepository.count());
    }

    @Test
    void getHousehold_Success() throws Exception {
        Long validHouseholdId = householdRepository.findAll().get(0).getId();

        this.mockMvc.perform(get("/households/" + validHouseholdId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.housingType").exists());
    }

    @Test
    void getHousehold_NotFound() throws Exception {
        List<Long> householdIds = householdRepository.findAll()
                                    .stream()
                                    .map(Household::getId)
                                    .collect(toList());
        Long invalidHouseholdId = 1L;
        while (householdIds.contains(invalidHouseholdId)) {
            invalidHouseholdId += 1;
        }  

        this.mockMvc.perform(get("/households/" + invalidHouseholdId))
            .andExpect(status().isNotFound());
    }

    @Test
    void addFamilyMember_Success() throws Exception {
        Household validHousehold = householdRepository.findAll().get(0);

        Person person = new Person();
        person.setName("Ah Lian");
        person.setGender(Gender.MALE);
        person.setMartialStatus(MartialStatus.MARRIED);
        person.setOccupationType(OccupationType.EMPLOYED);
        person.setAnnualIncome(100000);
        person.setDob(LocalDate.now());
        person.setHousehold(validHousehold);
        person = personRepository.save(person);

        CreateFamilyMemberRequest request = new CreateFamilyMemberRequest();
        request.setName("Ah Low");
        request.setGender(Gender.FEMALE);
        request.setMartialStatus(MartialStatus.MARRIED);
        request.setOccupationType(OccupationType.UNEMPLOYED);
        request.setDob(LocalDate.now());
        request.setSpouseId(person.getId());

        this.mockMvc.perform(post("/households/" + validHousehold.getId() + "/familymembers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.spouseId").value(person.getId()));

        assertEquals(getFamilyMembersLength() + 2, personRepository.count());
    }

    @Test
    void addFamilyMember_NonExistentHouseholdId() throws Exception {
        List<Long> householdIds = householdRepository.findAll()
                                    .stream()
                                    .map(Household::getId)
                                    .collect(toList());
        Long invalidHouseholdId = 1L;
        while (householdIds.contains(invalidHouseholdId)) {
            invalidHouseholdId += 1;
        }
        CreateFamilyMemberRequest request = new CreateFamilyMemberRequest();
        request.setName("Ah Lian");
        request.setGender(Gender.MALE);
        request.setMartialStatus(MartialStatus.SINGLE);
        request.setOccupationType(OccupationType.EMPLOYED);
        request.setAnnualIncome(100000);
        request.setDob(LocalDate.now());

        this.mockMvc.perform(post("/households/" + invalidHouseholdId + "/familymembers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void addFamilyMember_NonExistentSpouseId() throws Exception {
        Long validHouseholdId = householdRepository.findAll().get(0).getId();
        CreateFamilyMemberRequest request = new CreateFamilyMemberRequest();
        request.setName("Ah Lian");
        request.setGender(Gender.MALE);
        request.setMartialStatus(MartialStatus.SINGLE);
        request.setSpouseId(999L);
        request.setOccupationType(OccupationType.EMPLOYED);
        request.setAnnualIncome(100000);
        request.setDob(LocalDate.now());

        this.mockMvc.perform(post("/households/" + validHouseholdId + "/familymembers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        assertEquals(getFamilyMembersLength(), personRepository.count());
    }
    
}
