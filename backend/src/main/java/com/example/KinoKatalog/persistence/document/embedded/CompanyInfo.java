package com.example.KinoKatalog.persistence.document.embedded;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyInfo {
    private String name;
    private String originCountry;
}