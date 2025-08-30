package com.github.devopMarkz.api_liq_rota.utils;

public class GerenciadorDePermissoes {

    public static final String ROLE_USUARIO_COMUM = "hasAnyRole('ROLE_USUARIO_COMUM', 'ROLE_ADMINISTRADOR')";
    public static final String ROLE_ADMINISTRADOR = "hasRole('ROLE_ADMINISTRADOR')";

}