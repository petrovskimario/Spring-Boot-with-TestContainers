package com.iwlabs.testcontainers.dto;

public interface StudentWithRankProjection {
    String getFirstName();
    String getLastName();
    String getEmail();
    Double getGpa();
    Integer getRank();
}
