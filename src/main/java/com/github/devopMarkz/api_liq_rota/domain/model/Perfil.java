package com.github.devopMarkz.api_liq_rota.domain.model;

public enum Perfil {

    ROLE_USUARIO_COMUM("Usuário Comum"),
    ROLE_ADMINISTRADOR("Administrador");

    private final String descricao;

    Perfil(String perfil){
        this.descricao = perfil;
    }

    public String getDescricao(){
        return this.descricao;
    }

}
