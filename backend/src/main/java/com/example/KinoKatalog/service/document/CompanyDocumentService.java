package com.example.KinoKatalog.service.document;

import com.example.KinoKatalog.persistence.document.documents.CompanyDocument;
import com.example.KinoKatalog.persistence.document.repository.CompanyDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyDocumentService {

    private final CompanyDocumentRepository companyRepo;

    public void renameCompanyEverywhere(ObjectId companyId, String newName) {

        // update the standalone document
        CompanyDocument company = companyRepo.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        company.setName(newName);
        companyRepo.save(company);

        // update in all embedded movie documents
        companyRepo.updateCompanyNameInAllMovies(companyId, newName);
    }
}
