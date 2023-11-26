package com.zju.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data

public class Company {
    private Integer id;
    private String companyName;
    private BigDecimal registeredCapital;
    private String registrationType;
    private LocalDate registrationTime;
    private LocalDate establishmentDate;
    private String registeredAddress;
    private String businessScope;
    private String contactNumber;
    private String businessStatus;
    private String Uscc;
    private String Brn;
    private String industry;
    private String numberOfEmployees;
    private Integer numberOfInsured;
    private String businessRisk;
    private String legalRisk;
    private String selfRisk;
    private String surroundingRisk;
    private String historicalRisk;
    private String alertReminder;
    private Float objectiveRating;
    private Float subjectiveRating;
}
