package com.example.fluxeip.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "salary_bonus")
public class SalaryBonus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "salary_bonus_id",nullable = false)
	private Integer salaryBonusId;
	
	@JsonIgnore
	@ManyToMany(mappedBy = "bonuses")
	private List<SalaryDetail> salaryDetails = new ArrayList<>();
	
	@Column(name = "bonus_type", nullable = false)
    private String bonusType;

    @Column(name = "amount", nullable = false)
    private Integer amount;
    
    @Column(name = "IsActive", nullable = false)
    private boolean isActive;
}
