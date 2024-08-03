package com.cedie.api.smartparking.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.Objects;
import java.util.Set;

@Entity
public class Lot {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String location;
    private Long capacity;
    private Long occupiedSpaces=0L;
    @OneToMany(mappedBy = "id")
    private Set<Vehicle> occupants;

    public Lot() {
    }

    public Lot(String location, Long capacity) {
        this.location = location;
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getCapacity() {
        return capacity;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    public Long getOccupiedSpaces() {
        return occupiedSpaces;
    }

    public void setOccupiedSpaces(Long occupiedSpaces) {
        this.occupiedSpaces = occupiedSpaces;
    }

    public Set<Vehicle> getOccupants() {
        return occupants;
    }

    public void setOccupants(Set<Vehicle> occupants) {
        this.occupants = occupants;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Lot lot = (Lot) object;
        return Objects.equals(id, lot.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
