package com.yeongzhiwei.assessment.config;

import com.yeongzhiwei.assessment.dto.FamilyMemberResponse;
import com.yeongzhiwei.assessment.model.Person;
import com.yeongzhiwei.assessment.repository.PersonRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    
    @Bean
    public ModelMapper modelMapper(@Autowired PersonRepository repository) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(Person.class, FamilyMemberResponse.class).addMapping(
            src -> src.getSpouse().getId(), FamilyMemberResponse::setSpouseId);
        return modelMapper;
    }

}
