package com.military.asset.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bases")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String location;

    @Column(name = "commander_name", length = 100)
    private String commanderName;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Base() {}

    public Base(Long id, String code, String name, String location, String commanderName) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.location = location;
        this.commanderName = commanderName;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCommanderName() { return commanderName; }
    public void setCommanderName(String commanderName) { this.commanderName = commanderName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
