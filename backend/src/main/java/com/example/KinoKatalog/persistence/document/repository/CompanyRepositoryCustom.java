package com.example.KinoKatalog.persistence.document.repository;

import org.bson.types.ObjectId;

public interface CompanyRepositoryCustom {

    void updateCompanyNameInAllMovies(ObjectId companyId, String newName);
}
