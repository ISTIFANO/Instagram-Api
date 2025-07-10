package com.example.dashy_platforms.domaine.service;

import com.example.dashy_platforms.infrastructure.database.entities.Company;

public interface ICompanyService{

    /**
     * Retrieves company configuration by name including auto-actions
     * @param companyName The name of the company to find
     * @return The found company entity
     * @throws RuntimeException if company is not found
     */
    Company getCompanyConfig(String companyName);

    /**
     * Retrieves company by name including auto-actions (alias for getCompanyConfig)
     * @param companyName The name of the company to find
     * @return The found company entity
     * @throws RuntimeException if company is not found
     */
    Company getCompanyByname(String companyName);

    /**
     * Creates a new company
     * @param company The company entity to create
     * @return The created company entity
     * @throws RuntimeException if company with same name already exists
     */
    Company createCompany(Company company);
}