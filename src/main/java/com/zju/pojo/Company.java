package com.zju.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class Company {
    private Integer companyId;
    private String companyName;
    private BigDecimal registeredCapital;
    private String registrationType;
    private LocalDate registrationTime;
    private LocalDate establishmentDate;
    private String registeredAddress;
    private String businessScope;
    private String contactNumber;
    private String businessStatus;
    private String USCC;
    private String industry;
    private Integer numberOfEmployees;
    private Integer numberOfInsured;
    private String businessRisk;
    private String legalRisk;
    private String selfRisk;
    private String surroundingRisk;
    private String historicalRisk;
    private String alertReminder;
}
