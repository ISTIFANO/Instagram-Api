package com.example.dashy_platforms.infrastructure.database.service;

import com.example.dashy_platforms.domaine.service.ICompanyService;
import com.example.dashy_platforms.infrastructure.database.entities.Company;
import com.example.dashy_platforms.infrastructure.database.repositeries.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService implements ICompanyService {
    private final CompanyRepository companyRepository;

    @Override
    public Company getCompanyConfig(String companyName) {
        return companyRepository.findByNameWithAutoactions(companyName)
                .orElseThrow(() -> new RuntimeException("Company not found"));
    }
    @Override
    public Company getCompanyByname(String companyName) {
        return companyRepository.findByNameWithAutoactions(companyName)
                .orElseThrow(() -> new RuntimeException("Company not found"));
    }
@Override
    public Company createCompany(Company company) {
        if (companyRepository.existsByName(company.getName())) {
            throw new RuntimeException("Company already exists");
        }
        return companyRepository.save(company);
    }
}
