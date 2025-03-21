package com.example.fluxeip.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
	
	@ManyToOne
    @JoinColumn(name = "salary_detail_id", nullable = false)
	private SalaryDetail salaryDetail;
	
	@Column(name = "bonus_type", nullable = false)
    private String bonusType;

    @Column(name = "amount", nullable = false)
    private Integer amount;

}
