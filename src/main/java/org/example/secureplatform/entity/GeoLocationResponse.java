package org.example.secureplatform.entity;

import lombok.Data;

@Data
public class GeoLocationResponse {
    private String status;
    private String city;
    private String country;
}
