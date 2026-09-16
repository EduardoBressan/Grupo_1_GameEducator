package com.example.grupo_1_gameeducator.domain;

import com.example.grupo_1_gameeducator.domain.vo.AreaCurso;
import com.example.grupo_1_gameeducator.domain.vo.DescricaoCurso;
import com.example.grupo_1_gameeducator.domain.vo.TituloCurso;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cursos")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private TituloCurso titulo;

    @Embedded
    private DescricaoCurso descricao;

    // Usada no resgate para achar cursos relacionados.
    @Embedded
    private AreaCurso area;

    // So o JPA usa.
    protected Curso() {
    }

    public Curso(String titulo) {
        this(titulo, null, null);
    }

    public Curso(String titulo, String descricao) {
        this(titulo, descricao, null);
    }

    public Curso(String titulo, String descricao, String area) {
        this.titulo = new TituloCurso(titulo);
        this.descricao = new DescricaoCurso(descricao);
        this.area = new AreaCurso(area);
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo.getValor();
    }

    // Curso vindo do banco sem descricao chega com o VO nulo.
    public String getDescricao() {
        return descricao != null ? descricao.getValor() : null;
    }

    public String getArea() {
        return area.getValor();
    }

    public void alterarTitulo(String titulo) {
        this.titulo = new TituloCurso(titulo);
    }

    public void alterarDescricao(String descricao) {
        this.descricao = new DescricaoCurso(descricao);
    }
}
