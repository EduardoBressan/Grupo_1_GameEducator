package com.example.grupo_1_gameeducator.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Camada: ENTITY.
@Entity
@Table(name = "cursos")
public class CursoEntity {

    private static final String AREA_PADRAO = "Geral";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 500)
    private String descricao;

    // Cenario do Felipe: cursos relacionados no resgate sao da mesma area.
    @Column(nullable = false)
    private String area;

    protected CursoEntity() {
    }

    public CursoEntity(String titulo, String descricao, String area) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.area = (area == null || area.isBlank()) ? AREA_PADRAO : area;
    }

    public CursoEntity(String titulo, String descricao) {
        this(titulo, descricao, AREA_PADRAO);
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getArea() {
        return area;
    }

    public void alterarTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void alterarDescricao(String descricao) {
        this.descricao = descricao;
    }
}
